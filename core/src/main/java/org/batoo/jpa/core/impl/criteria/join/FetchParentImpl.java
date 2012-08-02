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
package org.batoo.jpa.core.impl.criteria.join;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.persistence.criteria.Fetch;
import javax.persistence.criteria.FetchParent;
import javax.persistence.criteria.JoinType;
import javax.persistence.metamodel.Attribute.PersistentAttributeType;
import javax.persistence.metamodel.PluralAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.Type.PersistenceType;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.mutable.MutableBoolean;
import org.batoo.jpa.core.impl.collections.ManagedCollection;
import org.batoo.jpa.core.impl.criteria.AbstractCriteriaQueryImpl;
import org.batoo.jpa.core.impl.criteria.BaseQueryImpl;
import org.batoo.jpa.core.impl.criteria.EntryImpl;
import org.batoo.jpa.core.impl.criteria.join.MapJoinImpl.MapSelectType;
import org.batoo.jpa.core.impl.instance.EnhancedInstance;
import org.batoo.jpa.core.impl.instance.ManagedId;
import org.batoo.jpa.core.impl.instance.ManagedInstance;
import org.batoo.jpa.core.impl.jdbc.AbstractColumn;
import org.batoo.jpa.core.impl.jdbc.AbstractTable;
import org.batoo.jpa.core.impl.jdbc.BasicColumn;
import org.batoo.jpa.core.impl.jdbc.CollectionTable;
import org.batoo.jpa.core.impl.jdbc.DiscriminatorColumn;
import org.batoo.jpa.core.impl.jdbc.EntityTable;
import org.batoo.jpa.core.impl.jdbc.JoinColumn;
import org.batoo.jpa.core.impl.jdbc.PkColumn;
import org.batoo.jpa.core.impl.jdbc.SecondaryTable;
import org.batoo.jpa.core.impl.manager.SessionImpl;
import org.batoo.jpa.core.impl.model.attribute.BasicAttribute;
import org.batoo.jpa.core.impl.model.mapping.AssociationMapping;
import org.batoo.jpa.core.impl.model.mapping.BasicMapping;
import org.batoo.jpa.core.impl.model.mapping.ElementCollectionMapping;
import org.batoo.jpa.core.impl.model.mapping.EmbeddedMapping;
import org.batoo.jpa.core.impl.model.mapping.JoinedMapping;
import org.batoo.jpa.core.impl.model.mapping.JoinedMapping.MappingType;
import org.batoo.jpa.core.impl.model.mapping.Mapping;
import org.batoo.jpa.core.impl.model.mapping.SingularAssociationMapping;
import org.batoo.jpa.core.impl.model.mapping.SingularMapping;
import org.batoo.jpa.core.impl.model.type.EmbeddableTypeImpl;
import org.batoo.jpa.core.impl.model.type.EntityTypeImpl;
import org.batoo.jpa.core.impl.model.type.TypeImpl;
import org.batoo.jpa.core.util.Pair;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * Implementation of {@link FetchParent}.
 * 
 * @param <Z>
 *            the source type
 * @param <X>
 *            the target type
 * 
 * @author hceylan
 * @since $version
 */
public class FetchParentImpl<Z, X> implements FetchParent<Z, X>, Joinable {

	private final EntityTypeImpl<X> entity;
	private final TypeImpl<X> type;
	private JoinedMapping<? super Z, ?, X> mapping;

	private final HashMap<Mapping<?, ?, ?>, FetchImpl<X, ?>> fetches = Maps.newHashMap();
	private final ArrayList<FetchImpl<X, ?>> joins = Lists.newArrayList();

	private String alias;
	private String primaryTableAlias;
	private String discriminatorAlias;

	private final HashMap<SecondaryTable, String> tableAliases = Maps.newHashMap();
	private final HashMap<AbstractColumn, String> idFields = Maps.newHashMap();
	private final HashMap<AbstractColumn, String> joinFields = Maps.newHashMap();
	private final List<SingularAssociationMapping<?, ?>> singularJoins = Lists.newArrayList();

	private int nextTableAlias = 1;
	private AbstractColumn[] columns;
	private String[] fields;
	private String keyColumnAlias;

	/**
	 * @param entity
	 *            the entity
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public FetchParentImpl(EntityTypeImpl<X> entity) {
		super();

		this.entity = entity;
		this.type = null;
	}

	/**
	 * @param mapping
	 *            the mapping
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public FetchParentImpl(JoinedMapping<? super Z, ?, X> mapping) {
		super();

		this.mapping = mapping;

		if (mapping.getType() instanceof EntityTypeImpl) {
			this.entity = (EntityTypeImpl<X>) mapping.getType();
			this.type = null;
		}
		else {
			this.type = mapping.getType();
			this.entity = null;
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public final <Y> FetchImpl<X, Y> fetch(PluralAttribute<? super X, ?, Y> attribute) {
		return this.fetch(attribute, JoinType.LEFT);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public final <Y> FetchImpl<X, Y> fetch(PluralAttribute<? super X, ?, Y> attribute, JoinType jt) {
		return this.fetch(attribute.getName(), jt);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public final <Y> FetchImpl<X, Y> fetch(SingularAttribute<? super X, Y> attribute) {
		return this.fetch(attribute, JoinType.LEFT);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public final <Y> FetchImpl<X, Y> fetch(SingularAttribute<? super X, Y> attribute, JoinType jt) {
		return this.fetch(attribute.getName(), jt);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public final <Y> FetchImpl<X, Y> fetch(String attributeName) {
		return (FetchImpl<X, Y>) this.fetch(attributeName, JoinType.LEFT);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <Y> FetchImpl<X, Y> fetch(String attributeName, JoinType jt) {
		final Mapping<?, ?, ?> mapping = this.getMapping(attributeName);

		if (!(mapping instanceof JoinedMapping)) {
			throw new IllegalArgumentException("Cannot dereference attribute " + attributeName);
		}

		if (this.fetches.get(mapping) != null) {
			return (FetchImpl<X, Y>) this.fetches.get(mapping);
		}

		final FetchImpl<X, Y> fetch = new FetchImpl<X, Y>(this, (JoinedMapping<? super X, ?, Y>) mapping, jt);
		this.fetches.put(mapping, fetch);

		return fetch;
	}

	/**
	 * Returns the restriction based on discrimination.
	 * 
	 * @return the restriction based on discrimination, <code>null</code>
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public String generateDiscrimination() {
		if ((this.entity.getRootType().getInheritanceType() == null) || (this.entity == this.entity.getRootType())) {
			return null;
		}

		final Collection<String> discriminators = Collections2.transform(this.entity.getDiscriminators(), new Function<String, String>() {

			@Override
			public String apply(String input) {
				return "'" + input + "'";
			}
		});

		return this.primaryTableAlias + "." + this.entity.getRootType().getDiscriminatorColumn().getName() + " IN (" + Joiner.on(",").join(discriminators)
			+ ")";
	}

	/**
	 * Returns the description of the fetch.
	 * 
	 * @param parent
	 *            the parent
	 * @return the description of the fetch *
	 */
	public String generateJpqlFetches(final String parent) {
		final StringBuilder description = new StringBuilder();

		final Collection<String> fetches = Collections2.transform(this.fetches.values(), new Function<FetchImpl<X, ?>, String>() {

			@Override
			public String apply(FetchImpl<X, ?> input) {
				return input.generateJpqlFetches(parent);
			}
		});

		description.append(Joiner.on("\n").join(fetches));

		return description.toString();
	}

	/**
	 * Generates the self SQL joins fragments.
	 * 
	 * @param query
	 *            the query
	 * @param selfJoins
	 *            the list of self joins
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void generateSqlJoins(AbstractCriteriaQueryImpl<?> query, final List<String> selfJoins) {
		for (final Entry<SecondaryTable, String> e : this.tableAliases.entrySet()) {
			final String alias = e.getValue();
			final SecondaryTable table = e.getKey();

			selfJoins.add(table.joinPrimary(this.getPrimaryTableAlias(query), alias));
		}
	}

	/**
	 * Generates joins SQL fragment for the fetch chain.
	 * 
	 * @param query
	 *            the query
	 * @param joins
	 *            the map of joins
	 * @param recurse
	 *            recurse to fetch children
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void generateSqlJoins(AbstractCriteriaQueryImpl<?> query, Map<Joinable, String> joins, boolean recurse) {
		final List<String> selfJoins = Lists.newArrayList();

		this.generateSqlJoins(query, selfJoins);

		final String join = Joiner.on("\n").skipNulls().join(selfJoins);
		if ((join != null) && (join.trim().length() > 0)) {
			joins.put(this, join);
		}
		else {
			joins.put(this, null);
		}

		for (final FetchImpl<X, ?> child : this.joins) {
			child.generateSqlJoins(query, joins, false);
		}

		if (recurse) {
			for (final FetchImpl<X, ?> fetch : this.fetches.values()) {
				fetch.generateSqlJoins(query, joins, true);
			}
		}
	}

	/**
	 * Returns the generated SQL fragment.
	 * 
	 * @param query
	 *            the query
	 * @param selected
	 *            if the item is selected
	 * @param root
	 *            if the generation is at root
	 * @return the generated SQL fragment
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public String generateSqlSelect(AbstractCriteriaQueryImpl<?> query, boolean selected, boolean root) {
		return this.generateSqlSelect(query, selected, root, MapSelectType.VALUE);
	}

	/**
	 * Returns the generated SQL fragment.
	 * 
	 * @param query
	 *            the query
	 * @param selected
	 *            if the item is selected
	 * @param root
	 *            if the generation is at root
	 * @param selectType
	 *            the select type
	 * @return the generated SQL fragment
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public String generateSqlSelect(AbstractCriteriaQueryImpl<?> query, boolean selected, boolean root, MapSelectType selectType) {
		final List<String> selects = Lists.newArrayList();

		// skip the embeddable mappings
		if (!(this.getMapping() instanceof EmbeddedMapping)) {
			selects.add(this.generateSqlSelectImpl(query, selected, root, selectType));
		}

		for (final FetchImpl<X, ?> fetch : this.fetches.values()) {
			final String select = fetch.generateSqlSelect(query, selected, false);
			if (StringUtils.isNotBlank(select)) {
				selects.add(select);
			}
		}

		return Joiner.on(",\n").join(selects);
	}

	private void generateSqlSelectForElementCollection(AbstractCriteriaQueryImpl<?> query, boolean selected, List<String> selects,
		Map<AbstractColumn, String> fieldMap, MapSelectType selectType) {

		final List<String> fields1 = Lists.newArrayList();

		final CollectionTable table = (CollectionTable) this.mapping.getTable();
		final String tableAlias = this.getTableAlias(query, table);

		for (final AbstractColumn column : table.getColumns()) {
			// we are not insterested in join columns at all
			if (column instanceof JoinColumn) {
				continue;
			}

			// if we are selecting only keys and the there is a key column, we are only interested in key column
			if ((selectType == MapSelectType.KEY) && (table.getKeyColumn() != null) && (table.getKeyColumn() != column)) {
				continue;
			}

			// if we are selecting only values and the there is a key column, we are only interested in value columns
			if ((selectType == MapSelectType.VALUE) && (table.getKeyColumn() == column)) {
				continue;
			}

			final String fieldAlias = tableAlias + "_F" + query.getFieldAlias(tableAlias, column);
			final String field = Joiner.on(".").skipNulls().join(tableAlias, column.getName());

			if (selected) {
				fields1.add(field + " AS " + fieldAlias);
			}
			else {
				fields1.add(field);
			}

			// seperate out the key column from the rest
			if (column == table.getKeyColumn()) {
				this.keyColumnAlias = fieldAlias;
			}
			else {
				fieldMap.put(column, fieldAlias);
			}
		}

		selects.add(Joiner.on(", ").join(fields1));
	}

	private void generateSqlSelectForEntityTable(AbstractCriteriaQueryImpl<?> query, boolean selected, boolean root, final List<String> selects,
		final Map<AbstractColumn, String> fieldMap, final EntityTable table) {

		final List<String> fields1 = Lists.newArrayList();

		final String tableAlias = this.getTableAlias(query, table);

		for (final AbstractColumn column : table.getColumns()) {
			final String fieldAlias;
			final String field;

			if (column instanceof PkColumn) {
				fieldAlias = tableAlias + "_F" + query.getFieldAlias(tableAlias, column);
				field = Joiner.on(".").skipNulls().join(tableAlias, column.getName());

				this.idFields.put(column, fieldAlias);
			}
			else if (column instanceof DiscriminatorColumn) {
				fieldAlias = tableAlias + "_F" + query.getFieldAlias(tableAlias, column);
				field = Joiner.on(".").skipNulls().join(tableAlias, column.getName());

				this.discriminatorAlias = fieldAlias;
			}
			else if (column.getMapping() instanceof SingularAssociationMapping) {
				final SingularAssociationMapping<?, ?> mapping = (SingularAssociationMapping<?, ?>) column.getMapping();

				// if we are on a join on that path then ignore
				if (!root && this.ignoreJoin(mapping)) {
					continue;
				}

				fieldAlias = tableAlias + "_F" + query.getFieldAlias(tableAlias, column);
				field = Joiner.on(".").skipNulls().join(tableAlias, column.getName());

				this.singularJoins.add(mapping);
				this.joinFields.put(column, fieldAlias);
			}
			else {
				fieldAlias = tableAlias + "_F" + query.getFieldAlias(tableAlias, column);
				field = Joiner.on(".").skipNulls().join(tableAlias, column.getName());

				fieldMap.put(column, fieldAlias);
			}

			if (selected) {
				fields1.add(field + " AS " + fieldAlias);
			}
			else {
				fields1.add(field);
			}
		}

		selects.add(Joiner.on(", ").join(fields1));
	}

	private String generateSqlSelectImpl(AbstractCriteriaQueryImpl<?> query, boolean selected, boolean root, MapSelectType selectType) {
		final List<String> selects = Lists.newArrayList();
		final Map<AbstractColumn, String> fieldMap = Maps.newHashMap();

		if (this.entity != null) {
			for (final EntityTable table : this.entity.getAllTables()) {
				this.generateSqlSelectForEntityTable(query, selected, root, selects, fieldMap, table);
			}
		}
		else {
			this.generateSqlSelectForElementCollection(query, selected, selects, fieldMap, selectType);
		}

		this.columns = new AbstractColumn[fieldMap.size()];
		this.fields = new String[fieldMap.size()];

		int i = 0;
		for (final Entry<AbstractColumn, String> entry : fieldMap.entrySet()) {
			this.columns[i] = entry.getKey();
			this.fields[i] = entry.getValue();
			i++;
		}

		return Joiner.on(",\n\t").join(selects);
	}

	/**
	 * Returns the alias of the fetch.
	 * 
	 * @param query
	 *            the query
	 * @return the alias of the fetch
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private String getAlias(BaseQueryImpl<?> query) {
		if (this.alias == null) {
			this.alias = query.generateTableAlias(this.entity != null);
		}

		return this.alias;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public EntityTypeImpl<?> getEntity() {
		return this.entity;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public final Set<Fetch<X, ?>> getFetches() {
		final Set<Fetch<X, ?>> fetches = Sets.newHashSet();
		fetches.addAll(this.fetches.values());

		return fetches;
	}

	private Object getId(ResultSet row) throws SQLException {
		if (this.entity.hasSingleIdAttribute()) {
			final SingularMapping<? super X, ?> idMapping = this.entity.getIdMapping();
			if (idMapping instanceof BasicMapping) {
				final BasicColumn column = ((BasicMapping<? super X, ?>) idMapping).getColumn();
				final String field = this.idFields.get(column);

				return row.getObject(field);
			}

			final MutableBoolean allNull = new MutableBoolean(true);
			final Object id = this.populateEmbeddedId(row, (EmbeddedMapping<? super X, ?>) idMapping, null, allNull);

			return allNull.booleanValue() ? null : id;
		}

		final Object id = ((EmbeddableTypeImpl<?>) this.entity.getIdType()).newInstance();
		for (final Pair<BasicMapping<? super X, ?>, BasicAttribute<?, ?>> pair : this.entity.getIdMappings()) {
			final BasicColumn column = pair.getFirst().getColumn();
			final String field = this.idFields.get(column);

			pair.getSecond().set(id, row.getObject(field));
		}

		return id;
	}

	private Object getId(SingularAssociationMapping<?, ?> mapping, ResultSet row) throws SQLException {
		final EntityTypeImpl<?> entity = mapping.getType();
		if (entity.hasSingleIdAttribute()) {
			final SingularMapping<?, ?> idMapping = entity.getIdMapping();
			if (idMapping instanceof BasicMapping) {
				final JoinColumn joinColumn = mapping.getForeignKey().getJoinColumns().get(0);
				final String field = this.joinFields.get(joinColumn);
				return row.getObject(field);
			}

			final MutableBoolean allNull = new MutableBoolean(true);
			final Object id = this.populateEmbeddedId(row, (EmbeddedMapping<?, ?>) idMapping, null, allNull);

			return allNull.booleanValue() ? null : id;
		}

		final Object id = ((EmbeddableTypeImpl<?>) entity.getIdType()).newInstance();
		for (final Pair<BasicMapping<? super X, ?>, BasicAttribute<?, ?>> pair : this.entity.getIdMappings()) {
			final BasicColumn column = pair.getFirst().getColumn();

			for (final JoinColumn joinColumn : mapping.getForeignKey().getJoinColumns()) {
				if (joinColumn.getReferencedColumnName().equals(column.getName())) {
					final String field = this.joinFields.get(joinColumn);
					pair.getSecond().set(id, row.getObject(field));

					break;
				}
			}
		}

		return id;
	}

	/**
	 * Returns the managed instance based on the id.
	 * 
	 * @param session
	 *            the session
	 * @param row
	 *            the data row
	 * @return the managed instance or null if the id is null
	 * @throws SQLException
	 *             thrown in case of an underlying SQL Error
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@SuppressWarnings("unchecked")
	private <Y extends X> ManagedInstance<Y> getInstance(SessionImpl session, ResultSet row) throws SQLException {
		// get the id of for the instance
		final Object id = this.getId(row);

		if (id == null) {
			return null;
		}

		// look for it in the session
		final ManagedId<X> managedId = new ManagedId<X>(id, this.entity);
		ManagedInstance<Y> instance = session.get(managedId);

		// if found then return it
		if (instance != null) {
			// if it is a new instance simply return it
			if (!(instance.getInstance() instanceof EnhancedInstance)) {
				return instance;
			}

			final EnhancedInstance enhancedInstance = (EnhancedInstance) instance.getInstance();

			// if it is a lazy instance mark as loading and initialize
			if (!enhancedInstance.__enhanced__$$__isInitialized()) {
				this.initializeInstance(session, row, instance);

				session.lazyInstanceLoading(instance);
				enhancedInstance.__enhanced__$$__setInitialized();
			}

			return instance;
		}

		// if no inheritance then initialize and return
		if (this.entity.getInheritanceType() == null) {
			instance = (ManagedInstance<Y>) this.entity.getManagedInstanceById(session, managedId, false);
		}
		// inheritance is in place then locate the correct child type
		else {
			final Object discriminatorValue = row.getObject(this.discriminatorAlias);

			// check if we have a legal discriminator value
			final EntityTypeImpl<Y> effectiveType = (EntityTypeImpl<Y>) this.entity.getChildType(discriminatorValue);
			if (effectiveType == null) {
				throw new IllegalArgumentException("Discriminator " + discriminatorValue + " not found in the type " + this.entity.getName());
			}

			// initialize and return
			instance = effectiveType.getManagedInstanceById(session, (ManagedId<Y>) managedId, false);
		}

		this.initializeInstance(session, row, instance);
		session.put(instance);

		return instance;
	}

	/**
	 * Returns the associate either from the session or by creating a lazy instance.
	 * 
	 * @param session
	 *            the session
	 * @param mapping
	 *            the mapping
	 * @param row
	 *            the row data
	 * @return the instance
	 * @throws SQLException
	 *             thrown in case of an underlying SQL Error
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Object getInstance(SessionImpl session, SingularAssociationMapping<?, ?> mapping, ResultSet row) throws SQLException {
		final Object id = this.getId(mapping, row);
		if (id == null) {
			return null;
		}

		ManagedInstance instance = session.get(new ManagedId(id, mapping.getType()));
		if (instance != null) {
			return instance.getInstance();
		}

		final ManagedId managedId = new ManagedId(id, mapping.getType());
		instance = mapping.getType().getManagedInstanceById(session, managedId, true);
		session.put(instance);

		return instance.getInstance();
	}

	/**
	 * Returns the mapping of the fetch.
	 * 
	 * @return the mapping of the fetch or <code>null</code> if the fetch is root
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public JoinedMapping<? super Z, ?, X> getMapping() {
		return null;
	}

	@SuppressWarnings("unchecked")
	private Mapping<?, ?, ?> getMapping(String attributeName) {
		Mapping<?, ?, ?> mapping = null;

		if (this.entity instanceof EntityTypeImpl) {
			mapping = this.entity.getRootMapping().getMapping(attributeName);
		}
		else if (this.type instanceof EmbeddableTypeImpl) {
			if (this.getMapping() instanceof EmbeddedMapping) {
				mapping = ((EmbeddedMapping<? super Z, X>) this.getMapping()).getChild(attributeName);
			}
			else {
				mapping = ((ElementCollectionMapping<? super Z, ?, X>) this.getMapping()).getMapping(attributeName);
			}
		}
		return mapping;
	}

	/**
	 * Returns the alias of the primary table.
	 * 
	 * @param query
	 *            the query
	 * @return the alias of the primary table
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public String getPrimaryTableAlias(BaseQueryImpl<?> query) {
		if (this.primaryTableAlias == null) {
			this.primaryTableAlias = this.getAlias(query) + "_P";
		}

		return this.primaryTableAlias;
	}

	/**
	 * Returns the SQL restriction in pairs of table alias and column.
	 * 
	 * @param query
	 *            the query
	 * @param selectType
	 *            the select type
	 * @return the SQL restriction in pairs of table alias and column
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public String[] getSqlRestrictionFragments(BaseQueryImpl<?> query, MapSelectType selectType) {
		final List<String> restrictions = Lists.newArrayList();

		if (this.entity != null) {
			for (final PkColumn column : this.entity.getPrimaryTable().getPkColumns()) {
				restrictions.add(this.getPrimaryTableAlias(query) + "." + column.getName());
			}
		}
		else {}

		return restrictions.toArray(new String[restrictions.size()]);
	}

	/**
	 * Returns the alias for the table.
	 * <p>
	 * if table does not have an alias, it is generated.
	 * 
	 * @param query
	 *            the query
	 * @param table
	 *            the table
	 * @return the alias for the table
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Override
	public String getTableAlias(BaseQueryImpl<?> query, AbstractTable table) {
		if (table instanceof SecondaryTable) {
			String alias = this.tableAliases.get(table);
			if (alias == null) {
				alias = this.alias + "_S" + this.nextTableAlias++;
				this.tableAliases.put((SecondaryTable) table, alias);
			}

			return alias;
		}

		return this.getPrimaryTableAlias(query);
	}

	/**
	 * Handles the row.
	 * 
	 * @param session
	 *            the session
	 * @param row
	 *            the current row
	 * @return the instance
	 * @throws SQLException
	 *             thrown in case of an underlying SQL Error
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@SuppressWarnings("unchecked")
	public X handle(SessionImpl session, ResultSet row) throws SQLException {
		return (X) this.handle(session, row, MapSelectType.VALUE).getValue().getInstance();
	}

	/**
	 * Handles the row.
	 * 
	 * @param session
	 *            the session
	 * @param row
	 *            the current row
	 * @param selectType
	 *            the map select type
	 * @return the instance
	 * @throws SQLException
	 *             thrown in case of an underlying SQL Error
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public EntryImpl<Object, ManagedInstance<?>> handle(SessionImpl session, ResultSet row, MapSelectType selectType) throws SQLException {
		return this.handleFetch(session, row, selectType);
	}

	private void handleAssociationFetch(SessionImpl session, final ResultSet row, Object instance, final FetchImpl<X, ?> fetch) throws SQLException {
		final EntryImpl<Object, ManagedInstance<?>> pair = fetch.handleFetch(session, row, MapSelectType.ENTRY);
		// if null then continue
		if (pair == null) {
			return;
		}

		final ManagedInstance<?> child = pair.getValue();

		final AssociationMapping<? super X, ?, ?> mapping = (AssociationMapping<? super X, ?, ?>) fetch.getMapping();

		if (mapping.getMappingType() == MappingType.PLURAL_ASSOCIATION) {
			// if it is a plural association then we will test if we processed the child
			if (((ManagedCollection<?>) mapping.get(instance)).addChild(pair)) {
				// if it is a one-to-many mapping and has an inverse then set the inverses
				if ((mapping.getInverse() != null) && //
					(mapping.getAttribute().getPersistentAttributeType() == PersistentAttributeType.ONE_TO_MANY)) {
					mapping.getInverse().set(child.getInstance(), instance);
					child.setJoinLoaded(mapping.getInverse());
				}
			}
		}
		// if it is singular association then set the instance
		else {
			mapping.set(instance, child.getInstance());

			// if this is a one-to-one mapping and has an inverse then set it
			if ((mapping.getInverse() != null) && (mapping.getAttribute().getPersistentAttributeType() == PersistentAttributeType.ONE_TO_ONE)) {
				mapping.getInverse().set(child.getInstance(), instance);
				child.setJoinLoaded(mapping);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private X handleElement(ResultSet row) throws SQLException {
		if (this.type.getPersistenceType() == PersistenceType.BASIC) {
			return (X) row.getObject(this.fields[0]);
		}

		final X instance = ((EmbeddableTypeImpl<X>) this.type).newInstance();
		for (int i = 0; i < this.fields.length; i++) {
			this.columns[i].setValue(instance, row.getObject(this.fields[i]));
		}

		return instance;
	}

	private void handleElementCollectionFetch(SessionImpl session, ResultSet row, Object instance, FetchImpl<X, ?> fetch) throws SQLException {
		final EntryImpl<Object, ?> child = fetch.handleElementFetch(row, MapSelectType.ENTRY);

		// if null then continue
		if (child == null) {
			return;
		}

		final ElementCollectionMapping<? super X, ?, ?> mapping = (ElementCollectionMapping<? super X, ?, ?>) fetch.getMapping();

		// if it is a plural association then we will test if we processed the child
		((ManagedCollection<?>) mapping.get(instance)).addElement(child);
	}

	/**
	 * Handles the row
	 * 
	 * @param row
	 *            the row
	 * @return the collection element
	 * @throws SQLException
	 *             thrown in case of an underlying SQL Error
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public EntryImpl<Object, X> handleElementFetch(ResultSet row) throws SQLException {
		return this.handleElementFetch(row, MapSelectType.VALUE);
	}

	/**
	 * Handles the row
	 * 
	 * @param row
	 *            the row
	 * @param selectType
	 *            the select type
	 * @return the collection element
	 * @throws SQLException
	 *             thrown in case of an underlying SQL Error
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public EntryImpl<Object, X> handleElementFetch(ResultSet row, MapSelectType selectType) throws SQLException {
		if ((selectType == MapSelectType.VALUE) || !this.mapping.isMap()) {
			return new EntryImpl<Object, X>(null, this.handleElement(row));
		}

		if (this.keyColumnAlias != null) {
			if (selectType == MapSelectType.KEY) {
				return new EntryImpl<Object, X>(row.getObject(this.keyColumnAlias), null);
			}
			else {
				return new EntryImpl<Object, X>(row.getObject(this.keyColumnAlias), this.handleElement(row));
			}
		}

		final X value = this.handleElement(row);
		final Object key = ((ElementCollectionMapping<? super Z, ?, X>) this.mapping).extractKey(value);

		return new EntryImpl<Object, X>(key, value);
	}

	/**
	 * Handles the row.
	 * 
	 * @param session
	 *            the session
	 * @param row
	 *            the current row
	 * @param selectType
	 *            the map select type
	 * @return the instance
	 * @throws SQLException
	 *             thrown in case of an underlying SQL Error
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public EntryImpl<Object, ManagedInstance<?>> handleFetch(SessionImpl session, ResultSet row, MapSelectType selectType) throws SQLException {
		// if instance is null then break
		final ManagedInstance<? extends X> instance = this.getInstance(session, row);
		if (instance == null) {
			return null;
		}

		// if instance is refreshing then re-initialize instance
		if (instance.isRefreshing()) {
			this.initializeInstance(session, row, instance);
		}

		// if the instance is loading then continue processing
		if (instance.isLoading()) {
			this.handleFetches(session, row, instance.getInstance());
		}

		if ((selectType == MapSelectType.VALUE) || (this.mapping == null) || !this.mapping.isMap()) {
			return new EntryImpl<Object, ManagedInstance<?>>(null, instance);
		}

		if (this.keyColumnAlias != null) {
			final Object key = row.getObject(this.keyColumnAlias);
			return new EntryImpl<Object, ManagedInstance<?>>(key, instance);
		}

		final Object key = this.mapping.extractKey(instance.getInstance());
		return new EntryImpl<Object, ManagedInstance<?>>(key, instance);
	}

	void handleFetches(SessionImpl session, final ResultSet row, Object instance) throws SQLException {
		for (final FetchImpl<X, ?> fetch : this.fetches.values()) {
			final JoinedMapping<? super X, ?, ?> mapping = fetch.getMapping();

			final MappingType mappingType = mapping.getMappingType();
			switch (mappingType) {
				case EMBEDDABLE:
					fetch.handleFetches(session, row, instance);
					break;
				case ELEMENT_COLLECTION:
					this.handleElementCollectionFetch(session, row, instance, fetch);
					break;
				default:
					this.handleAssociationFetch(session, row, instance, fetch);
			}
		}
	}

	/**
	 * Returns if the join should be ignored
	 * 
	 * @param mapping
	 *            the mapping
	 * @return true if the join should be ignored, false otherwise
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private boolean ignoreJoin(SingularAssociationMapping<?, ?> mapping) {
		// if we are joined on this mapping then ignore
		if ((mapping.getInverse() != null) && (mapping.getInverse() == this.getMapping())) {
			return true;
		}

		// if we will join on that mapping then ignore
		for (final FetchImpl<X, ?> fetch : this.fetches.values()) {
			if (fetch.getMapping() == mapping) {
				return true;
			}
		}

		return false;
	}

	private void initializeInstance(SessionImpl session, ResultSet row, ManagedInstance<? extends X> managedInstance) throws SQLException {
		managedInstance.setLoading(true);

		final X instance = managedInstance.getInstance();

		for (int i = 0; i < this.fields.length; i++) {
			this.columns[i].setValue(instance, row.getObject(this.fields[i]));
		}

		for (final SingularAssociationMapping<?, ?> mapping : this.singularJoins) {
			final Object child = this.getInstance(session, mapping, row);
			mapping.set(instance, child);
			managedInstance.setJoinLoaded(mapping);
		}

		for (final FetchImpl<X, ?> fetch : this.fetches.values()) {
			final JoinedMapping<? super X, ?, ?> mapping = fetch.getMapping();
			mapping.initialize(managedInstance);

			managedInstance.setJoinLoaded(mapping);
		}
	}

	/**
	 * Joins instead of fetch.
	 * 
	 * @param attributeName
	 *            the name of the attribute
	 * @param jt
	 *            the join type
	 * @param <Y>
	 *            the type of the return join
	 * @return the fetch for the join
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@SuppressWarnings("unchecked")
	public <Y> FetchImpl<X, Y> join(String attributeName, JoinType jt) {
		final Mapping<?, ?, ?> mapping = this.getMapping(attributeName);

		if (!(mapping instanceof JoinedMapping)) {
			throw new IllegalArgumentException("Cannot dereference attribute " + attributeName);
		}

		final FetchImpl<X, Y> fetch = new FetchImpl<X, Y>(this, (JoinedMapping<? super X, ?, Y>) mapping, jt);
		this.joins.add(fetch);

		return fetch;
	}

	/**
	 * Populates the embedded id fields from the result set
	 * 
	 * @param row
	 *            the row
	 * @param idMapping
	 *            the embedded mapping
	 * @return the generated embedded id
	 * @throws SQLException
	 *             thrown in case of an underlying SQL Error
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private Object populateEmbeddedId(ResultSet row, EmbeddedMapping<?, ?> idMapping, SingularAssociationMapping<?, ?> mapping, MutableBoolean allNull)
		throws SQLException {
		final Object id = idMapping.getAttribute().newInstance();

		for (final Mapping<?, ?, ?> child : idMapping.getChildren()) {
			if (child instanceof BasicMapping) {
				final BasicMapping<?, ?> basicMapping = (BasicMapping<?, ?>) child;
				final BasicColumn column = basicMapping.getColumn();

				if (mapping == null) {
					final String field = this.idFields.get(column);
					final Object value = row.getObject(field);

					if (value != null) {
						allNull.setValue(false);
					}

					basicMapping.getAttribute().set(id, value);
				}
				else {
					for (final JoinColumn joinColumn : mapping.getForeignKey().getJoinColumns()) {
						if (joinColumn.getReferencedColumnName().equals(column.getName())) {
							final String field = this.joinFields.get(joinColumn);
							final Object value = row.getObject(field);

							if (value != null) {
								allNull.setValue(false);
							}

							basicMapping.getAttribute().set(id, value);
						}
					}
				}
			}
			else {
				child.getAttribute().set(id, this.populateEmbeddedId(row, idMapping, mapping, allNull));
			}
		}

		return id;
	}
}
