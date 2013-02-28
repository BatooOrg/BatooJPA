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

import java.io.IOException;
import java.io.Serializable;
import java.util.AbstractSequentialList;
import java.util.Collection;
import java.util.Deque;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Queue;

/**
 * Doubly-linked list implementation that avoids interfaces.
 * <p>
 * Returns concrete interface classes from <code>iterator()</code> methods: {@link #iterator()} {@link #listIterator()}
 * {@link #descendingIterator()}.
 * <p>
 * Does not check list modifications during iterations.
 * 
 * @param <E>
 *            The type of the list
 * 
 * @author hceylan
 */
public class FastLinkedList<E> extends AbstractSequentialList<E> implements List<E>, Deque<E>, Cloneable, Serializable {

	static class FastLinkedListNode<E> {
		E item;
		FastLinkedListNode<E> next;
		FastLinkedListNode<E> prev;

		FastLinkedListNode(FastLinkedListNode<E> prev, E element, FastLinkedListNode<E> next) {
			super();

			this.item = element;
			this.next = next;
			this.prev = prev;
		}
	}

	transient final AbstractListListener<E> listener;
	transient int size = 0;
	transient FastLinkedListNode<E> first;
	transient FastLinkedListNode<E> last;

	/**
	 * Constructs an empty list.
	 * 
	 */
	public FastLinkedList() {
		this((AbstractListListener<E>) null);
	}

	/**
	 * Constructs an empty list with an optional addition removal listener.
	 * 
	 * @param listener
	 *            the listener to call on additions and removals
	 * 
	 */
	public FastLinkedList(AbstractListListener<E> listener) {
		super();

		this.listener = listener;
	}

	/**
	 * Constructs a list containing the elements of the specified collection, in the order they are returned by the collection's iterator.
	 * Additionally passes a list listener.
	 * 
	 * Listener will be effective after initial creation of the list.
	 * 
	 * @param listener
	 *            the listener to call on additions and removals
	 * @param c
	 *            the collection whose elements are to be placed into this list
	 * @throws NullPointerException
	 *             if the specified collection is null
	 * 
	 */
	public FastLinkedList(AbstractListListener<E> listener, Collection<? extends E> c) {
		super();

		this.addAll(c);
		this.listener = listener;
	}

	/**
	 * Constructs a list containing the elements of the specified collection, in the order they are returned by the collection's iterator.
	 * 
	 * @param c
	 *            the collection whose elements are to be placed into this list
	 * @throws NullPointerException
	 *             if the specified collection is null
	 * 
	 */
	public FastLinkedList(Collection<? extends E> c) {
		this(null, c);

		this.addAll(c);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean add(E e) {
		this.linkLast(e);

		return true;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void add(int index, E element) {
		this.checkPositionIndex(index);

		if (index == this.size) {
			this.linkLast(element);
		}
		else {
			this.linkBefore(element, this.node(index));
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean addAll(Collection<? extends E> c) {
		return this.addAll(this.size, c);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean addAll(int index, Collection<? extends E> c) {
		this.checkPositionIndex(index);

		final Object[] a = c.toArray();
		final int numNew = a.length;
		if (numNew == 0) {
			return false;
		}

		FastLinkedListNode<E> pred, succ;
		if (index == this.size) {
			succ = null;
			pred = this.last;
		}
		else {
			succ = this.node(index);
			pred = succ.prev;
		}

		for (final Object o : a) {
			@SuppressWarnings("unchecked")
			final E e = (E) o;
			final FastLinkedListNode<E> newNode = new FastLinkedListNode<E>(pred, e, null);
			if (pred == null) {
				this.first = newNode;
			}
			else {
				pred.next = newNode;
			}
			pred = newNode;
		}

		if (succ == null) {
			this.last = pred;
		}
		else {
			pred.next = succ;
			succ.prev = pred;
		}

		this.size += numNew;
		this.modCount++;
		return true;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void addFirst(E e) {
		this.linkFirst(e);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void addLast(E e) {
		this.linkLast(e);
	}

	private void checkElementIndex(int index) {
		if (!this.isElementIndex(index)) {
			throw new IndexOutOfBoundsException(this.outOfBoundsMsg(index));
		}
	}

	private void checkPositionIndex(int index) {
		if (!this.isPositionIndex(index)) {
			throw new IndexOutOfBoundsException(this.outOfBoundsMsg(index));
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void clear() {
		this.first = this.last = null;
		this.size = 0;

		this.modCount++;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Object clone() {
		final FastLinkedList<E> clone = this.superClone();

		// Put clone into "virgin" state
		clone.first = clone.last = null;
		clone.size = 0;
		clone.modCount = 0;

		// Initialize clone with our elements
		for (FastLinkedListNode<E> x = this.first; x != null; x = x.next) {
			clone.add(x.item);
		}

		return clone;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean contains(Object o) {
		return this.indexOf(o) != -1;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public FastLinkedListIterator<E> descendingIterator() {
		return new FastLinkedListIterator<E>(this, this.size, false);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public E element() {
		return this.getFirst();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public E get(int index) {
		this.checkElementIndex(index);

		return this.node(index).item;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public E getFirst() {
		final FastLinkedListNode<E> f = this.first;
		if (f == null) {
			throw new NoSuchElementException();
		}

		return f.item;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public E getLast() {
		final FastLinkedListNode<E> l = this.last;
		if (l == null) {
			throw new NoSuchElementException();
		}

		return l.item;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public int indexOf(Object o) {
		int index = 0;

		if (o == null) {
			for (FastLinkedListNode<E> x = this.first; x != null; x = x.next) {
				if (x.item == null) {
					return index;
				}
				index++;
			}
		}
		else {
			for (FastLinkedListNode<E> x = this.first; x != null; x = x.next) {
				if (o.equals(x.item)) {
					return index;
				}
				index++;
			}
		}

		return -1;
	}

	private boolean isElementIndex(int index) {
		return (index >= 0) && (index < this.size);
	}

	private boolean isPositionIndex(int index) {
		return (index >= 0) && (index <= this.size);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public FastLinkedListIterator<E> iterator() {
		return this.listIterator();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public int lastIndexOf(Object o) {
		int index = this.size;
		if (o == null) {
			for (FastLinkedListNode<E> x = this.last; x != null; x = x.prev) {
				index--;
				if (x.item == null) {
					return index;
				}
			}
		}
		else {
			for (FastLinkedListNode<E> x = this.last; x != null; x = x.prev) {
				index--;
				if (o.equals(x.item)) {
					return index;
				}
			}
		}
		return -1;
	}

	void linkBefore(E e, FastLinkedListNode<E> succ) {
		// assert succ != null;
		final FastLinkedListNode<E> pred = succ.prev;
		final FastLinkedListNode<E> newNode = new FastLinkedListNode<E>(pred, e, succ);
		succ.prev = newNode;
		if (pred == null) {
			this.first = newNode;
		}
		else {
			pred.next = newNode;
		}
		this.size++;
		this.modCount++;

		final AbstractListListener<E> listener = this.listener;
		if (listener != null) {
			listener.elementAdded(e);
		}
	}

	private void linkFirst(E e) {
		final FastLinkedListNode<E> f = this.first;
		final FastLinkedListNode<E> newNode = new FastLinkedListNode<E>(null, e, f);
		this.first = newNode;
		if (f == null) {
			this.last = newNode;
		}
		else {
			f.prev = newNode;
		}
		this.size++;
		this.modCount++;

		final AbstractListListener<E> listener = this.listener;
		if (listener != null) {
			listener.elementAdded(e);
		}
	}

	void linkLast(E e) {
		final FastLinkedListNode<E> l = this.last;
		final FastLinkedListNode<E> newNode = new FastLinkedListNode<E>(l, e, null);
		this.last = newNode;

		if (l == null) {
			this.first = newNode;
		}
		else {
			l.next = newNode;
		}

		this.size++;
		this.modCount++;

		final AbstractListListener<E> listener = this.listener;
		if (listener != null) {
			listener.elementAdded(e);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public FastLinkedListIterator<E> listIterator() {
		return this.listIterator(0);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public FastLinkedListIterator<E> listIterator(int index) {
		this.checkPositionIndex(index);

		return new FastLinkedListIterator<E>(this, index, true);
	}

	// Search Operations

	/**
	 * Returns the (non-null) Node at the specified element index.
	 */
	FastLinkedListNode<E> node(int index) {
		// assert isElementIndex(index);

		if (index < (this.size >> 1)) {
			FastLinkedListNode<E> x = this.first;
			for (int i = 0; i < index; i++) {
				x = x.next;
			}
			return x;
		}
		else {
			FastLinkedListNode<E> x = this.last;
			for (int i = this.size - 1; i > index; i--) {
				x = x.prev;
			}
			return x;
		}
	}

	/**
	 * Adds the specified element as the tail (last element) of this list.
	 * 
	 * @param e
	 *            the element to add
	 * @return {@code true} (as specified by {@link Queue#offer})
	 * @since 1.5
	 */
	@Override
	public boolean offer(E e) {
		return this.add(e);
	}

	// Queue operations.

	// Deque operations
	/**
	 * Inserts the specified element at the front of this list.
	 * 
	 * @param e
	 *            the element to insert
	 * @return {@code true} (as specified by {@link Deque#offerFirst})
	 * @since 1.6
	 */
	@Override
	public boolean offerFirst(E e) {
		this.addFirst(e);
		return true;
	}

	/**
	 * Inserts the specified element at the end of this list.
	 * 
	 * @param e
	 *            the element to insert
	 * @return {@code true} (as specified by {@link Deque#offerLast})
	 * @since 1.6
	 */
	@Override
	public boolean offerLast(E e) {
		this.addLast(e);
		return true;
	}

	/**
	 * Constructs an IndexOutOfBoundsException detail message. Of the many possible refactorings of the error handling code, this
	 * "outlining" performs best with both server and client VMs.
	 */
	private String outOfBoundsMsg(int index) {
		return "Index: " + index + ", Size: " + this.size;
	}

	/**
	 * Retrieves, but does not remove, the head (first element) of this list.
	 * 
	 * @return the head of this list, or {@code null} if this list is empty
	 * @since 1.5
	 */
	@Override
	public E peek() {
		final FastLinkedListNode<E> f = this.first;
		return (f == null) ? null : f.item;
	}

	/**
	 * Retrieves, but does not remove, the first element of this list, or returns {@code null} if this list is empty.
	 * 
	 * @return the first element of this list, or {@code null} if this list is empty
	 * @since 1.6
	 */
	@Override
	public E peekFirst() {
		final FastLinkedListNode<E> f = this.first;
		return (f == null) ? null : f.item;
	}

	/**
	 * Retrieves, but does not remove, the last element of this list, or returns {@code null} if this list is empty.
	 * 
	 * @return the last element of this list, or {@code null} if this list is empty
	 * @since 1.6
	 */
	@Override
	public E peekLast() {
		final FastLinkedListNode<E> l = this.last;
		return (l == null) ? null : l.item;
	}

	/**
	 * Retrieves and removes the head (first element) of this list.
	 * 
	 * @return the head of this list, or {@code null} if this list is empty
	 * @since 1.5
	 */
	@Override
	public E poll() {
		final FastLinkedListNode<E> f = this.first;
		return (f == null) ? null : this.unlinkFirst(f);
	}

	/**
	 * Retrieves and removes the first element of this list, or returns {@code null} if this list is empty.
	 * 
	 * @return the first element of this list, or {@code null} if this list is empty
	 * @since 1.6
	 */
	@Override
	public E pollFirst() {
		final FastLinkedListNode<E> f = this.first;
		return (f == null) ? null : this.unlinkFirst(f);
	}

	/**
	 * Retrieves and removes the last element of this list, or returns {@code null} if this list is empty.
	 * 
	 * @return the last element of this list, or {@code null} if this list is empty
	 * @since 1.6
	 */
	@Override
	public E pollLast() {
		final FastLinkedListNode<E> l = this.last;
		return (l == null) ? null : this.unlinkLast(l);
	}

	/**
	 * Pops an element from the stack represented by this list. In other words, removes and returns the first element of this list.
	 * 
	 * <p>
	 * This method is equivalent to {@link #removeFirst()}.
	 * 
	 * @return the element at the front of this list (which is the top of the stack represented by this list)
	 * @throws NoSuchElementException
	 *             if this list is empty
	 * @since 1.6
	 */
	@Override
	public E pop() {
		return this.removeFirst();
	}

	/**
	 * Pushes an element onto the stack represented by this list. In other words, inserts the element at the front of this list.
	 * 
	 * <p>
	 * This method is equivalent to {@link #addFirst}.
	 * 
	 * @param e
	 *            the element to push
	 * @since 1.6
	 */
	@Override
	public void push(E e) {
		this.addFirst(e);
	}

	/**
	 * Reconstitutes this {@code ChildrenList} instance from a stream (that is, deserializes it).
	 */
	@SuppressWarnings("unchecked")
	private void readObject(java.io.ObjectInputStream s) throws java.io.IOException, ClassNotFoundException {
		// Read in any hidden serialization magic
		s.defaultReadObject();

		// Read in size
		final int size = s.readInt();

		// Read in all elements in the proper order.
		for (int i = 0; i < size; i++) {
			this.linkLast((E) s.readObject());
		}
	}

	/**
	 * Retrieves and removes the head (first element) of this list.
	 * 
	 * @return the head of this list
	 * @throws NoSuchElementException
	 *             if this list is empty
	 * @since 1.5
	 */
	@Override
	public E remove() {
		return this.removeFirst();
	}

	/**
	 * Removes the element at the specified position in this list. Shifts any subsequent elements to the left (subtracts one from their
	 * indices). Returns the element that was removed from the list.
	 * 
	 * @param index
	 *            the index of the element to be removed
	 * @return the element previously at the specified position
	 * @throws IndexOutOfBoundsException
	 *             {@inheritDoc}
	 */
	@Override
	public E remove(int index) {
		this.checkElementIndex(index);
		return this.unlink(this.node(index));
	}

	/**
	 * Removes the first occurrence of the specified element from this list, if it is present. If this list does not contain the element, it
	 * is unchanged. More formally, removes the element with the lowest index {@code i} such that
	 * <tt>(o==null&nbsp;?&nbsp;get(i)==null&nbsp;:&nbsp;o.equals(get(i)))</tt> (if such an element exists). Returns {@code true} if this
	 * list contained the specified element (or equivalently, if this list changed as a result of the call).
	 * 
	 * @param o
	 *            element to be removed from this list, if present
	 * @return {@code true} if this list contained the specified element
	 */
	@Override
	public boolean remove(Object o) {
		if (o == null) {
			for (FastLinkedListNode<E> x = this.first; x != null; x = x.next) {
				if (x.item == null) {
					this.unlink(x);
					return true;
				}
			}
		}
		else {
			for (FastLinkedListNode<E> x = this.first; x != null; x = x.next) {
				if (o.equals(x.item)) {
					this.unlink(x);
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Removes and returns the first element from this list.
	 * 
	 * @return the first element from this list
	 * @throws NoSuchElementException
	 *             if this list is empty
	 */
	@Override
	public E removeFirst() {
		final FastLinkedListNode<E> f = this.first;
		if (f == null) {
			throw new NoSuchElementException();
		}
		return this.unlinkFirst(f);
	}

	/**
	 * Removes the first occurrence of the specified element in this list (when traversing the list from head to tail). If the list does not
	 * contain the element, it is unchanged.
	 * 
	 * @param o
	 *            element to be removed from this list, if present
	 * @return {@code true} if the list contained the specified element
	 * @since 1.6
	 */
	@Override
	public boolean removeFirstOccurrence(Object o) {
		return this.remove(o);
	}

	/**
	 * Removes and returns the last element from this list.
	 * 
	 * @return the last element from this list
	 * @throws NoSuchElementException
	 *             if this list is empty
	 */
	@Override
	public E removeLast() {
		final FastLinkedListNode<E> l = this.last;
		if (l == null) {
			throw new NoSuchElementException();
		}
		return this.unlinkLast(l);
	}

	/**
	 * Removes the last occurrence of the specified element in this list (when traversing the list from head to tail). If the list does not
	 * contain the element, it is unchanged.
	 * 
	 * @param o
	 *            element to be removed from this list, if present
	 * @return {@code true} if the list contained the specified element
	 * @since 1.6
	 */
	@Override
	public boolean removeLastOccurrence(Object o) {
		if (o == null) {
			for (FastLinkedListNode<E> x = this.last; x != null; x = x.prev) {
				if (x.item == null) {
					this.unlink(x);
					return true;
				}
			}
		}
		else {
			for (FastLinkedListNode<E> x = this.last; x != null; x = x.prev) {
				if (o.equals(x.item)) {
					this.unlink(x);
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Replaces the element at the specified position in this list with the specified element.
	 * 
	 * @param index
	 *            index of the element to replace
	 * @param element
	 *            element to be stored at the specified position
	 * @return the element previously at the specified position
	 * @throws IndexOutOfBoundsException
	 *             {@inheritDoc}
	 */
	@Override
	public E set(int index, E element) {
		this.checkElementIndex(index);
		final FastLinkedListNode<E> x = this.node(index);
		final E oldVal = x.item;
		x.item = element;
		return oldVal;
	}

	/**
	 * Returns the number of elements in this list.
	 * 
	 * @return the number of elements in this list
	 */
	@Override
	public int size() {
		return this.size;
	}

	@SuppressWarnings("unchecked")
	private FastLinkedList<E> superClone() {
		try {
			return (FastLinkedList<E>) super.clone();
		}
		catch (final CloneNotSupportedException e) {
			throw new InternalError();
		}
	}

	/**
	 * Returns an array containing all of the elements in this list in proper sequence (from first to last element).
	 * 
	 * <p>
	 * The returned array will be "safe" in that no references to it are maintained by this list. (In other words, this method must allocate
	 * a new array). The caller is thus free to modify the returned array.
	 * 
	 * <p>
	 * This method acts as bridge between array-based and collection-based APIs.
	 * 
	 * @return an array containing all of the elements in this list in proper sequence
	 */
	@Override
	public Object[] toArray() {
		final Object[] result = new Object[this.size];
		int i = 0;
		for (FastLinkedListNode<E> x = this.first; x != null; x = x.next) {
			result[i++] = x.item;
		}
		return result;
	}

	/**
	 * Returns an array containing all of the elements in this list in proper sequence (from first to last element); the runtime type of the
	 * returned array is that of the specified array. If the list fits in the specified array, it is returned therein. Otherwise, a new
	 * array is allocated with the runtime type of the specified array and the size of this list.
	 * 
	 * <p>
	 * If the list fits in the specified array with room to spare (i.e., the array has more elements than the list), the element in the
	 * array immediately following the end of the list is set to {@code null}. (This is useful in determining the length of the list
	 * <i>only</i> if the caller knows that the list does not contain any null elements.)
	 * 
	 * <p>
	 * Like the {@link #toArray()} method, this method acts as bridge between array-based and collection-based APIs. Further, this method
	 * allows precise control over the runtime type of the output array, and may, under certain circumstances, be used to save allocation
	 * costs.
	 * 
	 * <p>
	 * Suppose {@code x} is a list known to contain only strings. The following code can be used to dump the list into a newly allocated
	 * array of {@code String}:
	 * 
	 * <pre>
	 * String[] y = x.toArray(new String[0]);
	 * </pre>
	 * 
	 * Note that {@code toArray(new Object[0])} is identical in function to {@code toArray()}.
	 * 
	 * @param a
	 *            the array into which the elements of the list are to be stored, if it is big enough; otherwise, a new array of the same
	 *            runtime type is allocated for this purpose.
	 * @return an array containing the elements of the list
	 * @throws ArrayStoreException
	 *             if the runtime type of the specified array is not a supertype of the runtime type of every element in this list
	 * @throws NullPointerException
	 *             if the specified array is null
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <T> T[] toArray(T[] a) {
		if (a.length < this.size) {
			a = (T[]) java.lang.reflect.Array.newInstance(a.getClass().getComponentType(), this.size);
		}
		int i = 0;
		final Object[] result = a;
		for (FastLinkedListNode<E> x = this.first; x != null; x = x.next) {
			result[i++] = x.item;
		}

		if (a.length > this.size) {
			a[this.size] = null;
		}

		return a;
	}

	/**
	 * Unlinks non-null node x.
	 */
	E unlink(FastLinkedListNode<E> x) {
		final E element = x.item;
		final FastLinkedListNode<E> next = x.next;
		final FastLinkedListNode<E> prev = x.prev;

		if (prev == null) {
			this.first = next;
		}
		else {
			prev.next = next;
			x.prev = null;
		}

		if (next == null) {
			this.last = prev;
		}
		else {
			next.prev = prev;
			x.next = null;
		}

		x.item = null;
		this.size--;
		this.modCount++;

		final AbstractListListener<E> listener = this.listener;
		if (listener != null) {
			listener.elementRemoved(element);
		}

		return element;
	}

	/**
	 * Unlinks non-null first node f.
	 */
	private E unlinkFirst(FastLinkedListNode<E> f) {
		final E element = f.item;
		final FastLinkedListNode<E> next = f.next;
		f.item = null;
		f.next = null; // help GC
		this.first = next;
		if (next == null) {
			this.last = null;
		}
		else {
			next.prev = null;
		}
		this.size--;
		this.modCount++;

		final AbstractListListener<E> listener = this.listener;
		if (listener != null) {
			listener.elementRemoved(element);
		}

		return element;
	}

	/**
	 * Unlinks non-null last node l.
	 */
	private E unlinkLast(FastLinkedListNode<E> l) {
		final E element = l.item;
		final FastLinkedListNode<E> prev = l.prev;
		l.item = null;
		l.prev = null; // help GC
		this.last = prev;
		if (prev == null) {
			this.first = null;
		}
		else {
			prev.next = null;
		}
		this.size--;
		this.modCount++;

		final AbstractListListener<E> listener = this.listener;
		if (listener != null) {
			listener.elementRemoved(element);
		}

		return element;
	}

	/**
	 * Saves the state of this {@code ChildrenList} instance to a stream (that is, serializes it).
	 * 
	 * @serialData The size of the list (the number of elements it contains) is emitted (int), followed by all of its elements (each an
	 *             Object) in the proper order.
	 */
	private void writeObject(java.io.ObjectOutputStream s) throws IOException {
		// Write out any hidden serialization magic
		s.defaultWriteObject();

		// Write out size
		s.writeInt(this.size);

		// Write out all elements in the proper order.
		for (FastLinkedListNode<E> x = this.first; x != null; x = x.next) {
			s.writeObject(x.item);
		}
	}
}
