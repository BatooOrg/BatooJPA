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

import org.apache.commons.lang.StringUtils;

/**
 * A Simple property desciptor.
 * 
 * @author hceylan
 * @since $version
 */
public class PropertyDescriptor {

	private static String SET_PREFIX = "set";

	private final String name;

	private final Method reader;
	private final Method writer;

	/**
	 * @param clazz
	 *            the class
	 * @param name
	 *            the name of the property
	 * @param reader
	 *            the reader method
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public PropertyDescriptor(Class<?> clazz, String name, Method reader) {
		super();

		this.reader = reader;
		this.writer = this.getWriter(clazz);
		this.name = StringUtils.uncapitalize(name);
	}

	/**
	 * Returns the name of the PropertyDescriptor.
	 * 
	 * @return the name of the PropertyDescriptor
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Returns the reader of the PropertyDescriptor.
	 * 
	 * @return the reader of the PropertyDescriptor
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public Method getReader() {
		return this.reader;
	}

	/**
	 * Returns the writer of the PropertyDescriptor.
	 * 
	 * @return the writer of the PropertyDescriptor
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public Method getWriter() {
		return this.writer;
	}

	private Method getWriter(Class<?> clazz) {
		try {
			return clazz.getMethod(PropertyDescriptor.SET_PREFIX, this.reader.getReturnType());
		}
		catch (final Exception e) {
			return null;
		}
	}

}
