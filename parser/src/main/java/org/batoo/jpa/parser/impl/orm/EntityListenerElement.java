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

import java.util.List;
import java.util.Map;

import org.batoo.jpa.parser.metadata.CallbackMetadata;
import org.batoo.jpa.parser.metadata.EntityListenerMetadata;

import com.google.common.collect.Lists;

/**
 * Element for <code>entity-listener</code> elements.
 * 
 * @author hceylan
 * @since $version
 */
public class EntityListenerElement extends ParentElement implements EntityListenerMetadata {

	private abstract static class CallbackElement extends ChildElement implements CallbackMetadata {

		private String name;
		private final EntityListenerType type;

		/**
		 * @param type
		 *            the callback type
		 * @param parent
		 *            the parent element factory
		 * @param attributes
		 *            the attributes
		 * 
		 * @since $version
		 * @author hceylan
		 */
		public CallbackElement(EntityListenerType type, ParentElement parent, Map<String, String> attributes) {
			super(parent, attributes);

			this.type = type;
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
		@Override
		public String getName() {
			return this.name;
		}

		/**
		 * Returns the type of the callback.
		 * 
		 * @return the type of the callback
		 * 
		 * @since $version
		 * @author hceylan
		 */
		@Override
		public EntityListenerType getType() {
			return this.type;
		}
	}

	/**
	 * 
	 * @author hceylan
	 * @since $version
	 */
	public static class PostLoadElement extends CallbackElement {

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
			super(EntityListenerType.POST_LOAD, parent, attributes);
		}
	};

	/**
	 * 
	 * @author hceylan
	 * @since $version
	 */
	public static class PostPersistElement extends CallbackElement {

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
			super(EntityListenerType.POST_PERSIST, parent, attributes);
		}
	};

	/**
	 * 
	 * @author hceylan
	 * @since $version
	 */
	public static class PostRemoveElement extends CallbackElement {

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
			super(EntityListenerType.POST_REMOVE, parent, attributes);
		}
	};

	/**
	 * 
	 * @author hceylan
	 * @since $version
	 */
	public static class PostUpdateElement extends CallbackElement {

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
			super(EntityListenerType.PRE_UPDATE, parent, attributes);
		}
	};

	/**
	 * 
	 * @author hceylan
	 * @since $version
	 */
	public static class PrePersistElement extends CallbackElement {

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
			super(EntityListenerType.PRE_PERSIST, parent, attributes);
		}
	};

	/**
	 * 
	 * @author hceylan
	 * @since $version
	 */
	public static class PreRemoveElement extends CallbackElement {

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
			super(EntityListenerType.PRE_REMOVE, parent, attributes);
		}
	};

	/**
	 * 
	 * @author hceylan
	 * @since $version
	 */
	public static class PreUpdateElement extends CallbackElement {

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
			super(EntityListenerType.PRE_UPDATE, parent, attributes);
		}
	};

	private String clazz;
	private final List<CallbackMetadata> callbacks = Lists.newArrayList();

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
	public List<CallbackMetadata> getCallbacks() {
		return this.callbacks;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getClassName() {
		return this.clazz;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	protected void handleChild(Element child) {
		this.callbacks.add((CallbackMetadata) child);
	}
}
