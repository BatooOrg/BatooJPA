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

import javax.persistence.spi.LoadState;
import javax.persistence.spi.ProviderUtil;

import org.batoo.jpa.core.impl.instance.EnhancedInstance;

/**
 * Implementation of persistence util.
 * 
 * @author hceylan
 * @since $version
 */
public class PersistenceUtilImpl implements ProviderUtil {

	/**
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public PersistenceUtilImpl() {
		super();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public LoadState isLoaded(Object entity) {
		if (entity instanceof EnhancedInstance) {
			final EnhancedInstance instance = (EnhancedInstance) entity;

			return instance.__enhanced__$$__isInitialized() ? LoadState.LOADED : LoadState.NOT_LOADED;
		}

		return LoadState.LOADED;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public LoadState isLoadedWithoutReference(Object entity, String attributeName) {
		if (entity instanceof EnhancedInstance) {
			final EnhancedInstance instance = (EnhancedInstance) entity;

			return instance.__enhanced__$$__getManagedInstance().isJoinLoaded(attributeName) ? LoadState.LOADED : LoadState.NOT_LOADED;
		}

		return LoadState.LOADED;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public LoadState isLoadedWithReference(Object entity, String attributeName) {
		if (entity instanceof EnhancedInstance) {
			final EnhancedInstance instance = (EnhancedInstance) entity;

			return instance.__enhanced__$$__getManagedInstance().isJoinLoaded(attributeName) ? LoadState.LOADED : LoadState.NOT_LOADED;
		}

		return LoadState.LOADED;
	}
}
