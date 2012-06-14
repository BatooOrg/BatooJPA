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

import org.batoo.jpa.parser.metadata.EntityListenerMetadata;

import com.google.common.collect.Lists;

/**
 * Element for <code>entity-listeners</code> elements.
 * 
 * @author hceylan
 * @since $version
 */
public class EntityListenersElement extends ParentElement {

	private final List<EntityListenerMetadata> listeners = Lists.newArrayList();

	/**
	 * @param parent
	 *            the parent element factory
	 * @param attributes
	 *            the attributes
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public EntityListenersElement(ParentElement parent, Map<String, String> attributes) {
		super(parent, attributes, ElementConstants.ELEMENT_ENTITY_LISTENER);
	}

	/**
	 * Returns the listeners of the EntityListenersElement.
	 * 
	 * @return the listeners of the EntityListenersElement
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public List<EntityListenerMetadata> getListeners() {
		return this.listeners;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	protected void handleChild(Element child) {
		if (child instanceof EntityListenerMetadata) {
			this.listeners.add((EntityListenerMetadata) child);
		}
	}
}
