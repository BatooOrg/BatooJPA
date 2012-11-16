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
package org.batoo.jpa.core.impl.collections;

import java.util.ListIterator;

/**
 * A wrapper implementation of {@link ListIterator} to track changes in Managed collections.
 * 
 * @param <E>
 *            the type of elements returned by this iterator
 * 
 * @author hceylan
 * @since 2.0.0
 */
public class WrappedListIterator<E> extends WrappedIterator<E> implements ListIterator<E> {

	private final ListIterator<E> delegate;

	/**
	 * @param delegate
	 *            the delegate
	 * 
	 * @since 2.0.0
	 */
	public WrappedListIterator(ListIterator<E> delegate) {
		super(delegate);

		this.delegate = delegate;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void add(E e) {
		this.delegate.add(e);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean hasPrevious() {
		return this.delegate.hasPrevious();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public int nextIndex() {
		return this.delegate.nextIndex();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public E previous() {
		return this.delegate.previous();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public int previousIndex() {
		return this.delegate.previousIndex();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void set(E e) {
		this.delegate.set(e);
	}
}
