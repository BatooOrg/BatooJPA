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
package org.batoo.common.log;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.SystemUtils;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * Extended version of {@link ReflectionToStringBuilder} to further style and inline the children elements.
 * 
 * @author hceylan
 * @since 2.0.1
 */
public class ToStringBuilder extends ReflectionToStringBuilder {

	/**
	 * The detail level
	 * 
	 * @author hceylan
	 * @since 2.0.1
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

	@SuppressWarnings("rawtypes")
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
		protected void appendDetail(StringBuffer buffer, String fieldName, Collection coll) {
			final String indent = this.indent();

			buffer.append("{");

			try {
				for (final Iterator i = coll.iterator(); i.hasNext();) {
					buffer.append(SystemUtils.LINE_SEPARATOR);
					buffer.append(indent);
					buffer.append("  ");

					buffer.append(i.next());

					if (i.hasNext()) {
						buffer.append(",");
					}
				}
			}
			finally {
				ToStringBuilder.indent.set(ToStringBuilder.indent.get().substring(2));
			}

			buffer.append("}");
		}

		/**
		 * {@inheritDoc}
		 * 
		 */
		@Override
		protected void appendDetail(StringBuffer buffer, String fieldName, Map map) {
			final String indent = this.indent();

			buffer.append("{");

			try {
				for (final Iterator i = map.entrySet().iterator(); i.hasNext();) {
					buffer.append(SystemUtils.LINE_SEPARATOR);
					buffer.append(indent);
					buffer.append("  ");

					final Entry entry = (Entry) i.next();
					buffer.append(entry.getKey());
					buffer.append("=");
					buffer.append(entry.getValue());

					if (i.hasNext()) {
						buffer.append(",");
					}
				}
			}
			finally {
				ToStringBuilder.indent.set(ToStringBuilder.indent.get().substring(2));
			}

			buffer.append("}");
		}

		/**
		 * {@inheritDoc}
		 * 
		 */
		@Override
		public void appendEnd(StringBuffer buffer, Object object) {
			final String indent = ToStringBuilder.indent.get();
			ToStringBuilder.indent.set(indent.length() == 0 ? null : indent.substring(2));

			super.appendEnd(buffer, object);
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

		/**
		 * {@inheritDoc}
		 * 
		 */
		@Override
		public void appendStart(StringBuffer buffer, Object object) {
			this.indent();

			super.appendStart(buffer, object);
		}

		private String indent() {
			final String indent = ToStringBuilder.indent.get();

			ToStringBuilder.indent.set(indent != null ? indent + "  " : "");

			return indent;
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
	 * @since 2.0.1
	 */
	public static void setDetailLevel(DetailLevel level) {
		ToStringBuilder.level.set(level);
	}

	/**
	 * @param object
	 *            the instance
	 * 
	 * @since 2.0.1
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
	 * @since 2.0.1
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
	 * @since 2.0.1
	 */
	public ToStringBuilder excludeFieldNames(String... fieldNames) {
		super.setExcludeFieldNames(fieldNames);

		return this;
	}
}
