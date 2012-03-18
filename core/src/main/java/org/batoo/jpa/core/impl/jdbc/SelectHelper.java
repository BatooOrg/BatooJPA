/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.batoo.jpa.core.impl.jdbc;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Deque;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.lang.StringUtils;
import org.batoo.jpa.core.impl.SessionImpl;
import org.batoo.jpa.core.impl.instance.ManagedInstance;
import org.batoo.jpa.core.impl.mapping.Association;
import org.batoo.jpa.core.impl.mapping.OwnedAssociation;
import org.batoo.jpa.core.impl.mapping.OwnerAssociation;
import org.batoo.jpa.core.impl.types.EntityTypeImpl;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.BiMap;
import com.google.common.collect.Collections2;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Helper class for entity selects.
 * <p>
 * Primary functionality is:
 * <ul>
 * <li>Generating a select statement that spans all the tables
 * <li>Expanding the select statement to the eager associations
 * <li>Expanding the select statement to the eager element collections.
 * <li>Executing the select
 * <li>Populating the passed managed instance with the values returned.
 * </ul>
 * 
 * @author hceylan
 * @since $version
 */
public class SelectHelper<X> {

	private final EntityTypeImpl<X> type;

	private final BiMap<String, PhysicalColumn> columnAliases = HashBiMap.create();
	private final Map<String, PhysicalTable> tableAliases = HashBiMap.create();
	private final Map<String, PhysicalTable> secondarytableAliases = HashBiMap.create();
	private final Map<String, PhysicalTable> primaryTableAliases = HashBiMap.create();
	private final List<Deque<Association<?, ?>>> entityPaths = Lists.newArrayList();

	private final Map<String, PhysicalColumn> predicates = Maps.newHashMap();

	private String selectSql;

	/**
	 * @param entityTypeImpl
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public SelectHelper(EntityTypeImpl<X> type) {
		super();

		this.type = type;
	}

	/**
	 * Adds fields to the list of fields
	 * 
	 * @param fieldsBuffer
	 *            the fields buffer
	 * @param table
	 *            the table
	 * @param tableNo
	 *            the number of the table
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private void addFields(List<String> fieldsBuffer, PhysicalTable table, final int tableNo) {
		table.sortColumns();

		final List<String> fields = Lists.transform(table.getColumns(), new Function<PhysicalColumn, String>() {

			int fieldNo = 0;

			@Override
			public String apply(PhysicalColumn input) {
				// create the alias as TX_FY
				final String alias = "T" + tableNo + "_F" + this.fieldNo++;

				// save the mapping
				SelectHelper.this.columnAliases.put(alias, input);

				// if this is an id field on the primary table then add it to predicates
				if ((tableNo == 0) && input.isId()) {
					SelectHelper.this.predicates.put(alias, input);
				}

				// form the complete field
				// TX.[FieldNo] AS TX_FY
				return "T" + tableNo + "." + input.getPhysicalName() + " AS " + alias;
			}
		});

		// join the fields as F1, F2, ..., FN and add to the fields buffer
		fieldsBuffer.add("\t" + Joiner.on(", ").join(fields));
	}

	/**
	 * Adds a join for the association.
	 * 
	 * @param joinsBuffer
	 *            the joins buffer
	 * @param parentTableNo
	 *            the no of the parent table
	 * @param tableNo
	 *            the no of the table
	 * @param association
	 *            the association
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private void addPrimaryJoin(List<String> joinsBuffer, final int parentTableNo, final int tableNo, final Association<?, ?> association) {
		OwnerAssociation<?, ?> ownerAssociation;

		// find the owner side of the association
		if (association instanceof OwnerAssociation) {
			ownerAssociation = (OwnerAssociation<?, ?>) association;
		}
		else {
			ownerAssociation = ((OwnedAssociation<?, ?>) association).getOpposite();
		}

		final Collection<PhysicalColumn> foreignKeys = ownerAssociation.getPhysicalColumns().values();

		final Collection<String> restrictions = Collections2.transform(foreignKeys, new Function<PhysicalColumn, String>() {

			@Override
			public String apply(PhysicalColumn input) {
				final String left = "T" + (association instanceof OwnedAssociation ? tableNo : parentTableNo);
				final String right = "T" + (association instanceof OwnedAssociation ? parentTableNo : tableNo);

				return left + "." + input.getPhysicalName() + " = " + right + "." + input.getReferencedColumn().getPhysicalName();
			}
		});

		this.composeJoin(joinsBuffer, association.getType().getPrimaryTable(), tableNo, restrictions);
	}

	/**
	 * Joins all the parts and returns the final query.
	 * 
	 * @param fieldsBuffer
	 *            the fields buffer
	 * @param joinsBuffer
	 *            the joins buffer
	 * @param primaryTable
	 *            the primary table
	 * @return the final select query
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private String compose(final List<String> fieldsBuffer, final List<String> joinsBuffer, final PhysicalTable primaryTable) {
		// Generate the where statement
		// T1_F1 = ? [AND T1_F2 = ? [...]]
		final String where = Joiner.on(" AND ").join(//
			Collections2.transform(this.predicates.values(), new Function<PhysicalColumn, String>() {

				@Override
				public String apply(PhysicalColumn input) {
					return "T0." + input.getPhysicalName() + " = ?";
				}
			}));

		final String fields = Joiner.on(",\n").join(fieldsBuffer);
		final String join = Joiner.on("\n").join(joinsBuffer);
		final String primaryTableName = primaryTable.getQualifiedPhysicalName();

		// compose and return the final query
		return "SELECT \n"//
			+ fields //
			+ "\nFROM " + primaryTableName + " AS T0"//
			+ (StringUtils.isNotBlank(join) ? "\n" + join : "")//
			+ "\nWHERE " + where;
	}

	private void composeJoin(List<String> joinsBuffer, PhysicalTable table, int y, final Collection<String> restrictions) {
		// LEFT OUTER JOIN TABLE as Ty ON TX_F1 = TY_F1 [AND TX_F2 = TY_F2 [...]]
		joinsBuffer.add("\tLEFT OUTER JOIN " + table.getQualifiedPhysicalName() + " AS T" + y + " ON "
			+ Joiner.on(" AND ").join(restrictions));
	}

	/**
	 * Generates and returns the select SQL.
	 * The select SQL must
	 * <ul>
	 * <li>Span all the tables
	 * <li>Spans to the eager associations
	 * <li>Spans to the eager element collections.
	 * 
	 * @return the generated select SQL
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private String getSelectSql() {
		if (this.selectSql == null) {
			final List<String> fieldsBuffer = Lists.newArrayList();
			final List<String> joinsBuffer = Lists.newArrayList();

			// initialize the table number
			final int tableNo = 0;

			final PhysicalTable primaryTable = this.type.getPrimaryTable();

			this.processType(fieldsBuffer, joinsBuffer, null, this.type, Lists.<Association<?, ?>> newLinkedList(), tableNo, tableNo);

			// compose and return the final query
			this.selectSql = this.compose(fieldsBuffer, joinsBuffer, primaryTable);
		}

		return this.selectSql;
	}

	private void processType(final List<String> fieldsBuffer, final List<String> joinsBuffer, EntityTypeImpl<?> parentType,
		EntityTypeImpl<?> type, Deque<Association<?, ?>> path, int parentTableNo, int tableNo) {
		for (final PhysicalTable table : type.getTables().values()) {

			// add fields
			this.addFields(fieldsBuffer, table, tableNo);

			this.tableAliases.put("T" + tableNo, table);

			if (table.isPrimary()) {
				if (parentType != null) { // we are not the root entity
					this.addPrimaryJoin(joinsBuffer, parentTableNo, tableNo, path.getLast());
				}

				parentTableNo = tableNo;
				this.primaryTableAliases.put("T" + tableNo, table);
			}
			else {
				this.secondarytableAliases.put("T" + tableNo, table);
				// this.addSecondaryJoin(joinsBuffer, table, primaryTableNo, tableNo);
			}

			// increment the tableNo index
			tableNo++;
		}

		// process the associations
		for (final Association<?, ?> child : type.getAssociations()) {
			if (child.isEager()) {
				// if we already went down that road avoid circularity
				if (path.contains(child.getOpposite())) {
					continue;
				}

				// handle the path
				path = Lists.newLinkedList(path);
				path.addLast(child);
				this.entityPaths.add(path);

				this.processType(fieldsBuffer, joinsBuffer, child.getOwner(), child.getType(), path, parentTableNo, tableNo);
			}
		}
	}

	/**
	 * Performs the select and populates the managed instance.
	 * 
	 * @param session
	 *            the session
	 * @param managedInstance
	 *            the managed instance
	 * @throws SQLException
	 * 
	 * @since $version
	 * @author hceylan
	 * @return
	 */
	public ManagedInstance<X> select(SessionImpl session, final ManagedInstance<? extends X> managedInstance) throws SQLException {
		// Do not inline, generation of the select SQL will initialize the predicates!
		final String selectSql = this.getSelectSql();

		final Collection<Object> params = Collections2.transform(this.predicates.values(), new Function<PhysicalColumn, Object>() {
			@Override
			public Object apply(PhysicalColumn input) {
				return input.getPhysicalValue(managedInstance);
			}
		});

		final SingleSelectHandler<X> rsHandler = new SingleSelectHandler<X>(session, this.type, this.columnAliases, this.tableAliases,
			this.primaryTableAliases, this.secondarytableAliases, this.entityPaths);

		return new QueryRunner().query(session.getConnection(), selectSql, rsHandler, params.toArray());
	}
}
