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
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.PluralAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.Type;

import org.apache.commons.lang.mutable.MutableBoolean;
import org.batoo.jpa.core.impl.collections.ManagedCollection;
import org.batoo.jpa.core.impl.criteria.CriteriaQueryImpl;
import org.batoo.jpa.core.impl.instance.EnhancedInstance;
import org.batoo.jpa.core.impl.instance.ManagedId;
import org.batoo.jpa.core.impl.instance.ManagedInstance;
import org.batoo.jpa.core.impl.jdbc.AbstractColumn;
import org.batoo.jpa.core.impl.jdbc.BasicColumn;
import org.batoo.jpa.core.impl.jdbc.DiscriminatorColumn;
import org.batoo.jpa.core.impl.jdbc.EntityTable;
import org.batoo.jpa.core.impl.jdbc.JoinColumn;
import org.batoo.jpa.core.impl.jdbc.PkColumn;
import org.batoo.jpa.core.impl.jdbc.SecondaryTable;
import org.batoo.jpa.core.impl.manager.SessionImpl;
import org.batoo.jpa.core.impl.model.attribute.BasicAttribute;
import org.batoo.jpa.core.impl.model.mapping.AssociationMapping;
import org.batoo.jpa.core.impl.model.mapping.BasicMapping;
import org.batoo.jpa.core.impl.model.mapping.EmbeddedMapping;
import org.batoo.jpa.core.impl.model.mapping.Mapping;
import org.batoo.jpa.core.impl.model.mapping.PluralAssociationMapping;
import org.batoo.jpa.core.impl.model.mapping.SingularAssociationMapping;
import org.batoo.jpa.core.impl.model.mapping.SingularMapping;
import org.batoo.jpa.core.impl.model.type.EmbeddableTypeImpl;
import org.batoo.jpa.core.impl.model.type.EntityTypeImpl;
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
	private final ArrayList<FetchImpl<X, ?>> fetches = Lists.newArrayList();

	private String alias;
	private String primaryTableAlias;
	private String discriminatorAlias;

	private final HashMap<SecondaryTable, String> tableAliases = Maps.newHashMap();
	private final HashMap<AbstractColumn, String> idFields = Maps.newHashMap();
	private final HashMap<AbstractColumn, String> joinFields = Maps.newHashMap();
	private final List<SingularAssociationMapping<?, ?>> joins = Lists.newArrayList();

	private int nextTableAlias = 1;
	private AbstractColumn[] columns;
	private String[] fields;

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
		Type<Y> type;

		final Mapping<?, ?, ?> mapping = this.entity.getRootMapping().getMapping(attributeName);

		if (mapping instanceof SingularAssociationMapping) {
			type = ((SingularAssociationMapping<X, Y>) mapping).getType();
		}
		else {
			type = ((PluralAssociationMapping<X, ?, Y>) mapping).getType();
		}

		if (!(type instanceof EntityType)) {
			throw new IllegalArgumentException("Cannot dereference attribute " + attributeName);
		}

		final FetchImpl<X, Y> fetch = new FetchImpl<X, Y>(this, (AssociationMapping<? super X, ?, Y>) mapping, jt);
		this.fetches.add(fetch);

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
		if (this.entity.getRootType().getInheritanceType() == null) {
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
		final List<String> fetches = Lists.transform(this.fetches, new Function<FetchImpl<X, ?>, String>() {

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
	public void generateSqlJoins(CriteriaQueryImpl<?> query, final List<String> selfJoins) {
		for (final Entry<SecondaryTable, String> e : this.tableAliases.entrySet()) {
			final String alias = e.getValue();
			final SecondaryTable table = e.getKey();

			selfJoins.add(table.joinPrimary(this.primaryTableAlias, alias));
		}
	}

	/**
	 * Generates joins SQL fragment for the fetch chain.
	 * 
	 * @param query
	 *            the query
	 * @param joins
	 *            the map of joins
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void generateSqlJoins(CriteriaQueryImpl<?> query, Map<Joinable, String> joins) {
		final List<String> selfJoins = Lists.newArrayList();

		this.generateSqlJoins(query, selfJoins);

		if (selfJoins.size() > 0) {
			joins.put(this, Joiner.on("\n").join(selfJoins));
		}
		else {
			joins.put(this, null);
		}

		for (final FetchImpl<X, ?> fetch : this.fetches) {
			fetch.generateSqlJoins(query, joins);
		}
	}

	/**
	 * Returns the generated SQL fragment.
	 * 
	 * @param query
	 *            the query
	 * @param root
	 *            if the generation is at root
	 * @return the generated SQL fragment
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public String generateSqlSelect(CriteriaQueryImpl<?> query, boolean root) {
		final List<String> selects = Lists.newArrayList();
		selects.add(this.generateSqlSelectImpl(query, root));

		for (final FetchImpl<X, ?> fetch : this.fetches) {
			selects.add(fetch.generateSqlSelect(query, false));
		}

		return Joiner.on(",\n\t").join(selects);
	}

	private String generateSqlSelectImpl(CriteriaQueryImpl<?> query, boolean root) {
		final List<String> selects = Lists.newArrayList();
		final Map<AbstractColumn, String> fieldMap = Maps.newHashMap();

		for (final EntityTable table : this.entity.getAllTables()) {
			int fieldNo = 0;

			final List<String> fields = Lists.newArrayList();

			final String tableAlias = this.getTableAlias(query, table);

			final Collection<AbstractColumn> columns = table.getColumns();
			for (final AbstractColumn column : columns) {
				final String fieldAlias = tableAlias + "_F" + fieldNo++;

				final String field = Joiner.on(".").skipNulls().join(tableAlias, column.getName());
				if (column instanceof PkColumn) {
					this.idFields.put(column, fieldAlias);
				}
				else if (column instanceof DiscriminatorColumn) {
					this.discriminatorAlias = fieldAlias;
				}
				else if (column.getMapping() instanceof SingularAssociationMapping) {
					final SingularAssociationMapping<?, ?> mapping = (SingularAssociationMapping<?, ?>) column.getMapping();

					// if we are on a join on that path then ignore
					if (!root && this.ignoreJoin(mapping)) {
						continue;
					}

					this.joins.add(mapping);
					this.joinFields.put(column, fieldAlias);
				}
				else {
					fieldMap.put(column, fieldAlias);
				}

				fields.add(field + " AS " + fieldAlias);
			}

			selects.add(Joiner.on(", ").join(fields));
		}

		this.columns = new AbstractColumn[fieldMap.size()];
		this.fields = new String[fieldMap.size()];

		int i = 0;
		for (final Entry<AbstractColumn, String> entry : fieldMap.entrySet()) {
			this.columns[i] = entry.getKey();
			this.fields[i] = entry.getValue();
			i++;
		}

		return Joiner.on(",\n\t\t").join(selects);
	}

	/**
	 * Returns the alias of the fetch.
	 * 
	 * @return the alias of the fetch
	 * 
	 * @since $version
	 * @author hceylan
	 * @param query
	 */
	private String getAlias(CriteriaQueryImpl<?> query) {
		if (this.alias == null) {
			this.alias = query.generateEntityAlias();
		}

		return this.alias;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public final Set<Fetch<X, ?>> getFetches() {
		final Set<Fetch<X, ?>> fetches = Sets.newHashSet();
		fetches.addAll(this.fetches);

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
			instance = (ManagedInstance<Y>) this.entity.getManagedInstanceById(session, managedId);
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
			instance = effectiveType.getManagedInstanceById(session, (ManagedId<Y>) managedId);
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
	public AssociationMapping<? super Z, ?, X> getMapping() {
		return null;
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
	public String getPrimaryTableAlias(CriteriaQueryImpl<?> query) {
		if (this.primaryTableAlias == null) {
			this.primaryTableAlias = this.getAlias(query) + "_P";
		}
		return this.primaryTableAlias;
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
	public String getTableAlias(CriteriaQueryImpl<?> query, EntityTable table) {
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
	public X handle(SessionImpl session, ResultSet row) throws SQLException {
		return this.handleFetch(session, row).getInstance();
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
	public ManagedInstance<? extends X> handleFetch(SessionImpl session, ResultSet row) throws SQLException {
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
			return this.handleFetches(session, row, instance);
		}

		return instance;
	}

	private ManagedInstance<? extends X> handleFetches(SessionImpl session, final ResultSet row, ManagedInstance<? extends X> instance) throws SQLException {
		for (final FetchImpl<X, ?> fetch : this.fetches) {
			// resolve the child
			final ManagedInstance<?> child = fetch.handleFetch(session, row);

			// if null then continue
			if (child == null) {
				continue;
			}

			final AssociationMapping<? super X, ?, ?> mapping = fetch.getMapping();

			// if it is a plural association then we will test if we processed the child
			if (mapping instanceof PluralAssociationMapping) {
				if (((ManagedCollection<?>) mapping.get(instance.getInstance())).addChild(child.getInstance())) {
					// if it is a one-to-many mapping and has an inverse then set the inverses
					if ((mapping.getInverse() != null) && //
						(mapping.getAttribute().getPersistentAttributeType() == PersistentAttributeType.ONE_TO_MANY)) {
						mapping.getInverse().set(child, instance.getInstance());
						child.setAssociationLoaded(mapping.getInverse());
					}
				}
			}
			// if it is singular association then set the instance
			else {
				mapping.set(instance, child.getInstance());

				// if this is a one-to-one mapping and has an inverse then set it
				if ((mapping.getInverse() != null) && (mapping.getAttribute().getPersistentAttributeType() == PersistentAttributeType.ONE_TO_ONE)) {
					mapping.getInverse().set(child, instance.getInstance());
					child.setAssociationLoaded(mapping);
				}
			}
		}

		return instance;
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
		for (final FetchImpl<X, ?> fetch : this.fetches) {
			if (fetch.getMapping() == mapping) {
				return true;
			}
		}

		return false;
	}

	private void initializeInstance(SessionImpl session, ResultSet row, ManagedInstance<? extends X> instance) throws SQLException {
		instance.setLoading(true);

		for (int i = 0; i < this.fields.length; i++) {
			this.columns[i].setValue(instance, row.getObject(this.fields[i]));
		}

		for (final SingularAssociationMapping<?, ?> mapping : this.joins) {
			final Object child = this.getInstance(session, mapping, row);
			mapping.set(instance, child);
			instance.setAssociationLoaded(mapping);
		}

		for (final FetchImpl<X, ?> fetch : this.fetches) {
			final AssociationMapping<? super X, ?, ?> mapping = fetch.getMapping();
			if (mapping instanceof PluralAssociationMapping) {
				((PluralAssociationMapping<? super X, ?, ?>) mapping).initialize(instance);
			}

			instance.setAssociationLoaded(mapping);
		}
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
