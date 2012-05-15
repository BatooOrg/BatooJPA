/*
 * Copyright (c) 2008, 2009 Sun Microsystems. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Linda DeMichiel - Java Persistence 2.0 - Version 2.0 (October 1, 2009)
 *     Specification available from http://jcp.org/en/jsr/detail?id=317
 */

// $Id: Persistence.java 20957 2011-06-13 09:58:51Z stliu $

package javax.persistence;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.persistence.spi.LoadState;
import javax.persistence.spi.PersistenceProvider;
import javax.persistence.spi.PersistenceProviderResolverHolder;

/**
 * Bootstrap class that provides access to an EntityManagerFactory.
 */
public class Persistence {

	@Deprecated
	public static final String PERSISTENCE_PROVIDER = "javax.persistence.spi.PeristenceProvider";

	@Deprecated
	protected static final Set<PersistenceProvider> providers = new HashSet<PersistenceProvider>();

	/**
	 * Create and return an EntityManagerFactory for the named persistence unit.
	 *
	 * @param persistenceUnitName The name of the persistence unit
	 *
	 * @return The factory that creates EntityManagers configured according to the specified persistence unit
	 */
	public static EntityManagerFactory createEntityManagerFactory(String persistenceUnitName) {
		return createEntityManagerFactory( persistenceUnitName, null );
	}

	/**
	 * Create and return an EntityManagerFactory for the named persistence unit using the given properties.
	 *
	 * @param persistenceUnitName The name of the persistence unit
	 * @param properties Additional properties to use when creating the factory. The values of these properties override
	 * any values that may have been configured elsewhere
	 *
	 * @return The factory that creates EntityManagers configured according to the specified persistence unit
	 */
	@SuppressWarnings("rawtypes")
	public static EntityManagerFactory createEntityManagerFactory(String persistenceUnitName, Map properties) {
		EntityManagerFactory emf = null;
		List<PersistenceProvider> providers = getProviders();
		for ( PersistenceProvider provider : providers ) {
			emf = provider.createEntityManagerFactory( persistenceUnitName, properties );
			if ( emf != null ) {
				break;
			}
		}
		if ( emf == null ) {
			throw new PersistenceException( "No Persistence provider for EntityManager named " + persistenceUnitName );
		}
		return emf;
	}

	private static List<PersistenceProvider> getProviders() {
		return PersistenceProviderResolverHolder
				.getPersistenceProviderResolver()
				.getPersistenceProviders();
	}

	/**
	 * @return Returns a <code>PersistenceUtil</code> instance.
	 */
	public static PersistenceUtil getPersistenceUtil() {
		return util;
	}

	private static PersistenceUtil util =
			//TODO add an Hibernate specific optimization
		new PersistenceUtil() {
			public boolean isLoaded(Object entity, String attributeName) {
				List<PersistenceProvider> providers = Persistence.getProviders();
				for ( PersistenceProvider provider : providers ) {
					final LoadState state = provider.getProviderUtil().isLoadedWithoutReference( entity, attributeName );
					if ( state == LoadState.UNKNOWN ) continue;
					return state == LoadState.LOADED;
				}
				for ( PersistenceProvider provider : providers ) {
					final LoadState state = provider.getProviderUtil().isLoadedWithReference( entity, attributeName );
					if ( state == LoadState.UNKNOWN ) continue;
					return state == LoadState.LOADED;
				}
				return true;
			}

			public boolean isLoaded(Object object) {
				List<PersistenceProvider> providers = Persistence.getProviders();
				for ( PersistenceProvider provider : providers ) {
					final LoadState state = provider.getProviderUtil().isLoaded( object );
					if ( state == LoadState.UNKNOWN ) continue;
					return state == LoadState.LOADED;
				}
				return true;
			}
		};
}
