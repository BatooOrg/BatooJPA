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

import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.persistence.metamodel.Attribute;

import org.batoo.jpa.core.impl.model.attribute.AssociatedSingularAttribute;
import org.batoo.jpa.core.impl.model.attribute.AttributeImpl;
import org.batoo.jpa.core.impl.model.attribute.BasicAttribute;
import org.batoo.jpa.core.impl.model.attribute.EmbeddedAttribute;
import org.batoo.jpa.core.impl.model.attribute.PluralAttributeImpl;
import org.batoo.jpa.core.impl.model.type.EntityTypeImpl;
import org.batoo.jpa.core.impl.model.type.ManagedTypeImpl;
import org.batoo.jpa.core.impl.model.type.MappedSuperclassTypeImpl;

import com.google.common.collect.Maps;

/**
 * 
 * @param <Z>
 *            the source type
 * @param <X>
 *            the destination type
 * 
 * @author hceylan
 * @since $version
 */
public abstract class ParentMapping<Z, X> extends Mapping<Z, X, X> {

	private final Map<String, Mapping<? super X, ?, ?>> children = Maps.newHashMap();

	/**
	 * @param parent
	 *            the parent mapping
	 * @param entity
	 *            the entity
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
	public ParentMapping(ParentMapping<?, Z> parent, EntityTypeImpl<?> entity, EmbeddedAttribute<? super Z, X> attribute, Class<X> javaType, String name) {
		super(parent, entity, attribute, javaType, name);
	}

	/**
	 * Adds the associations to the list of associations.
	 * 
	 * @param associations
	 *            the list of associations
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void addAssociations(List<AssociationMapping<?, ?, ?>> associations) {
		for (final Mapping<? super X, ?, ?> mapping : this.children.values()) {
			if (mapping instanceof AssociationMapping) {
				associations.add((AssociationMapping<?, ?, ?>) mapping);
			}
			else if (mapping instanceof ParentMapping) {
				((ParentMapping<? super X, ?>) mapping).addAssociations(associations);
			}
		}
	}

	/**
	 * Creates a basic mapping for the attribute.
	 * 
	 * @param attribute
	 *            the attribute
	 * @param <Y>
	 *            the type of the attribute
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private <Y> void createBasicMapping(BasicAttribute<? super X, Y> attribute) {
		this.children.put(attribute.getName(), new BasicMapping<X, Y>(this, attribute));
	}

	/**
	 * Creates an embedded mapping for the attribute.
	 * 
	 * @param attribute
	 *            the attribute
	 * @param <Y>
	 *            the type of the attribute
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private <Y> void createEmbeddedMapping(EmbeddedAttribute<? super X, Y> attribute) {
		final EmbeddedMapping<X, Y> mapping = new EmbeddedMapping<X, Y>(this, attribute);

		this.children.put(attribute.getName(), mapping);

		mapping.createMappings();
	}

	/**
	 * Creates the mappings.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@SuppressWarnings("unchecked")
	public void createMappings() {
		for (final Attribute<? super X, ?> attribute : this.getType().getAttributes()) {
			// should we process the attribute
			final boolean process = (attribute.getDeclaringType() == this.getType()) //
				|| (attribute.getDeclaringType() instanceof MappedSuperclassTypeImpl);

			if (!process) {
				continue;
			}

			switch (attribute.getPersistentAttributeType()) {
				case BASIC:
					this.createBasicMapping((BasicAttribute<? super X, ?>) attribute);
					break;
				case ONE_TO_ONE:
				case MANY_TO_ONE:
					this.createSingularAssociationMapping((AssociatedSingularAttribute<? super X, ?>) attribute);
					break;
				case MANY_TO_MANY:
				case ONE_TO_MANY:
					this.createPluralAssociationMapping((PluralAttributeImpl<? super X, ?, ?>) attribute);
					break;
				case EMBEDDED:
					this.createEmbeddedMapping((EmbeddedAttribute<? super X, ?>) attribute);
			}
		}
	}

	/**
	 * Creates a plural association mapping for the attribute.
	 * 
	 * @param attribute
	 *            the attribute
	 * @param <Y>
	 *            the type of the attribute
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private <C, E> void createPluralAssociationMapping(PluralAttributeImpl<? super X, C, E> attribute) {
		this.children.put(attribute.getName(), new PluralAssociationMapping<X, C, E>(this, attribute));
	}

	/**
	 * Creates a singular association mapping for the attribute.
	 * 
	 * @param attribute
	 *            the attribute
	 * @param <Y>
	 *            the type of the attribute
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private <Y> void createSingularAssociationMapping(AssociatedSingularAttribute<? super X, Y> attribute) {
		this.children.put(attribute.getName(), new SingularAssociationMapping<X, Y>(this, attribute));
	}

	/**
	 * Returns the mapping corresponding to the name.
	 * 
	 * @param name
	 *            the name of the child mapping
	 * @return the mapping or null
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public Mapping<? super X, ?, ?> getChild(String name) {
		return this.children.get(name);
	}

	/**
	 * Returns the children of the mapping.
	 * 
	 * @return the children of the mapping
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public Collection<Mapping<? super X, ?, ?>> getChildren() {
		return this.children.values();
	}

	/**
	 * Returns the root attribute.
	 * 
	 * @param attribute
	 *            the current attribute
	 * @return the root attribute
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public AttributeImpl<?, ?> getRootAttribute(AttributeImpl<?, ?> attribute) {
		return this.getParent() instanceof EmbeddedMapping ? this.getParent().getAttribute() : attribute;
	}

	/**
	 * Returns the root path.
	 * 
	 * @param path
	 *            the current path
	 * @return the root path
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public String getRootPath(String path) {
		return this.getParent() instanceof EmbeddedMapping ? this.getParent().getAttribute().getName() + "." + path : path;
	}

	/**
	 * Returns the entity or embeddable type of the mapping.
	 * 
	 * @return the entity or embeddable type of the mapping
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public abstract ManagedTypeImpl<? super X> getType();

	/**
	 * Inherits the mappings from the parent.
	 * 
	 * @param children
	 *            the children to inherit
	 * 
	 * @since $version
	 * @author hceylan
	 */
	protected void inherit(Collection<Mapping<? super X, ?, ?>> children) {
		for (final Mapping<? super X, ?, ?> mapping : children) {
			this.children.put(mapping.getName(), mapping);
		}
	}

	/**
	 * Returns if the mapping is part of an id attribute.
	 * 
	 * @return true if the mapping is part of an id attribute, false otherwise
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public abstract boolean isId();
}
