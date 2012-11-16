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
package org.batoo.jpa.core.impl.criteria;

import javax.persistence.Tuple;
import javax.persistence.TupleElement;

/**
 * The definition of an element that is returned in a query result tuple.
 * 
 * @param <X>
 *            the type of the element
 * @see Tuple
 * 
 * @author hceylan
 * @since 2.0.0
 */
public abstract class TupleElementImpl<X> implements TupleElement<X> {

	private final Class<X> javaType;

	/**
	 * @param javaType
	 *            the java type
	 * 
	 * @since 2.0.0
	 */
	@SuppressWarnings("unchecked")
	public TupleElementImpl(Class<X> javaType) {
		super();

		this.javaType = (Class<X>) (javaType != null ? javaType : Object.class);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Class<? extends X> getJavaType() {
		return this.javaType;
	}
}
