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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.LockModeType;
import javax.persistence.metamodel.Attribute.PersistentAttributeType;
import javax.persistence.metamodel.PluralAttribute.CollectionType;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.mutable.MutableBoolean;
import org.batoo.common.log.BLogger;
import org.batoo.common.log.BLoggerFactory;
import org.batoo.common.reflect.AbstractAccessor;
import org.batoo.jpa.core.impl.manager.EntityManagerImpl;
import org.batoo.jpa.core.impl.manager.SessionImpl;
import org.batoo.jpa.core.impl.model.EntityTypeImpl;
import org.batoo.jpa.core.impl.model.attribute.BasicAttribute;
import org.batoo.jpa.core.impl.model.mapping.AbstractMapping;
import org.batoo.jpa.core.impl.model.mapping.AssociationMappingImpl;
import org.batoo.jpa.core.impl.model.mapping.BasicMappingImpl;
import org.batoo.jpa.core.impl.model.mapping.EmbeddedMappingImpl;
import org.batoo.jpa.core.impl.model.mapping.JoinedMapping;
import org.batoo.jpa.core.impl.model.mapping.PluralAssociationMappingImpl;
import org.batoo.jpa.core.impl.model.mapping.PluralMappingEx;
import org.batoo.jpa.core.impl.model.mapping.SingularAssociationMappingImpl;
import org.batoo.jpa.core.impl.model.mapping.SingularMappingEx;
import org.batoo.jpa.core.util.Pair;
import org.batoo.jpa.jdbc.mapping.SingularMapping;
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
	private LockModeType lockMode;

	private final HashMap<AbstractMapping<?, ?, ?>, Object> snapshot = Maps.newHashMap();
	private final HashSet<String> joinsLoaded;
	private final ArrayList<PluralMappingEx<?, ?, ?>> collectionsChanged;

	private boolean loading;
	private boolean loadingFromCache;
	private boolean refreshing;
	private boolean changed;

	private boolean hasInitialId;
	private ManagedId<? super X> id;
	private int h;

	private boolean prePersistCalled;
	private boolean preRemoveCalled;

	private Object oldVersion;

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

		for (final AssociationMappingImpl<?, ?, ?> association : this.type.getAssociationsDetachable()) {

			// if the association a collection attribute then we will cascade to each element
			if (association instanceof PluralAssociationMappingImpl) {
				final PluralAssociationMappingImpl<?, ?, ?> mapping = (PluralAssociationMappingImpl<?, ?, ?>) association;

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
				final SingularAssociationMappingImpl<?, ?> mapping = (SingularAssociationMappingImpl<?, ?>) association;
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
	 * @param instances
	 *            the managed instances
	 * @return true if an implicit flush is required, false otherwise
	 * 
	 * @since 2.0.0
	 */
	public boolean cascadePersist(EntityManagerImpl entityManager, ArrayList<Object> processed, LinkedList<ManagedInstance<?>> instances) {
		ManagedInstance.LOG.debug("Cascading persist on {0}", this);

		boolean requiresFlush = false;

		for (final AssociationMappingImpl<?, ?, ?> association : this.type.getAssociationsPersistable()) {

			// if the association a collection attribute then we will cascade to each element
			if (association instanceof PluralAssociationMappingImpl) {
				final PluralAssociationMappingImpl<?, ?, ?> mapping = (PluralAssociationMappingImpl<?, ?, ?>) association;

				switch (mapping.getAttribute().getCollectionType()) {
					case MAP:
						// extract the map
						final Map<?, ?> map = (Map<?, ?>) mapping.get(this.instance);

						// cascade to each element in the map
						for (final Object element : map.values()) {
							requiresFlush |= entityManager.persistImpl(element, processed, instances);
						}

						break;
					case LIST:
						// extract the list
						final List<?> list = (List<?>) mapping.get(this.instance);

						// cascade to each element in the list
						for (int i = 0; i < list.size(); i++) {
							requiresFlush |= entityManager.persistImpl(list.get(i), processed, instances);
						}

						break;
					default:
						// extract the collection
						final Collection<?> collection = (Collection<?>) mapping.get(this.instance);

						// cascade to each element in the collection
						if (collection instanceof List) {
							final List<?> castedList = (List<?>) collection;
							for (int i = 0; i < castedList.size(); i++) {
								requiresFlush |= entityManager.persistImpl(castedList.get(i), processed, instances);
							}
						}
						else {
							for (final Object element : collection) {
								requiresFlush |= entityManager.persistImpl(element, processed, instances);
							}
						}

						break;
				}
			}
			else {
				final SingularAssociationMappingImpl<?, ?> mapping = (SingularAssociationMappingImpl<?, ?>) association;
				final Object associate = mapping.get(this.instance);
				if (associate != null) {
					requiresFlush |= entityManager.persistImpl(associate, processed, instances);
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
	 * @param processed
	 *            registry of processed entities
	 * @param instances
	 *            the managed instances
	 * 
	 * @since 2.0.0
	 */
	public void cascadeRemove(EntityManagerImpl entityManager, ArrayList<Object> processed, LinkedList<ManagedInstance<?>> instances) {
		ManagedInstance.LOG.debug("Cascading remove on {0}", this);

		for (final AssociationMappingImpl<?, ?, ?> association : this.type.getAssociationsRemovable()) {

			// if the association a collection attribute then we will cascade to each element
			if (association instanceof PluralAssociationMappingImpl) {
				final PluralAssociationMappingImpl<?, ?, ?> mapping = (PluralAssociationMappingImpl<?, ?, ?>) association;

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
						entityManager.removeImpl(list.get(i), processed, instances);
					}
				}
				else {
					for (final Object element : collection) {
						entityManager.removeImpl(element, processed, instances);
					}
				}
			}
			else {
				final SingularAssociationMappingImpl<?, ?> mapping = (SingularAssociationMappingImpl<?, ?>) association;
				final Object associate = mapping.get(this.instance);

				if (associate != null) {
					entityManager.removeImpl(associate, processed, instances);
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
		for (final AssociationMappingImpl<?, ?, ?> association : this.type.getAssociationsNotPersistable()) {
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
		for (final AbstractMapping<?, ?, ?> mapping : this.type.getMappingsSingular()) {
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
	 * Enhances the collections of the managed instance.
	 * 
	 * @since 2.0.0
	 */
	public void enhanceCollections() {
		for (final PluralMappingEx<?, ?, ?> collection : this.type.getMappingsPlural()) {
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
			for (final Pair<SingularMapping<?, ?>, AbstractAccessor> mapping : _type.getIdMappings()) {
				if (!((SingularMappingEx<?, ?>) mapping.getFirst()).fillValue(_type.getRootType(), this, this.instance)) {
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

		// safeguard single invocation for PrePersists
		if (typeToFire == EntityListenerType.PRE_PERSIST) {
			if (!this.prePersistCalled) {
				this.prePersistCalled = true;

				this.type.fireCallbacks(this.instance, typeToFire);
			}
		}
		else if (typeToFire == EntityListenerType.PRE_REMOVE) {
			if (!this.preRemoveCalled) {
				this.preRemoveCalled = true;

				this.type.fireCallbacks(this.instance, typeToFire);
			}
		}
		else {
			this.type.fireCallbacks(this.instance, typeToFire);
		}
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
	 * Returns the old version of the instance.
	 * 
	 * @return the old version of the instance
	 * 
	 * @since $version
	 */
	public Object getOldVersion() {
		return this.oldVersion;
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
			final PluralMappingEx<?, ?, ?> collection = this.collectionsChanged.get(i);

			if (collection instanceof PluralAssociationMappingImpl) {
				((PluralAssociationMappingImpl<?, ?, ?>) collection).persistAdditions(entityManager, this);
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
			final PluralMappingEx<?, ?, ?> collection = this.collectionsChanged.get(i);
			if (collection.isAssociation()) {
				((PluralAssociationMappingImpl<?, ?, ?>) collection).removeOrphans(entityManager, this);
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

		if (this.oldVersion == null) {
			switch (this.type.getVersionType()) {
				case SHORT:
					final short shortValue = (((Number) version.get(this.instance)).shortValue());
					this.oldVersion = shortValue;
					version.set(this.instance, shortValue + 1);

					ManagedInstance.LOG.debug("Version upgraded instance: {0} - {1}", this, shortValue);

					break;
				case SHORT_OBJECT:
					final Short shortObjValue = version.get(this.instance) == null ? 0 : //
						Short.valueOf((((Number) version.get(this.instance)).shortValue()));
					this.oldVersion = shortObjValue;

					version.set(this.instance, shortObjValue + 1);

					ManagedInstance.LOG.debug("Version upgraded instance: {0} - {1}", this, shortObjValue);

					break;

				case INT:
					final int intValue = (((Number) version.get(this.instance)).intValue());
					this.oldVersion = intValue;

					version.set(this.instance, intValue + 1);

					ManagedInstance.LOG.debug("Version upgraded instance: {0} - {1}", this, intValue);

					break;
				case INT_OBJECT:
					final Integer intObjValue = version.get(this.instance) == null ? 0 : //
						Integer.valueOf(((Number) version.get(this.instance)).intValue());
					this.oldVersion = intObjValue;

					version.set(this.instance, intObjValue + 1);

					ManagedInstance.LOG.debug("Version upgraded instance: {0} - {1}", this, intObjValue);

					break;
				case LONG:
					final long longValue = (((Number) version.get(this.instance)).longValue());
					this.oldVersion = longValue;

					version.set(this.instance, longValue + 1);

					ManagedInstance.LOG.debug("Version upgraded instance: {0} - {1}", this, longValue);

					break;
				case LONG_OBJECT:
					final Long longObjValue = version.get(this.instance) == null ? 0l : //
						Long.valueOf((((Number) version.get(this.instance)).longValue()));
					this.oldVersion = longObjValue;

					version.set(this.instance, longObjValue + 1);

					ManagedInstance.LOG.debug("Version upgraded instance: {0} - {1}", this, longObjValue);

					break;

				case TIMESTAMP:
					final Timestamp value = new Timestamp(System.currentTimeMillis());
					this.oldVersion = version.get(this.instance);

					version.set(this.instance, value);

					ManagedInstance.LOG.debug("Version upgraded instance: {0} - {1}", this, value);
			}
		}

		if (commit) {
			final Object newVersion = version.get(this.instance);
			rootType.performVersionUpdate(connection, this, this.oldVersion, newVersion);

			ManagedInstance.LOG.debug("Version committed instance: {0} - {1} -> {2}", this, this.oldVersion, newVersion);

			this.oldVersion = null;
		}
		else {
			this.changed();
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
		final AbstractMapping<?, ?, ?> mapping = this.type.getRootMapping().getMapping(attributeName);

		if ((mapping instanceof BasicMappingImpl) || (mapping instanceof EmbeddedMappingImpl)) {
			return true;
		}

		if (((AssociationMappingImpl<?, ?, ?>) mapping).isEager()) {
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
	 * @param instances
	 *            the persisted instances
	 * 
	 * @since 2.0.0
	 */
	public void mergeWith(EntityManagerImpl entityManager, X entity, MutableBoolean requiresFlush, IdentityHashMap<Object, Object> processed,
		LinkedList<ManagedInstance<?>> instances) {
		this.snapshot();

		for (final BasicMappingImpl<?, ?> mapping : this.type.getBasicMappings()) {
			mapping.set(this.instance, mapping.get(entity));
		}

		for (final AssociationMappingImpl<?, ?, ?> association : this.type.getAssociations()) {
			association.mergeWith(entityManager, this, entity, requiresFlush, processed, instances);
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

		for (final PluralMappingEx<?, ?, ?> mapping : this.type.getMappingsPlural()) {
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

		for (final SingularAssociationMappingImpl<?, ?> mapping : this.type.getAssociationsSingular()) {
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

		for (final AssociationMappingImpl<?, ?, ?> association : this.type.getAssociations()) {
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
	 * Marks the plural association as changed.
	 * 
	 * @param association
	 *            the association that has changed
	 * 
	 * @since 2.0.0
	 */
	public void setChanged(PluralMappingEx<?, ?, ?> association) {
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
			for (final AbstractMapping<?, ?, ?> mapping : this.type.getMappingsSingular()) {
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
		for (final PluralMappingEx<?, ?, ?> mapping : this.type.getMappingsPluralSorted()) {
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
}
