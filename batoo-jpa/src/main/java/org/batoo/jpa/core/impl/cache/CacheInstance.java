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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.persistence.CacheStoreMode;
import javax.persistence.NoResultException;
import javax.persistence.metamodel.Attribute.PersistentAttributeType;

import org.batoo.jpa.core.impl.collections.ManagedCollection;
import org.batoo.jpa.core.impl.instance.EnhancedInstance;
import org.batoo.jpa.core.impl.instance.ManagedInstance;
import org.batoo.jpa.core.impl.manager.EntityManagerImpl;
import org.batoo.jpa.core.impl.model.EntityTypeImpl;
import org.batoo.jpa.core.impl.model.MetamodelImpl;
import org.batoo.jpa.core.impl.model.mapping.AssociationMappingImpl;
import org.batoo.jpa.core.impl.model.mapping.BasicMappingImpl;
import org.batoo.jpa.core.impl.model.mapping.PluralAssociationMappingImpl;
import org.batoo.jpa.core.impl.model.mapping.PluralMappingEx;
import org.batoo.jpa.core.impl.model.mapping.SingularAssociationMappingImpl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * The serializable cache information of the instance.
 * 
 * @author hceylan
 * @since 2.0.0
 */
public class CacheInstance implements Serializable {

	private final Map<String, Object> basicMappings = Maps.newHashMap();
	private final Map<String, List<CacheReference>> pluralMappings = Maps.newHashMap();
	private final Map<String, CacheReference> singularMappings = Maps.newHashMap();

	private final String entityName;
	private final Object id;

	/**
	 * @param cache
	 *            the cache
	 * @param managedInstance
	 *            the managed instance
	 * 
	 * @since 2.0.0
	 */
	public CacheInstance(CacheImpl cache, ManagedInstance<?> managedInstance) {
		super();

		this.entityName = managedInstance.getType().getName();
		this.id = managedInstance.getId().getId();

		final EntityTypeImpl<?> type = managedInstance.getType();
		final Object instance = managedInstance.getInstance();
		final MetamodelImpl metamodel = managedInstance.getSession().getEntityManager().getMetamodel();

		for (final BasicMappingImpl<?, ?> mapping : type.getBasicMappings()) {
			this.basicMappings.put(mapping.getPath(), mapping.get(instance));
		}

		for (final AssociationMappingImpl<?, ?, ?> mapping : type.getAssociations()) {
			if (mapping instanceof PluralAssociationMappingImpl) {
				this.updateCollection(metamodel, cache, (PluralMappingEx<?, ?, ?>) mapping, instance);
			}
			else {
				final Object value = mapping.get(instance);
				if (value == null) {
					this.singularMappings.put(mapping.getPath(), null);
				}
				else {
					if (value instanceof EnhancedInstance) {
						final EnhancedInstance enhancedInstance = (EnhancedInstance) value;
						if (enhancedInstance.__enhanced__$$__isInitialized()) {
							this.singularMappings.put(mapping.getPath(), new CacheReference(metamodel, value));
						}
					}
					else {
						this.singularMappings.put(mapping.getPath(), new CacheReference(metamodel, value));
					}
				}
			}
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
	 * @since 2.0.0
	 */
	public void copyTo(CacheImpl cache, ManagedInstance<?> managedInstance) {
		final EntityTypeImpl<?> type = managedInstance.getType();
		final Object instance = managedInstance.getInstance();

		final EntityManagerImpl em = managedInstance.getSession().getEntityManager();
		final MetamodelImpl metamodel = em.getMetamodel();

		for (final BasicMappingImpl<?, ?> mapping : type.getBasicMappings()) {
			mapping.set(instance, this.basicMappings.get(mapping.getPath()));
		}

		for (final SingularAssociationMappingImpl<?, ?> mapping : type.getAssociationsSingular()) {
			final CacheReference reference = this.singularMappings.get(mapping.getPath());
			if (reference != null) {
				final EntityTypeImpl<?> entity = metamodel.entity(reference.getType());
				mapping.set(managedInstance.getInstance(), em.find(entity.getJavaType(), reference.getId()));
			}
		}

		managedInstance.setCache(this);
	}

	/**
	 * @param managedInstance
	 *            the managed instance
	 * @param mapping
	 *            the mapping
	 * @return the collection of children or null
	 * 
	 * @since 2.0.0
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Collection<?> getCollection(ManagedInstance<?> managedInstance, PluralAssociationMappingImpl<?, ?, ?> mapping) {
		final List<CacheReference> children = this.pluralMappings.get(mapping.getPath());

		// if not null then fetch the references
		if (children == null) {
			return null;
		}

		final EntityManagerImpl entityManager = managedInstance.getSession().getEntityManager();
		final MetamodelImpl metamodel = entityManager.getMetamodel();
		final CacheImpl cache = entityManager.getEntityManagerFactory().getCache();
		final Object instance = managedInstance.getInstance();

		AssociationMappingImpl<?, ?, ?> inverse = null;
		if ((mapping.getInverse() != null) && (mapping.getInverse().getAttribute().getPersistentAttributeType() == PersistentAttributeType.MANY_TO_ONE)) {
			inverse = mapping.getInverse();
		}

		try {
			final ArrayList childEntities = Lists.newArrayList();
			for (int i = 0; i < children.size(); i++) {
				final CacheReference childReference = children.get(i);
				final EntityTypeImpl<?> childType = metamodel.entity(childReference.getType());
				final Object child = entityManager.find(childType.getJavaType(), childReference.getId());

				// if the child is null then it is removed
				if ((inverse != null) && (inverse.get(child) != instance)) {
					return this.handleStale(cache, mapping, managedInstance);
				}
				childEntities.add(child);

			}

			return childEntities;
		}
		catch (final NoResultException e) {}

		return this.handleStale(cache, mapping, managedInstance);
	}

	/**
	 * Returns the name of the entity of the cache instance.
	 * 
	 * @return the name of the entity of the cache instance
	 * 
	 * @since 2.0.0
	 */
	public String getEntityName() {
		return this.entityName;
	}

	/**
	 * @param cache
	 *            the cache
	 * @param mapping
	 *            the mapping
	 * @param managedInstance
	 *            the managed instance
	 * @return the null collection
	 * 
	 * @since 2.0.0
	 */
	private Collection<?> handleStale(CacheImpl cache, PluralMappingEx<?, ?, ?> mapping, ManagedInstance<?> managedInstance) {
		this.pluralMappings.remove(mapping.getPath());
		cache.put(managedInstance);

		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String toString() {
		return "CacheInstance [clazz=" + this.entityName + ", id=" + this.id + ", basicMappings=" + this.basicMappings + ", pluralMappings="
			+ this.pluralMappings + "]";
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
	 * @since 2.0.0
	 */
	public boolean updateCollection(MetamodelImpl metamodel, CacheImpl cache, PluralMappingEx<?, ?, ?> mapping, Object instance) {
		// if this is not a cachable type then skip
		if (mapping instanceof PluralAssociationMappingImpl) {
			final EntityTypeImpl<?> childType = ((PluralAssociationMappingImpl<?, ?, ?>) mapping).getType();

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
		if (collection.getDelegate() instanceof List) {
			final List<?> delegateList = (List<?>) collection.getDelegate();
			for (int i = 0; i < delegateList.size(); i++) {
				references.add(new CacheReference(metamodel, delegateList.get(i)));
			}
		}
		else {
			for (final Object child : collection.getDelegate()) {
				references.add(new CacheReference(metamodel, child));
			}
		}

		this.pluralMappings.put(mapping.getPath(), references);

		return true;
	}
}
