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
package org.batoo.jpa.core.impl.nativeQuery;

import javax.persistence.Parameter;

import org.apache.commons.lang.NotImplementedException;

/**
 * Parameter implementation for native queries.
 * 
 * @param <T>
 *            the type of the parameter.
 * 
 * @author hceylan
 * @since $version
 */
public class NativeParameter<T> implements Parameter<T> {

	private final String name;

	private final Integer position;

	/**
	 * @param position
	 *            the position of the parameter
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public NativeParameter(Integer position) {
		this(null, position);
	}

	/**
	 * @param name
	 *            the name of the parameter
	 * @param position
	 *            the ordinal number of the parameter
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public NativeParameter(String name, Integer position) {
		super();

		this.name = name;
		this.position = position;
	}

	/**
	 * Returns the name of the parameter.
	 * 
	 * @return the name number of the parameter
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Override
	public String getName() {
		return this.name;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Class<T> getParameterType() {
		throw new NotImplementedException("Native query types do not support value types");
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Integer getPosition() {
		return this.position;
	}
}
