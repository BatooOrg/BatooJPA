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
package org.batoo.jpa.parser.impl.orm;

import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;

import com.google.common.collect.Sets;

/**
 * Element for <code>cascade</code> elements.
 * 
 * @author hceylan
 * @since 2.0.0
 */
public class CascadesElement extends ParentElement {

	/**
	 * Element for <code>cascade-all</code> elements.
	 * 
	 * @author hceylan
	 * @since 2.0.0
	 */
	public static class CascadeAllElement extends CascadeElement {

		/**
		 * @param parent
		 *            the parent element factory
		 * @param attributes
		 *            the attributes
		 * 
		 * @since 2.0.0
		 * @author hceylan
		 */
		public CascadeAllElement(ParentElement parent, Map<String, String> attributes) {
			super(parent, attributes);
		}

		/**
		 * {@inheritDoc}
		 * 
		 */
		@Override
		public CascadeType getCascadeType() {
			return CascadeType.ALL;
		}

	}

	/**
	 * Element for <code>cascade-detach</code> elements.
	 * 
	 * @author hceylan
	 * @since 2.0.0
	 */
	public static class CascadeDetachElement extends CascadeElement {

		/**
		 * @param parent
		 *            the parent element factory
		 * @param attributes
		 *            the attributes
		 * 
		 * @since 2.0.0
		 * @author hceylan
		 */
		public CascadeDetachElement(ParentElement parent, Map<String, String> attributes) {
			super(parent, attributes);
		}

		/**
		 * {@inheritDoc}
		 * 
		 */
		@Override
		public CascadeType getCascadeType() {
			return CascadeType.DETACH;
		}

	}

	private abstract static class CascadeElement extends ChildElement {

		/**
		 * @param parent
		 *            the parent element factory
		 * @param attributes
		 *            the attributes
		 * 
		 * @since 2.0.0
		 * @author hceylan
		 */
		public CascadeElement(ParentElement parent, Map<String, String> attributes) {
			super(parent, attributes);
		}

		/**
		 * Returns the cascade type of the element.
		 * 
		 * @return the cascade type of the element
		 * 
		 * @since 2.0.0
		 * @author hceylan
		 */
		public abstract CascadeType getCascadeType();
	}

	/**
	 * Element for <code>cascade-merge</code> elements.
	 * 
	 * @author hceylan
	 * @since 2.0.0
	 */
	public static class CascadeMergeElement extends CascadeElement {

		/**
		 * @param parent
		 *            the parent element factory
		 * @param attributes
		 *            the attributes
		 * 
		 * @since 2.0.0
		 * @author hceylan
		 */
		public CascadeMergeElement(ParentElement parent, Map<String, String> attributes) {
			super(parent, attributes);
		}

		/**
		 * {@inheritDoc}
		 * 
		 */
		@Override
		public CascadeType getCascadeType() {
			return CascadeType.MERGE;
		}

	}

	/**
	 * Element for <code>cascade-persist</code> elements.
	 * 
	 * @author hceylan
	 * @since 2.0.0
	 */
	public static class CascadePersistElement extends CascadeElement {

		/**
		 * @param parent
		 *            the parent element factory
		 * @param attributes
		 *            the attributes
		 * 
		 * @since 2.0.0
		 * @author hceylan
		 */
		public CascadePersistElement(ParentElement parent, Map<String, String> attributes) {
			super(parent, attributes);
		}

		/**
		 * {@inheritDoc}
		 * 
		 */
		@Override
		public CascadeType getCascadeType() {
			return CascadeType.PERSIST;
		}

	}

	/**
	 * Element for <code>cascade-refresh</code> elements.
	 * 
	 * @author hceylan
	 * @since 2.0.0
	 */
	public static class CascadeRefreshElement extends CascadeElement {

		/**
		 * @param parent
		 *            the parent element factory
		 * @param attributes
		 *            the attributes
		 * 
		 * @since 2.0.0
		 * @author hceylan
		 */
		public CascadeRefreshElement(ParentElement parent, Map<String, String> attributes) {
			super(parent, attributes);
		}

		/**
		 * {@inheritDoc}
		 * 
		 */
		@Override
		public CascadeType getCascadeType() {
			return CascadeType.REFRESH;
		}

	}

	/**
	 * Element for <code>cascade-remove</code> elements.
	 * 
	 * @author hceylan
	 * @since 2.0.0
	 */
	public static class CascadeRemoveElement extends CascadeElement {

		/**
		 * @param parent
		 *            the parent element factory
		 * @param attributes
		 *            the attributes
		 * 
		 * @since 2.0.0
		 * @author hceylan
		 */
		public CascadeRemoveElement(ParentElement parent, Map<String, String> attributes) {
			super(parent, attributes);
		}

		/**
		 * {@inheritDoc}
		 * 
		 */
		@Override
		public CascadeType getCascadeType() {
			return CascadeType.REMOVE;
		}

	}

	private final Set<CascadeType> cascades = Sets.newHashSet();

	/**
	 * @param parent
	 *            the metamodel
	 * @param attributes
	 *            the attributes
	 * 
	 * @since 2.0.0
	 * @author hceylan
	 */
	public CascadesElement(ParentElement parent, Map<String, String> attributes) {
		super(parent, attributes, ElementConstants.ELEMENT_TEMPORAL, //
			ElementConstants.ELEMENT_COLUMN);
	}

	/**
	 * Returns the cascades of the CascadesElement.
	 * 
	 * @return the cascades of the CascadesElement
	 * 
	 * @since 2.0.0
	 * @author hceylan
	 */
	public Set<CascadeType> getCascades() {
		return this.cascades;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	protected void handleChild(Element child) {
		this.getCascades().add(((CascadeElement) child).getCascadeType());
	}
}
