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
package org.batoo.jpa.core.impl.manager;

import javax.persistence.PersistenceUnitUtil;

import org.apache.commons.lang.StringUtils;
import org.batoo.jpa.core.impl.collections.ManagedCollection;
import org.batoo.jpa.core.impl.instance.EnhancedInstance;
import org.batoo.jpa.core.impl.model.EntityTypeImpl;

/**
 * Implementation of persistence unit util.
 * 
 * @author hceylan
 * @since 2.0.0
 */
public class PersistenceUnitUtilImpl implements PersistenceUnitUtil {

	private final EntityManagerFactoryImpl emf;

	/**
	 * @param entityManagerFactory
	 *            the entity manager factory
	 * 
	 * @since 2.0.0
	 */
	public PersistenceUnitUtilImpl(EntityManagerFactoryImpl entityManagerFactory) {
		super();

		this.emf = entityManagerFactory;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Object getIdentifier(Object entity) {
		final EntityTypeImpl type = this.emf.getMetamodel().getEntity(entity.getClass());

		return type.getInstanceId(entity);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean isLoaded(Object entity) {
		if (entity instanceof EnhancedInstance) {
			final EnhancedInstance instance = (EnhancedInstance) entity;

			return instance.__enhanced__$$__isInitialized();
		}

		return true;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean isLoaded(Object entity, String attributeName) {
		if ((entity == null) || StringUtils.isBlank(attributeName)) {
			throw new NullPointerException();
		}

		if (entity instanceof EnhancedInstance) {
			final EnhancedInstance instance = (EnhancedInstance) entity;

			if (instance.__enhanced__$$__getManagedInstance() != null) {
				return instance.__enhanced__$$__getManagedInstance().isJoinLoaded(attributeName);
			}

			try {
				final Object value = entity.getClass().getMethod("get" + StringUtils.capitalize(attributeName)).invoke(entity);

				if (value instanceof EnhancedInstance) {
					return this.isLoaded(value);
				}

				if (value instanceof ManagedCollection) {
					return ((ManagedCollection<?>) value).isInitialized();
				}
			}
			catch (final Exception e) {
				throw new IllegalArgumentException("Unable to get attribute " + attributeName);
			}
		}

		return true;
	}
}
