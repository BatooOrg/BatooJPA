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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.mutable.MutableInt;
import org.batoo.jpa.core.impl.jdbc.AbstractTable.TableType;
import org.batoo.jpa.core.impl.jdbc.QueryItem.QueryItemType;
import org.batoo.jpa.core.impl.mapping.Association;
import org.batoo.jpa.core.impl.mapping.Mapping;
import org.batoo.jpa.core.impl.mapping.OwnedAssociation;
import org.batoo.jpa.core.impl.mapping.OwnerAssociation;
import org.batoo.jpa.core.impl.mapping.OwnerOneToManyMapping;
import org.batoo.jpa.core.impl.mapping.PersistableAssociation;
import org.batoo.jpa.core.impl.metamodel.AttributeImpl;
import org.batoo.jpa.core.impl.metamodel.EntityTypeImpl;

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

	protected HashMap<PhysicalColumn, String>[] columnAliases;
	protected final List<PhysicalColumn> predicates = Lists.newArrayList();
	protected final List<PhysicalColumn> parameters = Lists.newArrayList();

	private String selectSql;
	protected QueryItem root;

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
	 * @param columnAliases
	 *            the list that holds the column aliases
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
	 * @param type
	 */
	private void addFields(final List<Map<PhysicalColumn, String>> columnAliases, List<String> fieldsBuffer, final EntityTypeImpl<?> type,
		EntityTable table, final int tableNo, final int secondaryTableNo) {
		// Filter out the secondary table columns
		Collection<PhysicalColumn> filteredColumns;

		// first filter out the secondary table id fields.
		if (table.getTableType() == TableType.PRIMARY) {
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

		// second, filter out unrelated columns
		filteredColumns = Collections2.filter(filteredColumns, new Predicate<PhysicalColumn>() {

			@Override
			public boolean apply(PhysicalColumn input) {
				// let discriminator and id columns go
				if (input.isDiscriminator() || input.isId()) {
					return true;
				}

				final AttributeImpl<?, ?> root = input.getMapping().getPath().getFirst();
				final Class<?> related = root.getDeclaringType().getJavaType();
				final Class<?> javaType = type.getJavaType();

				return javaType.isAssignableFrom(related) || related.isAssignableFrom(javaType);
			}
		});

		final String tableAlias = secondaryTableNo >= 0 ? "S" + tableNo + "_" + secondaryTableNo : "T" + tableNo;

		final Collection<String> fields = Collections2.transform(filteredColumns, new Function<PhysicalColumn, String>() {

			int fieldNo = 0;

			@Override
			public String apply(PhysicalColumn input) {
				final String fieldAlias = tableAlias + "_F" + this.fieldNo++;

				// save the mapping
				Map<PhysicalColumn, String> aliasMap;
				if (columnAliases.size() == tableNo) {
					columnAliases.add(aliasMap = Maps.newHashMap());
				}
				else {
					aliasMap = columnAliases.get(tableNo);
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

		final List<PhysicalColumn> foreignKeys = Lists.newArrayList();
		for (final PhysicalColumn column : ownerAssociation.getPhysicalColumns()) {
			foreignKeys.add(column);
		}

		final int left = (association instanceof OwnedAssociation ? tableNo : parentTableNo);
		final int right = (association instanceof OwnedAssociation ? parentTableNo : tableNo);

		this.composeJoin(joinsBuffer, tableNo, association.getType().getRoot().getPrimaryTable(), foreignKeys, left, right);
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
		final EntityTable entityTable = inverse ? association.getOwner().getRoot().getPrimaryTable()
			: association.getType().getRoot().getPrimaryTable();

		// add join to entity table
		this.composeJoin(joinsBuffer, tableNo, entityTable, secondKeys, -tableNo, tableNo);
	}

	/**
	 * Adds a join buffer for the root join table of the {@link AssociationSelectHelper}
	 * 
	 * @param joinsBuffer
	 *            the joins buffer
	 * @param association
	 *            the root association
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private void addPrimaryJoin3(List<String> joinsBuffer, OwnerOneToManyMapping<?, ?, ?> association) {
		final JoinTable joinTable = association.getJoinTable();

		final String bindings = this.prepareJoinBindings(joinTable.getDestinationKeys(), "AJ", "T0");

		joinsBuffer.add("\tLEFT OUTER JOIN " + joinTable.getQualifiedName() + " AS AJ ON " + bindings);
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

		final String bindings = this.prepareJoinBindings(foreignKeys, leftAlias, rightAlias);

		final String alias = tableNo < 0 ? "TJ" + Math.abs(tableNo) : "T" + tableNo;

		// LEFT OUTER JOIN TABLE as Ty ON TX_F1 = TY_F1 [AND TX_F2 = TY_F2 [...]]
		joinsBuffer.add("\tLEFT OUTER JOIN " + table.getQualifiedName() + " AS " + alias + " ON " + bindings);
	}

	/**
	 * Returns the predicates as string.
	 * <p>
	 * Useful in toString() implementations of the extending classes.
	 * 
	 * @return the predicates as string
	 * 
	 * @since $version
	 * @author hceylan
	 */
	protected String getPredicatesAsString() {
		final String predicates = Joiner.on(", ").join(Lists.transform(this.predicates, new Function<PhysicalColumn, String>() {

			@Override
			public String apply(PhysicalColumn input) {
				return input.getTable().getName() + "." + input.getName();
			}
		}));
		return "[" + predicates + "]";
	}

	/**
	 * Returns the root association. Useful for {@link AssociationSelectHelper} types to provide the entry point.
	 * 
	 * @return the root association or null
	 * 
	 * @since $version
	 * @author hceylan
	 */
	protected abstract OwnerOneToManyMapping<?, ?, ?> getRootAssociation();

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

	@SuppressWarnings("unchecked")
	private synchronized void getSelectSQL0() {
		if (this.selectSql == null) {
			final List<String> joinsBuffer = Lists.newArrayList();
			final List<String> fieldsBuffer = Lists.newArrayList();
			final List<Map<PhysicalColumn, String>> columnAliases = Lists.newArrayList();

			this.preparePredicates();

			this.root = this.processType(columnAliases, fieldsBuffer, joinsBuffer, null, this.getRootAssociation(), this.type,
				new LinkedList<Association<?, ?>>(), 0, new MutableInt());

			// compose and return the final query
			this.selectSql = this.compose(fieldsBuffer, joinsBuffer, this.type.getRoot().getPrimaryTable());
			this.columnAliases = columnAliases.toArray(new HashMap[columnAliases.size()]);
		}
	}

	/**
	 * Returns the where part of the select statement.
	 * 
	 * @return the where part of the select statement.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	protected String getWhere() {
		// Generate the where statement
		// T1_F1 = ? [AND T1_F2 = ? [...]]
		return Joiner.on(" AND ").join(//
			Collections2.transform(this.predicates, new Function<PhysicalColumn, String>() {

				@Override
				public String apply(PhysicalColumn input) {
					return BaseSelectHelper.this.preparePredicate(input);
				}
			}));
	}

	private String prepareJoinBindings(final Collection<PhysicalColumn> foreignKeys, final String leftAlias, final String rightAlias) {
		final Collection<String> restrictions = Collections2.transform(foreignKeys, new Function<PhysicalColumn, String>() {

			@Override
			public String apply(PhysicalColumn input) {
				return leftAlias + "." + input.getPhysicalName() + " = " + rightAlias + "." + input.getReferencedColumn().getPhysicalName();
			}
		});

		final String bindings = Joiner.on(" AND ").join(restrictions);
		return bindings;
	}

	/**
	 * Subclasses must implement to generate predicate.
	 * 
	 * @param column
	 *            the column to generate predicate for
	 * @return the predicate
	 * 
	 * @since $version
	 * @author hceylan
	 */
	protected abstract String preparePredicate(PhysicalColumn column);

	/**
	 * Subclasses must implement to initialize the predicates.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	protected abstract void preparePredicates();

	private QueryItem processType(List<Map<PhysicalColumn, String>> columnAliases, final List<String> fieldsBuffer,
		final List<String> joinsBuffer, Association<?, ?> association, OwnerOneToManyMapping<?, ?, ?> rootAssociation,
		EntityTypeImpl<?> type, LinkedList<Association<?, ?>> path, int parentTableNo, MutableInt tableNo) {

		// handle the primary table
		final int thisTableNo = tableNo.intValue();
		final EntityTable primaryTable = type.getRoot().getPrimaryTable();

		this.addFields(columnAliases, fieldsBuffer, type, primaryTable, thisTableNo, -1);

		if (rootAssociation != null) {
			this.addPrimaryJoin3(joinsBuffer, rootAssociation);
		}

		if (association != null) { // we are not the root entity
			if ((association instanceof PersistableAssociation) && ((PersistableAssociation<?, ?>) association).hasJoin()) {
				this.addPrimaryJoin2(joinsBuffer, parentTableNo, thisTableNo, (PersistableAssociation<?, ?>) association, false);
			}
			else if ((association.getOpposite() != null) && (association.getOpposite() instanceof PersistableAssociation)
				&& ((PersistableAssociation<?, ?>) association.getOpposite()).hasJoin()) {
				this.addPrimaryJoin2(joinsBuffer, parentTableNo, thisTableNo, (PersistableAssociation<?, ?>) association.getOpposite(),
					true);
			}
			else {
				this.addPrimaryJoin(joinsBuffer, parentTableNo, thisTableNo, association);
			}
		}

		parentTableNo = thisTableNo;

		// handle the secondary and default tables;
		int secondaryTableNo = 0;
		for (final EntityTable table : type.getAllTables()) {
			if (table.getTableType() == TableType.PRIMARY) { // increment the tableNo index
				continue;
			}

			this.addFields(columnAliases, fieldsBuffer, type, table, thisTableNo, secondaryTableNo);
			this.addSecondaryJoin(joinsBuffer, parentTableNo, thisTableNo, secondaryTableNo++, table);
		}

		final List<QueryItem> children = Lists.newArrayList();

		// process the associations
		for (final Association<?, ?> childAssociation : type.getAssociations()) {
			if ((path.size() > 0) && path.getLast().equals(childAssociation.getOpposite())) {
				// that is a known join so reuse the inverse
				children.add(new QueryItem(childAssociation, QueryItemType.INVERSE));
			}
			else if ((path.size() >= MAX_PATH) || !this.cascades(childAssociation)) {
				// we are over the max path limit or the child association is not cascaded
				children.add(new QueryItem(childAssociation, QueryItemType.LAZY));
			}
			else if ((this.alwaysLazyMapping != null) && (childAssociation.getOpposite() == this.alwaysLazyMapping)) {
				// this association is marked as always lazy. This is particularyly useful in lazy association fetching
				children.add(new QueryItem(childAssociation, QueryItemType.LAZY));
			}
			else {
				// increment the table no
				tableNo.increment();

				// append the association to the path
				final LinkedList<Association<?, ?>> childPath = Lists.newLinkedList(path);
				childPath.addLast(childAssociation);

				// process the child type
				children.add(this.processType(columnAliases, fieldsBuffer, joinsBuffer, childAssociation, null, childAssociation.getType(),
					childPath, parentTableNo, tableNo));
			}
		}

		if (association != null) {
			return new QueryItem(association, thisTableNo, children.toArray(new QueryItem[children.size()]));
		}

		return new QueryItem(this.type, children.toArray(new QueryItem[children.size()]));
	}

}
