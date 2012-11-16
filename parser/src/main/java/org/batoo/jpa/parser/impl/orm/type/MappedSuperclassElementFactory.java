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
package org.batoo.jpa.parser.impl.orm.type;

import java.util.List;
import java.util.Map;

import javax.persistence.AccessType;

import org.batoo.jpa.parser.impl.orm.Element;
import org.batoo.jpa.parser.impl.orm.ElementConstants;
import org.batoo.jpa.parser.impl.orm.EntityListenersElement;
import org.batoo.jpa.parser.impl.orm.EntityMappings;
import org.batoo.jpa.parser.impl.orm.ExcludeDefaultListenersElement;
import org.batoo.jpa.parser.impl.orm.ExcludeSuperclassListenersElement;
import org.batoo.jpa.parser.impl.orm.IdClassElement;
import org.batoo.jpa.parser.impl.orm.ParentElement;
import org.batoo.jpa.parser.impl.orm.attribute.AttributesElement;
import org.batoo.jpa.parser.metadata.CallbackMetadata;
import org.batoo.jpa.parser.metadata.EntityListenerMetadata;
import org.batoo.jpa.parser.metadata.type.MappedSuperclassMetadata;

import com.google.common.collect.Lists;

/**
 * Element for <code>mapped-superclass</code> elements.
 * 
 * @author hceylan
 * @since 2.0.0
 */
public class MappedSuperclassElementFactory extends ParentElement implements MappedSuperclassMetadata {

	private String className;
	private boolean metadataComplete;
	private AccessType accessType;
	private AttributesElement attrs;
	private String idClass;
	private boolean excludeDefaultListeners;
	private boolean excludeSuperclassListeners;
	private final List<EntityListenerMetadata> listeners = Lists.newArrayList();
	private final List<CallbackMetadata> callbacks = Lists.newArrayList();

	/**
	 * Constructor for ORM File parsing
	 * 
	 * @param parent
	 *            the metamodel
	 * @param attributes
	 *            the attributes
	 * 
	 * @since 2.0.0
	 * @author hceylan
	 */
	public MappedSuperclassElementFactory(ParentElement parent, Map<String, String> attributes) {
		super(parent, attributes, //
			ElementConstants.ELEMENT_ATTRIBUTES, //
			ElementConstants.ELEMENT_ID_CLASS, //
			ElementConstants.ELEMENT_ENTITY_LISTENERS, //
			ElementConstants.ELEMENT_PRE_PERSIST, //
			ElementConstants.ELEMENT_PRE_REMOVE, //
			ElementConstants.ELEMENT_PRE_UPDATE, //
			ElementConstants.ELEMENT_POST_LOAD, //
			ElementConstants.ELEMENT_POST_PERSIST, //
			ElementConstants.ELEMENT_POST_REMOVE, //
			ElementConstants.ELEMENT_POST_UPDATE, //
			ElementConstants.ELEMENT_EXCLUDE_DEFAULT_LISTENERS, //
			ElementConstants.ELEMENT_EXCLUDE_SUPERCLASS_LISTENERS);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean excludeDefaultListeners() {
		return this.excludeDefaultListeners;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean excludeSuperclassListeners() {
		return this.excludeSuperclassListeners;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	protected void generate() {
		this.className = this.getAttribute(ElementConstants.ATTR_CLASS, ElementConstants.EMPTY);
		this.metadataComplete = this.getAttribute(ElementConstants.ATTR_METADATA_COMPLETE, false);
		this.accessType = this.getAttribute(ElementConstants.ATTR_ACCESS) != null ? AccessType.valueOf(this.getAttribute(ElementConstants.ATTR_ACCESS)) : null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public AccessType getAccessType() {
		return this.accessType != null ? this.accessType : ((EntityMappings) this.getParent()).getAccessType();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public AttributesElement getAttributes() {
		return this.attrs;
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
		return this.className;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getIdClass() {
		return this.idClass;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public List<EntityListenerMetadata> getListeners() {
		return this.listeners;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	protected void handleChild(Element child) {
		if (child instanceof AttributesElement) {
			this.attrs = (AttributesElement) child;
		}

		if (child instanceof IdClassElement) {
			this.idClass = ((IdClassElement) child).getIdClass();
		}

		if (child instanceof EntityListenersElement) {
			this.listeners.addAll(((EntityListenersElement) child).getListeners());
		}

		if (child instanceof CallbackMetadata) {
			this.callbacks.add((CallbackMetadata) this.callbacks);
		}

		if (child instanceof ExcludeDefaultListenersElement) {
			this.excludeDefaultListeners = true;
		}

		if (child instanceof ExcludeSuperclassListenersElement) {
			this.excludeSuperclassListeners = true;
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean isMetadataComplete() {
		return this.metadataComplete;
	}
}
