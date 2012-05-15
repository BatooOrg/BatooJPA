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

// $Id: $

package javax.persistence.spi;

/**
 * Utility interface implemented by the persistence provider.  This
 * interface is invoked by the {@link
 * javax.persistence.PersistenceUtil} implementation to determine
 * the load status of an entity or entity attribute.
 *
 * @since Java Persistence 2.0
 */
public interface ProviderUtil {
	/**
	 * If the provider determines that the entity has been provided
	 * by itself and that the state of the specified attribute has
	 * been loaded, this method returns <code>LoadState.LOADED</code>.
	 * <p> If the provider determines that the entity has been provided
	 * by itself and that either entity attributes with <code>FetchType.EAGER</code>
	 * have not been loaded or that the state of the specified
	 * attribute has not been loaded, this methods returns
	 * <code>LoadState.NOT_LOADED</code>.
	 * <p> If a provider cannot determine the load state, this method
	 * returns <code>LoadState.UNKNOWN</code>.
	 * <p> The provider's implementation of this method must not obtain
	 * a reference to an attribute value, as this could trigger the
	 * loading of entity state if the entity has been provided by a
	 * different provider.
	 *
	 * @param entity entity instance
	 * @param attributeName name of attribute whose load status is
	 * to be determined
	 *
	 * @return load status of the attribute
	 */
	public LoadState isLoadedWithoutReference(Object entity, String attributeName);

	/**
	 * If the provider determines that the entity has been provided
	 * by itself and that the state of the specified attribute has
	 * been loaded, this method returns <code>LoadState.LOADED</code>.
	 * <p> If a provider determines that the entity has been provided
	 * by itself and that either the entity attributes with <code>FetchType.EAGER</code>
	 * have not been loaded or that the state of the specified
	 * attribute has not been loaded, this method returns
	 * return <code>LoadState.NOT_LOADED</code>.
	 * <p> If the provider cannot determine the load state, this method
	 * returns <code>LoadState.UNKNOWN</code>.
	 * <p> The provider's implementation of this method is permitted to
	 * obtain a reference to the attribute value.  (This access is
	 * safe because providers which might trigger the loading of the
	 * attribute state will have already been determined by
	 * <code>isLoadedWithoutReference</code>. )
	 *
	 * @param entity entity instance
	 * @param attributeName name of attribute whose load status is
	 * to be determined
	 *
	 * @return load status of the attribute
	 */
	public LoadState isLoadedWithReference(Object entity, String attributeName);

	/**
	 * If the provider determines that the entity has been provided
	 * by itself and that the state of all attributes for which
	 * <code>FetchType.EAGER</code> has been specified have been loaded, this
	 * method returns <code>LoadState.LOADED</code>.
	 * <p> If the provider determines that the entity has been provided
	 * by itself and that not all attributes with <code>FetchType.EAGER</code>
	 * have been loaded, this method returns <code>LoadState.NOT_LOADED</code>.
	 * <p> If the provider cannot determine if the entity has been
	 * provided by itself, this method returns <code>LoadState.UNKNOWN</code>.
	 * <p> The provider's implementation of this method must not obtain
	 * a reference to any attribute value, as this could trigger the
	 * loading of entity state if the entity has been provided by a
	 * different provider.
	 *
	 * @param entity whose loaded status is to be determined
	 *
	 * @return load status of the entity
	 */
	public LoadState isLoaded(Object entity);
}
