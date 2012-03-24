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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.lang.StringUtils;
import org.batoo.jpa.core.impl.SessionImpl;
import org.batoo.jpa.core.impl.instance.ManagedId;
import org.batoo.jpa.core.impl.instance.ManagedInstance;
import org.batoo.jpa.core.impl.mapping.Association;
import org.batoo.jpa.core.impl.mapping.OwnedAssociation;
import org.batoo.jpa.core.impl.mapping.OwnerAssociation;
import org.batoo.jpa.core.impl.mapping.PersistableAssociation;
import org.batoo.jpa.core.impl.types.EntityTypeImpl;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
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
	private void addFields(List<String> fieldsBuffer, EntityTable table, final int tableNo) {
		// Filter out the secondary table columns
		final Collection<PhysicalColumn> filteredColumns;

		if (table.isPrimary()) {
			filteredColumns = table.getColumns();
		}
		else {
			filteredColumns = Collections2.filter(table.getColumns(), new Predicate<PhysicalColumn>() {

				@Override
				public boolean apply(PhysicalColumn input) {
					return !input.isId();
				}
			});
		}

		final Collection<String> fields = Collections2.transform(filteredColumns, new Function<PhysicalColumn, String>() {

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

		final int left = (association instanceof OwnedAssociation ? tableNo : parentTableNo);
		final int right = (association instanceof OwnedAssociation ? parentTableNo : tableNo);

		this.composeJoin(joinsBuffer, tableNo, association.getType().getPrimaryTable(), foreignKeys, left, right);
	}

	/**
	 * Adds a join for the association with a join table.
	 * 
	 * @param joinsBuffer
	 *            the joins buffer
	 * @param parentTableNo
	 *            the no of the parent table
	 * @param tableNo
	 *            the no of the table
	 * @param association
	 *            the association with join
	 * @param if the association is inverse
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private void addPrimaryJoin2(List<String> joinsBuffer, int parentTableNo, int tableNo, PersistableAssociation<?, ?> association,
		boolean inverse) {
		final JoinTable joinTable = association.getJoinTable();

		final List<PhysicalColumn> firstKeys = inverse ? joinTable.getDestinationKeys() : joinTable.getSourceKeys();
		final List<PhysicalColumn> secondKeys = inverse ? joinTable.getSourceKeys() : joinTable.getDestinationKeys();

		// add join to joint table
		this.composeJoin(joinsBuffer, -tableNo, joinTable, firstKeys, -tableNo, parentTableNo);
		final EntityTable entityTable = inverse ? association.getOwner().getPrimaryTable() : association.getType().getPrimaryTable();

		// add join to entity table
		this.composeJoin(joinsBuffer, tableNo, entityTable, secondKeys, -tableNo, tableNo);
	}

	/**
	 * Adds a join for secondary table
	 * 
	 * @param joinsBuffer
	 *            the joins buffer
	 * @param parentTableNo
	 *            the no of the parent table
	 * @param tableNo
	 *            the no of the table
	 * @param table
	 *            the secondary table
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private void addSecondaryJoin(List<String> joinsBuffer, final int parentTableNo, final int tableNo, EntityTable table) {
		final Collection<String> restrictions = Collections2.transform(table.getPrimaryKeys(), new Function<PhysicalColumn, String>() {

			@Override
			public String apply(PhysicalColumn input) {
				final String left = "T" + parentTableNo;
				final String right = "T" + tableNo;

				return left + "." + input.getPhysicalName() + " = " + right + "." + input.getReferencedColumn().getPhysicalName();
			}
		});

		this.composeJoin(joinsBuffer, table, tableNo, restrictions);
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
	private String compose(final List<String> fieldsBuffer, final List<String> joinsBuffer, final EntityTable primaryTable) {
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
		final String primaryTableName = primaryTable.getQualifiedName();

		// compose and return the final query
		return "SELECT \n"//
			+ fields //
			+ "\nFROM " + primaryTableName + " AS T0"//
			+ (StringUtils.isNotBlank(join) ? "\n" + join : "")//
			+ "\nWHERE " + where;
	}

	private void composeJoin(List<String> joinsBuffer, AbstractTable table, int tableNo, final Collection<String> restrictions) {
		final String alias = tableNo < 0 ? "TJ" + Math.abs(tableNo) : "T" + tableNo;

		// LEFT OUTER JOIN TABLE as Ty ON TX_F1 = TY_F1 [AND TX_F2 = TY_F2 [...]]
		joinsBuffer.add("\tLEFT OUTER JOIN " + table.getQualifiedName() + " AS " + alias + " ON " + Joiner.on(" AND ").join(restrictions));
	}

	private void composeJoin(List<String> joinsBuffer, final int tableNo, AbstractTable table,
		final Collection<PhysicalColumn> foreignKeys, final int left, final int right) {
		final String leftAlias = left < 0 ? "TJ" + Math.abs(left) : "T" + left;
		final String rightAlias = right < 0 ? "TJ" + Math.abs(right) : "T" + right;

		final Collection<String> restrictions = Collections2.transform(foreignKeys, new Function<PhysicalColumn, String>() {

			@Override
			public String apply(PhysicalColumn input) {
				return leftAlias + "." + input.getPhysicalName() + " = " + rightAlias + "." + input.getReferencedColumn().getPhysicalName();
			}
		});

		this.composeJoin(joinsBuffer, table, tableNo, restrictions);
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

			this.processType(new HashSet<EntityTypeImpl<?>>(), fieldsBuffer, joinsBuffer, null, this.type,
				Lists.<Association<?, ?>> newLinkedList(), tableNo, tableNo);

			// compose and return the final query
			this.selectSql = this.compose(fieldsBuffer, joinsBuffer, this.type.getPrimaryTable());
		}

		return this.selectSql;
	}

	private void processType(Set<EntityTypeImpl<?>> processed, final List<String> fieldsBuffer, final List<String> joinsBuffer,
		EntityTypeImpl<?> parentType, EntityTypeImpl<?> type, Deque<Association<?, ?>> path, int parentTableNo, int tableNo) {

		processed.add(type);

		// handle the primary table
		final EntityTable primaryTable = type.getPrimaryTable();
		this.addFields(fieldsBuffer, primaryTable, tableNo);

		if (parentType != null) { // we are not the root entity
			final Association<?, ?> association = path.getLast();
			if ((association instanceof PersistableAssociation) && ((PersistableAssociation<?, ?>) association).hasJoin()) {
				this.addPrimaryJoin2(joinsBuffer, parentTableNo, tableNo, (PersistableAssociation<?, ?>) association, false);
			}
			else if ((association.getOpposite() != null) && (association.getOpposite() instanceof PersistableAssociation)
				&& ((PersistableAssociation<?, ?>) association.getOpposite()).hasJoin()) {
				this.addPrimaryJoin2(joinsBuffer, parentTableNo, tableNo, (PersistableAssociation<?, ?>) association.getOpposite(), true);
			}
			else {
				this.addPrimaryJoin(joinsBuffer, parentTableNo, tableNo, association);
			}
		}

		parentTableNo = tableNo;

		// handle the secondary tables;
		for (final EntityTable table : type.getTables().values()) {
			if (table.isPrimary()) { // increment the tableNo index
				continue;
			}

			tableNo++;

			this.addFields(fieldsBuffer, table, tableNo);
			this.addSecondaryJoin(joinsBuffer, parentTableNo, tableNo, table);
		}

		// process the associations
		for (final Association<?, ?> child : type.getAssociations()) {
			if (child.isEager()) {
				// if we already went down that road avoid circularity
				if (path.contains(child.getOpposite())) {
					continue;
				}

				if (processed.contains(child.getType())) {
					continue;
				}

				// handle the path
				path = Lists.newLinkedList(path);
				path.addLast(child);
				this.entityPaths.add(path);

				this.processType(processed, fieldsBuffer, joinsBuffer, child.getOwner(), child.getType(), path, parentTableNo, ++tableNo);
			}
		}
	}

	/**
	 * Performs the select and populates the managed instance.
	 * 
	 * @param session
	 *            the session
	 * @param managedId
	 *            the managed instance
	 * @throws SQLException
	 * 
	 * @since $version
	 * @author hceylan
	 * @return
	 */
	public ManagedInstance<X> select(SessionImpl session, final ManagedId<X> managedId) throws SQLException {
		// Do not inline, generation of the select SQL will initialize the predicates!
		final String selectSql = this.getSelectSql();

		final Collection<Object> params = Collections2.transform(this.predicates.values(), new Function<PhysicalColumn, Object>() {
			@Override
			public Object apply(PhysicalColumn input) {
				return input.getPhysicalValue(managedId.getSession(), managedId.getInstance());
			}
		});

		final SingleSelectHandler<X> rsHandler = new SingleSelectHandler<X>(session, this.type, this.columnAliases, this.entityPaths);

		return new QueryRunner().query(session.getConnection(), selectSql, rsHandler, params.toArray());
	}
}
