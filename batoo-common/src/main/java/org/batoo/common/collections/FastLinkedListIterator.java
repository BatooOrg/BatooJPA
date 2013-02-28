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
package org.batoo.common.collections;

import java.util.ListIterator;

import org.batoo.common.collections.FastLinkedList.FastLinkedListNode;

/**
 * Iterator for {@link FastLinkedList}s.
 * <p>
 * Does not check for modifications in the list.
 * 
 * @param <E>
 *            the type of the iterator
 * @author hceylan
 */
public class FastLinkedListIterator<E> implements ListIterator<E> {

	private final FastLinkedList<E> list;
	private final boolean forward;

	private FastLinkedListNode<E> lastReturned;
	private FastLinkedListNode<E> next;

	private int nextIndex;

	FastLinkedListIterator(FastLinkedList<E> list, int index, boolean forward) {
		super();

		this.list = list;
		this.forward = forward;

		this.next = (index == this.list.size) ? null : this.list.node(index);
		this.nextIndex = index;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void add(E e) {
		this.lastReturned = null;

		if (this.next == null) {
			this.list.linkLast(e);
		}
		else {
			this.list.linkBefore(e, this.next);
		}

		this.nextIndex++;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean hasNext() {
		if (this.forward) {
			return this.nextIndex < this.list.size;
		}

		return this.nextIndex > 0;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean hasPrevious() {
		if (this.forward) {
			return this.nextIndex > 0;
		}

		return this.nextIndex < this.list.size;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public E next() {
		if (this.forward) {
			return this.realNext();
		}

		return this.realPrevious();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public int nextIndex() {
		return this.nextIndex;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public E previous() {
		// if (!this.hasPrevious()) {
		// throw new NoSuchElementException();
		// }

		if (this.forward) {
			return this.realPrevious();
		}

		return this.realNext();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public int previousIndex() {
		return this.nextIndex - 1;
	}

	private E realNext() {
		final FastLinkedListNode<E> next = this.next;

		this.lastReturned = next;
		this.next = this.next.next;
		this.nextIndex++;

		return next.item;
	}

	private E realPrevious() {
		this.lastReturned = this.next = (this.next == null) ? this.list.last : this.next.prev;
		this.nextIndex--;

		return this.lastReturned.item;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void remove() {
		if (this.lastReturned == null) {
			throw new IllegalStateException();
		}

		final FastLinkedListNode<E> lastNext = this.lastReturned.next;
		this.list.unlink(this.lastReturned);
		if (this.next == this.lastReturned) {
			this.next = lastNext;
		}
		else {
			this.nextIndex--;
		}

		this.lastReturned = null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void set(E e) {
		if (this.lastReturned == null) {
			throw new IllegalStateException();
		}

		this.lastReturned.item = e;
	}
}
