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

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.persistence.CacheRetrieveMode;
import javax.persistence.CacheStoreMode;

import org.batoo.jpa.core.impl.collections.ManagedCollection;
import org.batoo.jpa.core.impl.instance.ManagedInstance;
import org.batoo.jpa.core.impl.manager.EntityManagerImpl;
import org.batoo.jpa.core.impl.model.MetamodelImpl;
import org.batoo.jpa.core.impl.model.mapping.BasicMapping;
import org.batoo.jpa.core.impl.model.mapping.PluralMapping;
import org.batoo.jpa.core.impl.model.type.EntityTypeImpl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * The serializable cache information of the instance.
 * 
 * @author hceylan
 * @since $version
 */
public class CacheInstance implements Serializable {

	public static final CacheInstance REMOVED = new CacheInstance();

	private final Map<String, Object> basicMappings;
	private final Map<String, List<CacheReference>> pluralMappings;

	private String clazz;

	private Object id;

	/**
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private CacheInstance() {
		super();

		this.basicMappings = null;
		this.pluralMappings = null;
	}

	/**
	 * @param cache
	 *            the cache
	 * @param managedInstance
	 *            the managed instance
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public CacheInstance(CacheImpl cache, ManagedInstance<?> managedInstance) {
		super();

		this.clazz = managedInstance.getType().getJavaType().getName();
		this.id = managedInstance.getId().getId();

		final EntityTypeImpl<?> type = managedInstance.getType();
		final Object instance = managedInstance.getInstance();
		final MetamodelImpl metamodel = managedInstance.getSession().getEntityManager().getMetamodel();

		this.basicMappings = Maps.newHashMap();
		for (final BasicMapping<?, ?> mapping : type.getBasicMappings()) {
			this.basicMappings.put(mapping.getPath(), mapping.get(instance));
		}

		this.pluralMappings = Maps.newHashMap();
		for (final PluralMapping<?, ?, ?> mapping : type.getMappingsPlural()) {
			this.updateCollection(metamodel, cache, mapping, instance);
		}

		managedInstance.setCache(this);
	}

	/**
	 * Copies the instance state to the given managed instance.
	 * 
	 * @param cache
	 *            the cache
	 * @param managedInstance
	 *            the managed instance to copy to
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void copyTo(CacheImpl cache, ManagedInstance<?> managedInstance) {
		final EntityTypeImpl<?> type = managedInstance.getType();
		final Object instance = managedInstance.getInstance();

		for (final BasicMapping<?, ?> mapping : type.getBasicMappings()) {
			mapping.set(instance, this.basicMappings.get(mapping.getPath()));
		}

		final EntityManagerImpl em = managedInstance.getSession().getEntityManager();
		final MetamodelImpl metamodel = em.getMetamodel();

		for (final PluralMapping<?, ?, ?> mapping : type.getMappingsPlural()) {
			// if the mapping is lazy then set lazy and skip
			if (!mapping.isEager()) {
				mapping.setLazy(managedInstance);
				continue;
			}

			// if this is not a cachable type then skip
			if (mapping.getType() instanceof EntityTypeImpl) {
				final EntityTypeImpl<?> childType = (EntityTypeImpl<?>) mapping.getType();

				if (cache.getCacheRetrieveMode(childType.getRootType()) == CacheRetrieveMode.BYPASS) {
					continue;
				}
			}

			final List<CacheReference> children = this.pluralMappings.get(mapping.getPath());

			// if not null then fetch the references
			if (children == null) {
				mapping.setLazy(managedInstance);
			}

			final Collection childEntities = Lists.newArrayList();
			for (final CacheReference child : children) {
				final EntityTypeImpl<?> childType = metamodel.entity(child.getType());
				childEntities.add(em.find(childType.getJavaType(), child.getId()));
			}

			mapping.setCollection(managedInstance, childEntities);
		}

		managedInstance.setCache(this);
	}

	/**
	 * @param entityManager
	 *            the entity manager
	 * @param mapping
	 *            the mapping
	 * @return the collection of children or null
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Collection<?> getCollection(EntityManagerImpl entityManager, PluralMapping<?, ?, ?> mapping) {
		final List<CacheReference> children = this.pluralMappings.get(mapping.getPath());

		// if not null then fetch the references
		if (children == null) {
			return null;
		}

		final MetamodelImpl metamodel = entityManager.getMetamodel();

		final Collection childEntities = Lists.newArrayList();
		for (final CacheReference child : children) {
			final EntityTypeImpl<?> childType = metamodel.entity(child.getType());
			childEntities.add(entityManager.find(childType.getJavaType(), child.getId()));
		}

		return childEntities;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String toString() {
		return "CacheInstance [clazz=" + this.clazz + ", id=" + this.id + ", basicMappings=" + this.basicMappings + ", pluralMappings=" + this.pluralMappings
			+ "]";
	}

	/**
	 * Updates the cache instance's collection mapping
	 * 
	 * @param metamodel
	 *            the metamodel
	 * @param cache
	 *            the cache
	 * @param mapping
	 *            the mapping
	 * @param instance
	 *            the instance
	 * @return true if cache update is necessar, false otherwise
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public boolean updateCollection(MetamodelImpl metamodel, CacheImpl cache, PluralMapping<?, ?, ?> mapping, Object instance) {
		// if this is not a cachable type then skip
		if (mapping.getType() instanceof EntityTypeImpl) {
			final EntityTypeImpl<?> childType = (EntityTypeImpl<?>) mapping.getType();

			if (cache.getCacheStoreMode(childType.getRootType()) == CacheStoreMode.BYPASS) {
				return false;
			}
		}

		// prepare the reference list
		final List<CacheReference> references = Lists.newArrayList();

		// if it is null then set to null and then set to empty reference list and continue
		final ManagedCollection<?> collection = (ManagedCollection<?>) mapping.get(instance);
		if (collection == null) {
			this.pluralMappings.put(mapping.getPath(), references);
			return true;
		}

		// if the collection is not initialized then continue with no reference, so it means the collection is in lazy state
		if (!collection.isInitialized()) {
			return true;
		}

		// populate the reference list and put to cache
		for (final Object child : collection.getDelegate()) {
			references.add(new CacheReference(metamodel, child));
		}

		this.pluralMappings.put(mapping.getPath(), references);

		return true;
	}
}
