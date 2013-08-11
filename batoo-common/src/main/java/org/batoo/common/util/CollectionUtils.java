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
package org.batoo.common.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.batoo.common.BatooVersion;

/**
 * Miscellaneous collection utility methods. Mainly for internal use within the framework.
 * 
 * <p>
 * The original code is based on org.springframework.util.
 * 
 * @author hceylan
 * @since 2.0.1
 */
public abstract class CollectionUtils {

	/**
	 * Iterator wrapping an Enumeration.
	 */
	private static class EnumerationIterator<E> implements Iterator<E> {

		private final Enumeration<E> enumeration;

		public EnumerationIterator(Enumeration<E> enumeration) {
			this.enumeration = enumeration;
		}

		@Override
		public boolean hasNext() {
			return this.enumeration.hasMoreElements();
		}

		@Override
		public E next() {
			return this.enumeration.nextElement();
		}

		@Override
		public void remove() throws UnsupportedOperationException {
			throw new UnsupportedOperationException("Not supported");
		}
	}

	/**
	 * Adapts a Map to the MultiValueMap contract.
	 */
	private static class MultiValueMapAdapter<K, V> implements MultiValueMap<K, V>, Serializable {
		private static final long serialVersionUID = BatooVersion.SERIAL_VERSION_UID;

		private final Map<K, List<V>> map;

		public MultiValueMapAdapter(Map<K, List<V>> map) {
			Assert.notNull(map, "'map' must not be null");
			this.map = map;
		}

		@Override
		public void add(K key, V value) {
			List<V> values = this.map.get(key);
			if (values == null) {
				values = new LinkedList<V>();
				this.map.put(key, values);
			}
			values.add(value);
		}

		@Override
		public void clear() {
			this.map.clear();
		}

		@Override
		public boolean containsKey(Object key) {
			return this.map.containsKey(key);
		}

		@Override
		public boolean containsValue(Object value) {
			return this.map.containsValue(value);
		}

		@Override
		public Set<Entry<K, List<V>>> entrySet() {
			return this.map.entrySet();
		}

		@Override
		public boolean equals(Object other) {
			if (this == other) {
				return true;
			}
			return this.map.equals(other);
		}

		@Override
		public List<V> get(Object key) {
			return this.map.get(key);
		}

		@Override
		public V getFirst(K key) {
			final List<V> values = this.map.get(key);
			return (values != null ? values.get(0) : null);
		}

		@Override
		public int hashCode() {
			return this.map.hashCode();
		}

		@Override
		public boolean isEmpty() {
			return this.map.isEmpty();
		}

		@Override
		public Set<K> keySet() {
			return this.map.keySet();
		}

		@Override
		public List<V> put(K key, List<V> value) {
			return this.map.put(key, value);
		}

		@Override
		public void putAll(Map<? extends K, ? extends List<V>> m) {
			this.map.putAll(m);
		}

		@Override
		public List<V> remove(Object key) {
			return this.map.remove(key);
		}

		@Override
		public void set(K key, V value) {
			final List<V> values = new LinkedList<V>();
			values.add(value);
			this.map.put(key, values);
		}

		@Override
		public void setAll(Map<K, V> values) {
			for (final Entry<K, V> entry : values.entrySet()) {
				this.set(entry.getKey(), entry.getValue());
			}
		}

		@Override
		public int size() {
			return this.map.size();
		}

		@Override
		public Map<K, V> toSingleValueMap() {
			final LinkedHashMap<K, V> singleValueMap = new LinkedHashMap<K, V>(this.map.size());
			for (final Entry<K, List<V>> entry : this.map.entrySet()) {
				singleValueMap.put(entry.getKey(), entry.getValue().get(0));
			}
			return singleValueMap;
		}

		@Override
		public String toString() {
			return this.map.toString();
		}

		@Override
		public Collection<List<V>> values() {
			return this.map.values();
		}
	}

	/**
	 * Convert the supplied array into a List. A primitive array gets converted into a List of the appropriate wrapper type.
	 * <p>
	 * A <code>null</code> source value will be converted to an empty List.
	 * 
	 * @param source
	 *            the (potentially primitive) array
	 * @return the converted List result
	 * @see ObjectUtils#toObjectArray(Object)
	 */
	public static List<?> arrayToList(Object source) {
		return Arrays.asList(ObjectUtils.toObjectArray(source));
	}

	/**
	 * Check whether the given Enumeration contains the given element.
	 * 
	 * @param enumeration
	 *            the Enumeration to check
	 * @param element
	 *            the element to look for
	 * @return <code>true</code> if found, <code>false</code> else
	 */
	public static boolean contains(Enumeration<?> enumeration, Object element) {
		if (enumeration != null) {
			while (enumeration.hasMoreElements()) {
				final Object candidate = enumeration.nextElement();
				if (ObjectUtils.nullSafeEquals(candidate, element)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Check whether the given Iterator contains the given element.
	 * 
	 * @param iterator
	 *            the Iterator to check
	 * @param element
	 *            the element to look for
	 * @return <code>true</code> if found, <code>false</code> else
	 */
	public static boolean contains(Iterator<?> iterator, Object element) {
		if (iterator != null) {
			while (iterator.hasNext()) {
				final Object candidate = iterator.next();
				if (ObjectUtils.nullSafeEquals(candidate, element)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Return <code>true</code> if any element in '<code>candidates</code>' is contained in '<code>source</code>'; otherwise returns
	 * <code>false</code>.
	 * 
	 * @param source
	 *            the source Collection
	 * @param candidates
	 *            the candidates to search for
	 * @return whether any of the candidates has been found
	 */
	public static boolean containsAny(Collection<?> source, Collection<?> candidates) {
		if (CollectionUtils.isEmpty(source) || CollectionUtils.isEmpty(candidates)) {
			return false;
		}
		for (final Object candidate : candidates) {
			if (source.contains(candidate)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Check whether the given Collection contains the given element instance.
	 * <p>
	 * Enforces the given instance to be present, rather than returning <code>true</code> for an equal element as well.
	 * 
	 * @param collection
	 *            the Collection to check
	 * @param element
	 *            the element to look for
	 * @return <code>true</code> if found, <code>false</code> else
	 */
	public static boolean containsInstance(Collection<?> collection, Object element) {
		if (collection != null) {
			for (final Object candidate : collection) {
				if (candidate == element) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Find the common element type of the given Collection, if any.
	 * 
	 * @param collection
	 *            the Collection to check
	 * @return the common element type, or <code>null</code> if no clear common type has been found (or the collection was empty)
	 */
	public static Class<?> findCommonElementType(Collection<?> collection) {
		if (CollectionUtils.isEmpty(collection)) {
			return null;
		}
		Class<?> candidate = null;
		for (final Object val : collection) {
			if (val != null) {
				if (candidate == null) {
					candidate = val.getClass();
				}
				else if (candidate != val.getClass()) {
					return null;
				}
			}
		}
		return candidate;
	}

	/**
	 * Return the first element in '<code>candidates</code>' that is contained in '<code>source</code>'. If no element in '
	 * <code>candidates</code>' is present in '<code>source</code>' returns <code>null</code>. Iteration order is {@link Collection}
	 * implementation specific.
	 * 
	 * @param source
	 *            the source Collection
	 * @param candidates
	 *            the candidates to search for
	 * @return the first present object, or <code>null</code> if not found
	 */
	public static Object findFirstMatch(Collection<?> source, Collection<?> candidates) {
		if (CollectionUtils.isEmpty(source) || CollectionUtils.isEmpty(candidates)) {
			return null;
		}
		for (final Object candidate : candidates) {
			if (source.contains(candidate)) {
				return candidate;
			}
		}
		return null;
	}

	/**
	 * Find a single value of one of the given types in the given Collection: searching the Collection for a value of the first type, then
	 * searching for a value of the second type, etc.
	 * 
	 * @param collection
	 *            the collection to search
	 * @param types
	 *            the types to look for, in prioritized order
	 * @return a value of one of the given types found if there is a clear match, or <code>null</code> if none or more than one such value
	 *         found
	 */
	public static Object findValueOfType(Collection<?> collection, Class<?>[] types) {
		if (CollectionUtils.isEmpty(collection) || ObjectUtils.isEmpty(types)) {
			return null;
		}
		for (final Class<?> type : types) {
			final Object value = CollectionUtils.findValueOfType(collection, type);
			if (value != null) {
				return value;
			}
		}
		return null;
	}

	/**
	 * Find a single value of the given type in the given Collection.
	 * 
	 * @param collection
	 *            the Collection to search
	 * @param type
	 *            the type to look for
	 * @return a value of the given type found if there is a clear match, or <code>null</code> if none or more than one such value found
	 * @param <T>
	 *            the type of the type
	 */
	@SuppressWarnings("unchecked")
	public static <T> T findValueOfType(Collection<?> collection, Class<T> type) {
		if (CollectionUtils.isEmpty(collection)) {
			return null;
		}
		T value = null;
		for (final Object element : collection) {
			if ((type == null) || type.isInstance(element)) {
				if (value != null) {
					// More than one value found... no clear single value.
					return null;
				}
				value = (T) element;
			}
		}
		return value;
	}

	/**
	 * Determine whether the given Collection only contains a single unique object.
	 * 
	 * @param collection
	 *            the Collection to check
	 * @return <code>true</code> if the collection contains a single reference or multiple references to the same instance,
	 *         <code>false</code> else
	 */
	public static boolean hasUniqueObject(Collection<?> collection) {
		if (CollectionUtils.isEmpty(collection)) {
			return false;
		}
		boolean hasCandidate = false;
		Object candidate = null;
		for (final Object elem : collection) {
			if (!hasCandidate) {
				hasCandidate = true;
				candidate = elem;
			}
			else if (candidate != elem) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Return <code>true</code> if the supplied Collection is <code>null</code> or empty. Otherwise, return <code>false</code>.
	 * 
	 * @param collection
	 *            the Collection to check
	 * @return whether the given Collection is empty
	 */
	public static boolean isEmpty(Collection<?> collection) {
		return ((collection == null) || collection.isEmpty());
	}

	/**
	 * Return <code>true</code> if the supplied Map is <code>null</code> or empty. Otherwise, return <code>false</code>.
	 * 
	 * @param map
	 *            the Map to check
	 * @return whether the given Map is empty
	 */
	public static boolean isEmpty(Map<?, ?> map) {
		return ((map == null) || map.isEmpty());
	}

	/**
	 * Merge the given array into the given Collection.
	 * 
	 * @param array
	 *            the array to merge (may be <code>null</code>)
	 * @param collection
	 *            the target Collection to merge the array into
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void mergeArrayIntoCollection(Object array, Collection collection) {
		if (collection == null) {
			throw new IllegalArgumentException("Collection must not be null");
		}
		final Object[] arr = ObjectUtils.toObjectArray(array);
		for (final Object elem : arr) {
			collection.add(elem);
		}
	}

	/**
	 * Merge the given Properties instance into the given Map, copying all properties (key-value pairs) over.
	 * <p>
	 * Uses <code>Properties.propertyNames()</code> to even catch default properties linked into the original Properties instance.
	 * 
	 * @param props
	 *            the Properties instance to merge (may be <code>null</code>)
	 * @param map
	 *            the target Map to merge the properties into
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void mergePropertiesIntoMap(Properties props, Map map) {
		if (map == null) {
			throw new IllegalArgumentException("Map must not be null");
		}
		if (props != null) {
			for (final Enumeration<?> en = props.propertyNames(); en.hasMoreElements();) {
				final String key = (String) en.nextElement();
				Object value = props.getProperty(key);
				if (value == null) {
					// Potentially a non-String value...
					value = props.get(key);
				}
				map.put(key, value);
			}
		}
	}

	/**
	 * Marshal the elements from the given enumeration into an array of the given type. Enumeration elements must be assignable to the type
	 * of the given array. The array returned will be a different instance than the array given.
	 * 
	 * @param enumeration
	 *            the enumeration
	 * @param array
	 *            the array
	 * @return the array representation of the enumeration
	 * @param <A>
	 *            the type of the array
	 * @param <E>
	 *            the type of th enumeration
	 */
	public static <A, E extends A> A[] toArray(Enumeration<E> enumeration, A[] array) {
		final ArrayList<A> elements = new ArrayList<A>();
		while (enumeration.hasMoreElements()) {
			elements.add(enumeration.nextElement());
		}
		return elements.toArray(array);
	}

	/**
	 * Adapt an enumeration to an iterator.
	 * 
	 * @param enumeration
	 *            the enumeration
	 * @return the iterator
	 * @param <E>
	 *            the type of the iterator
	 */
	public static <E> Iterator<E> toIterator(Enumeration<E> enumeration) {
		return new EnumerationIterator<E>(enumeration);
	}

	/**
	 * Adapts a {@code Map<K, List<V>>} to an {@code MultiValueMap<K,V>}.
	 * 
	 * @param map
	 *            the map
	 * @return the multi-value map
	 * @param <K>
	 *            the key type of the map
	 * @param <V>
	 *            the value type of the map
	 */
	public static <K, V> MultiValueMap<K, V> toMultiValueMap(Map<K, List<V>> map) {
		return new MultiValueMapAdapter<K, V>(map);

	}

	/**
	 * Returns an unmodifiable view of the specified multi-value map.
	 * 
	 * @param map
	 *            the map for which an unmodifiable view is to be returned.
	 * @return an unmodifiable view of the specified multi-value map.
	 * @param <K>
	 *            the key type of the map
	 * @param <V>
	 *            the value type of the map
	 */
	public static <K, V> MultiValueMap<K, V> unmodifiableMultiValueMap(MultiValueMap<? extends K, ? extends V> map) {
		Assert.notNull(map, "'map' must not be null");
		final Map<K, List<V>> result = new LinkedHashMap<K, List<V>>(map.size());

		for (final Map.Entry<? extends K, ? extends List<? extends V>> entry : map.entrySet()) {
			final List<V> values = Collections.unmodifiableList(entry.getValue());
			result.put(entry.getKey(), values);
		}

		final Map<K, List<V>> unmodifiableMap = Collections.unmodifiableMap(result);
		return CollectionUtils.toMultiValueMap(unmodifiableMap);
	}

}
