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
 * Element factory for <code>cascade</code> elements.
 * 
 * @author hceylan
 * @since $version
 */
public class CascadesElementFactory extends ParentElementFactory {

	/**
	 * Element factory for <code>cascade-all</code> elements.
	 * 
	 * @author hceylan
	 * @since $version
	 */
	public static class CascadeAllElementFactory extends CascadeElementFactory {

		/**
		 * @param parent
		 *            the parent element factory
		 * @param attributes
		 *            the attributes
		 * 
		 * @since $version
		 * @author hceylan
		 */
		public CascadeAllElementFactory(ParentElementFactory parent, Map<String, String> attributes) {
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
	 * Element factory for <code>cascade-detach</code> elements.
	 * 
	 * @author hceylan
	 * @since $version
	 */
	public static class CascadeDetachElementFactory extends CascadeElementFactory {

		/**
		 * @param parent
		 *            the parent element factory
		 * @param attributes
		 *            the attributes
		 * 
		 * @since $version
		 * @author hceylan
		 */
		public CascadeDetachElementFactory(ParentElementFactory parent, Map<String, String> attributes) {
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

	private abstract static class CascadeElementFactory extends ChildElementFactory {

		/**
		 * @param parent
		 *            the parent element factory
		 * @param attributes
		 *            the attributes
		 * 
		 * @since $version
		 * @author hceylan
		 */
		public CascadeElementFactory(ParentElementFactory parent, Map<String, String> attributes) {
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
	 * Element factory for <code>cascade-merge</code> elements.
	 * 
	 * @author hceylan
	 * @since $version
	 */
	public static class CascadeMergeElementFactory extends CascadeElementFactory {

		/**
		 * @param parent
		 *            the parent element factory
		 * @param attributes
		 *            the attributes
		 * 
		 * @since $version
		 * @author hceylan
		 */
		public CascadeMergeElementFactory(ParentElementFactory parent, Map<String, String> attributes) {
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
	 * Element factory for <code>cascade-persist</code> elements.
	 * 
	 * @author hceylan
	 * @since $version
	 */
	public static class CascadePersistElementFactory extends CascadeElementFactory {

		/**
		 * @param parent
		 *            the parent element factory
		 * @param attributes
		 *            the attributes
		 * 
		 * @since $version
		 * @author hceylan
		 */
		public CascadePersistElementFactory(ParentElementFactory parent, Map<String, String> attributes) {
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
	 * Element factory for <code>cascade-refresh</code> elements.
	 * 
	 * @author hceylan
	 * @since $version
	 */
	public static class CascadeRefreshElementFactory extends CascadeElementFactory {

		/**
		 * @param parent
		 *            the parent element factory
		 * @param attributes
		 *            the attributes
		 * 
		 * @since $version
		 * @author hceylan
		 */
		public CascadeRefreshElementFactory(ParentElementFactory parent, Map<String, String> attributes) {
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
	 * Element factory for <code>cascade-remove</code> elements.
	 * 
	 * @author hceylan
	 * @since $version
	 */
	public static class CascadeRemoveElementFactory extends CascadeElementFactory {

		/**
		 * @param parent
		 *            the parent element factory
		 * @param attributes
		 *            the attributes
		 * 
		 * @since $version
		 * @author hceylan
		 */
		public CascadeRemoveElementFactory(ParentElementFactory parent, Map<String, String> attributes) {
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
	public CascadesElementFactory(ParentElementFactory parent, Map<String, String> attributes) {
		super(parent, attributes, ElementFactoryConstants.ELEMENT_TEMPORAL, //
			ElementFactoryConstants.ELEMENT_COLUMN);
	}

	/**
	 * Returns the cascades of the CascadesElementFactory.
	 * 
	 * @return the cascades of the CascadesElementFactory
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
	protected void handleChild(ElementFactory child) {
		this.getCascades().add(((CascadeElementFactory) child).getCascadeType());
	}
}
