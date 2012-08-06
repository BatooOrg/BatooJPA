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

import org.batoo.jpa.core.impl.instance.EnhancedInstance;
import org.batoo.jpa.core.impl.model.type.EntityTypeImpl;

/**
 * Implementation of persistence unit util.
 * 
 * @author hceylan
 * @since $version
 */
public class PersistenceUnitUtilImpl implements PersistenceUnitUtil {

	private final EntityManagerFactoryImpl emf;

	/**
	 * @param entityManagerFactory
	 *            the entity manager factory
	 * 
	 * @since $version
	 * @author hceylan
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
		if (entity instanceof EnhancedInstance) {
			final EnhancedInstance instance = (EnhancedInstance) entity;

			return instance.__enhanced__$$__getManagedInstance().isJoinLoaded(attributeName);
		}

		return true;
	}
}
