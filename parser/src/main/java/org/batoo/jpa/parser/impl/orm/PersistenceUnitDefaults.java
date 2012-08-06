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

import java.util.List;
import java.util.Map;

import javax.persistence.AccessType;

import org.batoo.jpa.parser.impl.orm.CascadesElement.CascadePersistElement;
import org.batoo.jpa.parser.metadata.EntityListenerMetadata;

import com.google.common.collect.Lists;

/**
 * 
 * 
 * @author hceylan
 * @since $version
 */
public class PersistenceUnitDefaults extends ParentElement {

	private final List<EntityListenerMetadata> listeners = Lists.newArrayList();
	private String catalog;
	private String schema;
	private boolean cascadePersist = false;
	private AccessType accessType = AccessType.FIELD;

	/**
	 * @param parent
	 *            the parent
	 * @param attributes
	 *            the attributes
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public PersistenceUnitDefaults(ParentElement parent, Map<String, String> attributes) {
		super(parent, attributes, //
			ElementConstants.ELEMENT_ACCESS, //
			ElementConstants.ELEMENT_CATALOG, //
			ElementConstants.ELEMENT_SCHEMA, //
			ElementConstants.ELEMENT_CASCADE_PERSIST, //
			ElementConstants.ELEMENT_CASCADE_PERSIST, //
			ElementConstants.ELEMENT_ENTITY_LISTENERS);
	}

	/**
	 * Returns the accessType.
	 * 
	 * @return the accessType
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public AccessType getAccessType() {
		return this.accessType;
	}

	/**
	 * Returns the catalog.
	 * 
	 * @return the catalog
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public String getCatalog() {
		return this.catalog;
	}

	/**
	 * Returns the listeners.
	 * 
	 * @return the listeners
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public List<EntityListenerMetadata> getListeners() {
		return this.listeners;
	}

	/**
	 * Returns the schema.
	 * 
	 * @return the schema
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public String getSchema() {
		return this.schema;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	protected void handleChild(Element child) {
		if (child instanceof AccessElement) {
			this.accessType = ((AccessElement) child).getAccessType();
		}

		if (child instanceof CatalogElement) {
			this.catalog = ((CatalogElement) child).getCatalog();
		}

		if (child instanceof SchemaElement) {
			this.schema = ((SchemaElement) child).getSchema();
		}

		if (child instanceof CascadePersistElement) {
			this.cascadePersist = true;
		}

		if (child instanceof EntityListenersElement) {
			this.listeners.addAll(((EntityListenersElement) child).getListeners());
		}
	}

	/**
	 * Returns the cascadePersist.
	 * 
	 * @return the cascadePersist
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public boolean isCascadePersist() {
		return this.cascadePersist;
	}
}
