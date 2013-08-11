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
package org.batoo.common.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * The interface to construct objects.
 * 
 * @author hceylan
 * @since 2.0.1
 */
public abstract class ConstructorAccessor {

	/**
	 * Constructs the object.
	 * 
	 * @param args
	 *            the arguments
	 * @return the object constructor
	 * 
	 * @exception IllegalAccessException
	 *                if this {@code Constructor} object is enforcing Java language access control and the underlying constructor is
	 *                inaccessible.
	 * @exception IllegalArgumentException
	 *                if the number of actual and formal parameters differ; if an unwrapping conversion for primitive arguments fails; or
	 *                if, after possible unwrapping, a parameter value cannot be converted to the corresponding formal parameter type by a
	 *                method invocation conversion; if this constructor pertains to an enum type.
	 * @exception InstantiationException
	 *                if the class that declares the underlying constructor represents an abstract class.
	 * @exception InvocationTargetException
	 *                if the underlying constructor throws an exception.
	 * @exception ExceptionInInitializerError
	 *                if the initialization provoked by this method fails.
	 * 
	 * @see Constructor#newInstance(Object...)
	 * 
	 * @since 2.0.1
	 */
	public abstract Object newInstance(Object[] args)
		throws InstantiationException, IllegalArgumentException, InvocationTargetException, IllegalAccessException;
}
