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
package org.batoo.jpa.core.impl.nativequery;

import javax.persistence.Parameter;

import org.apache.commons.lang.NotImplementedException;

/**
 * Parameter implementation for native queries.
 * 
 * @param <T>
 *            the type of the parameter.
 * 
 * @author hceylan
 * @since 2.0.0
 */
public class NativeParameter<T> implements Parameter<T> {

	private final Integer position;

	/**
	 * @param position
	 *            the position of the parameter
	 * 
	 * @since 2.0.0
	 */
	public NativeParameter(Integer position) {
		super();
		this.position = position;
	}

	/**
	 * Returns the name of the parameter.
	 * 
	 * @return the name number of the parameter
	 * 
	 * @since 2.0.0
	 */
	@Override
	public String getName() {
		// JSR-317 3.8.15 >> The use of named parameters is not defined for native queries.
		throw new NotImplementedException("Native queries do not support named parameters.");
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
