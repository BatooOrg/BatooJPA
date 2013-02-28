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
package org.batoo.common.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Constructor accessor for non-sun java environments.
 * 
 * @author hceylan
 * @since $version
 */
public class SimpleConstructorAccessor extends ConstructorAccessor {

	private final Constructor<?> constructor;

	/**
	 * @param constructor
	 *            the constructor
	 * 
	 * @since $version
	 */
	public SimpleConstructorAccessor(Constructor<?> constructor) {
		super();

		this.constructor = constructor;
		this.constructor.setAccessible(true);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Object newInstance(Object[] args) throws InstantiationException, IllegalArgumentException, InvocationTargetException, IllegalAccessException {
		return this.constructor.newInstance(args);
	}
}
