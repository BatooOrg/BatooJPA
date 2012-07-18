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
package org.batoo.jpa.core.impl.model.mapping;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.criteria.Path;
import javax.persistence.metamodel.Attribute.PersistentAttributeType;
import javax.persistence.metamodel.PluralAttribute.CollectionType;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.mutable.MutableBoolean;
import org.batoo.jpa.core.impl.collections.ManagedCollection;
import org.batoo.jpa.core.impl.collections.ManagedList;
import org.batoo.jpa.core.impl.criteria.CriteriaBuilderImpl;
import org.batoo.jpa.core.impl.criteria.CriteriaQueryImpl;
import org.batoo.jpa.core.impl.criteria.PredicateImpl;
import org.batoo.jpa.core.impl.criteria.RootImpl;
import org.batoo.jpa.core.impl.criteria.TypedQueryImpl;
import org.batoo.jpa.core.impl.criteria.expression.ParameterExpressionImpl;
import org.batoo.jpa.core.impl.criteria.join.AbstractJoin;
import org.batoo.jpa.core.impl.instance.ManagedInstance;
import org.batoo.jpa.core.impl.jdbc.ConnectionImpl;
import org.batoo.jpa.core.impl.jdbc.ForeignKey;
import org.batoo.jpa.core.impl.jdbc.JoinTable;
import org.batoo.jpa.core.impl.manager.EntityManagerImpl;
import org.batoo.jpa.core.impl.manager.SessionImpl;
import org.batoo.jpa.core.impl.model.MetamodelImpl;
import org.batoo.jpa.core.impl.model.attribute.BasicAttribute;
import org.batoo.jpa.core.impl.model.attribute.MapAttributeImpl;
import org.batoo.jpa.core.impl.model.attribute.PluralAttributeImpl;
import org.batoo.jpa.core.impl.model.type.EmbeddableTypeImpl;
import org.batoo.jpa.core.impl.model.type.EntityTypeImpl;
import org.batoo.jpa.core.impl.model.type.TypeImpl;
import org.batoo.jpa.core.util.BatooUtils;
import org.batoo.jpa.core.util.Pair;
import org.batoo.jpa.parser.MappingException;
import org.batoo.jpa.parser.metadata.AssociationMetadata;
import org.batoo.jpa.parser.metadata.ColumnMetadata;
import org.batoo.jpa.parser.metadata.attribute.AssociationAttributeMetadata;
import org.batoo.jpa.parser.metadata.attribute.PluralAttributeMetadata;

import com.google.common.collect.Lists;

/**
 * 
 * @param <Z>
 *            the source type
 * @param <E>
 *            the element type
 * @param <C>
 *            the collection type
 * 
 * @author hceylan
 * @since $version
 */
public class PluralAssociationMapping<Z, C, E> extends AssociationMapping<Z, C, E> implements PluralMapping<Z, C, E> {

	private final PluralAttributeImpl<? super Z, C, E> attribute;
	private final JoinTable joinTable;
	private EntityTypeImpl<E> type;
	private AssociationMapping<?, ?, ?> inverse;
	private CriteriaQueryImpl<E> selectCriteria;

	private SingularMapping<? super E, ?> mapKeyIdMapping;
	private Pair<BasicMapping<? super E, ?>, BasicAttribute<?, ?>>[] mapKeyIdMappings;
	private EmbeddableTypeImpl<?> keyClass;
	private String orderBy;
	private Comparator<E> comparator;
	private ColumnMetadata orderColumn;

	/**
	 * @param parent
	 *            the parent mapping
	 * @param attribute
	 *            the attribute
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public PluralAssociationMapping(ParentMapping<?, Z> parent, PluralAttributeImpl<? super Z, C, E> attribute) {
		super(parent, (AssociationAttributeMetadata) attribute.getMetadata(), attribute);

		this.attribute = attribute;

		final AssociationMetadata metadata = this.getAssociationMetadata();

		if (this.isOwner()) {
			if ((this.attribute.getPersistentAttributeType() == PersistentAttributeType.MANY_TO_MANY) || (metadata.getJoinColumns().size() == 0)) {
				this.joinTable = new JoinTable(this.getRoot().getType(), metadata.getJoinTable());
			}
			else {
				this.joinTable = null;
			}
		}
		else {
			this.joinTable = null;
		}

		final PluralAttributeMetadata pluralAttributeMetadata = (PluralAttributeMetadata) attribute.getMetadata();
		if (attribute.getCollectionType() == CollectionType.LIST) {
			this.orderBy = pluralAttributeMetadata.getOrderBy();
			this.orderColumn = pluralAttributeMetadata.getOrderColumn();
		}
		else {
			this.orderBy = null;
			this.orderColumn = null;
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void checkTransient(ManagedInstance<?> managedInstance) {
		final Object values = this.get(managedInstance.getInstance());

		final SessionImpl session = managedInstance.getSession();

		if (values instanceof Collection) {
			for (final Object entity : ((Collection<E>) values).toArray()) {
				session.checkTransient(entity);
			}
		}
		else if (values instanceof Map) {
			for (final E entity : ((Map<?, E>) values).values()) {
				session.checkTransient(entity);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void enhance(ManagedInstance<?> instance) {
		final C c = this.get(instance.getInstance());
		if (c == null) {
			this.set(instance, this.attribute.newCollection(this, instance, false));
		}
		else {
			this.set(instance, this.attribute.newCollection(this, instance, c));
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void flush(ConnectionImpl connection, ManagedInstance<?> managedInstance, boolean removals, boolean force) throws SQLException {
		final Object collection = this.get(managedInstance.getInstance());

		((ManagedCollection<E>) collection).flush(connection, removals, force);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public PluralAttributeImpl<? super Z, C, E> getAttribute() {
		return this.attribute;
	}

	private Comparator<E> getComparator() {
		if (this.comparator != null) {
			return this.comparator;
		}
		synchronized (this) {
			if (this.comparator != null) {
				return this.comparator;
			}

			return this.comparator = new ListComparator<E>(this);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public ForeignKey getForeignKey() {
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public AssociationMapping<?, ?, ?> getInverse() {
		return this.inverse;
	}

	/**
	 * Returns the key class of the association.
	 * 
	 * @return the key class of the association
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Override
	public TypeImpl<?> getMapKeyClass() {
		return this.keyClass;
	}

	/**
	 * Returns the key mapping of the association.
	 * 
	 * @return the key mapping of the association
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public SingularMapping<? super E, ?> getMapKeyIdMapping() {
		return this.mapKeyIdMapping;
	}

	/**
	 * Returns the map key id mappings of the association.
	 * 
	 * @return the map key id mappings of the association
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public Pair<BasicMapping<? super E, ?>, BasicAttribute<?, ?>>[] getMapKeyIdMappings() {
		return this.mapKeyIdMappings;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Mapping<?, ?, ?> getMapping(String path) {
		return this.type.getRootMapping().getMapping(path);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public MappingType getMappingType() {
		return MappingType.PLURAL_ASSOCIATION;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getOrderBy() {
		return this.orderBy;
	}

	private CriteriaQueryImpl<E> getSelectCriteria() {
		if (this.selectCriteria != null) {
			return this.selectCriteria;
		}

		synchronized (this) {
			// other thread prepared before this one
			if (this.selectCriteria != null) {
				return this.selectCriteria;
			}

			final MetamodelImpl metamodel = this.getRoot().getType().getMetamodel();
			final CriteriaBuilderImpl cb = metamodel.getEntityManagerFactory().getCriteriaBuilder();

			final EntityTypeImpl<E> entity = metamodel.entity(this.attribute.getBindableJavaType());

			CriteriaQueryImpl<E> q = cb.createQuery(this.attribute.getBindableJavaType());
			q.internal();
			final RootImpl<?> r = q.from(this.getRoot().getType());
			r.alias(BatooUtils.acronym(this.getRoot().getName()).toLowerCase());
			// TODO handle embeddables along the path
			final AbstractJoin<?, E> join = r.<E> join(this.attribute.getName());
			join.alias(BatooUtils.acronym(entity.getName()).toLowerCase());
			q = q.select(join);

			entity.prepareEagerAssociations(join, 0, this);

			// has single id mapping
			final EntityTypeImpl<?> rootType = this.getRoot().getType();
			if (rootType.hasSingleIdAttribute()) {
				final SingularMapping<?, ?> idMapping = rootType.getIdMapping();
				final ParameterExpressionImpl<?> pe = cb.parameter(idMapping.getAttribute().getJavaType());
				final Path<?> path = r.get(idMapping.getAttribute().getName());
				final PredicateImpl predicate = cb.equal(path, pe);

				return this.selectCriteria = q.where(predicate);
			}

			// has multiple id mappings
			final List<PredicateImpl> predicates = Lists.newArrayList();
			for (final Pair<?, BasicAttribute<?, ?>> pair : rootType.getIdMappings()) {
				final BasicMapping<?, ?> idMapping = (BasicMapping<?, ?>) pair.getFirst();
				final ParameterExpressionImpl<?> pe = cb.parameter(idMapping.getAttribute().getJavaType());
				final Path<?> path = r.get(idMapping.getAttribute().getName());
				final PredicateImpl predicate = cb.equal(path, pe);

				predicates.add(predicate);
			}

			return this.selectCriteria = q.where(predicates.toArray(new PredicateImpl[predicates.size()]));
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public JoinTable getTable() {
		return this.joinTable;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public EntityTypeImpl<E> getType() {
		return this.type;
	}

	/**
	 * Initializes the managed collection of the instance
	 * 
	 * @param instance
	 *            the instance
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void initialize(ManagedInstance<?> instance) {
		this.set(instance, this.attribute.newCollection(this, instance, false));
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean isAssociation() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void link() throws MappingException {
		final EntityTypeImpl<?> entity = this.getRoot().getType();

		this.type = (EntityTypeImpl<E>) this.attribute.getElementType();

		if (!this.isOwner()) {
			this.inverse = (AssociationMapping<?, ?, ?>) this.type.getRootMapping().getMapping(this.getMappedBy());

			if (this.inverse == null) {
				throw new MappingException("Cannot find the mappedBy attribute " + this.getMappedBy() + " specified on " + this.attribute.getJavaMember());
			}

			this.inverse.setInverse(this);
		}
		else {
			// initialize the join table
			if (this.joinTable != null) {
				this.joinTable.link(entity, this.type);

				if (this.attribute.getCollectionType() == CollectionType.LIST) {
					if (this.orderColumn != null) {
						final String name = StringUtils.isNotBlank(this.orderColumn.getName()) ? //
							this.orderColumn.getName() : this.attribute.getName() + "_ORDER";
						this.joinTable.setOrderColumn(this.orderColumn, name);
					}
				}
			}
		}

		if (this.attribute.getCollectionType() == CollectionType.MAP) {
			final MapAttributeImpl<? super Z, Map<?, E>, E> mapAttribute = (MapAttributeImpl<? super Z, Map<?, E>, E>) this.attribute;
			final String mapKey = mapAttribute.getMapKey();
			if (StringUtils.isBlank(mapKey)) {
				if (this.type.hasSingleIdAttribute()) {
					this.mapKeyIdMapping = this.type.getIdMapping();
				}
				else {
					this.mapKeyIdMappings = this.type.getIdMappings();
					this.keyClass = (EmbeddableTypeImpl<?>) this.type.getIdType();
				}
			}
			else {
				this.mapKeyIdMapping = (SingularMapping<? super E, ?>) this.type.getRootMapping().getMapping(mapKey);

				if (this.mapKeyIdMapping == null) {
					throw new MappingException("Cannot locate the MapKey: " + mapKey, this.attribute.getLocator());
				}
			}
		}
	}

	/**
	 * Loads the eager association.
	 * 
	 * @param instance
	 *            the managed instance
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@SuppressWarnings("unchecked")
	public void load(ManagedInstance<?> instance) {
		final ManagedCollection<E> collection = (ManagedCollection<E>) this.attribute.newCollection(this, instance, false);
		collection.getDelegate().addAll(this.loadCollection(instance));
		this.set(instance, collection);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Collection<? extends E> loadCollection(ManagedInstance<?> managedInstance) {
		final EntityManagerImpl em = managedInstance.getSession().getEntityManager();
		final TypedQueryImpl<E> q = em.createQuery(this.getSelectCriteria());

		final EntityTypeImpl<?> rootType = managedInstance.getType();

		final Object id = managedInstance.getId().getId();

		// if has single id then pass it on
		if (rootType.hasSingleIdAttribute()) {
			q.setParameter(1, id);
		}
		else {
			int i = 1;
			for (final Pair<?, BasicAttribute<?, ?>> pair : rootType.getIdMappings()) {
				q.setParameter(i++, pair.getSecond().get(id));
			}
		}

		return q.getResultList();

	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void mergeWith(EntityManagerImpl entityManager, ManagedInstance<?> instance, Object entity, MutableBoolean requiresFlush,
		IdentityHashMap<Object, Object> processed) {
		// get the managed collection
		final ManagedCollection<E> collection = (ManagedCollection<E>) this.get(instance.getInstance());

		// merge with the new entities
		collection.mergeWith(entityManager, entity, requiresFlush, processed);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void persistAdditions(EntityManagerImpl entityManager, ManagedInstance<?> instance) {
		if (this.cascadesPersist()) {
			final ManagedCollection<E> collection = (ManagedCollection<E>) this.get(instance.getInstance());

			collection.persistAdditions(entityManager);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean references(Object instance, Object reference) {
		final Object values = this.get(instance);

		if (values == null) {
			return false;
		}

		if (values instanceof Collection) {
			return ((Collection<?>) values).contains(reference);
		}

		return ((Map<?, ?>) values).containsValue(reference);
	}

	/**
	 * Refreshes the collection.
	 * 
	 * @param entityManager
	 *            the entity manager
	 * @param instance
	 *            the managed instance owning the collection
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@SuppressWarnings("unchecked")
	public void refreshCollection(EntityManagerImpl entityManager, ManagedInstance<?> instance) {
		// load the children
		final Collection<? extends E> children = this.loadCollection(instance);

		final ManagedCollection<E> collection = (ManagedCollection<E>) this.get(instance.getInstance());
		collection.refreshChildren(children);

		if (this.cascadesRefresh()) {
			for (final E child : children) {
				entityManager.refresh(child);
			}
		}
	}

	/**
	 * Removes the children that have been orphaned due to removal from the managed collection
	 * 
	 * @param entityManager
	 *            the entity manager
	 * @param instance
	 *            the managed instance
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@SuppressWarnings("unchecked")
	public void removeOrphans(EntityManagerImpl entityManager, ManagedInstance<?> instance) {
		if (this.removesOrphans()) {
			final ManagedCollection<E> collection = (ManagedCollection<E>) this.get(instance.getInstance());
			collection.removeOrphans(entityManager);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void setInverse(AssociationMapping<?, ?, ?> inverse) {
		this.inverse = inverse;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void setLazy(ManagedInstance<?> instance) {
		this.set(instance, this.attribute.newCollection(this, instance, true));
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void sortList(Object instance) {
		final ManagedList<Z, E> list = (ManagedList<Z, E>) this.get(instance);
		final ArrayList<E> delegate = list.getDelegate();
		if ((list != null) && list.isInitialized()) {
			Collections.sort(delegate, this.getComparator());
		}
	}
}
