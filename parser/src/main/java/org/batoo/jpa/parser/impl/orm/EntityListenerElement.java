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
 * @since 2.0.0
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
		 * @since 2.0.0
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
		 * @since 2.0.0
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
		 * @since 2.0.0
			 */
		@Override
		public EntityListenerType getType() {
			return this.type;
		}
	}

	/**
	 * 
	 * @since 2.0.0
	 */
	public static class PostLoadElement extends CallbackElement {

		/**
		 * @param parent
		 *            the parent element factory
		 * @param attributes
		 *            the attributes
		 * 
		 * @since 2.0.0
			 */
		public PostLoadElement(ParentElement parent, Map<String, String> attributes) {
			super(EntityListenerType.POST_LOAD, parent, attributes);
		}
	};

	/**
	 * 
	 * @since 2.0.0
	 */
	public static class PostPersistElement extends CallbackElement {

		/**
		 * @param parent
		 *            the parent element factory
		 * @param attributes
		 *            the attributes
		 * 
		 * @since 2.0.0
			 */
		public PostPersistElement(ParentElement parent, Map<String, String> attributes) {
			super(EntityListenerType.POST_PERSIST, parent, attributes);
		}
	};

	/**
	 * 
	 * @since 2.0.0
	 */
	public static class PostRemoveElement extends CallbackElement {

		/**
		 * @param parent
		 *            the parent element factory
		 * @param attributes
		 *            the attributes
		 * 
		 * @since 2.0.0
			 */
		public PostRemoveElement(ParentElement parent, Map<String, String> attributes) {
			super(EntityListenerType.POST_REMOVE, parent, attributes);
		}
	};

	/**
	 * 
	 * @since 2.0.0
	 */
	public static class PostUpdateElement extends CallbackElement {

		/**
		 * @param parent
		 *            the parent element factory
		 * @param attributes
		 *            the attributes
		 * 
		 * @since 2.0.0
			 */
		public PostUpdateElement(ParentElement parent, Map<String, String> attributes) {
			super(EntityListenerType.PRE_UPDATE, parent, attributes);
		}
	};

	/**
	 * 
	 * @since 2.0.0
	 */
	public static class PrePersistElement extends CallbackElement {

		/**
		 * @param parent
		 *            the parent element factory
		 * @param attributes
		 *            the attributes
		 * 
		 * @since 2.0.0
			 */
		public PrePersistElement(ParentElement parent, Map<String, String> attributes) {
			super(EntityListenerType.PRE_PERSIST, parent, attributes);
		}
	};

	/**
	 * 
	 * @since 2.0.0
	 */
	public static class PreRemoveElement extends CallbackElement {

		/**
		 * @param parent
		 *            the parent element factory
		 * @param attributes
		 *            the attributes
		 * 
		 * @since 2.0.0
			 */
		public PreRemoveElement(ParentElement parent, Map<String, String> attributes) {
			super(EntityListenerType.PRE_REMOVE, parent, attributes);
		}
	};

	/**
	 * 
	 * @since 2.0.0
	 */
	public static class PreUpdateElement extends CallbackElement {

		/**
		 * @param parent
		 *            the parent element factory
		 * @param attributes
		 *            the attributes
		 * 
		 * @since 2.0.0
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
	 * @since 2.0.0
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
