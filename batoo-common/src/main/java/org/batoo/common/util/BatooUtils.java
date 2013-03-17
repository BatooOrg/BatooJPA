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
package org.batoo.common.util;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * 
 * @author hceylan
 * @since 2.0.1
 */
public class BatooUtils {

	/**
	 * Returns the acronym of the name
	 * 
	 * @param name
	 *            the name
	 * @return the acronym
	 * 
	 * @since 2.0.1
	 */
	public static String acronym(String name) {
		final StringBuilder builder = new StringBuilder();
		for (int i = 0; i < name.length(); i++) {
			if (Character.isUpperCase(name.charAt(i))) {
				builder.append(name.charAt(i));
			}
		}

		if (builder.length() == 0) {
			builder.append(name.charAt(0));
		}

		return builder.toString();
	}

	/**
	 * Adds all the elements in the source to target.
	 * 
	 * @param source
	 *            the source collection
	 * @param target
	 *            the destination collection
	 * @param <E>
	 *            the type of the collections
	 * 
	 * @since 2.0.1
	 */
	public static <E> void addAll(Collection<? extends E> source, Collection<E> target) {
		if (source instanceof List) {
			final List<? extends E> list = (List<? extends E>) source;
			for (int i = 0; i < list.size(); i++) {
				target.add(list.get(i));
			}
		}
		else {
			target.addAll(source);
		}
	}

	/**
	 * Indents the <code>string</code> by one <code>tab</code>.
	 * 
	 * @param str
	 *            string to indent
	 * @return the indented string
	 * 
	 * @since 2.0.1
	 */
	public static String indent(String str) {
		if (StringUtils.isBlank(str)) {
			return "";
		}

		return "\t" + str.replaceAll("\n", "\n\t");
	}

	/**
	 * Converts the string to lower case.
	 * 
	 * @param string
	 *            the string to convert
	 * @return the converted string
	 * 
	 * @since 2.0.1
	 */
	public static String lower(String string) {
		return string != null ? string.toLowerCase() : null;
	}

	/**
	 * @param a
	 *            the collection a
	 * @param b
	 *            the collection b
	 * @return the subtracted collection
	 * @param <X>
	 *            the type of the collecions
	 * 
	 * @since 2.0.1
	 */
	public static <X> List<X> subtract(final Collection<X> a, final Collection<X> b) {
		final List<X> list = Lists.newArrayList(a);

		for (final Object element : b) {
			list.remove(element);
		}

		return list;
	}

	/**
	 * 
	 * @param <X>
	 *            type of key
	 * @param <Y>
	 *            type of value
	 * @param a
	 *            first map
	 * @param b
	 *            second map
	 * @return the subtracted map
	 * @since 2.0.1
	 */
	public static <X, Y> Map<X, Y> subtract(final Map<X, Y> a, final Map<X, Y> b) {
		final Map<X, Y> map = Maps.newHashMap();
		for (final X key : a.keySet()) {
			if (!(b.containsKey(key) && b.get(key).equals(a.get(key)))) {
				map.put(key, b.get(key));
			}
		}
		return map;
	}

	/**
	 * Indents the <code>string</code> by one <code>tab</code>.
	 * 
	 * @param str
	 *            string to indent
	 * @return the indented string
	 * 
	 * @since 2.0.1
	 */
	public static String tree(String str) {
		if (StringUtils.isBlank(str)) {
			return "";
		}

		return "|-->" + str.replaceAll("\n", "\n|   ");
	}

	/**
	 * Converts the string to upper case.
	 * 
	 * @param string
	 *            the string to convert
	 * @return the converted string
	 * 
	 * @since 2.0.1
	 */
	public static String upper(String string) {
		return string != null ? string.toUpperCase() : null;
	}
}
