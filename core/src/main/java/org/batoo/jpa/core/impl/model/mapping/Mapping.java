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

import org.batoo.jpa.core.impl.model.MetamodelImpl;
import org.batoo.jpa.core.impl.model.attribute.AttributeImpl;
import org.batoo.jpa.core.impl.model.type.EntityTypeImpl;

/**
 * The base implementation of mappings.
 * 
 * @param <Z>
 *            the source type
 * @param <X>
 *            the destination type
 * @param <Y>
 *            the attribute type
 * 
 * @author hceylan
 * @since $version
 */
public abstract class Mapping<Z, X, Y> {

	private final ParentMapping<?, Z> parent;
	private final String path;
	private final Class<X> javaType;
	private final String name;
	private final MetamodelImpl metamodel;
	private final boolean root;
	private final boolean inherited;
	private final EntityTypeImpl<?> entity;
	private final AttributeImpl<? super Z, X> attribute;

	private int h;

	/**
	 * @param parent
	 *            the parent mapping
	 * @param attribute
	 *            the attribute
	 * @param javaType
	 *            the java type
	 * @param name
	 *            the name of the mapping
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public Mapping(ParentMapping<?, Z> parent, AttributeImpl<? super Z, X> attribute, Class<X> javaType, String name) {
		super();

		this.javaType = javaType;
		this.parent = parent;
		this.attribute = attribute;
		this.name = name;
		this.metamodel = attribute != null ? attribute.getMetamodel() : null;

		this.path = (parent != null) && (parent.getPath() != null) ? parent.getPath() + "." + name : name;
		this.root = parent instanceof RootMapping;
		this.entity = (EntityTypeImpl<?>) (this.getRoot().isEntity() ? this.getRoot().getType() : null);
		this.inherited = (this.entity != null) && (this.entity.getRootType().getInheritanceType() != null);
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

		final Mapping<?, ?, ?> other = (Mapping<?, ?, ?>) obj;

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
	public final X get(Object instance) {
		if (!this.root) {
			instance = this.getParent().get(instance);

			if (instance == null) {
				return null;
			}
		}

		return this.attribute.get(instance);
	}

	/**
	 * Returns the attribute of the mapping.
	 * 
	 * @return the attribute of the mapping
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public abstract AttributeImpl<? super Z, X> getAttribute();

	/**
	 * Returns the javaType of the mapping.
	 * 
	 * @return the javaType of the mapping
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public Class<X> getJavaType() {
		return this.javaType;
	}

	/**
	 * Returns the name of the Mapping.
	 * 
	 * @return the name of the Mapping
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Returns the parent of the mapping.
	 * 
	 * @return the parent of the mapping
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public ParentMapping<?, Z> getParent() {
		return this.parent;
	}

	/**
	 * Returns the path of the mapping.
	 * 
	 * @return the path of the mapping
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
	 * @return the root of the mapping
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public RootMapping<?> getRoot() {
		return this.parent.getRoot();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public int hashCode() {
		if (this.h != 0) {
			return this.h;
		}

		final StringBuilder sb = new StringBuilder(this.getRoot().getType().getName());
		if (this.path != null) {
			sb.append(".").append(this.path);
		}

		return this.h = this.path.toString().hashCode();
	}

	/**
	 * Sets the mapping value of instance.
	 * 
	 * @param instance
	 *            the instance of which the value to set
	 * @param value
	 *            the value to set
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public final void set(Object instance, Object value) {
		if (!this.root) {
			Z newInstance = this.parent.get(instance);

			if (newInstance == null) {
				newInstance = ((EmbeddedMapping<?, Z>) this.parent).getAttribute().newInstance();
				this.parent.set(instance, newInstance);
				instance = newInstance;
			}
		}

		if (!this.inherited) {
			this.attribute.set(instance, value);
		}
		else {
			final EntityTypeImpl<? extends Object> type = this.metamodel.getEntity(instance.getClass());
			if (type.extendz(this.entity)) {
				this.attribute.set(instance, value);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String toString() {
		return this.getClass().getSimpleName() + " " + this.getAttribute().toString();
	}
}
