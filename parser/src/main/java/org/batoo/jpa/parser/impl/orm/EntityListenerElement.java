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

import org.batoo.jpa.parser.metadata.EntityListenerMetadata;

import com.google.common.collect.Maps;

/**
 * Element for <code>entity-listener</code> elements.
 * 
 * @author hceylan
 * @since $version
 */
public class EntityListenerElement extends ParentElement implements EntityListenerMetadata {

	private abstract static class Callback extends ChildElement {

		private String name;

		/**
		 * @param parent
		 *            the parent element factory
		 * @param attributes
		 *            the attributes
		 * 
		 * @since $version
		 * @author hceylan
		 */
		public Callback(ParentElement parent, Map<String, String> attributes) {
			super(parent, attributes);
		}

		/**
		 * {@inheritDoc}
		 * 
		 */
		@Override
		protected void generate() {
			this.name = this.getAttribute(ElementConstants.ATTR_NAME);
		}

		/**
		 * Returns the method name of the callback.
		 * 
		 * @return the method name of the callback
		 * 
		 * @since $version
		 * @author hceylan
		 */
		public String getName() {
			return this.name;
		}
	}

	/**
	 * 
	 * @author hceylan
	 * @since $version
	 */
	public static class PostLoadElement extends Callback {

		/**
		 * @param parent
		 *            the parent element factory
		 * @param attributes
		 *            the attributes
		 * 
		 * @since $version
		 * @author hceylan
		 */
		public PostLoadElement(ParentElement parent, Map<String, String> attributes) {
			super(parent, attributes);
		}
	};

	/**
	 * 
	 * @author hceylan
	 * @since $version
	 */
	public static class PostPersistElement extends Callback {

		/**
		 * @param parent
		 *            the parent element factory
		 * @param attributes
		 *            the attributes
		 * 
		 * @since $version
		 * @author hceylan
		 */
		public PostPersistElement(ParentElement parent, Map<String, String> attributes) {
			super(parent, attributes);
		}
	};

	/**
	 * 
	 * @author hceylan
	 * @since $version
	 */
	public static class PostRemoveElement extends Callback {

		/**
		 * @param parent
		 *            the parent element factory
		 * @param attributes
		 *            the attributes
		 * 
		 * @since $version
		 * @author hceylan
		 */
		public PostRemoveElement(ParentElement parent, Map<String, String> attributes) {
			super(parent, attributes);
		}
	};

	/**
	 * 
	 * @author hceylan
	 * @since $version
	 */
	public static class PostUpdateElement extends Callback {

		/**
		 * @param parent
		 *            the parent element factory
		 * @param attributes
		 *            the attributes
		 * 
		 * @since $version
		 * @author hceylan
		 */
		public PostUpdateElement(ParentElement parent, Map<String, String> attributes) {
			super(parent, attributes);
		}
	};

	/**
	 * 
	 * @author hceylan
	 * @since $version
	 */
	public static class PrePersistElement extends Callback {

		/**
		 * @param parent
		 *            the parent element factory
		 * @param attributes
		 *            the attributes
		 * 
		 * @since $version
		 * @author hceylan
		 */
		public PrePersistElement(ParentElement parent, Map<String, String> attributes) {
			super(parent, attributes);
		}
	};

	/**
	 * 
	 * @author hceylan
	 * @since $version
	 */
	public static class PreRemoveElement extends Callback {

		/**
		 * @param parent
		 *            the parent element factory
		 * @param attributes
		 *            the attributes
		 * 
		 * @since $version
		 * @author hceylan
		 */
		public PreRemoveElement(ParentElement parent, Map<String, String> attributes) {
			super(parent, attributes);
		}
	};

	/**
	 * 
	 * @author hceylan
	 * @since $version
	 */
	public static class PreUpdateElement extends Callback {

		/**
		 * @param parent
		 *            the parent element factory
		 * @param attributes
		 *            the attributes
		 * 
		 * @since $version
		 * @author hceylan
		 */
		public PreUpdateElement(ParentElement parent, Map<String, String> attributes) {
			super(parent, attributes);
		}
	};

	private String clazz;
	private final Map<EntityListenerType, String> listeners = Maps.newHashMap();

	/**
	 * @param parent
	 *            the parent element factory
	 * @param attributes
	 *            the attributes
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public EntityListenerElement(ParentElement parent, Map<String, String> attributes) {
		super(parent, attributes, //
			ElementConstants.ELEMENT_PRE_PERSIST, //
			ElementConstants.ELEMENT_PRE_REMOVE, //
			ElementConstants.ELEMENT_PRE_UPDATE, //
			ElementConstants.ELEMENT_POST_LOAD, //
			ElementConstants.ELEMENT_POST_PERSIST, //
			ElementConstants.ELEMENT_POST_REMOVE, //
			ElementConstants.ELEMENT_POST_UPDATE);

	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	protected void generate() {
		this.clazz = this.getAttribute(ElementConstants.ATTR_CLASS);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getClazz() {
		return this.clazz;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Map<EntityListenerType, String> getListeners() {
		return this.listeners;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	protected void handleChild(Element child) {
		final Callback callback = (Callback) child;

		if (child instanceof PostLoadElement) {
			this.listeners.put(EntityListenerType.POST_LOAD, callback.getName());
		}

		if (child instanceof PostPersistElement) {
			this.listeners.put(EntityListenerType.POST_PERSIST, callback.getName());
		}

		if (child instanceof PostRemoveElement) {
			this.listeners.put(EntityListenerType.POST_REMOVE, callback.getName());
		}

		if (child instanceof PostUpdateElement) {
			this.listeners.put(EntityListenerType.POST_UPDATE, callback.getName());
		}

		if (child instanceof PrePersistElement) {
			this.listeners.put(EntityListenerType.PRE_PERSIST, callback.getName());
		}

		if (child instanceof PreRemoveElement) {
			this.listeners.put(EntityListenerType.PRE_REMOVE, callback.getName());
		}

		if (child instanceof PreUpdateElement) {
			this.listeners.put(EntityListenerType.PRE_UPDATE, callback.getName());
		}

	}
}
