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

package org.batoo.jpa.core.impl.collections;

import java.util.Iterator;

/**
 * A wrapper implementation of {@link Iterator} to track changes in Managed collections.
 * 
 * @param <E>
 *            the type of elements returned by this iterator
 * 
 * @author hceylan
 * @since 2.0.0
 */
public class WrappedIterator<E> implements Iterator<E> {

	private final Iterator<E> delegate;

	/**
	 * @param delegate
	 *            the delegate
	 * 
	 * @since 2.0.0
	 */
	public WrappedIterator(Iterator<E> delegate) {
		super();

		this.delegate = delegate;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean hasNext() {
		return this.delegate.hasNext();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public E next() {
		return this.delegate.next();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void remove() {
		this.delegate.remove();
	}

}
