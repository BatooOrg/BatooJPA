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

import java.lang.reflect.Member;

import org.batoo.jpa.core.impl.model.EntityTypeImpl;
import org.batoo.jpa.core.impl.model.MetamodelImpl;
import org.batoo.jpa.core.impl.model.attribute.AttributeImpl;
import org.batoo.jpa.jdbc.mapping.Mapping;
import org.batoo.jpa.jdbc.mapping.RootMapping;
import org.batoo.jpa.parser.AbstractLocator;

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
 * @since 2.0.0
 */
public abstract class AbstractMapping<Z, X, Y> implements Mapping<Z, X, Y> {

	private final AbstractParentMapping<?, Z> parent;
	private final String path;
	private final Class<X> javaType;
	private final String name;
	private final MetamodelImpl metamodel;
	private final boolean root;
	private final boolean inherited;
	private final EntityTypeImpl<?> entity;
	private final AttributeImpl<? super Z, X> attribute;

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
	 * @since 2.0.0
	 */
	public AbstractMapping(AbstractParentMapping<?, Z> parent, AttributeImpl<? super Z, X> attribute, Class<X> javaType, String name) {
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

		final AbstractMapping<?, ?, ?> other = (AbstractMapping<?, ?, ?>) obj;

		return this.getPath().equals(other.getPath());
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public final X get(Object instance) {
		if (instance == null) {
			return null;
		}

		if (!this.root && !instance.getClass().isAssignableFrom(this.getParent().getJavaType())) {
			instance = this.getParent().get(instance);
		}

		return this.attribute.get(instance);
	}

	/**
	 * Returns the attribute of the mapping.
	 * 
	 * @return the attribute of the mapping
	 * 
	 * @since 2.0.1
	 */
	public AttributeImpl<? super Z, X> getAttribute() {
		return this.attribute;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Member getJavaMember() {
		return this.attribute.getJavaMember();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Class<X> getJavaType() {
		return this.javaType;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public AbstractLocator getLocator() {
		return this.attribute.getLocator();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getName() {
		return this.name;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public AbstractParentMapping<?, Z> getParent() {
		return this.parent;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getPath() {
		return this.path;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public RootMapping<?> getRoot() {
		return this.parent.getRoot();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public int hashCode() {
		return this.getPath().hashCode();
	}

	/**
	 * Returns if the mapping is a collection mapping.
	 * 
	 * @return <code>true</code> if the mapping is a collection mapping, <code>false</code> otherwise
	 * 
	 * @since 2.0.1
	 */
	@Override
	public boolean isCollection() {
		return this.attribute.isCollection();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public final void set(Object instance, Object value) {
		if (!this.root) {
			Z newInstance = this.parent.get(instance);

			if (newInstance == null) {
				newInstance = ((EmbeddedMappingImpl<?, Z>) this.parent).getAttribute().newInstance();
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
		return this.getClass().getSimpleName() + " " + this.attribute.toString();
	}
}
