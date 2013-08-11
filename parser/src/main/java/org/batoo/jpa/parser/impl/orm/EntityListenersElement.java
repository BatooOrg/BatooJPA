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

import org.batoo.jpa.parser.metadata.EntityListenerMetadata;

import com.google.common.collect.Lists;

/**
 * Element for <code>entity-listeners</code> elements.
 * 
 * @author hceylan
 * @since 2.0.0
 */
public class EntityListenersElement extends ParentElement {

	private final List<EntityListenerMetadata> listeners = Lists.newArrayList();

	/**
	 * @param parent
	 *            the parent element factory
	 * @param attributes
	 *            the attributes
	 * 
	 * @since 2.0.0
	 */
	public EntityListenersElement(ParentElement parent, Map<String, String> attributes) {
		super(parent, attributes, ElementConstants.ELEMENT_ENTITY_LISTENER);
	}

	/**
	 * Returns the listeners of the EntityListenersElement.
	 * 
	 * @return the listeners of the EntityListenersElement
	 * 
	 * @since 2.0.0
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
