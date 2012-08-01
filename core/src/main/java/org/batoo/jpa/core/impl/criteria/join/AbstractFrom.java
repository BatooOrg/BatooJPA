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
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Fetch;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.MapAttribute;
import javax.persistence.metamodel.PluralAttribute;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.Type.PersistenceType;

import org.apache.commons.lang.StringUtils;
import org.batoo.jpa.core.impl.criteria.AbstractCriteriaQueryImpl;
import org.batoo.jpa.core.impl.criteria.QueryImpl;
import org.batoo.jpa.core.impl.criteria.expression.EntityTypeExpression;
import org.batoo.jpa.core.impl.criteria.expression.StaticTypeExpression;
import org.batoo.jpa.core.impl.criteria.join.MapJoinImpl.MapSelectType;
import org.batoo.jpa.core.impl.criteria.path.ParentPath;
import org.batoo.jpa.core.impl.jdbc.AbstractTable;
import org.batoo.jpa.core.impl.manager.SessionImpl;
import org.batoo.jpa.core.impl.model.attribute.PluralAttributeImpl;
import org.batoo.jpa.core.impl.model.mapping.JoinedMapping;
import org.batoo.jpa.core.impl.model.mapping.JoinedMapping.MappingType;
import org.batoo.jpa.core.impl.model.mapping.Mapping;
import org.batoo.jpa.core.impl.model.mapping.PluralMapping;
import org.batoo.jpa.core.impl.model.type.EntityTypeImpl;
import org.batoo.jpa.core.impl.model.type.TypeImpl;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

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
public abstract class AbstractFrom<Z, X> extends ParentPath<Z, X> implements From<Z, X>, Joinable {

	private final FetchParentImpl<Z, X> fetchRoot;
	private final EntityTypeImpl<X> entity;
	private final Set<AbstractJoin<X, ?>> joins = Sets.newHashSet();
	private final JoinedMapping<? super Z, ?, X> mapping;
	private boolean selected;

	/**
	 * Constructor for joined types
	 * 
	 * @param parent
	 *            the parent
	 * @param type
	 *            the joined type
	 * @param mapping
	 *            the join mapping
	 * @param joinType
	 *            the join type
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public AbstractFrom(AbstractFrom<?, Z> parent, TypeImpl<X> type, JoinedMapping<? super Z, ?, X> mapping, JoinType joinType) {
		super(parent, type.getJavaType());

		this.fetchRoot = parent.getFetchRoot().join(mapping.getAttribute().getName(), joinType);

		if (type.getPersistenceType() == PersistenceType.ENTITY) {
			this.entity = (EntityTypeImpl<X>) type;
			this.mapping = null;
		}
		else {
			this.entity = null;
			this.mapping = mapping;
		}
	}

	/**
	 * Constructor for root types
	 * 
	 * @param entity
	 *            the entity
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public AbstractFrom(EntityTypeImpl<X> entity) {
		super(null, entity.getJavaType());

		this.fetchRoot = new FetchParentImpl<Z, X>(entity);
		this.entity = entity;
		this.mapping = null;
	}

	/**
	 * Ensure that the alias is assigned.
	 * 
	 * @param query
	 *            the criteria query
	 * 
	 * @since $version
	 * @author hceylan
	 */
	protected void ensureAlias(AbstractCriteriaQueryImpl<?> query) {
		if (StringUtils.isBlank(this.getAlias())) {
			this.alias(query.getAlias(this));
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <Y> Fetch<X, Y> fetch(PluralAttribute<? super X, ?, Y> attribute) {
		return this.fetchRoot.fetch(attribute);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <Y> Fetch<X, Y> fetch(PluralAttribute<? super X, ?, Y> attribute, JoinType jt) {
		return this.fetchRoot.fetch(attribute, jt);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <Y> Fetch<X, Y> fetch(SingularAttribute<? super X, Y> attribute) {
		return this.fetchRoot.fetch(attribute);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <Y> Fetch<X, Y> fetch(SingularAttribute<? super X, Y> attribute, JoinType jt) {
		return this.fetchRoot.fetch(attribute, jt);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <Y> Fetch<X, Y> fetch(String attributeName) {
		return this.fetchRoot.fetch(attributeName);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <Y> Fetch<X, Y> fetch(String attributeName, JoinType jt) {
		return this.fetchRoot.fetch(attributeName, jt);
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
		return this.fetchRoot.generateDiscrimination();
	}

	/**
	 * Returns the JPQL joins fragment.
	 * 
	 * @param criteriaQuery
	 *            the criteria query
	 * @return the JPQL joins fragment
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public String generateJpqlJoins(AbstractCriteriaQueryImpl<?> criteriaQuery) {
		this.ensureAlias(criteriaQuery);

		final List<String> joins = Lists.newArrayList();
		if (this.selected) {
			final String fetches = this.fetchRoot.generateJpqlFetches(this.getAlias());
			if (StringUtils.isNotBlank(fetches)) {
				joins.add(fetches);
			}
		}

		for (final AbstractJoin<X, ?> join : this.joins) {
			joins.add(join.generateJpqlJoins(criteriaQuery));
		}

		return Joiner.on("\n").join(joins);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateJpqlRestriction(AbstractCriteriaQueryImpl<?> query) {
		this.ensureAlias(query);

		return this.getAlias();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateJpqlSelect(AbstractCriteriaQueryImpl<?> query, boolean selected) {
		this.selected |= selected;

		return null;
	}

	/**
	 * Generates SQL joins fragment.
	 * 
	 * @param query
	 *            the query
	 * @param joins
	 *            the map of joins
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void generateSqlJoins(AbstractCriteriaQueryImpl<?> query, Map<Joinable, String> joins) {
		this.fetchRoot.generateSqlJoins(query, joins, this.selected);

		for (final AbstractJoin<X, ?> join : this.joins) {
			join.generateSqlJoins(query, joins);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateSqlSelect(AbstractCriteriaQueryImpl<?> query, boolean selected) {
		this.select(selected);

		return this.fetchRoot.generateSqlSelect(query, selected, this.getParentPath() == null);
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
	 * Returns the entity of the from.
	 * 
	 * @return the entity of the from
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public EntityTypeImpl<X> getEntity() {
		return this.entity;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Set<Fetch<X, ?>> getFetches() {
		return this.fetchRoot.getFetches();
	}

	/**
	 * Returns the fetchRoot of the AbstractFrom.
	 * 
	 * @return the fetchRoot of the AbstractFrom
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Override
	public FetchParentImpl<Z, X> getFetchRoot() {
		return this.fetchRoot;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Set<Join<X, ?>> getJoins() {
		final Set<Join<X, ?>> joins = Sets.newHashSet();
		joins.addAll(this.joins);
		return joins;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String[] getSqlRestrictionFragments(AbstractCriteriaQueryImpl<?> query) {
		return this.fetchRoot.getSqlRestrictionFragments(query, MapSelectType.VALUE);
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
	public String[] getSqlRestrictionFragments(AbstractCriteriaQueryImpl<?> query, MapSelectType selectType) {
		return this.fetchRoot.getSqlRestrictionFragments(query, selectType);
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
	public String getTableAlias(AbstractCriteriaQueryImpl<?> query, AbstractTable table) {
		return this.getFetchRoot().getTableAlias(query, table);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public X handle(QueryImpl<?> query, SessionImpl session, ResultSet row) throws SQLException {
		if (this.entity != null) {
			return this.fetchRoot.handle(session, row);
		}

		return this.fetchRoot.handleElementFetch(row).getValue();
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
	public <Y> CollectionJoinImpl<X, Y> join(CollectionAttribute<? super X, Y> collection) {
		return this.join(collection, JoinType.INNER);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <Y> CollectionJoinImpl<X, Y> join(CollectionAttribute<? super X, Y> collection, JoinType jt) {
		return (CollectionJoinImpl<X, Y>) this.join(collection.getName(), jt);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <Y> ListJoinImpl<X, Y> join(ListAttribute<? super X, Y> list) {
		return this.join(list, JoinType.INNER);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <Y> ListJoinImpl<X, Y> join(ListAttribute<? super X, Y> list, JoinType jt) {
		return (ListJoinImpl<X, Y>) this.join(list.getName(), jt);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <K, V> MapJoinImpl<X, K, V> join(MapAttribute<? super X, K, V> map) {
		return this.join(map, JoinType.INNER);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <K, V> MapJoinImpl<X, K, V> join(MapAttribute<? super X, K, V> map, JoinType jt) {
		return (MapJoinImpl<X, K, V>) this.join(map.getName(), jt);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <Y> SetJoinImpl<X, Y> join(SetAttribute<? super X, Y> set) {
		return this.join(set, JoinType.INNER);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <Y> SetJoinImpl<X, Y> join(SetAttribute<? super X, Y> set, JoinType jt) {
		return (SetJoinImpl<X, Y>) this.join(set.getName(), jt);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <Y> AbstractJoin<X, Y> join(SingularAttribute<? super X, Y> attribute) {
		return this.join(attribute, JoinType.INNER);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <Y> AbstractJoin<X, Y> join(SingularAttribute<? super X, Y> attribute, JoinType jt) {
		return this.join(attribute.getName(), jt);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <Y> AbstractJoin<X, Y> join(String attributeName) {
		return this.join(attributeName, JoinType.INNER);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <Y> AbstractJoin<X, Y> join(String attributeName, JoinType jt) {
		Mapping<? super X, ?, ?> mapping = null;

		if (this.entity != null) {
			mapping = this.entity.getRootMapping().getChild(attributeName);
		}
		else if (this.mapping.getMappingType() == MappingType.ELEMENT_COLLECTION) {
			// TODO handle embeddable element collections
		}

		AbstractJoin<X, Y> join = null;

		final JoinedMapping<X, ?, Y> joinedMapping = (JoinedMapping<X, ?, Y>) mapping;
		if (joinedMapping.getMappingType() == MappingType.SINGULAR_ASSOCIATION) {
			join = new SingularJoin<X, Y>(this, joinedMapping, jt);
		}
		else {
			final PluralAttributeImpl<? super X, ?, Y> attribute = (PluralAttributeImpl<? super X, ?, Y>) mapping.getAttribute();

			switch (attribute.getCollectionType()) {
				case SET:
					join = new SetJoinImpl<X, Y>(this, (PluralMapping<? super X, Set<Y>, Y>) joinedMapping, jt);
					break;
				case COLLECTION:
					join = new CollectionJoinImpl<X, Y>(this, (PluralMapping<? super X, Collection<Y>, Y>) joinedMapping, jt);
					break;
				case LIST:
					join = new ListJoinImpl<X, Y>(this, (PluralMapping<? super X, List<Y>, Y>) joinedMapping, jt);
					break;
				case MAP:
					join = new MapJoinImpl(this, (PluralMapping<? super X, Map<?, Y>, Y>) joinedMapping, jt);
			}
		}

		this.joins.add(join);

		return join;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <Y> CollectionJoinImpl<X, Y> joinCollection(String attributeName) {
		return this.joinCollection(attributeName, JoinType.INNER);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <Y> CollectionJoinImpl<X, Y> joinCollection(String attributeName, JoinType jt) {
		return (CollectionJoinImpl<X, Y>) this.join(attributeName, jt);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <Y> ListJoinImpl<X, Y> joinList(String attributeName) {
		return this.joinList(attributeName, JoinType.INNER);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <Y> ListJoinImpl<X, Y> joinList(String attributeName, JoinType jt) {
		return (ListJoinImpl<X, Y>) this.join(attributeName, jt);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <K, V> MapJoinImpl<X, K, V> joinMap(String attributeName) {
		return this.joinMap(attributeName, JoinType.INNER);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <K, V> MapJoinImpl<X, K, V> joinMap(String attributeName, JoinType jt) {
		return (MapJoinImpl<X, K, V>) this.join(attributeName, jt);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <Y> SetJoinImpl<X, Y> joinSet(String attributeName) {
		return this.joinSet(attributeName, JoinType.INNER);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <Y> SetJoinImpl<X, Y> joinSet(String attributeName, JoinType jt) {
		return (SetJoinImpl<X, Y>) this.join(attributeName, jt);
	}

	/**
	 * Updates the selected status.
	 * 
	 * @param selected
	 *            if selected
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void select(boolean selected) {
		this.selected |= selected;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Expression<Class<? extends X>> type() {
		if ((this.entity != null) && (this.entity.getRootType().getInheritanceType() != null)) {
			return new EntityTypeExpression<X>(this, this.entity.getRootType().getDiscriminatorColumn());
		}

		if (this.entity != null) {
			return new StaticTypeExpression<X>(this, this.entity.getJavaType());
		}

		return new StaticTypeExpression<X>(this, this.getModel().getBindableJavaType());
	}
}
