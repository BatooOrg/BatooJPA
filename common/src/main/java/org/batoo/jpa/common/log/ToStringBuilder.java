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
package org.batoo.jpa.common.log;

import org.apache.commons.lang.SystemUtils;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * Extended version of {@link ReflectionToStringBuilder} to further style and inline the children elements.
 * 
 * @author hceylan
 * @since $version
 */
public class ToStringBuilder extends ReflectionToStringBuilder {

	/**
	 * The detail level
	 * 
	 * @author hceylan
	 * @since $version
	 */
	public enum DetailLevel {
		/**
		 * The short mode
		 */
		SHORT,

		/**
		 * The long mode
		 */
		LONG
	}

	private static final class LongToStringStyle extends ToStringStyle {

		private static final long serialVersionUID = 1L;

		LongToStringStyle() {
			super();

			this.setUseShortClassName(true);
			this.setUseIdentityHashCode(false);

			this.setContentStart(" [");
			this.setFieldSeparator(SystemUtils.LINE_SEPARATOR);
			this.setFieldSeparatorAtStart(true);
			this.setContentEnd("]");
		}

		/**
		 * {@inheritDoc}
		 * 
		 */
		@Override
		protected void appendFieldStart(StringBuffer buffer, String fieldName) {
			buffer.append(ToStringBuilder.indent.get());

			super.appendFieldStart(buffer, fieldName);
		}
	}

	private static final class ShortToStringStyle extends ToStringStyle {

		private static final long serialVersionUID = 1L;

		ShortToStringStyle() {
			super();

			this.setUseShortClassName(true);
			this.setUseIdentityHashCode(false);

			this.setContentStart(" [");
			this.setFieldSeparator(", ");
			this.setContentEnd("]");
		}
	}

	private static final ToStringStyle LONG = new LongToStringStyle();
	private static final ToStringStyle SHORT = new ShortToStringStyle();

	private static ThreadLocal<DetailLevel> level = new ThreadLocal<DetailLevel>();
	private static ThreadLocal<String> indent = new ThreadLocal<String>();

	private static ToStringStyle getStyle(final DetailLevel level) {
		if ((level == null) || (level == DetailLevel.SHORT)) {
			return ToStringBuilder.SHORT;
		}

		return ToStringBuilder.LONG;
	}

	/**
	 * Sets the context detail level.
	 * 
	 * @param level
	 *            the detail level to set
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public static void setDetailLevel(DetailLevel level) {
		ToStringBuilder.level.set(level);
	}

	/**
	 * @param object
	 *            the instance
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public ToStringBuilder(Object object) {
		super(object, ToStringBuilder.getStyle(ToStringBuilder.level.get()));
	}

	/**
	 * @param object
	 *            the instance
	 * @param level
	 *            the static detail level
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public ToStringBuilder(Object object, DetailLevel level) {
		super(object, ToStringBuilder.getStyle(level));
	}

	/**
	 * Sets the field names to exclude.
	 * 
	 * @param fieldNames
	 *            The excludeFieldNames to excluding from toString or <code>null</code>.
	 * @return <code>this</code>
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public ToStringBuilder excludeFieldNames(String... fieldNames) {
		super.setExcludeFieldNames(fieldNames);

		return this;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String toString() {
		final String depth = ToStringBuilder.indent.get() != null ? ToStringBuilder.indent.get() : "";

		ToStringBuilder.indent.set(depth + "  ");
		try {
			return super.toString();
		}
		finally {
			ToStringBuilder.indent.set(depth);
		}
	}
}
