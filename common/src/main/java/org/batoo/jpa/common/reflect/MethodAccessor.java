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
package org.batoo.jpa.common.reflect;

import java.lang.reflect.Method;

import org.batoo.jpa.common.BatooException;

/**
 * Accessor implementation of {@link AbstractAccessor} for the members of {@link Method}s.
 * 
 * @author hceylan
 * @since $version
 */
public class MethodAccessor extends AbstractAccessor {

	private final Method getter;
	private final Method setter;

	/**
	 * @param getter
	 *            the method to get
	 * @param setter
	 *            the method to set
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public MethodAccessor(Method getter, Method setter) {
		super();

		this.getter = getter;
		this.setter = setter;

		this.getter.setAccessible(true);
		this.setter.setAccessible(true);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Object get(Object instance) {
		try {
			return this.getter.invoke(instance);
		}
		catch (final Exception e) {
			throw new BatooException("Getter invocation failed");
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void set(Object instance, Object value) {
		try {
			this.setter.invoke(instance, value);
		}
		catch (final Exception e) {
			throw new BatooException("Setter invocation failed");
		}
	}

}
