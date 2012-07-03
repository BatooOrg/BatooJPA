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

import org.batoo.jpa.core.impl.model.attribute.EmbeddedAttribute;
import org.batoo.jpa.core.impl.model.type.EmbeddableTypeImpl;
import org.batoo.jpa.parser.MappingException;
import org.batoo.jpa.parser.metadata.AssociationMetadata;
import org.batoo.jpa.parser.metadata.ColumnMetadata;

/**
 * Mapping for the entities.
 * 
 * @param <Z>
 *            the source type
 * @param <X>
 *            the destination type
 * 
 * @author hceylan
 * @since $version
 */
public class EmbeddedMapping<Z, X> extends ParentMapping<Z, X> implements SingularMapping<Z, X> {

	private final EmbeddableTypeImpl<X> embeddable;
	private final EmbeddedAttribute<? super Z, X> attribute;

	/**
	 * @param parent
	 *            the parent mapping
	 * @param attribute
	 *            the attribute
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public EmbeddedMapping(ParentMapping<?, Z> parent, EmbeddedAttribute<? super Z, X> attribute) {
		super(parent, parent.getRoot().getType(), attribute.getBindableJavaType(), attribute.getName());

		this.attribute = attribute;
		this.embeddable = attribute.getType();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean fillValue(Object instance) {
		final X value = this.get(instance);

		if (value == null) {
			throw new NullPointerException();
		}

		for (final Mapping<? super X, ?, ?> mapping : this.getChildren()) {
			// mapping is another embedded mapping
			if (mapping instanceof EmbeddedMapping) {
				((EmbeddedMapping<? super X, ?>) mapping).fillValue(value);
			}
			// mapping is basic mapping
			else if (mapping instanceof BasicMapping) {
				((BasicMapping<? super X, ?>) mapping).getAttribute().fillValue(instance);
			}
			// no other mappings allowed in id classes
			else {
				throw new MappingException("Embbeded ids can only have basic and embedded attributes.", this.getType().getLocator());
			}
		}

		return true;
	}

	/**
	 * Returns the association override or <code>null</code>
	 * 
	 * @param path
	 *            the current path
	 * @return the association override or <code>null</code>
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public AssociationMetadata getAssociationOverride(String path) {
		AssociationMetadata metadata = null;

		if (this.getParent() instanceof EmbeddedMapping) {
			metadata = ((EmbeddedMapping<?, Z>) this.getParent()).getAssociationOverride(this.getAttribute().getName() + "." + path);
		}

		if (metadata != null) {
			return metadata;
		}

		return this.getAttribute().getAssociationOverride(path);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public EmbeddedAttribute<? super Z, X> getAttribute() {
		return this.attribute;
	}

	/**
	 * Returns the attribute override or <code>null</code>
	 * 
	 * @param path
	 *            the current path
	 * @return the attribute override or <code>null</code>
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public ColumnMetadata getAttributeOverride(String path) {
		ColumnMetadata metadata = null;

		if (this.getParent() instanceof EmbeddedMapping) {
			metadata = ((EmbeddedMapping<?, Z>) this.getParent()).getAttributeOverride(this.getAttribute().getName() + "." + path);
		}

		if (metadata != null) {
			return metadata;
		}

		return this.getAttribute().getAttributeOverride(path);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public RootMapping<?> getRoot() {
		return this.getParent().getRoot();
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
	public boolean isId() {
		return this.attribute.isId();
	}
}
