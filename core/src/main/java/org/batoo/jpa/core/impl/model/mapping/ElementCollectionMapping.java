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
import java.util.List;
import java.util.Map;

import javax.persistence.EnumType;
import javax.persistence.FetchType;
import javax.persistence.TemporalType;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Path;
import javax.persistence.metamodel.PluralAttribute.CollectionType;
import javax.persistence.metamodel.Type.PersistenceType;

import org.apache.commons.lang.StringUtils;
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
import org.batoo.jpa.core.impl.jdbc.CollectionTable;
import org.batoo.jpa.core.impl.jdbc.ConnectionImpl;
import org.batoo.jpa.core.impl.jdbc.JoinableTable;
import org.batoo.jpa.core.impl.manager.EntityManagerImpl;
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
import org.batoo.jpa.parser.metadata.ColumnMetadata;
import org.batoo.jpa.parser.metadata.attribute.ElementCollectionAttributeMetadata;

import com.google.common.collect.Lists;

/**
 * Mapping for element collections.
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
public class ElementCollectionMapping<Z, C, E> extends Mapping<Z, C, E> implements PluralMapping<Z, C, E> {

	private final PluralAttributeImpl<? super Z, C, E> attribute;
	private final boolean eager;
	private final CollectionTable collectionTable;
	private final ColumnMetadata column;
	private final EnumType enumType;
	private final boolean lob;
	private final TemporalType temporalType;
	private final String orderBy;
	private final ColumnMetadata orderColumn;

	private TypeImpl<E> type;
	private SingularMapping<? super E, ?> keyMapping;
	private ElementMapping<E> rootMapping;
	private Comparator<E> comparator;
	private CriteriaQueryImpl<E> selectCriteria;

	/**
	 * @param parent
	 *            the parent mapping
	 * @param attribute
	 *            the attribute
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public ElementCollectionMapping(ParentMapping<?, Z> parent, PluralAttributeImpl<? super Z, C, E> attribute) {
		super(parent, parent.getRoot().getType(), attribute, attribute.getJavaType(), attribute.getName());

		final ElementCollectionAttributeMetadata metadata = (ElementCollectionAttributeMetadata) attribute.getMetadata();

		this.attribute = attribute;
		this.eager = metadata.getFetchType() == FetchType.EAGER;

		this.collectionTable = new CollectionTable(this.getRoot().getType(), metadata.getCollectionTable());
		this.column = metadata.getColumn();
		this.enumType = metadata.getEnumType();
		this.lob = metadata.isLob();
		this.temporalType = metadata.getTemporalType();

		if (this.attribute.getCollectionType() == CollectionType.LIST) {
			this.orderColumn = metadata.getOrderColumn();
			this.orderBy = metadata.getOrderBy();
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
	public boolean cascadesMerge() {
		return false;
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

	/**
	 * Returns the collection table.
	 * 
	 * @return the collection table
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public CollectionTable getCollectionTable() {
		return this.collectionTable;
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
	public TypeImpl<?> getMapKeyClass() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Mapping<?, ?, ?> getMapping(String path) {
		return this.rootMapping.getMapping(path);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public MappingType getMappingType() {
		return MappingType.ELEMENT_COLLECTION;
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

			CriteriaQueryImpl<E> q = cb.createQuery(this.attribute.getBindableJavaType());
			q.internal();
			final RootImpl<?> r = q.from(this.getRoot().getType());
			r.alias(BatooUtils.acronym(this.getRoot().getName()).toLowerCase());
			// TODO handle embeddables along the path
			final AbstractJoin<?, E> join = r.<E> join(this.attribute.getName());
			join.alias(BatooUtils.acronym(this.attribute.getName()));
			q = q.select(join);

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
	public JoinableTable getTable() {
		return this.collectionTable;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public TypeImpl<E> getType() {
		return this.type;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void initialize(ManagedInstance<?> instance) {
		this.set(instance, this.attribute.newCollection(this, instance, false));
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean isAssociation() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean isEager() {
		return this.eager;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String join(String parentAlias, String alias, JoinType joinType) {
		return this.collectionTable.getKey().createSourceJoin(joinType, parentAlias, alias);
	}

	/**
	 * Links the attribute to its collection table
	 * 
	 * @throws MappingException
	 *             thrown in case of a linkage error
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@SuppressWarnings("unchecked")
	public void link() {
		this.getRoot().getType();

		this.type = this.attribute.getElementType();

		if (this.type.getPersistenceType() == PersistenceType.EMBEDDABLE) {
			this.rootMapping = new ElementMapping<E>((EmbeddableTypeImpl<E>) this.type);
		}

		if (this.attribute.getCollectionType() == CollectionType.MAP) {
			final MapAttributeImpl<? super Z, Map<?, E>, E> mapAttribute = (MapAttributeImpl<? super Z, Map<?, E>, E>) this.attribute;
			final String mapKey = mapAttribute.getMapKey();
			if (mapKey != null) {
				if (this.type.getPersistenceType() == PersistenceType.EMBEDDABLE) {
					this.keyMapping = (SingularMapping<? super E, ?>) this.rootMapping.getMapping(mapKey);
				}

				if (this.keyMapping == null) {
					throw new MappingException("Cannot locate the MapKey: " + mapKey, this.attribute.getLocator());
				}
			}
		}

		final String defaultName = this.getAttribute().getName();
		if (this.type.getPersistenceType() == PersistenceType.EMBEDDABLE) {
			this.collectionTable.link((EmbeddableTypeImpl<E>) this.type, defaultName, this.rootMapping);
		}
		else {
			this.collectionTable.link(this.type, defaultName, this.column, this.enumType, this.temporalType, this.lob);
		}

		if (this.attribute.getCollectionType() == CollectionType.LIST) {
			if (this.orderColumn != null) {
				final String name = StringUtils.isNotBlank(this.orderColumn.getName()) ? this.orderColumn.getName() : this.attribute.getName() + "_ORDER";
				this.collectionTable.setOrderColumn(this.orderColumn, name);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
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
	public Collection<? extends E> loadCollection(ManagedInstance<?> instance) {
		final EntityManagerImpl em = instance.getSession().getEntityManager();
		final TypedQueryImpl<E> q = em.createQuery(this.getSelectCriteria());

		final EntityTypeImpl<?> rootType = instance.getType();

		final Object id = instance.getId().getId();

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
