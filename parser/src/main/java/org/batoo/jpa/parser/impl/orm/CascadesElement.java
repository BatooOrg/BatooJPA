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
package org.batoo.jpa.parser.impl.orm;

import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;

import com.google.common.collect.Sets;

/**
 * Element for <code>cascade</code> elements.
 * 
 * @author hceylan
 * @since $version
 */
public class CascadesElement extends ParentElement {

	/**
	 * Element for <code>cascade-all</code> elements.
	 * 
	 * @author hceylan
	 * @since $version
	 */
	public static class CascadeAllElement extends CascadeElement {

		/**
		 * @param parent
		 *            the parent element factory
		 * @param attributes
		 *            the attributes
		 * 
		 * @since $version
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
	 * @since $version
	 */
	public static class CascadeDetachElement extends CascadeElement {

		/**
		 * @param parent
		 *            the parent element factory
		 * @param attributes
		 *            the attributes
		 * 
		 * @since $version
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
		 * @since $version
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
		 * @since $version
		 * @author hceylan
		 */
		public abstract CascadeType getCascadeType();
	}

	/**
	 * Element for <code>cascade-merge</code> elements.
	 * 
	 * @author hceylan
	 * @since $version
	 */
	public static class CascadeMergeElement extends CascadeElement {

		/**
		 * @param parent
		 *            the parent element factory
		 * @param attributes
		 *            the attributes
		 * 
		 * @since $version
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
	 * @since $version
	 */
	public static class CascadePersistElement extends CascadeElement {

		/**
		 * @param parent
		 *            the parent element factory
		 * @param attributes
		 *            the attributes
		 * 
		 * @since $version
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
	 * @since $version
	 */
	public static class CascadeRefreshElement extends CascadeElement {

		/**
		 * @param parent
		 *            the parent element factory
		 * @param attributes
		 *            the attributes
		 * 
		 * @since $version
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
	 * @since $version
	 */
	public static class CascadeRemoveElement extends CascadeElement {

		/**
		 * @param parent
		 *            the parent element factory
		 * @param attributes
		 *            the attributes
		 * 
		 * @since $version
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
	 * @since $version
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
	 * @since $version
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
