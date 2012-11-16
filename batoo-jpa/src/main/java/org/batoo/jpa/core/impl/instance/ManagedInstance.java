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
package org.batoo.jpa.core.impl.instance;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.LockModeType;
import javax.persistence.PersistenceException;
import javax.persistence.metamodel.Attribute.PersistentAttributeType;
import javax.persistence.metamodel.PluralAttribute.CollectionType;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.mutable.MutableBoolean;
import org.batoo.common.log.BLogger;
import org.batoo.common.log.BLoggerFactory;
import org.batoo.common.reflect.AbstractAccessor;
import org.batoo.jpa.core.impl.cache.CacheImpl;
import org.batoo.jpa.core.impl.cache.CacheInstance;
import org.batoo.jpa.core.impl.manager.EntityManagerFactoryImpl;
import org.batoo.jpa.core.impl.manager.EntityManagerImpl;
import org.batoo.jpa.core.impl.manager.SessionImpl;
import org.batoo.jpa.core.impl.model.attribute.BasicAttribute;
import org.batoo.jpa.core.impl.model.mapping.AssociationMapping;
import org.batoo.jpa.core.impl.model.mapping.BasicMapping;
import org.batoo.jpa.core.impl.model.mapping.EmbeddedMapping;
import org.batoo.jpa.core.impl.model.mapping.JoinedMapping;
import org.batoo.jpa.core.impl.model.mapping.Mapping;
import org.batoo.jpa.core.impl.model.mapping.PluralAssociationMapping;
import org.batoo.jpa.core.impl.model.mapping.PluralMapping;
import org.batoo.jpa.core.impl.model.mapping.SingularAssociationMapping;
import org.batoo.jpa.core.impl.model.mapping.SingularMapping;
import org.batoo.jpa.core.impl.model.type.EntityTypeImpl;
import org.batoo.jpa.core.util.Pair;
import org.batoo.jpa.parser.metadata.EntityListenerMetadata.EntityListenerType;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * The managed instance to track entity instances.
 * 
 * @param <X>
 *            the type of the managed instance
 * 
 * @author hceylan
 * @since 2.0.0
 */
public class ManagedInstance<X> {

	private static final BLogger LOG = BLoggerFactory.getLogger(ManagedInstance.class);

	private final EntityTypeImpl<X> type;
	private final SessionImpl session;
	private final X instance;
	private Status status;
	private Status oldStatus;
	private boolean optimisticLock;
	private LockModeType lockMode;

	private final HashMap<Mapping<?, ?, ?>, Object> snapshot = Maps.newHashMap();
	private final HashSet<String> joinsLoaded;
	private final ArrayList<PluralMapping<?, ?, ?>> collectionsChanged;

	private boolean loading;
	private boolean loadingFromCache;
	private boolean refreshing;
	private boolean changed;
	private CacheInstance cacheInstance;

	private boolean hasInitialId;
	private ManagedId<? super X> id;
	private int h;

	/**
	 * The current lock mode context.
	 */
	public static ThreadLocal<LockModeType> LOCK_CONTEXT = new ThreadLocal<LockModeType>();

	/**
	 * @param type
	 *            the entity type of the instance
	 * @param session
	 *            the session
	 * @param instance
	 *            the instance
	 * 
	 * @since 2.0.0
	 */
	public ManagedInstance(EntityTypeImpl<X> type, SessionImpl session, X instance) {
		super();

		this.type = type;
		this.session = session;
		this.instance = instance;
		this.lockMode = ManagedInstance.LOCK_CONTEXT.get();

		this.collectionsChanged = Lists.newArrayList();
		this.joinsLoaded = Sets.newHashSet();

		this.status = Status.MANAGED;
	}

	/**
	 * @param type
	 *            the entity type of the instance
	 * @param session
	 *            the session
	 * @param instance
	 *            the instance
	 * @param id
	 *            the id of the instance
	 * 
	 * @since 2.0.0
	 */
	public ManagedInstance(EntityTypeImpl<X> type, SessionImpl session, X instance, ManagedId<? super X> id) {
		this(type, session, instance);

		type.setId(session, instance, id.getId());

		this.id = id;
	}

	/**
	 * Cascades the detach operation.
	 * 
	 * @param entityManager
	 *            the entity manager
	 * 
	 * @since 2.0.0
	 */
	public void cascadeDetach(EntityManagerImpl entityManager) {
		this.status = Status.DETACHED;

		ManagedInstance.LOG.debug("Cascading detach on {0}", this);

		for (final AssociationMapping<?, ?, ?> association : this.type.getAssociationsDetachable()) {

			// if the association a collection attribute then we will cascade to each element
			if (association instanceof PluralAssociationMapping) {
				final PluralAssociationMapping<?, ?, ?> mapping = (PluralAssociationMapping<?, ?, ?>) association;

				final Collection<?> collection;
				if (mapping.getAttribute().getCollectionType() == CollectionType.MAP) {
					collection = ((Map<?, ?>) mapping.get(this.instance)).values();
				}
				else {
					// extract the collection
					collection = (Collection<?>) mapping.get(this.instance);
				}

				// cascade to each element in the collection
				if (collection instanceof List) {
					final List<?> list = (List<?>) collection;
					for (int i = 0; i < list.size(); i++) {
						entityManager.detach(list.get(i));
					}
				}
				else {
					for (final Object element : collection) {
						entityManager.detach(element);
					}
				}
			}
			else {
				final SingularAssociationMapping<?, ?> mapping = (SingularAssociationMapping<?, ?>) association;
				final Object associate = mapping.get(this.instance);

				entityManager.detach(associate);
			}
		}
	}

	/**
	 * Cascades the persist operation.
	 * 
	 * @param entityManager
	 *            the entity manager
	 * @param processed
	 *            registry of processed entities
	 * @return true if an implicit flush is required, false otherwise
	 * 
	 * @since 2.0.0
	 */
	public boolean cascadePersist(EntityManagerImpl entityManager, ArrayList<Object> processed) {
		ManagedInstance.LOG.debug("Cascading persist on {0}", this);

		boolean requiresFlush = false;

		for (final AssociationMapping<?, ?, ?> association : this.type.getAssociationsPersistable()) {

			// if the association a collection attribute then we will cascade to each element
			if (association instanceof PluralAssociationMapping) {
				final PluralAssociationMapping<?, ?, ?> mapping = (PluralAssociationMapping<?, ?, ?>) association;

				switch (mapping.getAttribute().getCollectionType()) {
					case MAP:
						// extract the map
						final Map<?, ?> map = (Map<?, ?>) mapping.get(this.instance);

						// cascade to each element in the map
						for (final Object element : map.values()) {
							requiresFlush |= entityManager.persistImpl(element, processed);
						}

						break;
					case LIST:
						// extract the list
						final List<?> list = (List<?>) mapping.get(this.instance);

						// cascade to each element in the list
						for (int i = 0; i < list.size(); i++) {
							requiresFlush |= entityManager.persistImpl(list.get(i), processed);
						}

						break;
					default:
						// extract the collection
						final Collection<?> collection = (Collection<?>) mapping.get(this.instance);

						// cascade to each element in the collection
						if (collection instanceof List) {
							final List<?> castedList = (List<?>) collection;
							for (int i = 0; i < castedList.size(); i++) {
								requiresFlush |= entityManager.persistImpl(castedList.get(i), processed);
							}
						}
						else {
							for (final Object element : collection) {
								requiresFlush |= entityManager.persistImpl(element, processed);
							}
						}

						break;
				}
			}
			else {
				final SingularAssociationMapping<?, ?> mapping = (SingularAssociationMapping<?, ?>) association;
				final Object associate = mapping.get(this.instance);
				if (associate != null) {
					requiresFlush |= entityManager.persistImpl(associate, processed);
				}
			}
		}

		return requiresFlush;
	}

	/**
	 * Cascades the remove operation
	 * 
	 * @param entityManager
	 *            the entity manager
	 * 
	 * @since 2.0.0
	 */
	public void cascadeRemove(EntityManagerImpl entityManager) {
		ManagedInstance.LOG.debug("Cascading remove on {0}", this);

		for (final AssociationMapping<?, ?, ?> association : this.type.getAssociationsRemovable()) {

			// if the association a collection attribute then we will cascade to each element
			if (association instanceof PluralAssociationMapping) {
				final PluralAssociationMapping<?, ?, ?> mapping = (PluralAssociationMapping<?, ?, ?>) association;

				// extract the collection
				final Collection<?> collection;
				if (mapping.getAttribute().getCollectionType() == CollectionType.MAP) {
					collection = ((Map<?, ?>) mapping.get(this.instance)).values();
				}
				else {
					collection = (Collection<?>) mapping.get(this.instance);
				}

				// cascade to each element in the collection
				if (collection instanceof List) {
					final List<?> list = (List<?>) collection;
					for (int i = 0; i < list.size(); i++) {
						entityManager.remove(list.get(i));
					}
				}
				else {
					for (final Object element : collection) {
						entityManager.remove(element);
					}
				}
			}
			else {
				final SingularAssociationMapping<?, ?> mapping = (SingularAssociationMapping<?, ?>) association;
				final Object associate = mapping.get(this.instance);

				if (associate != null) {
					entityManager.remove(associate);
				}
			}
		}
	}

	/**
	 * Marks the instance as may have changed.
	 * 
	 * @since 2.0.0
	 */
	public void changed() {
		if (!this.changed && (this.collectionsChanged.size() == 0)) {
			this.session.setChanged(this);

		}

		if (!this.changed) {
			this.snapshot();
			this.changed = true;
		}
	}

	/**
	 * Checks that no association of the instance is transient
	 * 
	 * @since 2.0.0
	 */
	public void checkTransients() {
		for (final AssociationMapping<?, ?, ?> association : this.type.getAssociationsNotPersistable()) {
			association.checkTransient(this);
		}
	}

	/**
	 * Checks if the instance updated.
	 * <p>
	 * Only meaningful for external entities as their instances' are not enhanced.
	 * 
	 * @since 2.0.0
	 */
	public void checkUpdated() {
		// no snapshot, nothing to check
		if ((this.snapshot.size() == 0) || this.changed) {
			return;
		}

		if (this.checkUpdatedImpl()) {
			this.changed();
		}
	}

	private boolean checkUpdatedImpl() {
		// iterate over old values
		for (final Mapping<?, ?, ?> mapping : this.type.getMappingsSingular()) {
			final Object newValue = mapping.get(this.instance);
			final Object oldValue = this.snapshot.get(mapping);

			// if it is changed then mark as changed and bail out
			if (mapping.getAttribute().getPersistentAttributeType() == PersistentAttributeType.BASIC) {
				if (!ObjectUtils.equals(oldValue, newValue)) {
					return true;
				}

				continue;
			}

			if (oldValue != newValue) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Checks the optimistic lock for the instance.
	 * 
	 * @param connection
	 *            the connection
	 * @throws SQLException
	 *             thrown in case of an SQL error
	 * 
	 * @since 2.0.0
	 */
	public void checkVersion(Connection connection) throws SQLException {
		// no optimistic lock, nothing to check
		if (!this.optimisticLock) {
			return;
		}

		ManagedInstance.LOG.debug("Optimistic lock on {0}", this);

		final EntityTypeImpl<? super X> rootType = this.type.getRootType();
		final Object currentVersion = rootType.getVersionAttribute().get(this.instance);
		final Object expectedVersion = rootType.performSelectVersion(connection, this);

		if (ObjectUtils.notEqual(currentVersion, expectedVersion)) {
			ManagedInstance.LOG.debug("Optimistic lock matches on {0}: {1}", this, expectedVersion);

			throw new PersistenceException("Entity was updated by a different transaction " + this.instance);
		}
		else {
			ManagedInstance.LOG.debug("Optimistic lock mismatches on {0}, found {1}, expected {2}", this, expectedVersion);
		}
	}

	/**
	 * Enhances the collections of the managed instance.
	 * 
	 * @since 2.0.0
	 */
	public void enhanceCollections() {
		for (final PluralMapping<?, ?, ?> collection : this.type.getMappingsPlural()) {
			collection.enhance(this);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj == null) {
			return false;
		}

		final ManagedInstance<?> other = (ManagedInstance<?>) obj;

		return this.getId().equals(other.getId());
	}

	/**
	 * Fills the sequence / table generated values. The operation returns false if at least one entity needs to obtain identity from the
	 * database.
	 * 
	 * @return false if all OK, true if if at least one entity needs to obtain identity from the database
	 * 
	 * @since 2.0.0
	 */
	public boolean fillIdValues() {
		ManagedInstance.LOG.debug("Auto generating id values for {0}", this);

		return this.hasInitialId = this.fillValuesImpl();
	}

	private boolean fillValuesImpl() {
		final EntityTypeImpl<X> _type = this.type;

		if (_type.hasSingleIdAttribute()) {
			return this.type.getRootType().getIdMapping().fillValue(_type.getRootType(), this, this.instance);
		}
		else {
			for (final Pair<SingularMapping<? super X, ?>, AbstractAccessor> mapping : _type.getIdMappings()) {
				if (!mapping.getFirst().fillValue(_type.getRootType(), this, this.instance)) {
					return false;
				}
			}

			return true;
		}
	}

	/**
	 * Fires the callbacks.
	 * 
	 * @param type
	 *            the type of the callbacks
	 * 
	 * @since 2.0.0
	 */
	public void fireCallbacks(EntityListenerType type) {
		EntityListenerType typeToFire = type;

		if ((type == EntityListenerType.PRE_UPDATE) && (this.status == Status.NEW)) {
			typeToFire = EntityListenerType.PRE_PERSIST;
		}

		if ((type == EntityListenerType.POST_UPDATE) && (this.oldStatus == Status.NEW)) {
			typeToFire = EntityListenerType.POST_PERSIST;
		}

		this.type.fireCallbacks(this.instance, typeToFire);
	}

	/**
	 * Flushes the associations.
	 * 
	 * @param connection
	 *            the connection
	 * @param removals
	 *            true if the removals should be flushed and false for the additions
	 * @param force
	 *            true to force, effective only for insertions and for new entities.
	 * @throws SQLException
	 *             thrown if there is an underlying SQL Exception
	 * 
	 * @since 2.0.0
	 */
	public void flushAssociations(Connection connection, boolean removals, boolean force) throws SQLException {
		if (!removals || (this.status != Status.NEW)) {
			ManagedInstance.LOG.debug("Flushing associations for instance {0}", this);

			for (final JoinedMapping<?, ?, ?> collection : this.type.getMappingsJoined()) {
				collection.flush(connection, this, removals, force);
			}
		}
	}

	/**
	 * Returns the id of the instance.
	 * 
	 * @return the id of the instance
	 * 
	 * @since 2.0.0
	 */
	public ManagedId<? super X> getId() {
		if (this.id != null) {
			return this.id;
		}

		return this.id = this.type.getId(this.instance);
	}

	/**
	 * Returns the instance.
	 * 
	 * @return the instance
	 * @since 2.0.0
	 */
	public X getInstance() {
		return this.instance;
	}

	/**
	 * Returns the lock mode of the instance.
	 * 
	 * @return the lock mode
	 * 
	 * @since 2.0.0
	 */
	public LockModeType getLockMode() {
		return this.lockMode;
	}

	/**
	 * Returns the session.
	 * 
	 * @return the session
	 * @since 2.0.0
	 */
	public SessionImpl getSession() {
		return this.session;
	}

	/**
	 * Returns the status.
	 * 
	 * @return the status
	 * @since 2.0.0
	 */
	public Status getStatus() {
		return this.status;
	}

	/**
	 * Returns the type.
	 * 
	 * @return the type
	 * @since 2.0.0
	 */
	public EntityTypeImpl<X> getType() {
		return this.type;
	}

	/**
	 * Handles the entities that have been added.
	 * 
	 * @param entityManager
	 *            the entity manager
	 * 
	 * @since 2.0.0
	 */
	public void handleAdditions(EntityManagerImpl entityManager) {
		ManagedInstance.LOG.debug("Inspecting additions for instance {0}", this);

		for (int i = 0; i < this.collectionsChanged.size(); i++) {
			final PluralMapping<?, ?, ?> collection = this.collectionsChanged.get(i);

			if (collection instanceof PluralAssociationMapping) {
				((PluralAssociationMapping<?, ?, ?>) collection).persistAdditions(entityManager, this);
			}
		}
	}

	/**
	 * Handles the entities that have been orphaned.
	 * 
	 * @param entityManager
	 *            the entity manager
	 * 
	 * @since 2.0.0
	 */
	public void handleOrphans(EntityManagerImpl entityManager) {
		ManagedInstance.LOG.debug("Inspecting orphans for instance {0}", this);

		for (int i = 0; i < this.collectionsChanged.size(); i++) {
			final PluralMapping<?, ?, ?> collection = this.collectionsChanged.get(i);
			if (collection.isAssociation()) {
				((PluralAssociationMapping<?, ?, ?>) collection).removeOrphans(entityManager, this);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public int hashCode() {
		if (this.h != 0) {
			return this.h;
		}

		final Object id = this.getId();

		final int prime = 31;
		final int result = 1;

		this.h = (prime * result) + this.type.getRootType().getName().hashCode();

		return this.h = (prime * result) + id.hashCode();
	}

	/**
	 * Returns if the instance has initial id.
	 * 
	 * @return true if the instance has initial id, false otherwise
	 * 
	 * @since 2.0.0
	 */
	public boolean hasInitialId() {
		return this.hasInitialId;
	}

	/**
	 * Returns if the instance has self update.
	 * 
	 * @return true if the instance has self update, false otherwise
	 * 
	 * @since 2.0.0
	 */
	public boolean hasSelfUpdate() {
		if (!this.changed && (this.snapshot.size() == 0)) {
			return false;
		}

		if (this.collectionsChanged.size() > 0) {
			return true;
		}

		return this.checkUpdatedImpl();
	}

	/**
	 * Increments the version of the instance.
	 * 
	 * @param connection
	 *            the connection
	 * @param commit
	 *            true if version update should be committed immediately
	 * @throws SQLException
	 *             thrown in case of an underlying SQL error
	 * 
	 * @since 2.0.0
	 */
	public void incrementVersion(Connection connection, boolean commit) throws SQLException {
		if (!this.type.getRootType().hasVersionAttribute()) {
			return;
		}

		final EntityTypeImpl<? super X> rootType = this.type.getRootType();

		final BasicAttribute<? super X, ?> version = rootType.getVersionAttribute();

		switch (this.type.getVersionType()) {
			case SHORT:
				final short shortValue = (short) (((Number) version.get(this.instance)).shortValue() + 1);
				version.set(this.instance, shortValue);

				ManagedInstance.LOG.debug("Version upgraded instance: {0} - {1}", this, shortValue);

				break;
			case SHORT_OBJECT:
				final Short shortObjValue = version.get(this.instance) == null ? 1 : //
					Short.valueOf((short) (((Number) version.get(this.instance)).shortValue() + 1));
				version.set(this.instance, shortObjValue);

				ManagedInstance.LOG.debug("Version upgraded instance: {0} - {1}", this, shortObjValue);

				break;

			case INT:
				final int intValue = (((Number) version.get(this.instance)).intValue() + 1);
				version.set(this.instance, intValue);

				ManagedInstance.LOG.debug("Version upgraded instance: {0} - {1}", this, intValue);

				break;
			case INT_OBJECT:
				final Integer intObjValue = version.get(this.instance) == null ? 1 : //
					Integer.valueOf(((Number) version.get(this.instance)).intValue() + 1);
				version.set(this.instance, intObjValue);

				ManagedInstance.LOG.debug("Version upgraded instance: {0} - {1}", this, intObjValue);

				break;

			case LONG:
				final long longValue = (((Number) version.get(this.instance)).longValue() + 1);
				version.set(this.instance, longValue);

				ManagedInstance.LOG.debug("Version upgraded instance: {0} - {1}", this, longValue);

				break;
			case LONG_OBJECT:
				final Long longObjValue = version.get(this.instance) == null ? 1l : //
					Long.valueOf((((Number) version.get(this.instance)).longValue() + 1));
				version.set(this.instance, longObjValue);

				ManagedInstance.LOG.debug("Version upgraded instance: {0} - {1}", this, longObjValue);

				break;

			case TIMESTAMP:
				final Timestamp value = new Timestamp(System.currentTimeMillis());
				version.set(this.instance, value);

				ManagedInstance.LOG.debug("Version upgraded instance: {0} - {1}", this, value);
		}

		if (commit) {
			rootType.performVersionUpdate(connection, this);

			ManagedInstance.LOG.debug("Version committed instance: {0} - {1}", this);
		}
		else {
			this.changed = true;
		}
	}

	/**
	 * Returns if attribute name <code>attributeNaöe</code> has been loaded.
	 * 
	 * @param attributeName
	 *            the name of the attribute
	 * @return true if join is loaded, false otherwise
	 * 
	 * @since 2.0.0
	 */
	public boolean isJoinLoaded(String attributeName) {
		final Mapping<?, ?, ?> mapping = this.type.getRootMapping().getMapping(attributeName);

		if ((mapping instanceof BasicMapping) || (mapping instanceof EmbeddedMapping)) {
			return true;
		}

		if (((AssociationMapping<?, ?, ?>) mapping).isEager()) {
			return true;
		}

		return this.joinsLoaded.contains(mapping.getPath());
	}

	/**
	 * Returns if the instance is loading.
	 * 
	 * @return true if the instance is loading, false otherwise
	 * 
	 * @since 2.0.0
	 */
	public boolean isLoading() {
		return this.loading;
	}

	/**
	 * Returns if the instance is loading from the cache.
	 * 
	 * @return true if the instance is loading from the cache, false otherwise
	 * 
	 * @since 2.0.0
	 */
	public boolean isLoadingFromCache() {
		return this.loadingFromCache;
	}

	/**
	 * Returns if the instance is refreshing.
	 * 
	 * @return true if the instance is refreshing, false otherwise
	 * 
	 * @since 2.0.0
	 */
	public boolean isRefreshing() {
		return this.refreshing;
	}

	/**
	 * Merges the instance state with the <code>entity</code>.
	 * 
	 * @param entityManager
	 *            the entity manager
	 * @param entity
	 *            the entity to merge
	 * @param requiresFlush
	 *            if an implicit flush is required
	 * @param processed
	 *            registry of processed entities
	 * 
	 * @since 2.0.0
	 */
	public void mergeWith(EntityManagerImpl entityManager, X entity, MutableBoolean requiresFlush, IdentityHashMap<Object, Object> processed) {
		this.snapshot();

		for (final BasicMapping<?, ?> mapping : this.type.getBasicMappings()) {
			mapping.set(this.instance, mapping.get(entity));
		}

		for (final AssociationMapping<?, ?, ?> association : this.type.getAssociations()) {
			association.mergeWith(entityManager, this, entity, requiresFlush, processed);
		}

		this.checkUpdated();
	}

	/**
	 * Processes the associations.
	 * 
	 * @since 2.0.0
	 */
	public void processJoinedMappings() {
		ManagedInstance.LOG.debug("Post processing associations for instance {0}", this);

		final HashSet<String> _joinsLoaded = this.joinsLoaded;

		for (final PluralMapping<?, ?, ?> mapping : this.type.getMappingsPlural()) {
			final HashSet<String> joinsLoaded2 = _joinsLoaded;
			if (!joinsLoaded2.contains(mapping.getPath())) {
				if (mapping.isEager()) {
					mapping.load(this);
				}
				else {
					mapping.setLazy(this);
				}
			}
		}

		final X _instance = this.instance;
		final EntityManagerImpl entityManager = this.session.getEntityManager();

		for (final SingularAssociationMapping<?, ?> mapping : this.type.getAssociationsSingular()) {
			if (mapping.isEager()) {
				if (!_joinsLoaded.contains(mapping.getPath())) {
					mapping.initialize(this);
				}
				else {
					final Object associate = mapping.get(_instance);
					if (associate instanceof EnhancedInstance) {
						final EnhancedInstance enhancedInstance = (EnhancedInstance) associate;
						if (!enhancedInstance.__enhanced__$$__isInitialized()) {
							final ManagedInstance<?> associateManagedInstance = enhancedInstance.__enhanced__$$__getManagedInstance();
							entityManager.find(associateManagedInstance.getType().getJavaType(), associateManagedInstance.getId().getId());
						}
					}
				}
			}
		}
	}

	/**
	 * Refreshes the instance from the database.
	 * 
	 * @param entityManager
	 *            the entity manager
	 * @param connection
	 *            the connection
	 * @param lockMode
	 *            the lock mode
	 * @param processed
	 *            the set of processed instances
	 * 
	 * @since 2.0.0
	 */
	public void refresh(EntityManagerImpl entityManager, Connection connection, LockModeType lockMode, Set<Object> processed) {
		ManagedInstance.LOG.debug("Refeshing instance {0}", this);

		this.type.performRefresh(connection, this, lockMode, processed);

		for (final AssociationMapping<?, ?, ?> association : this.type.getAssociations()) {
			association.refresh(this, processed);
		}
	}

	/**
	 * Resets the change status of the instance.
	 * 
	 * @since 2.0.0
	 */
	public void reset() {
		ManagedInstance.LOG.trace("Reset instance {0}", this);

		this.collectionsChanged.clear();

		this.changed = false;

		this.snapshot.clear();
		this.snapshot();
	}

	/**
	 * Sets the cache instance
	 * 
	 * @param cacheInstance
	 *            the cache instance
	 * 
	 * @since 2.0.0
	 */
	public void setCache(CacheInstance cacheInstance) {
		this.cacheInstance = cacheInstance;

		this.loading = true;
		this.loadingFromCache = true;
	}

	/**
	 * Marks the plural association as changed.
	 * 
	 * @param association
	 *            the association that has changed
	 * 
	 * @since 2.0.0
	 */
	public void setChanged(PluralMapping<?, ?, ?> association) {
		if ((this.collectionsChanged.size() == 0) && !this.changed) {
			this.session.setChanged(this);
		}

		this.collectionsChanged.add(association);
	}

	/**
	 * Sets the association as loaded.
	 * 
	 * @param mapping
	 *            the association
	 * 
	 * @since 2.0.0
	 */
	public void setJoinLoaded(JoinedMapping<?, ?, ?> mapping) {
		this.joinsLoaded.add(mapping.getPath());
	}

	/**
	 * Marks the instance as loading.
	 * 
	 * @param loading
	 *            loading to set
	 * 
	 * @since 2.0.0
	 */
	public void setLoading(boolean loading) {
		this.loading = loading;
	}

	/**
	 * Marks the instance as loading loading from the cahce.
	 * 
	 * @param loadingFromCache
	 *            loading to set
	 * 
	 * @since 2.0.0
	 */
	public void setLoadingFromCache(boolean loadingFromCache) {
		this.loadingFromCache = loadingFromCache;
	}

	/**
	 * Sets the optimistic lock on
	 * 
	 * @since 2.0.0
	 */
	public void setOptimisticLock() {
		ManagedInstance.LOG.debug("Optimistic lock enabled for instance {0}", this);

		this.optimisticLock = true;
	}

	/**
	 * Marks the instance as refreshing.
	 * 
	 * @param refreshing
	 *            refreshing to set
	 * 
	 * @since 2.0.0
	 */
	public void setRefreshing(boolean refreshing) {
		this.refreshing = refreshing;
	}

	/**
	 * Sets the status.
	 * 
	 * @param status
	 *            the status to set
	 * @since 2.0.0
	 */
	public void setStatus(Status status) {
		this.oldStatus = this.status;

		if (status != this.status) {
			ManagedInstance.LOG.debug("Instance status changing for {0}: {1} -> {2}", this, this.status, status);

			this.status = status;
		}
	}

	/**
	 * Creates a snapshot of the entity.
	 * 
	 * @since 2.0.0
	 */
	private void snapshot() {
		ManagedInstance.LOG.trace("Snapshot generated for instance {0}", this);

		if (this.snapshot.size() == 0) {
			for (final Mapping<?, ?, ?> mapping : this.type.getMappingsSingular()) {
				this.snapshot.put(mapping, mapping.get(this.instance));
			}
		}
	}

	/**
	 * Sorts the list associations.
	 * 
	 * @since 2.0.0
	 */
	public void sortLists() {
		for (final PluralMapping<?, ?, ?> mapping : this.type.getMappingsPluralSorted()) {
			mapping.sortList(this.instance);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String toString() {
		return "ManagedInstance [session=" + this.session //
			+ ", type=" + this.type.getName() //
			+ ", status=" + this.status //
			+ ", id=" + (this.id != null ? this.id.getId() : null) + "]";
	}

	/**
	 * Tries to load the collection from the cache.
	 * 
	 * @param mapping
	 *            the mapping of the collection
	 * @return true if the collection was found in the cache, false otherwise
	 * 
	 * @since 2.0.0
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public boolean tryLoadFromCache(PluralAssociationMapping<?, ?, ?> mapping) {
		if (this.cacheInstance != null) {
			final Collection children = this.cacheInstance.getCollection(this, mapping);
			if (children != null) {
				mapping.setCollection(this, children);

				return true;
			}
		}

		return false;
	}

	/**
	 * Updates the collection cache of the managed instance for the mapping
	 * 
	 * @param mapping
	 *            the mapping to update
	 * 
	 * @since 2.0.0
	 */
	public void updateCollectionCache(PluralMapping<?, ?, ?> mapping) {
		if (this.cacheInstance != null) {
			final EntityManagerFactoryImpl emf = this.session.getEntityManager().getEntityManagerFactory();
			final CacheImpl cache = emf.getCache();

			if (this.cacheInstance.updateCollection(emf.getMetamodel(), cache, mapping, this.instance)) {
				cache.put(this);
			}
		}
	}
}
