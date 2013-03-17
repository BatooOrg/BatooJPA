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

import java.lang.reflect.Method;

import org.batoo.common.BatooException;

/**
 * Accessor implementation of {@link AbstractAccessor} for the members of properties.
 * 
 * @author hceylan
 * @since 2.0.1
 */
public class PropertyAccessor extends AbstractAccessor {

	private final String name;
	private final Method reader;
	private final Method writer;

	/**
	 * 
	 * @param descriptor
	 *            the property descriptor
	 * @since 2.0.1
	 */
	public PropertyAccessor(PropertyDescriptor descriptor) {
		super();

		this.name = descriptor.getName();
		this.reader = descriptor.getReader();
		this.writer = descriptor.getWriter();

		this.reader.setAccessible(true);
		this.writer.setAccessible(true);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Object get(Object instance) {
		try {
			if (instance instanceof InternalInstance) {
				final InternalInstance enhancedInstance = (InternalInstance) instance;

				enhancedInstance.__enhanced__$$__setInternalCall(true);
				try {
					return this.reader.invoke(instance);
				}
				finally {
					enhancedInstance.__enhanced__$$__setInternalCall(true);
				}
			}
			else {
				return this.reader.invoke(instance);
			}
		}
		catch (final Exception e) {
			throw new BatooException("Cannot get field value: " + this.name, e);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void set(Object instance, Object value) {
		try {
			if (instance instanceof InternalInstance) {
				final InternalInstance enhancedInstance = (InternalInstance) instance;

				enhancedInstance.__enhanced__$$__setInternalCall(true);
				try {
					this.writer.invoke(instance, value);
				}
				finally {
					enhancedInstance.__enhanced__$$__setInternalCall(true);
				}
			}
			else {
				this.writer.invoke(instance, value);
			}

		}
		catch (final Exception e) {
			throw new BatooException("Cannot set field value: " + this.name, e);
		}
	}
}
