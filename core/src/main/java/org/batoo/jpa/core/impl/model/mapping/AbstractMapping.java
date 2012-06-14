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
	private final String path;

	/**
	 * 
	 * @param parent
	 *            the parent mapping, may be <code>null</code>
	 * @param entity
	 *            the entity
	 * @param attribute
	 *            the attribute
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public AbstractMapping(EmbeddedMapping<?, ?> parent, EntityTypeImpl<X> entity, AttributeImpl<? super X, Y> attribute) {
		super();

		this.parent = parent;
		this.entity = entity;
		this.inherited = entity.getRootType().getInheritanceType() != null;

		this.path = parent != null ? parent.getPath() + "." + attribute.getName() : this.entity.getName() + "." + attribute.getName();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		final AbstractMapping<?, ?> other = (AbstractMapping<?, ?>) obj;

		return this.getPath().equals(other.getPath());
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
	 * Returns the fully qualified path of the mapping.
	 * 
	 * @return the fully qualified path of the mapping
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public String getPath() {
		return this.path;
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
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public int hashCode() {
		return this.path.hashCode();
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
			instance = this.parent.get(managedInstance.getInstance());

			if (instance == null) {
				instance = this.parent.getAttribute().newInstance();
				this.parent.set(managedInstance, instance);
			}
		}

		if (!this.inherited || managedInstance.getType().doesExtend(this.entity)) {
			this.set(managedInstance, instance, value);
		}
	}

	/**
	 * Sets the mapping value of the instance
	 * 
	 * @param managedInstance
	 *            the managed instance
	 * @param instance
	 *            the instance of which the value to set
	 * @param value
	 *            the value to set
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void set(ManagedInstance<?> managedInstance, Object instance, Object value) {
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
