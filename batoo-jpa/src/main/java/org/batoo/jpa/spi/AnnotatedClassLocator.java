/*
 * Copyright (c) 2012-2013, Batu Alp Ceylan
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
package org.batoo.jpa.spi;

import java.net.URL;
import java.util.Set;

import javax.persistence.spi.PersistenceUnitInfo;

/**
 * The Application server integration interface.
 * 
 * @author hceylan
 * @since 2.0.0
 */
public interface AnnotatedClassLocator {

	/**
	 * Locates the classes available at the location given with the url.
	 * 
	 * @param persistenceUnitInfo
	 *            the application server generated persistence unit info
	 * @param url
	 *            the url to use to locate the classes
	 * @return the array of classes located
	 * 
	 * @since 2.0.0
	 */
	Set<Class<?>> locateClasses(PersistenceUnitInfo persistenceUnitInfo, URL url);
}
