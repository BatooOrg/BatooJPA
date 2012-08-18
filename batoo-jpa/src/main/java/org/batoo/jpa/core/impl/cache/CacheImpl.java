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
package org.batoo.jpa.core.impl.cache;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.Cache;
import javax.persistence.CacheRetrieveMode;
import javax.persistence.CacheStoreMode;
import javax.persistence.SharedCacheMode;

import org.batoo.jpa.common.log.BLogger;
import org.batoo.jpa.common.log.BLoggerFactory;
import org.batoo.jpa.core.impl.instance.ManagedInstance;
import org.batoo.jpa.core.impl.instance.Status;
import org.batoo.jpa.core.impl.model.MetamodelImpl;
import org.batoo.jpa.core.impl.model.type.EntityTypeImpl;

import com.google.common.collect.Maps;

/**
 * The implementation of {@link Cache}.
 * 
 * @author hceylan
 * @since $version
 */
public class CacheImpl implements Cache {

	private static final BLogger LOG = BLoggerFactory.getLogger(CacheImpl.class);

	private static final ThreadLocal<CacheRetrieveMode> cacheRetrieveMode = new ThreadLocal<CacheRetrieveMode>();
	private static final ThreadLocal<CacheStoreMode> cacheStoreMode = new ThreadLocal<CacheStoreMode>();

	private final MetamodelImpl metamodel;
	private final SharedCacheMode cacheMode;
	private final boolean on;

	private final HashMap<Class<?>, Map<Object, CacheInstance>> cacheDelegate = Maps.newHashMap();
	private final HashMap<Class<?>, CacheStats> statsDelegate = Maps.newHashMap();

	private CacheStats stats;

	/**
	 * @param metamodel
	 *            the metamodel
	 * @param cacheMode
	 *            the cache mode
	 * @since $version
	 * @author hceylan
	 */
	public CacheImpl(MetamodelImpl metamodel, SharedCacheMode cacheMode) {
		super();

		this.metamodel = metamodel;
		this.cacheMode = cacheMode;

		switch (this.cacheMode) {
			case ALL:
			case DISABLE_SELECTIVE:
			case ENABLE_SELECTIVE:
				this.on = true;
				break;
			default:
				this.on = false;
		}

		this.reset();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean contains(Class<?> cls, Object primaryKey) {
		final EntityTypeImpl<?> entity = this.metamodel.getEntity(cls);

		return this.cacheDelegate.containsKey(entity + "_" + primaryKey);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void evict(Class<?> clazz) {
		CacheImpl.LOG.info("Clearing the entity cache completely for {0}...", clazz);

		this.getEntityMap(clazz).clear();
		this.statsDelegate.remove(clazz);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void evict(Class<?> clazz, Object primaryKey) {
		this.getStats(clazz);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void evictAll() {
		CacheImpl.LOG.info("Clearing the cache completely...");

		this.cacheDelegate.clear();
		this.statsDelegate.clear();

		this.reset();
	}

	/**
	 * Returns the instance with the key
	 * 
	 * @param clazz
	 *            the class of the instance
	 * @param primaryKey
	 *            the primary key of the instance
	 * @return the cached instance
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public CacheInstance get(Class<?> clazz, Object primaryKey) {
		final CacheInstance cacheInstance = this.getEntityMap(clazz).get(primaryKey);

		if (cacheInstance != null) {
			this.stats.hit(primaryKey);
			this.getStats(clazz).hit(primaryKey);
		}
		else {
			this.stats.miss(primaryKey);
			this.getStats(clazz).miss(primaryKey);
		}

		return cacheInstance;
	}

	/**
	 * Returns the cache mode of the cache.
	 * 
	 * @return the cache mode of the cache
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public SharedCacheMode getCacheMode() {
		return this.cacheMode;
	}

	/**
	 * Returns the cache retrieve mode for the entity
	 * 
	 * @param type
	 *            the type
	 * @return the cache retrieve mode
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public CacheRetrieveMode getCacheRetrieveMode(EntityTypeImpl<?> type) {
		if (!this.on) {
			return CacheRetrieveMode.BYPASS;
		}

		final CacheRetrieveMode mode = CacheImpl.cacheRetrieveMode.get();

		return mode != null ? mode : type.isCachable() ? CacheRetrieveMode.USE : CacheRetrieveMode.BYPASS;
	}

	/**
	 * Returns the cache store mode for the entity
	 * 
	 * @param type
	 *            the type
	 * @return the cache store mode
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public CacheStoreMode getCacheStoreMode(EntityTypeImpl<?> type) {
		if (!this.on) {
			return CacheStoreMode.BYPASS;
		}

		final CacheStoreMode mode = CacheImpl.cacheStoreMode.get();

		return mode != null ? mode : type.isCachable() ? CacheStoreMode.USE : CacheStoreMode.BYPASS;
	}

	private Map<Object, CacheInstance> getEntityMap(Class<?> clazz) {
		Map<Object, CacheInstance> entityMap = this.cacheDelegate.get(clazz);

		if (entityMap == null) {
			this.lockEntityMap(clazz);
			try {
				entityMap = this.cacheDelegate.get(clazz);

				if (entityMap == null) {
					entityMap = Maps.newHashMap();
					this.cacheDelegate.put(clazz, entityMap);
				}
			}
			finally {
				this.unlockEntityMap(clazz);
			}
		}

		return entityMap;
	}

	/**
	 * Returns the global cache stats.
	 * 
	 * @return the global cache stats
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public CacheStats getStats() {
		return this.stats;
	}

	/**
	 * Returns the stats for the class.
	 * 
	 * @param clazz
	 *            the class
	 * @return the stats
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public CacheStats getStats(Class<?> clazz) {
		CacheStats cacheStats = this.statsDelegate.get(clazz);
		if (cacheStats == null) {
			synchronized (this) {
				cacheStats = this.statsDelegate.get(clazz);

				if (cacheStats == null) {
					cacheStats = new CacheStats(clazz.getName());
					this.statsDelegate.put(clazz, cacheStats);
				}
			}
		}

		return cacheStats;
	}

	/**
	 * Returns if the cache is on.
	 * 
	 * @return true if the cache is on, false otherwise
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public boolean isOn() {
		return this.on;
	}

	/**
	 * @param clazz
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private void lockEntityMap(Class<?> clazz) {
		// TODO Auto-generated method stub

	}

	/**
	 * Puts the instance to cache.
	 * 
	 * @param instance
	 *            the instance to put
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void put(ManagedInstance<?> instance) {
		final Class<?> clazz = instance.getType().getRootType().getJavaType();
		final Object key = instance.getId().getId();

		if (instance.getStatus() == Status.REMOVED) {
			this.getEntityMap(clazz).remove(key);

			this.stats.evict(key);
			this.getStats(clazz).evict(key);
		}
		else {
			this.getEntityMap(clazz).put(key, new CacheInstance(this, instance));

			this.stats.put(key);
			this.getStats(clazz).put(key);
		}
	}

	private void reset() {
		this.stats = new CacheStats("Global");
	}

	/**
	 * Sets the cache retrieve mode for the entity
	 * 
	 * @param mode
	 *            the mode or null to set
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void setCacheRetrieveMode(CacheRetrieveMode mode) {
		if (!this.on) {
			CacheImpl.LOG.warn("Ignoring cache mode hint as the shared cache is disabled: {0}", mode);

			return;
		}

		CacheImpl.cacheRetrieveMode.set(mode);
	}

	/**
	 * Sets the cache store mode for the entity
	 * 
	 * @param mode
	 *            the mode or null to set
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void setCacheStoreMode(CacheStoreMode mode) {
		if (!this.on) {
			CacheImpl.LOG.warn("Ignoring cache mode hint as the shared cache is disabled: {0}", mode);

			return;
		}

		CacheImpl.cacheStoreMode.set(mode);
	}

	/**
	 * @param clazz
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private void unlockEntityMap(Class<?> clazz) {
		// TODO Auto-generated method stub
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <T> T unwrap(Class<T> cls) {
		return null;
	}
}
