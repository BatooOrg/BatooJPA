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
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EnumType;
import javax.persistence.FetchType;
import javax.persistence.TemporalType;
import javax.persistence.criteria.JoinType;
import javax.persistence.metamodel.PluralAttribute.CollectionType;
import javax.persistence.metamodel.Type.PersistenceType;

import org.apache.commons.lang.StringUtils;
import org.batoo.common.util.BatooUtils;
import org.batoo.common.util.FinalWrapper;
import org.batoo.jpa.core.impl.collections.ManagedCollection;
import org.batoo.jpa.core.impl.collections.ManagedList;
import org.batoo.jpa.core.impl.criteria.CriteriaBuilderImpl;
import org.batoo.jpa.core.impl.criteria.CriteriaQueryImpl;
import org.batoo.jpa.core.impl.criteria.QueryImpl;
import org.batoo.jpa.core.impl.criteria.RootImpl;
import org.batoo.jpa.core.impl.criteria.join.AbstractJoin;
import org.batoo.jpa.core.impl.criteria.join.MapJoinImpl;
import org.batoo.jpa.core.impl.instance.ManagedInstance;
import org.batoo.jpa.core.impl.manager.EntityManagerImpl;
import org.batoo.jpa.core.impl.model.EmbeddableTypeImpl;
import org.batoo.jpa.core.impl.model.EntityTypeImpl;
import org.batoo.jpa.core.impl.model.MetamodelImpl;
import org.batoo.jpa.core.impl.model.TypeImpl;
import org.batoo.jpa.core.impl.model.attribute.MapAttributeImpl;
import org.batoo.jpa.core.impl.model.attribute.PluralAttributeImpl;
import org.batoo.jpa.jdbc.CollectionTable;
import org.batoo.jpa.jdbc.Joinable;
import org.batoo.jpa.jdbc.JoinableTable;
import org.batoo.jpa.jdbc.OrderColumn;
import org.batoo.jpa.jdbc.mapping.ElementCollectionMapping;
import org.batoo.jpa.jdbc.mapping.MappingType;
import org.batoo.jpa.parser.MappingException;
import org.batoo.jpa.parser.metadata.ColumnMetadata;
import org.batoo.jpa.parser.metadata.attribute.ElementCollectionAttributeMetadata;

import com.google.common.collect.Maps;

/**
 * AbstractMapping for element collections.
 * 
 * @param <Z>
 *            the source type
 * @param <E>
 *            the element type
 * @param <C>
 *            the collection type
 * 
 * @author hceylan
 * @since 2.0.0
 */
public class ElementCollectionMappingImpl<Z, C, E> extends AbstractMapping<Z, C, E> implements PluralMappingEx<Z, C, E>, ElementCollectionMapping<Z, C, E> {

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
	private SingularMappingEx<? super E, ?> keyMapping;

	private ElementMappingImpl<E> rootMapping;
	private FinalWrapper<Comparator<E>> comparator;
	private FinalWrapper<CriteriaQueryImpl<E>> selectCriteria;
	private FinalWrapper<CriteriaQueryImpl<Object[]>> selectMapCriteria;

	/**
	 * @param parent
	 *            the parent mapping
	 * @param attribute
	 *            the attribute
	 * 
	 * @since 2.0.0
	 */
	public ElementCollectionMappingImpl(AbstractParentMapping<?, Z> parent, PluralAttributeImpl<? super Z, C, E> attribute) {
		super(parent, attribute, attribute.getJavaType(), attribute.getName());

		final ElementCollectionAttributeMetadata metadata = (ElementCollectionAttributeMetadata) attribute.getMetadata();

		this.attribute = attribute;
		this.eager = metadata.getFetchType() == FetchType.EAGER;

		this.collectionTable = new CollectionTable(attribute.getMetamodel().getJdbcAdaptor(), (EntityTypeImpl<?>) attribute.getDeclaringType(), this,
			metadata.getCollectionTable());
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
	public Object extractKey(Object value) {
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

		if (collection != null) {
			((ManagedCollection<E>) collection).flush(connection, removals, force);
		}
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
	 * @since 2.0.0
	 */
	public CollectionTable getCollectionTable() {
		return this.collectionTable;
	}

	private Comparator<E> getComparator() {
		FinalWrapper<Comparator<E>> wrapper = this.comparator;

		if (wrapper == null) {
			synchronized (this) {
				if (this.comparator == null) {
					this.comparator = new FinalWrapper<Comparator<E>>(new ListComparator<E>(this));
				}

				wrapper = this.comparator;
			}
		}

		return wrapper.value;
	}

	/**
	 * Returns the key mapping of the element collection mapping.
	 * 
	 * @return the key mapping of the element collection mapping
	 * 
	 * @since 2.0.0
	 */
	public SingularMappingEx<? super E, ?> getKeyMapping() {
		return this.keyMapping;
	}

	/**
	 * Returns the map key of the element collection mapping.
	 * 
	 * @return the map key of the element collection mapping
	 * 
	 * @since 2.0.0
	 */
	public String getMapKey() {
		return this.mapKey;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public AbstractMapping<?, ?, ?> getMapping(String path) {
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
		FinalWrapper<CriteriaQueryImpl<E>> wrapper = this.selectCriteria;

		synchronized (this) {
			// other thread prepared before this one
			if (this.selectCriteria == null) {

				final MetamodelImpl metamodel = this.attribute.getMetamodel();
				final CriteriaBuilderImpl cb = metamodel.getEntityManagerFactory().getCriteriaBuilder();

				CriteriaQueryImpl<E> q = cb.createQuery(this.attribute.getBindableJavaType());
				q.internal();

				final EntityTypeImpl<?> type = (EntityTypeImpl<?>) this.getRoot().getType();
				final RootImpl<?> r = q.from(type);
				r.alias(BatooUtils.acronym(type.getName()).toLowerCase());

				final AbstractJoin<?, E> join = r.<E> join(this.attribute.getName());
				join.alias(BatooUtils.acronym(this.attribute.getName()));

				q = q.select(join);
				q.where(cb.equal(r, cb.parameter(type.getJavaType())));

				this.selectCriteria = new FinalWrapper<CriteriaQueryImpl<E>>(q);
			}

			wrapper = this.selectCriteria;
		}

		return wrapper.value;
	}

	@SuppressWarnings("unchecked")
	private CriteriaQueryImpl<Object[]> getSelectMapCriteria() {
		FinalWrapper<CriteriaQueryImpl<Object[]>> wrapper = this.selectMapCriteria;

		synchronized (this) {
			// other thread prepared before this one
			if (this.selectCriteria == null) {

				final MetamodelImpl metamodel = this.attribute.getMetamodel();
				final CriteriaBuilderImpl cb = metamodel.getEntityManagerFactory().getCriteriaBuilder();

				CriteriaQueryImpl<Object[]> q = cb.createQuery(Object[].class);
				q.internal();

				final EntityTypeImpl<?> type = (EntityTypeImpl<?>) this.getRoot().getType();
				final RootImpl<?> r = q.from(type);
				r.alias(BatooUtils.acronym(type.getName()).toLowerCase());
				final MapJoinImpl<?, ?, E> join = (MapJoinImpl<?, ?, E>) r.<E> join(this.attribute.getName());
				join.alias(BatooUtils.acronym(this.attribute.getName()));

				q = q.multiselect(join.key(), join.value());
				q = q.where(cb.equal(r, cb.parameter(type.getJavaType())));

				this.selectMapCriteria = new FinalWrapper<CriteriaQueryImpl<Object[]>>(q);
			}

			wrapper = this.selectMapCriteria;
		}

		return wrapper.value;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public JoinableTable getJoinTable() {
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
	 * @since 2.0.0
	 */
	@SuppressWarnings("unchecked")
	public void link() {
		this.type = this.attribute.getElementType();

		if (this.type.getPersistenceType() == PersistenceType.EMBEDDABLE) {
			this.rootMapping = new ElementMappingImpl<E>(this, (EmbeddableTypeImpl<E>) this.type);
		}

		if (this.attribute.getCollectionType() == CollectionType.MAP) {
			final MapAttributeImpl<? super Z, Map<?, E>, E> mapAttribute = (MapAttributeImpl<? super Z, Map<?, E>, E>) this.attribute;
			if (this.mapKey != null) {
				if (this.type.getPersistenceType() == PersistenceType.EMBEDDABLE) {
					this.keyMapping = (SingularMappingEx<? super E, ?>) this.rootMapping.getMapping(this.mapKey);
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
			final String name = (this.orderColumn != null) && StringUtils.isNotBlank(this.orderColumn.getName()) ? this.orderColumn.getName()
				: this.attribute.getName() + "_ORDER";
			this.collectionTable.setOrderColumn(this.orderColumn, name, this.attribute.getLocator());
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

		q.setParameter(1, instance.getInstance());

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

		q.setParameter(1, instance.getInstance());

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

		BatooUtils.addAll(children, collection.getDelegate());

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
		if (list.isInitialized()) {
			Collections.sort(list.getDelegate(), this.getComparator());
		}
	}
}
