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
package org.batoo.jpa.jdbc.mapping;

import java.lang.reflect.Member;

import org.batoo.jpa.parser.AbstractLocator;

/**
 * The interface for mappings.
 * 
 * @param <Z>
 *            the source type
 * @param <X>
 *            the destination type
 * @param <Y>
 *            the attribute type
 * 
 * @author hceylan
 * @since 2.0.1
 */
public interface Mapping<Z, X, Y> {

	/**
	 * Returns the mapping value of instance.
	 * 
	 * @param instance
	 *            the instance of which the value to be returned
	 * @return the mapping value of instance
	 * 
	 * @since 2.0.0
	 */
	X get(Object instance);

	/**
	 * Returns the java member of the mapping.
	 * 
	 * @return the java member of the mapping
	 * 
	 * @since 2.0.1
	 */
	Member getJavaMember();

	/**
	 * Returns the javaType of the mapping.
	 * 
	 * @return the javaType of the mapping
	 * 
	 * @since 2.0.0
	 */
	Class<X> getJavaType();

	/**
	 * Returns the locator of the mapping.
	 * 
	 * @return the locator of the mapping
	 * 
	 * @since 2.0.1
	 */
	AbstractLocator getLocator();

	/**
	 * Returns the name of the AbstractMapping.
	 * 
	 * @return the name of the AbstractMapping
	 * 
	 * @since 2.0.0
	 */
	String getName();

	/**
	 * Returns the parent of the mapping.
	 * 
	 * @return the parent of the mapping
	 * 
	 * @since 2.0.0
	 */
	ParentMapping<?, Z> getParent();

	/**
	 * Returns the path of the mapping.
	 * 
	 * @return the path of the mapping
	 * 
	 * @since 2.0.0
	 */
	String getPath();

	/**
	 * Returns the root of the mapping.
	 * 
	 * @return the root of the mapping
	 * 
	 * @since 2.0.0
	 */
	RootMapping<?> getRoot();

	/**
	 * Returns if the mapping is a collection mapping.
	 * 
	 * @return <code>true</code> if the mapping is a collection mapping, <code>false</code> otherwise
	 * 
	 * @since 2.0.1
	 */
	boolean isCollection();

	/**
	 * Sets the mapping value of instance.
	 * 
	 * @param instance
	 *            the instance of which the value to set
	 * @param value
	 *            the value to set
	 * 
	 * @since 2.0.0
	 */
	void set(Object instance, Object value);
}
