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
