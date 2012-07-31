/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.batoo.jpa.core.util;

import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.google.common.collect.Lists;

/**
 * 
 * 
 * @author hceylan
 * @since $version
 */
public class BatooUtils {

	/**
	 * Returns the acronym of the name
	 * 
	 * @param name
	 *            the name
	 * @return the acronym
	 * 
	 * @since $version
	 * @author hceylan
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
	 * Indents the <code>string</code> by one <code>tab</code>.
	 * 
	 * @param str
	 *            string to indent
	 * @return the indented string
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public static String indent(String str) {
		if (StringUtils.isBlank(str)) {
			return "";
		}

		return "\t" + str.replaceAll("\n", "\n\t");
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
	 * @since $version
	 * @author hceylan
	 */
	public static <X> Collection<X> subtract(final Collection<X> a, final Collection<X> b) {
		final List<X> list = Lists.newArrayList(a);

		for (final Object element : b) {
			list.remove(element);
		}

		return list;
	}

	/**
	 * Indents the <code>string</code> by one <code>tab</code>.
	 * 
	 * @param str
	 *            string to indent
	 * @return the indented string
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public static String tree(String str) {
		if (StringUtils.isBlank(str)) {
			return "";
		}

		return "|-->" + str.replaceAll("\n", "\n|   ");
	}
}
