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
import java.util.Set;

import javax.persistence.criteria.CollectionJoin;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Fetch;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.ListJoin;
import javax.persistence.criteria.MapJoin;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.SetJoin;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.MapAttribute;
import javax.persistence.metamodel.PluralAttribute;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;

import org.batoo.jpa.core.impl.jdbc.AbstractColumn;
import org.batoo.jpa.core.impl.jdbc.EntityTable;
import org.batoo.jpa.core.impl.jdbc.PkColumn;
import org.batoo.jpa.core.impl.model.EntityTypeImpl;
import org.batoo.jpa.core.impl.model.attribute.PhysicalAttributeImpl;

import com.google.common.base.Joiner;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Represents a bound type, usually an entity that appears in the from clause, but may also be an embeddable belonging to an entity in the
 * from clause.
 * <p>
 * Serves as a factory for Joins of associations, embeddables, and collections belonging to the type, and for Paths of attributes belonging
 * to the type.
 * 
 * @param <Z>
 *            the source type
 * @param <X>
 *            the target type
 * 
 * @author hceylan
 * @since $version
 */
public abstract class AbstractFromImpl<Z, X> extends AbstractPathImpl<X> implements From<Z, X> {

	protected final EntityTypeImpl<X> entity;

	private final Map<String, AbstractPathImpl<?>> children = Maps.newHashMap();
	protected final HashBiMap<String, EntityTable> tableAliases = HashBiMap.create();
	protected final HashBiMap<String, PkColumn> idFields = HashBiMap.create();
	protected final HashBiMap<String, AbstractColumn> fields = HashBiMap.create();
	private int nextTableAlias = 0;

	/**
	 * @param entity
	 *            the entity
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public AbstractFromImpl(EntityTypeImpl<X> entity) {
		super(null);

		this.entity = entity;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <Y> Fetch<X, Y> fetch(PluralAttribute<? super X, ?, Y> attribute) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <Y> Fetch<X, Y> fetch(PluralAttribute<? super X, ?, Y> attribute, JoinType jt) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <Y> Fetch<X, Y> fetch(SingularAttribute<? super X, Y> attribute) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <Y> Fetch<X, Y> fetch(SingularAttribute<? super X, Y> attribute, JoinType jt) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <X, Y> Fetch<X, Y> fetch(String attributeName) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <X, Y> Fetch<X, Y> fetch(String attributeName, JoinType jt) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generate(CriteriaQueryImpl<?> query) {
		final List<String> fields = Lists.newArrayList();

		for (final EntityTable table : this.entity.getTables()) {
			int fieldNo = 0;

			final String tableAlias = this.getTableAlias(query, table);

			final Collection<AbstractColumn> columns = table.getColumns();
			for (final AbstractColumn column : columns) {

				final String fieldAlias = tableAlias + "_F" + fieldNo++;

				final String field = Joiner.on(".").skipNulls().join(tableAlias, column.getName());
				if (column instanceof PkColumn) {
					this.idFields.put(fieldAlias, (PkColumn) column);
				}
				else {
					this.fields.put(fieldAlias, column);
				}

				fields.add(field + " AS " + fieldAlias);
			}
		}

		return Joiner.on(", ").join(fields);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <K, V, M extends Map<K, V>> Expression<M> get(MapAttribute<X, K, V> map) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <E, C extends Collection<E>> Expression<C> get(PluralAttribute<X, C, E> collection) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <Y> AbstractPathImpl<Y> get(SingularAttribute<? super X, Y> attribute) {
		if (attribute.getDeclaringType() != this.entity) {
			throw new IllegalArgumentException("Cannot dereference");
		}

		// try to resolve from path
		AbstractPathImpl<Y> path = (AbstractPathImpl<Y>) this.children.get(attribute.getName());
		if (path != null) {
			return path;
		}

		// generate and return
		if (attribute instanceof PhysicalAttributeImpl) {
			path = new PhysicalAttributePathImpl<Y>(this, (PhysicalAttributeImpl<?, Y>) attribute);
			this.children.put(attribute.getName(), path);
		}

		return path;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <Y> Path<Y> get(String attributeName) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public From<Z, X> getCorrelationParent() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Set<Fetch<X, ?>> getFetches() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Set<Join<X, ?>> getJoins() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public EntityTypeImpl<X> getModel() {
		return this.entity;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public AbstractFromImpl<?, ?> getRoot() {
		return this;
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
	 * @return
	 *         the alias for the table
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public String getTableAlias(CriteriaQueryImpl<?> query, EntityTable table) {
		if (this.getAlias() == null) {
			this.alias(query.generateAlias());
		}

		String alias = this.tableAliases.inverse().get(table);
		if (alias == null) {
			alias = this.getAlias() + "_T" + this.nextTableAlias++;
			this.tableAliases.put(alias, table);
		}

		return alias;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean isCorrelated() {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <Y> CollectionJoin<X, Y> join(CollectionAttribute<? super X, Y> collection) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <Y> CollectionJoin<X, Y> join(CollectionAttribute<? super X, Y> collection, JoinType jt) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <Y> ListJoin<X, Y> join(ListAttribute<? super X, Y> list) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <Y> ListJoin<X, Y> join(ListAttribute<? super X, Y> list, JoinType jt) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <K, V> MapJoin<X, K, V> join(MapAttribute<? super X, K, V> map) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <K, V> MapJoin<X, K, V> join(MapAttribute<? super X, K, V> map, JoinType jt) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <Y> SetJoin<X, Y> join(SetAttribute<? super X, Y> set) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <Y> SetJoin<X, Y> join(SetAttribute<? super X, Y> set, JoinType jt) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <Y> Join<X, Y> join(SingularAttribute<? super X, Y> attribute) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <Y> Join<X, Y> join(SingularAttribute<? super X, Y> attribute, JoinType jt) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <X, Y> Join<X, Y> join(String attributeName) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <X, Y> Join<X, Y> join(String attributeName, JoinType jt) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <X, Y> CollectionJoin<X, Y> joinCollection(String attributeName) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <X, Y> CollectionJoin<X, Y> joinCollection(String attributeName, JoinType jt) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <X, Y> ListJoin<X, Y> joinList(String attributeName) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <X, Y> ListJoin<X, Y> joinList(String attributeName, JoinType jt) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <X, K, V> MapJoin<X, K, V> joinMap(String attributeName) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <X, K, V> MapJoin<X, K, V> joinMap(String attributeName, JoinType jt) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <X, Y> SetJoin<X, Y> joinSet(String attributeName) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <X, Y> SetJoin<X, Y> joinSet(String attributeName, JoinType jt) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Expression<Class<? extends X>> type() {
		// TODO Auto-generated method stub
		return null;
	}
}
