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

import java.util.Collection;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.lang.StringUtils;
import org.batoo.jpa.core.impl.mapping.Association;
import org.batoo.jpa.core.impl.mapping.Mapping;
import org.batoo.jpa.core.impl.mapping.OwnedAssociation;
import org.batoo.jpa.core.impl.mapping.OwnerAssociation;
import org.batoo.jpa.core.impl.mapping.PersistableAssociation;
import org.batoo.jpa.core.impl.types.EntityTypeImpl;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Helper class for entity selects.
 * <p>
 * Primary functionality is:
 * <ul>
 * <li>Generating a select statement that spans all the tables
 * <li>Expanding the select statement to the cascaded associations
 * <li>Expanding the select statement to the cascaded element collections.
 * <li>Executing the select
 * <li>Populating the passed managed instance with the values returned.
 * </ul>
 * 
 * @author hceylan
 * @since $version
 */
public abstract class BaseSelectHelper<X> {
	private static final int MAX_PATH = 10;

	protected final EntityTypeImpl<X> type;
	private final Mapping<X, ?> alwaysLazyMapping;
	protected final QueryRunner runner;

	protected final Map<Integer, Map<PhysicalColumn, String>> columnAliases = Maps.newHashMap();
	protected final List<Deque<Association<?, ?>>> entityPaths = Lists.newArrayList();
	protected final List<Deque<Association<?, ?>>> lazyPaths = Lists.newArrayList();
	protected final List<Deque<Association<?, ?>>> inversePaths = Lists.newArrayList();
	protected final List<PhysicalColumn> predicates = Lists.newArrayList();

	private String selectSql;

	/**
	 * @param type
	 *            the type to select against
	 * @param alwaysLazyMapping
	 *            the mapping which should be treated as always lazy, maybe be null
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public BaseSelectHelper(EntityTypeImpl<X> type, Mapping<X, ?> alwaysLazyMapping) {
		super();

		this.type = type;
		this.alwaysLazyMapping = alwaysLazyMapping;

		this.runner = new QueryRunner();
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
	 * @param secondaryTableNo
	 *            the secondary table no or -1
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private void addFields(List<String> fieldsBuffer, EntityTable table, final int tableNo, final int secondaryTableNo) {
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

		final String tableAlias = secondaryTableNo >= 0 ? "S" + tableNo + "_" + secondaryTableNo : "T" + tableNo;

		final Collection<String> fields = Collections2.transform(filteredColumns, new Function<PhysicalColumn, String>() {

			int fieldNo = 0;

			@Override
			public String apply(PhysicalColumn input) {
				final String fieldAlias = tableAlias + "_F" + this.fieldNo++;

				// save the mapping
				Map<PhysicalColumn, String> aliasMap = BaseSelectHelper.this.columnAliases.get(tableNo);
				if (aliasMap == null) {
					BaseSelectHelper.this.columnAliases.put(tableNo, aliasMap = Maps.newHashMap());
				}
				aliasMap.put(input, fieldAlias);

				// form the complete field
				// TX.[FieldNo] AS TX_FY
				return tableAlias + "." + input.getPhysicalName() + " AS " + fieldAlias;
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

		final Collection<PhysicalColumn> foreignKeys = ownerAssociation.getPhysicalColumns();

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
	 * @param secondaryTableNo
	 */
	private void addSecondaryJoin(List<String> joinsBuffer, final int parentTableNo, final int tableNo, final int secondaryTableNo,
		EntityTable table) {
		final String left = "T" + parentTableNo;
		final String right = "S" + tableNo + "_" + secondaryTableNo;

		final Collection<String> restrictions = Collections2.transform(table.getPrimaryKeys(), new Function<PhysicalColumn, String>() {

			@Override
			public String apply(PhysicalColumn input) {
				return left + "." + input.getPhysicalName() + " = " + right + "." + input.getReferencedColumn().getPhysicalName();
			}
		});

		// LEFT OUTER JOIN TABLE as Ty ON TX_F1 = TY_F1 [AND TX_F2 = TY_F2 [...]]
		joinsBuffer.add("\tLEFT OUTER JOIN " + table.getQualifiedName() + " AS " + right + " ON " + Joiner.on(" AND ").join(restrictions));
	}

	protected abstract boolean cascades(Association<?, ?> association);

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
		final String fields = Joiner.on(",\n").join(fieldsBuffer);
		final String join = Joiner.on("\n").join(joinsBuffer);
		final String primaryTableName = primaryTable.getQualifiedName();

		// compose and return the final query
		return "SELECT \n"//
			+ fields //
			+ "\nFROM " + primaryTableName + " AS T0"//
			+ (StringUtils.isNotBlank(join) ? "\n" + join : "")//
			+ "\nWHERE " + this.getWhere();
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

		final String alias = tableNo < 0 ? "TJ" + Math.abs(tableNo) : "T" + tableNo;

		// LEFT OUTER JOIN TABLE as Ty ON TX_F1 = TY_F1 [AND TX_F2 = TY_F2 [...]]
		joinsBuffer.add("\tLEFT OUTER JOIN " + table.getQualifiedName() + " AS " + alias + " ON " + Joiner.on(" AND ").join(restrictions));
	}

	/**
	 * Generates and returns the select SQL.
	 * The select SQL must
	 * <ul>
	 * <li>Span all the tables
	 * <li>Spans to the cascaded associations
	 * <li>Spans to the cascaded element collections.
	 * 
	 * @return the generated select SQL
	 * 
	 * @since $version
	 * @author hceylan
	 */
	protected String getSelectSql() {
		if (this.selectSql == null) {
			this.getSelectSQL0();
		}

		return this.selectSql;
	}

	private synchronized void getSelectSQL0() {
		if (this.selectSql == null) {
			final List<String> fieldsBuffer = Lists.newArrayList();
			final List<String> joinsBuffer = Lists.newArrayList();

			// initialize the table number
			final int tableNo = 0;

			this.preparePredicates();

			this.processType(fieldsBuffer, joinsBuffer, null, this.type, Lists.<Association<?, ?>> newLinkedList(), tableNo, tableNo);

			// compose and return the final query
			this.selectSql = this.compose(fieldsBuffer, joinsBuffer, this.type.getPrimaryTable());
		}
	}

	/**
	 * Subclasses must implement to return the where part of the select statement.
	 * 
	 * @return the where part of the select statement.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	protected abstract String getWhere();

	/**
	 * Subclasses must implement to initialize the predicates.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	protected abstract void preparePredicates();

	private void processType(final List<String> fieldsBuffer, final List<String> joinsBuffer, EntityTypeImpl<?> parentType,
		EntityTypeImpl<?> type, Deque<Association<?, ?>> path, int parentTableNo, int tableNo) {

		// handle the primary table
		final EntityTable primaryTable = type.getPrimaryTable();
		this.addFields(fieldsBuffer, primaryTable, tableNo, -1);

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

		int secondaryTableNo = 0;
		// handle the secondary tables;
		for (final EntityTable table : type.getTables().values()) {
			if (table.isPrimary()) { // increment the tableNo index
				continue;
			}

			this.addFields(fieldsBuffer, table, tableNo, secondaryTableNo);
			this.addSecondaryJoin(joinsBuffer, parentTableNo, tableNo, secondaryTableNo++, table);
		}

		// process the associations
		for (final Association<?, ?> child : type.getAssociations()) {
			// handle the path
			final LinkedList<Association<?, ?>> childpath = Lists.newLinkedList(path);
			childpath.addLast(child);

			this.entityPaths.add(childpath);

			if ((path.size() > 0) && path.getLast().equals(child.getOpposite())) {
				this.inversePaths.add(childpath);
			}
			else if ((path.size() >= MAX_PATH) || !this.cascades(child)
				|| ((parentType == null) && (this.alwaysLazyMapping != null) && (child.getOpposite() == this.alwaysLazyMapping))) {
				this.lazyPaths.add(childpath);
			}
			else {
				this.processType(fieldsBuffer, joinsBuffer, child.getOwner(), child.getType(), childpath, parentTableNo, ++tableNo);
			}
		}
	}
}
