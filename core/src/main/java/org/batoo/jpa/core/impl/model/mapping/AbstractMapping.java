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
package org.batoo.jpa.core.impl.model.mapping;

import org.batoo.jpa.core.impl.instance.ManagedInstance;
import org.batoo.jpa.core.impl.manager.SessionImpl;
import org.batoo.jpa.core.impl.model.attribute.AttributeImpl;
import org.batoo.jpa.core.impl.model.type.EntityTypeImpl;

/**
 * The base class for the mappings.
 * 
 * @param <X>
 *            the type of the entity
 * @param <Y>
 *            the type of the value
 * 
 * @author hceylan
 * @since $version
 */
public abstract class AbstractMapping<X, Y> {

	private final EmbeddedMapping<?, ?> parent;
	private final EntityTypeImpl<X> entity;
	private final boolean inherited;

	/**
	 * 
	 * @param parent
	 *            the parent mapping, may be <code>null</code>
	 * @param entity
	 *            the entity
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public AbstractMapping(EmbeddedMapping<?, ?> parent, EntityTypeImpl<X> entity) {
		super();

		this.parent = parent;
		this.entity = entity;
		this.inherited = entity.getRootType().getInheritanceType() != null;
	}

	/**
	 * Returns the mapping value of instance.
	 * 
	 * @param instance
	 *            the instance of which the value to be returned
	 * @return the mapping value of instance
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public final Y get(Object instance) {
		if (this.getParent() != null) {
			instance = this.getParent().get(instance);

			if (instance == null) {
				return null;
			}
		}

		return this.getAttribute().get(instance);
	}

	/**
	 * Returns the attribute of the mapping.
	 * 
	 * @return the attribute of the mapping
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public abstract AttributeImpl<? super X, Y> getAttribute();

	/**
	 * Returns the entity of the mapping.
	 * 
	 * @return the entity of the mapping
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public final EntityTypeImpl<X> getEntity() {
		return this.entity;
	}

	/**
	 * Returns the parent of the AbstractMapping.
	 * 
	 * @return the parent of the AbstractMapping
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public EmbeddedMapping<?, ?> getParent() {
		return this.parent;
	}

	/**
	 * Returns the root of the mapping.
	 * 
	 * @return the root mapping
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public final AbstractMapping<?, ?> getRootMapping() {
		AbstractMapping<?, ?> rootMapping = this;
		while (rootMapping.getParent() != null) {
			rootMapping = rootMapping.getParent();
		}

		return rootMapping;
	}

	/**
	 * Sets the mapping value of instance.
	 * 
	 * @param managedInstance
	 *            the instance of which the value to set
	 * @param value
	 *            the value to set
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public final void set(ManagedInstance<?> managedInstance, Object value) {
		Object instance = managedInstance.getInstance();
		if (this.getParent() != null) {
			instance = this.getParent().get(managedInstance.getInstance());

			if (instance == null) {
				instance = this.getParent().getAttribute().newInstance();
				this.getParent().set(managedInstance, instance);
			}
		}

		if (!this.inherited || managedInstance.getType().doesExtend(this.entity)) {
			this.set(managedInstance.getSession(), managedInstance.getId(), instance, value);
		}
	}

	/**
	 * Sets the mapping value of the instance
	 * 
	 * @param session
	 *            the session
	 * @param id
	 *            the id of the root instance
	 * @param instance
	 *            the instance of which the value to set
	 * @param value
	 *            the value to set
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void set(SessionImpl session, Object id, Object instance, Object value) {
		this.getAttribute().set(instance, value);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String toString() {
		return this.getAttribute().toString();
	}
}
