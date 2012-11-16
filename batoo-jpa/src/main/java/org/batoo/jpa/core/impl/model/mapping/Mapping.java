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
package org.batoo.jpa.core.impl.model.mapping;

import org.batoo.jpa.core.impl.model.MetamodelImpl;
import org.batoo.jpa.core.impl.model.attribute.AttributeImpl;
import org.batoo.jpa.core.impl.model.type.EntityTypeImpl;
import org.batoo.jpa.core.jdbc.IdType;

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
		this.inherited = (this.entity != null) && (this.entity.getRootType() != this.entity);
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

		if (obj == null) {
			return false;
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
	 */
	public final X get(Object instance) {
		if (instance == null) {
			return null;
		}

		if (!this.root && !instance.getClass().isAssignableFrom(this.getParent().getAttribute().getJavaType())) {
			instance = this.getParent().get(instance);
		}

		return this.attribute.get(instance);
	}

	/**
	 * Returns the attribute of the mapping.
	 * 
	 * @return the attribute of the mapping
	 * 
	 * @since $version
	 */
	public abstract AttributeImpl<? super Z, X> getAttribute();

	/**
	 * Returns the id type of the mapping.
	 * 
	 * @return the id type of the mapping or <code>null</code>
	 * 
	 * @since $version
	 */
	public IdType getIdType() {
		return null;
	}

	/**
	 * Returns the javaType of the mapping.
	 * 
	 * @return the javaType of the mapping
	 * 
	 * @since $version
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

		this.h = sb.toString().hashCode();

		return this.h;
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
	 */
	public final void set(Object instance, Object value) {
		if (!this.root) {
			Z newInstance = this.parent.get(instance);

			if (newInstance == null) {
				newInstance = ((EmbeddedMapping<?, Z>) this.parent).getAttribute().newInstance();
				this.parent.set(instance, newInstance);
				instance = newInstance;
			}
			else {
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
