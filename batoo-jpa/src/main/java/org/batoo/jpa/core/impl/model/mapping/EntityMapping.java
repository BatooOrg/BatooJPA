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

import java.util.Iterator;

import org.batoo.jpa.core.impl.model.attribute.AttributeImpl;
import org.batoo.jpa.core.impl.model.type.EntityTypeImpl;

import com.google.common.base.Splitter;

/**
 * Mapping for the entities.
 * 
 * @param <X>
 *            the type of the entity
 * 
 * @author hceylan
 * @since $version
 */
public class EntityMapping<X> extends ParentMapping<X, X> implements RootMapping<X> {

	private final EntityTypeImpl<X> entity;

	/**
	 * @param entity
	 *            the entity
	 * 
	 * @since $version
	 */
	@SuppressWarnings("unchecked")
	public EntityMapping(EntityTypeImpl<X> entity) {
		super(null, null, entity.getJavaType(), entity.getName());

		this.entity = entity;

		// inherit the mappings
		if (!entity.isRoot()) {
			this.inherit(((EntityMapping<X>) entity.getParent().getRootMapping()).getChildren());
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public AttributeImpl<? super X, X> getAttribute() {
		return null; // N/A
	}

	/**
	 * Returns the entity of the root mapping.
	 * 
	 * @return the entity of the root mapping
	 * 
	 * @since $version
	 */
	public EntityTypeImpl<X> getEntity() {
		return this.entity;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Mapping<?, ?, ?> getMapping(String path) {
		final Iterator<String> segments = Splitter.on('.').split(path).iterator();
		Mapping<?, ?, ?> mapping = this;
		while (segments.hasNext()) {
			if (mapping instanceof ParentMapping) {
				mapping = ((ParentMapping<?, ?>) mapping).getChild(segments.next());

				if (mapping == null) {
					return null;
				}
			}
			else {
				return null;
			}
		}

		return mapping;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public EntityMapping<?> getRoot() {
		return this;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public EntityTypeImpl<X> getType() {
		return this.entity;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean isEntity() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean isId() {
		return false;
	}
}
