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

import org.batoo.jpa.core.impl.jdbc.AbstractTable;
import org.batoo.jpa.core.impl.model.attribute.AttributeImpl;
import org.batoo.jpa.core.impl.model.type.EmbeddableTypeImpl;

import com.google.common.base.Splitter;

/**
 * Root mapping for Embeddable element mappings.
 * 
 * @param <X>
 *            the embeddable type
 * 
 * @author hceylan
 * @since $version
 */
public class ElementMapping<X> extends ParentMapping<X, X> implements RootMapping<X> {

	private final EmbeddableTypeImpl<X> embeddable;
	private final ElementCollectionMapping<?, ?, X> mapping;

	/**
	 * @param mapping
	 *            the element collection mapping
	 * @param embeddable
	 *            the embeddable
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public ElementMapping(ElementCollectionMapping<?, ?, X> mapping, EmbeddableTypeImpl<X> embeddable) {
		super(null, null, embeddable.getJavaType(), null);

		this.mapping = mapping;
		this.embeddable = embeddable;

		this.createMappings();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public AttributeImpl<? super X, X> getAttribute() {
		return null;
	}

	/**
	 * Returns the collection table.
	 * 
	 * @return the collection table
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public AbstractTable getCollectionTable() {
		return this.mapping.getCollectionTable();
	}

	/**
	 * Returns the mapping corresponding to the path.
	 * 
	 * @param path
	 *            the path of the mapping
	 * @return the mapping
	 * 
	 * @since $version
	 * @author hceylan
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
	public RootMapping<?> getRoot() {
		return this;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public EmbeddableTypeImpl<X> getType() {
		return this.embeddable;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean isEntity() {
		return false;
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
