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

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.persistence.criteria.JoinType;

import org.batoo.jpa.core.impl.instance.ManagedInstance;
import org.batoo.jpa.core.impl.jdbc.JoinableTable;
import org.batoo.jpa.core.impl.model.attribute.AttributeImpl;
import org.batoo.jpa.core.impl.model.attribute.EmbeddedAttribute;
import org.batoo.jpa.core.impl.model.type.EmbeddableTypeImpl;
import org.batoo.jpa.core.impl.model.type.EntityTypeImpl;
import org.batoo.jpa.core.jdbc.IdType;
import org.batoo.jpa.parser.MappingException;
import org.batoo.jpa.parser.metadata.AssociationMetadata;
import org.batoo.jpa.parser.metadata.ColumnMetadata;

import com.google.common.collect.Lists;

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
public class EmbeddedMapping<Z, X> extends ParentMapping<Z, X> implements SingularMapping<Z, X>, JoinedMapping<Z, X, X> {

	private final EmbeddableTypeImpl<X> embeddable;
	private final EmbeddedAttribute<? super Z, X> attribute;
	private Mapping<? super X, ?, ?>[] singularMappings;

	/**
	 * @param parent
	 *            the parent mapping
	 * @param attribute
	 *            the attribute
	 * 
	 * @since $version
	 */
	public EmbeddedMapping(ParentMapping<?, Z> parent, EmbeddedAttribute<? super Z, X> attribute) {
		super(parent, attribute, attribute.getBindableJavaType(), attribute.getName());

		this.attribute = attribute;
		this.embeddable = attribute.getType();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Object extractKey(Object value) {
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean fillValue(EntityTypeImpl<?> type, ManagedInstance<?> managedInstance, Object instance) {
		X value = this.get(instance);
		if (value == null) {
			value = this.getAttribute().newInstance();
			this.getAttribute().set(instance, value);
		}

		for (final Mapping<? super X, ?, ?> mapping : this.getChildren()) {
			// mapping is another embedded mapping
			if (mapping instanceof EmbeddedMapping) {
				((EmbeddedMapping<? super X, ?>) mapping).fillValue(type, managedInstance, value);
			}
			// mapping is basic mapping
			else if (mapping instanceof BasicMapping) {
				((BasicMapping<? super X, ?>) mapping).getAttribute().fillValue(type, managedInstance, value);
			}
			// no other mappings allowed in id classes
			else if (!(mapping instanceof SingularAssociationMapping)) {
				throw new MappingException("Embbeded ids can only have basic and embedded attributes.", this.getType().getLocator());
			}
		}

		return true;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void flush(Connection connection, ManagedInstance<?> managedInstance, boolean removals, boolean force) throws SQLException {
		// noop
	}

	/**
	 * Returns the association override or <code>null</code>
	 * 
	 * @param path
	 *            the current path
	 * @return the association override or <code>null</code>
	 * 
	 * @since $version
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
	public IdType getIdType() {
		if (this.attribute.isId()) {
			return IdType.MANUAL;
		}

		if (this.getParent() != null) {
			return this.getParent().getIdType();
		}

		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public MappingType getMappingType() {
		return MappingType.EMBEDDABLE;
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
	 * Returns the sorted singular mappings of the embeddable
	 * 
	 * @return the list of sorted singular attributes
	 * 
	 * @since $version
	 */
	@SuppressWarnings("unchecked")
	public Mapping<? super X, ?, ?>[] getSingularMappings() {
		if (this.singularMappings != null) {
			return this.singularMappings;
		}

		synchronized (this) {
			if (this.singularMappings != null) {
				return this.singularMappings;
			}

			final List<Mapping<? super X, ?, ?>> singularMappings = Lists.newArrayList();
			for (final Mapping<? super X, ?, ?> mapping : this.getChildren()) {
				final AttributeImpl<?, ?> attribute = mapping.getAttribute();
				switch (attribute.getPersistentAttributeType()) {
					case BASIC:
					case EMBEDDED:
						singularMappings.add(mapping);
						break;
					case MANY_TO_ONE:
					case ONE_TO_ONE:
						final SingularAssociationMapping<? super X, ?> association = (SingularAssociationMapping<? super X, ?>) mapping;
						if (!association.isOwner()) {
							continue;
						}

						if (association.getTable() != null) {
							continue;
						}

						singularMappings.add(mapping);
					case ONE_TO_MANY:
					case MANY_TO_MANY:
					case ELEMENT_COLLECTION:
						// N/A
				}
			}

			Collections.sort(singularMappings, new Comparator<Mapping<? super X, ?, ?>>() {

				@Override
				public int compare(Mapping<? super X, ?, ?> o1, Mapping<? super X, ?, ?> o2) {
					return o1.getAttribute().getAttributeId().compareTo(o2.getAttribute().getAttributeId());
				}
			});

			return this.singularMappings = singularMappings.toArray(new Mapping[singularMappings.size()]);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public JoinableTable getTable() {
		return null; // N/A
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
	public void initialize(ManagedInstance<?> instance) {
		// noop
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean isAssociation() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean isEager() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean isId() {
		return this.attribute.isId();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean isJoined() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean isMap() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String join(String parentAlias, String alias, JoinType joinType) {
		return null;
	}
}
