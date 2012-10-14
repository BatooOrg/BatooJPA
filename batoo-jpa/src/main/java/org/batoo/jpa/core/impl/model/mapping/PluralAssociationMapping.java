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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.persistence.EnumType;
import javax.persistence.TemporalType;
import javax.persistence.metamodel.Attribute.PersistentAttributeType;
import javax.persistence.metamodel.PluralAttribute.CollectionType;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.mutable.MutableBoolean;
import org.batoo.jpa.core.impl.cache.CacheInstance;
import org.batoo.jpa.core.impl.collections.ManagedCollection;
import org.batoo.jpa.core.impl.collections.ManagedList;
import org.batoo.jpa.core.impl.criteria.QueryImpl;
import org.batoo.jpa.core.impl.instance.ManagedInstance;
import org.batoo.jpa.core.impl.jdbc.ConnectionImpl;
import org.batoo.jpa.core.impl.jdbc.ForeignKey;
import org.batoo.jpa.core.impl.jdbc.JoinTable;
import org.batoo.jpa.core.impl.jdbc.Joinable;
import org.batoo.jpa.core.impl.jdbc.OrderColumn;
import org.batoo.jpa.core.impl.manager.EntityManagerImpl;
import org.batoo.jpa.core.impl.manager.SessionImpl;
import org.batoo.jpa.core.impl.model.attribute.BasicAttribute;
import org.batoo.jpa.core.impl.model.attribute.MapAttributeImpl;
import org.batoo.jpa.core.impl.model.attribute.PluralAttributeImpl;
import org.batoo.jpa.core.impl.model.type.EmbeddableTypeImpl;
import org.batoo.jpa.core.impl.model.type.EntityTypeImpl;
import org.batoo.jpa.core.impl.model.type.TypeImpl;
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
	private final ForeignKey foreignKey;
	private EntityTypeImpl<E> type;
	private AssociationMapping<?, ?, ?> inverse;

	private SingularMapping<? super E, ?> mapKeyMapping;
	private Pair<BasicMapping<? super E, ?>, BasicAttribute<?, ?>>[] mapKeyMappings;
	private EmbeddableTypeImpl<?> keyClass;
	private String orderBy;
	private Comparator<E> comparator;
	private ColumnMetadata orderColumn;
	private final String mapKey;
	private ColumnMetadata mapKeyColumn;
	private TemporalType mapKeyTemporalType;
	private EnumType mapKeyEnumType;

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
				this.joinTable = new JoinTable((EntityTypeImpl<?>) this.getRoot().getType(), metadata.getJoinTable());
				this.foreignKey = null;
			}
			else {
				this.foreignKey = new ForeignKey(this.getAttribute().getMetamodel().getJdbcAdaptor(), metadata.getJoinColumns(), true);
				this.joinTable = null;
			}
		}
		else {
			this.joinTable = null;
			this.foreignKey = null;
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

		if (this.attribute.getCollectionType() == CollectionType.MAP) {
			this.mapKeyColumn = pluralAttributeMetadata.getMapKeyColumn();
			this.mapKeyTemporalType = pluralAttributeMetadata.getMapKeyTemporalType();
			this.mapKeyEnumType = pluralAttributeMetadata.getMapKeyEnumType();
			this.mapKey = pluralAttributeMetadata.getMapKey();
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
	public void attach(ConnectionImpl connection, ManagedInstance<?> instance, Joinable[] batch, int batchSize) throws SQLException {
		if (this.joinTable != null) {
			this.joinTable.performInsert(connection, instance.getInstance(), batch, batchSize);
		}
		else if (this.foreignKey != null) {
			this.foreignKey.performAttachChild(connection, instance, batch, batchSize);
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
	public void detach(ConnectionImpl connection, ManagedInstance<?> instance, Object key, Object child) throws SQLException {
		if (this.joinTable != null) {
			this.joinTable.performRemove(connection, instance.getInstance(), key, child);
		}
		else if (this.foreignKey != null) {
			this.foreignKey.performDetachChild(connection, key, child);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void detachAll(ConnectionImpl connection, ManagedInstance<?> instance) throws SQLException {
		if (this.joinTable != null) {
			this.joinTable.performRemoveAll(connection, instance.getInstance());
		}
		else if (this.foreignKey != null) {
			this.foreignKey.performDetachAll(connection, instance);
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
		Object key = null;

		if (this.mapKeyMapping != null) {
			return this.mapKeyMapping.get(value);
		}

		key = this.keyClass.newInstance();
		for (final Pair<BasicMapping<? super E, ?>, BasicAttribute<?, ?>> pair : this.mapKeyMappings) {
			pair.getSecond().set(key, pair.getFirst().get(value));
		}

		return key;
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
		return this.foreignKey;
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
		return this.mapKeyMapping;
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
		return this.mapKeyMappings;
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

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public OrderColumn getOrderColumn() {
		if (this.joinTable != null) {
			return this.joinTable.getOrderColumn();
		}

		if (this.foreignKey != null) {
			return this.foreignKey.getOrderColumn();
		}

		return null;
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
		return true;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean isJoined() {
		return (this.joinTable != null) || (this.foreignKey != null);
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
	@SuppressWarnings("unchecked")
	public void link() throws MappingException {
		final EntityTypeImpl<?> entity = (EntityTypeImpl<?>) this.getRoot().getType();

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

			if (this.foreignKey != null) {
				this.foreignKey.link(null, (EntityTypeImpl<?>) this.getRoot().getType());
				this.foreignKey.setTable(this.type.getPrimaryTable());

				if (this.orderColumn != null) {
					final String name = StringUtils.isNotBlank(this.orderColumn.getName()) ? //
						this.orderColumn.getName() : this.attribute.getName() + "_ORDER";

					this.foreignKey.setOrderColumn(this.orderColumn, name);
				}
			}
		}

		if (this.attribute.getCollectionType() == CollectionType.MAP) {
			if (this.mapKeyColumn != null) {
				final MapAttributeImpl<? super Z, Map<?, E>, E> mapAttribute = (MapAttributeImpl<? super Z, Map<?, E>, E>) this.attribute;

				final String name = StringUtils.isNotBlank(this.mapKeyColumn.getName()) ? this.mapKeyColumn.getName() : this.attribute.getName() + "_KEY";

				this.joinTable.setKeyColumn(this.mapKeyColumn, name, this.mapKeyTemporalType, this.mapKeyEnumType, mapAttribute.getKeyJavaType());
			}
			else {
				if (StringUtils.isBlank(this.mapKey)) {
					if (this.type.hasSingleIdAttribute()) {
						this.mapKeyMapping = this.type.getIdMapping();
					}
					else {
						this.mapKeyMappings = this.type.getIdMappings();
						this.keyClass = (EmbeddableTypeImpl<?>) this.type.getIdType();
					}
				}
				else {
					this.mapKeyMapping = (SingularMapping<? super E, ?>) this.type.getRootMapping().getMapping(this.mapKey);

					if (this.mapKeyMapping == null) {
						throw new MappingException("Cannot locate the MapKey: " + this.mapKey, this.attribute.getLocator());
					}
				}
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
	@SuppressWarnings("unchecked")
	public Collection<? extends E> loadCollection(ManagedInstance<?> managedInstance) {
		final EntityManagerImpl em = managedInstance.getSession().getEntityManager();

		List<E> children = null;

		// try to load from the cache
		if (this.type.isCachable()) {

			final CacheInstance cacheInstance = em.getEntityManagerFactory().getCache().get(managedInstance.getId());

			final Collection<E> collection = (Collection<E>) cacheInstance.getCollection(managedInstance, this);
			if (collection != null) {
				children = Lists.newArrayList(collection);
			}
		}

		// load from the database
		if (children == null) {
			final QueryImpl<E> q = em.createQuery(this.getSelectCriteria());

			final EntityTypeImpl<?> rootType = managedInstance.getType();

			final Object id = managedInstance.getId().getId();

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

			children = q.getResultList();
			if (this.type.isCachable()) {

			}
		}

		final Object instance = managedInstance.getInstance();
		if ((this.getInverse() != null) && (this.getAttribute().getPersistentAttributeType() == PersistentAttributeType.ONE_TO_MANY)) {
			for (final Iterator<E> i = children.iterator(); i.hasNext();) {
				final E child = i.next();
				final Object newParent = this.getInverse().get(child);
				if ((newParent != null) && (newParent != managedInstance.getInstance())) {
					i.remove();
				}
				else {
					this.getInverse().set(child, instance);
				}
			}
		}

		return children;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <K> Map<? extends K, ? extends E> loadMap(ManagedInstance<?> instance) {
		// TODO Auto-generated method stub
		return null;
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

		// if initialized then merge with the new entities
		if ((collection != null) && collection.isInitialized()) {
			collection.mergeWith(entityManager, entity, requiresFlush, processed);
		}
	}

	/**
	 * Persists the children that have been added to the managed collection
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
		final ManagedCollection<E> collection = (ManagedCollection<E>) this.get(instance.getInstance());
		collection.refreshChildren();

		if (this.cascadesRefresh()) {
			for (final E child : collection.getDelegate()) {
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
	public void setInverse(AssociationMapping<?, ?, ?> inverse) {
		this.inverse = inverse;
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
