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
package org.batoo.jpa.core.impl.criteria;

import java.util.Collection;
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

import org.apache.commons.lang.mutable.MutableInt;
import org.batoo.jpa.core.impl.instance.ManagedInstance;
import org.batoo.jpa.core.impl.jdbc.AbstractColumn;
import org.batoo.jpa.core.impl.jdbc.BasicColumn;
import org.batoo.jpa.core.impl.jdbc.DiscriminatorColumn;
import org.batoo.jpa.core.impl.jdbc.EntityTable;
import org.batoo.jpa.core.impl.jdbc.SecondaryTable;
import org.batoo.jpa.core.impl.manager.SessionImpl;
import org.batoo.jpa.core.impl.model.attribute.BasicAttribute;
import org.batoo.jpa.core.impl.model.mapping.AbstractMapping;
import org.batoo.jpa.core.impl.model.mapping.AssociationMapping;
import org.batoo.jpa.core.impl.model.mapping.BasicMapping;
import org.batoo.jpa.core.impl.model.mapping.EmbeddedMapping;
import org.batoo.jpa.core.impl.model.mapping.PluralAssociationMapping;
import org.batoo.jpa.core.impl.model.mapping.SingularAssociationMapping;
import org.batoo.jpa.core.impl.model.mapping.SingularMapping;
import org.batoo.jpa.core.impl.model.type.EmbeddableTypeImpl;
import org.batoo.jpa.core.impl.model.type.EntityTypeImpl;
import org.batoo.jpa.core.util.BatooUtils;
import org.batoo.jpa.core.util.Pair;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Collections2;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Lists;
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
public class FetchParentImpl<Z, X> implements FetchParent<Z, X> {

	private final EntityTypeImpl<X> entity;
	private final List<FetchImpl<X, ?>> fetches = Lists.newArrayList();

	private String alias;
	private String primaryTableAlias;
	private final HashBiMap<String, SecondaryTable> tableAliases = HashBiMap.create();
	private final HashBiMap<String, AbstractColumn> fields = HashBiMap.create();
	private int nextTableAlias = 0;
	private String discriminatorAlias;

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
	 * Returns the description of the fetch.
	 * 
	 * @param parent
	 *            the parent
	 * @return the description of the fetch *
	 */
	public String describe(final String parent) {
		final List<String> fetches = Lists.transform(this.fetches, new Function<FetchImpl<X, ?>, String>() {

			@Override
			public String apply(FetchImpl<X, ?> input) {
				return input.describe(parent);
			}
		});

		return BatooUtils.indent(Joiner.on("\n").join(fetches));
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public final <Y> Fetch<X, Y> fetch(PluralAttribute<? super X, ?, Y> attribute) {
		return this.fetch(attribute, JoinType.INNER);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public final <Y> Fetch<X, Y> fetch(PluralAttribute<? super X, ?, Y> attribute, JoinType jt) {
		return this.fetch(attribute.getName(), jt);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public final <Y> Fetch<X, Y> fetch(SingularAttribute<? super X, Y> attribute) {
		return this.fetch(attribute, JoinType.LEFT);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public final <Y> Fetch<X, Y> fetch(SingularAttribute<? super X, Y> attribute, JoinType jt) {
		return this.fetch(attribute.getName(), jt);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public final <Y> Fetch<X, Y> fetch(String attributeName) {
		return (Fetch<X, Y>) this.fetch(attributeName, JoinType.INNER);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <Y> Fetch<X, Y> fetch(String attributeName, JoinType jt) {
		Type<Y> type;

		final AbstractMapping<X, Y> mapping = (AbstractMapping<X, Y>) this.entity.getMapping(attributeName);

		if (mapping instanceof SingularAssociationMapping) {
			type = ((SingularAssociationMapping<? super X, Y>) mapping).getType();
		}
		else {
			type = ((PluralAssociationMapping<? super X, Y, ?>) mapping).getType();
		}

		if (!(type instanceof EntityType)) {
			throw new IllegalArgumentException("Cannot dereference attribute " + attributeName);
		}

		final FetchImpl<X, Y> fetch = new FetchImpl<X, Y>(this, (AssociationMapping<? super X, Y, ?>) mapping, jt);
		this.fetches.add(fetch);

		return fetch;
	}

	/**
	 * Returns the generated SQL fragment.
	 * 
	 * @param query
	 *            the query
	 * @return the generated SQL fragment
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public String generate(CriteriaQueryImpl<?> query) {
		final List<String> selects = Lists.newArrayList();
		selects.add(this.generateImpl(query));

		for (final FetchImpl<X, ?> fetch : this.fetches) {
			selects.add(fetch.generate(query));
		}

		return Joiner.on(",\n\t").join(selects);
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

		return this.primaryTableAlias + "." + this.entity.getRootType().getDiscriminatorColumn().getName() + " IN ("
			+ Joiner.on(",").join(discriminators) + ")";
	}

	private String generateImpl(CriteriaQueryImpl<?> query) {
		final List<String> selects = Lists.newArrayList();

		for (final EntityTable table : this.entity.getAllTables()) {
			int fieldNo = 0;

			final List<String> fields = Lists.newArrayList();

			final String tableAlias = this.getTableAlias(query, table);

			final Collection<AbstractColumn> columns = table.getColumns();
			for (final AbstractColumn column : columns) {

				final String fieldAlias = tableAlias + "_F" + fieldNo++;

				final String field = Joiner.on(".").skipNulls().join(tableAlias, column.getName());
				if (column instanceof DiscriminatorColumn) {
					this.discriminatorAlias = fieldAlias;
				}
				else {
					this.fields.put(fieldAlias, column);
				}

				fields.add(field + " AS " + fieldAlias);
			}

			selects.add(Joiner.on(", ").join(fields));
		}

		return Joiner.on(",\n\t\t").join(selects);
	}

	/**
	 * Returns the generated joins SQL fragment.
	 * 
	 * @param query
	 *            the query
	 * @return the generated joins SQL fragment
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public String generateJoins(CriteriaQueryImpl<?> query) {
		final List<String> joins = Lists.newArrayList();

		for (final Entry<String, SecondaryTable> e : this.tableAliases.entrySet()) {
			final String alias = e.getKey();
			final SecondaryTable table = e.getValue();

			joins.add("\t" + table.joinPrimary(this.primaryTableAlias, alias));
		}

		for (final FetchImpl<X, ?> fetch : this.fetches) {
			joins.add(fetch.generateJoins(query));
		}

		return Joiner.on("\n").join(joins);
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

	private Object getId(Map<String, Object> row) {
		if (this.entity.hasSingleIdAttribute()) {
			final SingularMapping<? super X, ?> idMapping = this.entity.getIdMapping();
			if (idMapping instanceof BasicMapping) {
				final BasicColumn column = ((BasicMapping<? super X, ?>) idMapping).getColumn();
				final String field = this.fields.inverse().get(column);

				return row.get(field);
			}

			if (idMapping instanceof EmbeddedMapping) {
				final EmbeddedMapping<? super X, ?> mapping = (EmbeddedMapping<? super X, ?>) idMapping;

				return this.populateEmbeddedId(row, mapping);
			}
		}

		final Object id = ((EmbeddableTypeImpl<?>) this.entity.getIdType()).newInstance();
		for (final Pair<BasicMapping<? super X, ?>, BasicAttribute<?, ?>> pair : this.entity.getIdMappings()) {
			final BasicColumn column = pair.getFirst().getColumn();
			final String field = this.fields.inverse().get(column);

			pair.getSecond().set(id, row.get(field));
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
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public ManagedInstance<? extends X> getInstance(SessionImpl session, Map<String, Object> row) {
		// get the id of for the instance
		final Object id = this.getId(row);

		if (id == null) {
			return null;
		}

		// if inheritance is in place then locate the correct child type
		if (this.entity.getRootType().getInheritanceType() != null) {
			final Object discriminatorValue = row.get(this.discriminatorAlias);

			final EntityTypeImpl<? extends X> effectiveType = this.entity.getChildType(discriminatorValue);
			if (effectiveType != null) {
				return effectiveType.getManagedInstanceById(session, id);
			}
		}

		return this.entity.getManagedInstanceById(session, id);
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
			String alias = this.tableAliases.inverse().get(table);
			if (alias == null) {
				alias = this.alias + "_S" + this.nextTableAlias++;
				this.tableAliases.put(alias, (SecondaryTable) table);
			}

			return alias;
		}
		else if (table.getEntity() != table.getEntity().getRootType()) {

		}

		return this.getPrimaryTableAlias(query);
	}

	/**
	 * Handles the row for multiple results.
	 * 
	 * @param session
	 *            the session
	 * @param query
	 *            the query
	 * @param data
	 *            the resultset data
	 * @param rowNo
	 *            the current row no
	 * @param jumpSize
	 *            the jump size
	 * @return the list of instances
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public List<X> handle(SessionImpl session, BaseTypedQueryImpl<?> query, List<Map<String, Object>> data, MutableInt rowNo, int jumpSize) {
		final List<ManagedInstance<? extends X>> instances = this.handleMulti(session, query, data, null, rowNo, jumpSize);

		return Lists.transform(instances, new Function<ManagedInstance<? extends X>, X>() {

			@Override
			public X apply(ManagedInstance<? extends X> input) {
				return input.getInstance();
			}
		});
	}

	/**
	 * Handles the row for multiple results.
	 * 
	 * @param session
	 *            the session
	 * @param query
	 *            the query
	 * @param data
	 *            the resultset data
	 * @param parent
	 *            the parent instance
	 * @param rowNo
	 *            the current row no
	 * @param jumpSize
	 *            the jump size
	 * @return the list of instances
	 * 
	 * @since $version
	 * @author hceylan
	 */
	protected List<ManagedInstance<? extends X>> handleMulti(SessionImpl session, BaseTypedQueryImpl<?> query,
		List<Map<String, Object>> data, ManagedInstance<?> parent, MutableInt rowNo, int jumpSize) {
		final List<ManagedInstance<? extends X>> instances = Lists.newArrayList();

		while ((rowNo.intValue() < data.size())) {
			final ManagedInstance<? extends X> instance = this.handleSingle(instances, session, query, data, parent, rowNo, jumpSize);
			if (instance == null) {
				break;
			}
		}

		return instances;
	}

	/**
	 * Handles the row for a single result.
	 * 
	 * @param instances
	 *            the instances already loaded
	 * @param session
	 *            the session
	 * @param query
	 *            the query
	 * @param data
	 *            the resultset data
	 * @param parent
	 *            the parent instance
	 * @param rowNo
	 *            the current row no
	 * @param jumpSize
	 *            the jump size
	 * @return the managed instance
	 * 
	 * @since $version
	 * @author hceylan
	 */
	protected ManagedInstance<? extends X> handleSingle(List<ManagedInstance<? extends X>> instances, SessionImpl session,
		BaseTypedQueryImpl<?> query, List<Map<String, Object>> data, ManagedInstance<?> parent, MutableInt rowNo, int jumpSize) {
		int leap = jumpSize;

		final Map<String, Object> row = data.get(rowNo.intValue());

		// if id is null then break
		final ManagedInstance<? extends X> instance = this.getInstance(session, row);
		if (instance == null) {
			return null;
		}

		// if different parent then break
		if (!this.shouldContinue(session, parent, row)) {
			return null;
		}

		// if we processed the same instance then break
		if ((instances != null) && instances.contains(instance)) {
			return null;
		}

		final ManagedInstance<? extends X> managedInstance = session.get(instance);
		if (managedInstance != null) {
			instances.add(managedInstance);
			rowNo.add(leap);

			return managedInstance;
		}

		for (final Entry<String, AbstractColumn> entry : this.fields.entrySet()) {
			final String field = entry.getKey();
			final AbstractColumn column = entry.getValue();
			column.setValue(instance, row.get(field));
		}

		instances.add(instance);
		session.put(instance);

		for (int i = this.fetches.size() - 1; i >= 0; i--) {
			final FetchImpl<X, ?> fetch = this.fetches.get(i);
			final MutableInt subRowNo = new MutableInt(rowNo);
			final AssociationMapping<? super X, ?, ?> mapping = fetch.getMapping();

			// if it is a plural association then get a list of instances
			if (mapping.getAttribute().isCollection()) {
				// resolve the children
				final List<?> children = fetch.handleMulti(session, query, data, instance, subRowNo, leap);

				// set the children
				mapping.set(instance, Lists.transform(children, new Function<Object, Object>() {

					@Override
					public Object apply(Object input) {
						return ((ManagedInstance<?>) input).getInstance();
					}
				}));

				// if it is a one-to-many mapping and has an inverse then set the inverses
				if ((mapping.getInverse() != null) && //
					(mapping.getAttribute().getPersistentAttributeType() == PersistentAttributeType.ONE_TO_MANY)) {
					for (final Object child : children) {
						mapping.getInverse().set((ManagedInstance<?>) child, instance.getInstance());
					}
				}

				leap = subRowNo.intValue() - rowNo.intValue();
			}
			// if it is singular association then get a single instance
			else {
				final ManagedInstance<?> value = fetch.handleSingle(null, session, query, data, instance, subRowNo, leap);
				if (value == null) {
					mapping.set(instance, null);
				}
				else {
					mapping.set(instance, value.getInstance());

					// if this is a one-to-one mapping and has an inverse then set it
					if ((mapping.getInverse() != null)
						&& (mapping.getAttribute().getPersistentAttributeType() == PersistentAttributeType.ONE_TO_ONE)) {
						mapping.getInverse().set(value, instance.getInstance());
					}

					leap = subRowNo.intValue() - rowNo.intValue();
				}
			}

		}

		rowNo.add(leap);

		return instance;
	}

	/**
	 * Populates the embedded id fields from the result set
	 * 
	 * @param row
	 *            the row
	 * @param mapping
	 *            the embedded mapping
	 * @return the generated embedded id
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private Object populateEmbeddedId(Map<String, Object> row, EmbeddedMapping<? super X, ?> mapping) {
		final Object id = mapping.getAttribute().newInstance();

		for (final AbstractMapping<?, ?> child : mapping.getMappings()) {
			if (child instanceof BasicMapping) {
				final BasicMapping<?, ?> basicMapping = (BasicMapping<?, ?>) child;
				final String field = this.fields.inverse().get(basicMapping.getColumn());
				basicMapping.getAttribute().set(id, row.get(field));
			}
			else {
				child.getAttribute().set(id, this.populateEmbeddedId(row, mapping));
			}
		}

		return id;
	}

	/**
	 * Returns whether the handling should continue.
	 * <p>
	 * For Fetch parents this is always true, where as Fetch checks if row still belongs to the parent
	 * 
	 * @param session
	 *            the session
	 * @param parent
	 *            the parent instance
	 * @param row
	 *            the data row
	 * @return true if the handling should continue, false otherwise
	 * 
	 * @since $version
	 * @author hceylan
	 */
	protected boolean shouldContinue(SessionImpl session, ManagedInstance<?> parent, Map<String, Object> row) {
		return true;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String toString() {
		return this.entity.getName();
	}
}
