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

import com.google.common.collect.Lists;
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
	private JoinedMapping<?, ?, ?>[] eagerMappings;

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
	public ParentMapping(ParentMapping<?, Z> parent, EmbeddedAttribute<? super Z, X> attribute, Class<X> javaType, String name) {
		super(parent, attribute, javaType, name);
	}

	/**
	 * Adds the associations to the list of associations.
	 * 
	 * @param associations
	 *            the list of associations
	 * 
	 * @since $version
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
	 * Adds the basic mappings to the list of mappings.
	 * 
	 * @param mappings
	 *            the list of mappings
	 * 
	 * @since $version
	 */
	public void addBasicMappings(List<BasicMapping<?, ?>> mappings) {
		for (final Mapping<? super X, ?, ?> mapping : this.children.values()) {
			if (mapping instanceof BasicMapping) {
				mappings.add((BasicMapping<?, ?>) mapping);
			}
			else if (mapping instanceof ParentMapping) {
				((ParentMapping<? super X, ?>) mapping).addBasicMappings(mappings);
			}
		}
	}

	/**
	 * Adds the joined mappings to the list of mappings.
	 * 
	 * @param mappingsJoined
	 *            the list of mappings
	 * 
	 * @since $version
	 */
	public void addJoinedMappings(List<JoinedMapping<?, ?, ?>> mappingsJoined) {
		for (final Mapping<? super X, ?, ?> mapping : this.children.values()) {
			if (mapping instanceof JoinedMapping) {
				final JoinedMapping<?, ?, ?> joinedMapping = (JoinedMapping<?, ?, ?>) mapping;

				if ((joinedMapping.isJoined())) {
					mappingsJoined.add(joinedMapping);
				}
			}
			else if (mapping instanceof ParentMapping) {
				((ParentMapping<? super X, ?>) mapping).addJoinedMappings(mappingsJoined);
			}
		}
	}

	/**
	 * Adds the plural mappings to the list of element collections.
	 * 
	 * @param elementCollections
	 *            the list of element collections
	 * 
	 * @since $version
	 */
	public void addPluralMappings(List<PluralMapping<?, ?, ?>> elementCollections) {
		for (final Mapping<? super X, ?, ?> mapping : this.children.values()) {
			if (mapping instanceof PluralMapping) {
				elementCollections.add((PluralMapping<?, ?, ?>) mapping);
			}
			else if (mapping instanceof ParentMapping) {
				((ParentMapping<? super X, ?>) mapping).addPluralMappings(elementCollections);
			}
		}
	}

	/**
	 * Adds the singular mappings to the list of mappings.
	 * 
	 * @param mappings
	 *            the list of mappings
	 * 
	 * @since $version
	 */
	public void addSingularMappings(List<Mapping<?, ?, ?>> mappings) {
		for (final Mapping<? super X, ?, ?> mapping : this.children.values()) {
			if (mapping instanceof ParentMapping) {
				((ParentMapping<? super X, ?>) mapping).addSingularMappings(mappings);
			}
			else if (!mapping.getAttribute().isCollection()) {
				mappings.add(mapping);
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
	 */
	private <Y> void createBasicMapping(BasicAttribute<? super X, Y> attribute) {
		this.children.put(attribute.getName(), new BasicMapping<X, Y>(this, attribute));
	}

	/**
	 * Creates an element collection mapping for the attribute.
	 * 
	 * @param attribute
	 *            the attribute
	 * @param <Y>
	 *            the type of the attribute
	 * 
	 * @since $version
	 */
	private <C, E> void createElementCollectionMapping(PluralAttributeImpl<? super X, C, E> attribute) {
		this.children.put(attribute.getName(), new ElementCollectionMapping<X, C, E>(this, attribute));
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
	 */
	private <Y> void createEmbeddedMapping(EmbeddedAttribute<? super X, Y> attribute) {
		final EmbeddedMapping<X, Y> mapping = new EmbeddedMapping<X, Y>(this, attribute);

		this.children.put(attribute.getName(), mapping);

		mapping.createMappings();
	}

	@SuppressWarnings("unchecked")
	private void createMapping(final Attribute<? super X, ?> attribute) {
		switch (attribute.getPersistentAttributeType()) {
			case BASIC:
				this.createBasicMapping((BasicAttribute<? super X, ?>) attribute);
				break;
			case ELEMENT_COLLECTION:
				this.createElementCollectionMapping((PluralAttributeImpl<? super X, ?, ?>) attribute);
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

	/**
	 * Creates the mappings.
	 * 
	 * @since $version
	 */
	@SuppressWarnings("unchecked")
	public void createMappings() {
		for (final Attribute<? super X, ?> attribute : this.getType().getAttributes()) {

			// if the declaring type is not this mapping's type then inspect
			if (attribute.getDeclaringType() != this.getType()) {

				// if the declaring type is mapped super type then inspect
				if (attribute.getDeclaringType() instanceof MappedSuperclassTypeImpl) {
					final EntityTypeImpl<X> type = (EntityTypeImpl<X>) this.getType();
					if (type.getRootType() != type) {
						continue;
					}
				}
				else if (attribute.getDeclaringType() instanceof EntityTypeImpl) {
					continue;
				}
			}

			this.createMapping(attribute);
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
	 */
	public Collection<Mapping<? super X, ?, ?>> getChildren() {
		return this.children.values();
	}

	/**
	 * Returns the eager mappings.
	 * 
	 * @return the array of mappings eager
	 * 
	 * @since $version
	 */
	public JoinedMapping<?, ?, ?>[] getEagerMappings() {
		if (this.eagerMappings != null) {
			return this.eagerMappings;
		}

		synchronized (this) {
			if (this.eagerMappings != null) {
				return this.eagerMappings;
			}

			final List<JoinedMapping<?, ?, ?>> eagerMappings = Lists.newArrayList();

			for (final Mapping<? super X, ?, ?> mapping : this.children.values()) {
				if (mapping instanceof JoinedMapping) {
					final JoinedMapping<?, ?, ?> joinedMapping = (JoinedMapping<?, ?, ?>) mapping;

					if (joinedMapping.isEager()) {
						eagerMappings.add(joinedMapping);
					}
				}
			}

			final JoinedMapping<?, ?, ?>[] eagerMappings0 = new JoinedMapping[eagerMappings.size()];
			eagerMappings.toArray(eagerMappings0);

			return this.eagerMappings = eagerMappings0;
		}
	}

	/**
	 * Returns the root attribute.
	 * 
	 * @param attribute
	 *            the current attribute
	 * @return the root attribute
	 * 
	 * @since $version
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
	 */
	public abstract ManagedTypeImpl<? super X> getType();

	/**
	 * Inherits the mappings from the parent.
	 * 
	 * @param children
	 *            the children to inherit
	 * 
	 * @since $version
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
	 */
	public abstract boolean isId();
}
