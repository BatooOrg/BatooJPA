/*
 * Copyright (c) 2012 - Batoo Software ve Consultancy Ltd.
 * 
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */
package org.batoo.jpa.core.impl.model.mapping;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
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
import org.batoo.jpa.core.impl.criteria.QueryImpl;
import org.batoo.jpa.core.impl.criteria.RootImpl;
import org.batoo.jpa.core.impl.criteria.expression.ParameterExpressionImpl;
import org.batoo.jpa.core.impl.criteria.expression.PredicateImpl;
import org.batoo.jpa.core.impl.criteria.join.AbstractJoin;
import org.batoo.jpa.core.impl.criteria.join.MapJoinImpl;
import org.batoo.jpa.core.impl.instance.ManagedInstance;
import org.batoo.jpa.core.impl.jdbc.CollectionTable;
import org.batoo.jpa.core.impl.jdbc.Joinable;
import org.batoo.jpa.core.impl.jdbc.JoinableTable;
import org.batoo.jpa.core.impl.jdbc.OrderColumn;
import org.batoo.jpa.core.impl.manager.EntityManagerImpl;
import org.batoo.jpa.core.impl.model.MetamodelImpl;
import org.batoo.jpa.core.impl.model.attribute.BasicAttribute;
import org.batoo.jpa.core.impl.model.attribute.MapAttributeImpl;
import org.batoo.jpa.core.impl.model.attribute.PluralAttributeImpl;
import org.batoo.jpa.core.impl.model.type.EmbeddableTypeImpl;
import org.batoo.jpa.core.impl.model.type.EntityTypeImpl;
import org.batoo.jpa.core.impl.model.type.TypeImpl;
import org.batoo.jpa.core.util.Pair;
import org.batoo.jpa.parser.MappingException;
import org.batoo.jpa.parser.metadata.ColumnMetadata;
import org.batoo.jpa.parser.metadata.attribute.ElementCollectionAttributeMetadata;
import org.batoo.jpa.util.BatooUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

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
	private final ColumnMetadata mapKeyColumn;

	private final String mapKey;
	private final EnumType mapKeyEnumType;
	private final TemporalType mapKeyTemporalType;
	private TypeImpl<E> type;
	private SingularMapping<? super E, ?> keyMapping;

	private ElementMapping<E> rootMapping;
	private Comparator<E> comparator;
	private CriteriaQueryImpl<E> selectCriteria;

	private CriteriaQueryImpl<Object[]> selectMapCriteria;

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
		super(parent, attribute, attribute.getJavaType(), attribute.getName());

		final ElementCollectionAttributeMetadata metadata = (ElementCollectionAttributeMetadata) attribute.getMetadata();

		this.attribute = attribute;
		this.eager = metadata.getFetchType() == FetchType.EAGER;

		this.collectionTable = new CollectionTable((EntityTypeImpl<?>) this.getRoot().getType(), metadata.getCollectionTable());
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

		if (this.attribute.getCollectionType() == CollectionType.MAP) {
			this.mapKeyColumn = metadata.getMapKeyColumn();
			this.mapKeyTemporalType = metadata.getMapKeyTemporalType();
			this.mapKeyEnumType = metadata.getMapKeyEnumType();
			this.mapKey = metadata.getMapKey();
		}
		else {
			this.mapKeyColumn = null;
			this.mapKeyTemporalType = null;
			this.mapKeyEnumType = null;
			this.mapKey = null;
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void attach(Connection connection, ManagedInstance<?> instance, Joinable[] batch, int size) throws SQLException {
		this.collectionTable.performInsert(connection, instance.getInstance(), batch, size);
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
	public void detach(Connection connection, ManagedInstance<?> instance, Object key, Object child) throws SQLException {
		this.collectionTable.performRemove(connection, instance.getInstance(), key, child);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void detachAll(Connection connection, ManagedInstance<?> instance) throws SQLException {
		this.collectionTable.performRemoveAll(connection, instance.getInstance());
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void enhance(ManagedInstance<?> instance) {
		final C c = this.get(instance.getInstance());
		if (c == null) {
			this.set(instance.getInstance(), this.attribute.newCollection(this, instance, false));
		}
		else {
			this.set(instance.getInstance(), this.attribute.newCollection(this, instance, c));
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Object extractKey(E value) {
		return this.keyMapping.get(value);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void flush(Connection connection, ManagedInstance<?> managedInstance, boolean removals, boolean force) throws SQLException {
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
	 * Returns the key mapping of the element collection mapping.
	 * 
	 * @return the key mapping of the element collection mapping
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public SingularMapping<? super E, ?> getKeyMapping() {
		return this.keyMapping;
	}

	/**
	 * Returns the map key of the element collection mapping.
	 * 
	 * @return the map key of the element collection mapping
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public String getMapKey() {
		return this.mapKey;
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

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public OrderColumn getOrderColumn() {
		if (this.collectionTable != null) {
			return this.collectionTable.getOrderColumn();
		}

		return null;
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

			final EntityTypeImpl<?> type = (EntityTypeImpl<?>) this.getRoot().getType();
			final RootImpl<?> r = q.from(type);
			r.alias(BatooUtils.acronym(type.getName()).toLowerCase());
			final AbstractJoin<?, E> join = r.<E> join(this.attribute.getName());
			join.alias(BatooUtils.acronym(this.attribute.getName()));
			q = q.select(join);

			// has single id mapping
			if (type.hasSingleIdAttribute()) {
				final SingularMapping<?, ?> idMapping = type.getIdMapping();
				final ParameterExpressionImpl<?> pe = cb.parameter(idMapping.getAttribute().getJavaType());
				final Path<?> path = r.get(idMapping.getAttribute().getName());
				final PredicateImpl predicate = cb.equal(path, pe);

				return this.selectCriteria = q.where(predicate);
			}

			// has multiple id mappings
			final List<PredicateImpl> predicates = Lists.newArrayList();
			for (final Pair<?, BasicAttribute<?, ?>> pair : type.getIdMappings()) {
				final BasicMapping<?, ?> idMapping = (BasicMapping<?, ?>) pair.getFirst();
				final ParameterExpressionImpl<?> pe = cb.parameter(idMapping.getAttribute().getJavaType());
				final Path<?> path = r.get(idMapping.getAttribute().getName());
				final PredicateImpl predicate = cb.equal(path, pe);

				predicates.add(predicate);
			}

			return this.selectCriteria = q.where(predicates.toArray(new PredicateImpl[predicates.size()]));
		}
	}

	@SuppressWarnings("unchecked")
	private CriteriaQueryImpl<Object[]> getSelectMapCriteria() {
		if (this.selectCriteria != null) {
			return this.selectMapCriteria;
		}

		synchronized (this) {
			// other thread prepared before this one
			if (this.selectCriteria != null) {
				return this.selectMapCriteria;
			}

			final MetamodelImpl metamodel = this.getRoot().getType().getMetamodel();
			final CriteriaBuilderImpl cb = metamodel.getEntityManagerFactory().getCriteriaBuilder();

			CriteriaQueryImpl<Object[]> q = cb.createQuery(Object[].class);
			q.internal();

			final EntityTypeImpl<?> type = (EntityTypeImpl<?>) this.getRoot().getType();
			final RootImpl<?> r = q.from(type);
			r.alias(BatooUtils.acronym(type.getName()).toLowerCase());
			final MapJoinImpl<?, ?, E> join = (MapJoinImpl<?, ?, E>) r.<E> join(this.attribute.getName());
			join.alias(BatooUtils.acronym(this.attribute.getName()));

			q = q.multiselect(join.key(), join.value());

			// has single id mapping
			if (type.hasSingleIdAttribute()) {
				final SingularMapping<?, ?> idMapping = type.getIdMapping();
				final ParameterExpressionImpl<?> pe = cb.parameter(idMapping.getAttribute().getJavaType());
				final Path<?> path = r.get(idMapping.getAttribute().getName());
				final PredicateImpl predicate = cb.equal(path, pe);

				return this.selectMapCriteria = q.where(predicate);
			}

			// has multiple id mappings
			final List<PredicateImpl> predicates = Lists.newArrayList();
			for (final Pair<?, BasicAttribute<?, ?>> pair : type.getIdMappings()) {
				final BasicMapping<?, ?> idMapping = (BasicMapping<?, ?>) pair.getFirst();
				final ParameterExpressionImpl<?> pe = cb.parameter(idMapping.getAttribute().getJavaType());
				final Path<?> path = r.get(idMapping.getAttribute().getName());
				final PredicateImpl predicate = cb.equal(path, pe);

				predicates.add(predicate);
			}

			return this.selectMapCriteria = q.where(predicates.toArray(new PredicateImpl[predicates.size()]));
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
		this.set(instance.getInstance(), this.attribute.newCollection(this, instance, false));
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
	public boolean isJoined() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean isMap() {
		return this.getAttribute().getCollectionType() == CollectionType.MAP;
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
		this.type = this.attribute.getElementType();

		if (this.type.getPersistenceType() == PersistenceType.EMBEDDABLE) {
			this.rootMapping = new ElementMapping<E>(this, (EmbeddableTypeImpl<E>) this.type);
		}

		if (this.attribute.getCollectionType() == CollectionType.MAP) {
			final MapAttributeImpl<? super Z, Map<?, E>, E> mapAttribute = (MapAttributeImpl<? super Z, Map<?, E>, E>) this.attribute;
			if (this.mapKey != null) {
				if (this.type.getPersistenceType() == PersistenceType.EMBEDDABLE) {
					this.keyMapping = (SingularMapping<? super E, ?>) this.rootMapping.getMapping(this.mapKey);
				}

				if (this.keyMapping == null) {
					throw new MappingException("Cannot locate the MapKey: " + this.mapKey, this.attribute.getLocator());
				}
			}
			else {
				final String name = (this.mapKeyColumn != null) && StringUtils.isNotBlank(this.mapKeyColumn.getName()) ? //
					this.mapKeyColumn.getName() : this.attribute.getName() + "_KEY";
				this.collectionTable.setKeyColumn(this.mapKeyColumn, name, this.mapKeyTemporalType, this.mapKeyEnumType, mapAttribute.getKeyJavaType());
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
	public void load(ManagedInstance<?> instance) {
		this.setCollection(instance, this.loadCollection(instance));
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Collection<? extends E> loadCollection(ManagedInstance<?> instance) {
		final EntityManagerImpl em = instance.getSession().getEntityManager();
		final QueryImpl<E> q = em.createQuery(this.getSelectCriteria());

		final EntityTypeImpl<?> rootType = instance.getType();

		final Object id = instance.getId().getId();

		// if has single id then pass it on
		if (rootType.hasSingleIdAttribute()) {
			q.setParameter(0, id);
		}
		else {
			int i = 0;
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
	public <K> Map<? extends K, ? extends E> loadMap(ManagedInstance<?> instance) {
		final EntityManagerImpl em = instance.getSession().getEntityManager();
		final QueryImpl<Object[]> q = em.createQuery(this.getSelectMapCriteria());

		final EntityTypeImpl<?> rootType = instance.getType();

		final Object id = instance.getId().getId();

		// if has single id then pass it on
		if (rootType.hasSingleIdAttribute()) {
			q.setParameter(0, id);
		}
		else {
			int i = 0;
			for (final Pair<?, BasicAttribute<?, ?>> pair : rootType.getIdMappings()) {
				q.setParameter(i++, pair.getSecond().get(id));
			}
		}

		final HashMap<K, E> resultMap = Maps.newHashMap();

		for (final Object[] pair : q.getResultList()) {
			resultMap.put((K) pair[0], (E) pair[1]);
		}

		return resultMap;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void setCollection(ManagedInstance<?> instance, Collection<? extends E> children) {
		final ManagedCollection<E> collection = (ManagedCollection<E>) this.attribute.newCollection(this, instance, false);

		collection.getDelegate().addAll(children);

		this.set(instance.getInstance(), collection);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void setLazy(ManagedInstance<?> instance) {
		this.set(instance.getInstance(), this.attribute.newCollection(this, instance, true));
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
