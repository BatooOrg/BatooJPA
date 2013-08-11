/*
 * Copyright (c) 2012-2013, Batu Alp Ceylan
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
import org.batoo.jpa.core.impl.model.EmbeddableTypeImpl;
import org.batoo.jpa.core.impl.model.EntityTypeImpl;
import org.batoo.jpa.core.impl.model.attribute.AttributeImpl;
import org.batoo.jpa.core.impl.model.attribute.EmbeddedAttribute;
import org.batoo.jpa.jdbc.IdType;
import org.batoo.jpa.jdbc.JoinableTable;
import org.batoo.jpa.jdbc.mapping.EmbeddedMapping;
import org.batoo.jpa.jdbc.mapping.Mapping;
import org.batoo.jpa.jdbc.mapping.MappingType;
import org.batoo.jpa.jdbc.mapping.RootMapping;
import org.batoo.jpa.parser.MappingException;
import org.batoo.jpa.parser.metadata.AssociationMetadata;
import org.batoo.jpa.parser.metadata.ColumnMetadata;

import com.google.common.collect.Lists;

/**
 * AbstractMapping for the entities.
 * 
 * @param <Z>
 *            the source type
 * @param <X>
 *            the destination type
 * 
 * @author hceylan
 * @since 2.0.0
 */
public class EmbeddedMappingImpl<Z, X> extends AbstractParentMapping<Z, X> implements SingularMappingEx<Z, X>, JoinedMapping<Z, X, X>, EmbeddedMapping<Z, X> {

	private final EmbeddableTypeImpl<X> embeddable;
	private final EmbeddedAttribute<? super Z, X> attribute;
	private AbstractMapping<? super X, ?, ?>[] singularMappings;

	/**
	 * @param parent
	 *            the parent mapping
	 * @param attribute
	 *            the attribute
	 * 
	 * @since 2.0.0
	 */
	public EmbeddedMappingImpl(AbstractParentMapping<?, Z> parent, EmbeddedAttribute<? super Z, X> attribute) {
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
			if (mapping instanceof EmbeddedMappingImpl) {
				((EmbeddedMappingImpl<? super X, ?>) mapping).fillValue(type, managedInstance, value);
			}
			// mapping is basic mapping
			else if (mapping instanceof BasicMappingImpl) {
				((BasicMappingImpl<? super X, ?>) mapping).getAttribute().fillValue(type, managedInstance, value);
			}
			// no other mappings allowed in id classes
			else if (!(mapping instanceof SingularAssociationMappingImpl)) {
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
	 * @since 2.0.0
	 */
	public AssociationMetadata getAssociationOverride(String path) {
		AssociationMetadata metadata = null;

		if (this.getParent() instanceof EmbeddedMappingImpl) {
			metadata = ((EmbeddedMappingImpl<?, Z>) this.getParent()).getAssociationOverride(this.getAttribute().getName() + "." + path);
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
	 * @since 2.0.0
	 */
	public ColumnMetadata getAttributeOverride(String path) {
		ColumnMetadata metadata = null;

		if (this.getParent() instanceof EmbeddedMappingImpl) {
			metadata = ((EmbeddedMappingImpl<?, Z>) this.getParent()).getAttributeOverride(this.getAttribute().getName() + "." + path);
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

		final AbstractParentMapping<?, Z> parent = this.getParent();
		if (parent instanceof EmbeddedMappingImpl) {
			return ((EmbeddedMappingImpl<?, Z>) parent).getIdType();
		}

		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public JoinableTable getJoinTable() {
		return null; // N/A
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
	 * @since 2.0.0
	 */
	@SuppressWarnings("unchecked")
	public AbstractMapping<? super X, ?, ?>[] getSingularMappings() {
		if (this.singularMappings != null) {
			return this.singularMappings;
		}

		synchronized (this) {
			if (this.singularMappings != null) {
				return this.singularMappings;
			}

			final List<AbstractMapping<? super X, ?, ?>> singularMappings = Lists.newArrayList();
			for (final Mapping<? super X, ?, ?> mapping : this.getChildren()) {
				final AttributeImpl<?, ?> attribute = ((AbstractMapping<? super X, ?, ?>) mapping).getAttribute();
				switch (attribute.getPersistentAttributeType()) {
					case BASIC:
					case EMBEDDED:
						singularMappings.add((AbstractMapping<? super X, ?, ?>) mapping);
						break;
					case MANY_TO_ONE:
					case ONE_TO_ONE:
						final SingularAssociationMappingImpl<? super X, ?> association = (SingularAssociationMappingImpl<? super X, ?>) mapping;
						if (!association.isOwner()) {
							continue;
						}

						if (association.getJoinTable() != null) {
							continue;
						}

						singularMappings.add((AbstractMapping<? super X, ?, ?>) mapping);
					case ONE_TO_MANY:
					case MANY_TO_MANY:
					case ELEMENT_COLLECTION:
						// N/A
				}
			}

			Collections.sort(singularMappings, new Comparator<AbstractMapping<? super X, ?, ?>>() {

				@Override
				public int compare(AbstractMapping<? super X, ?, ?> o1, AbstractMapping<? super X, ?, ?> o2) {
					return o1.getAttribute().getAttributeId().compareTo(o2.getAttribute().getAttributeId());
				}
			});

			return this.singularMappings = singularMappings.toArray(new AbstractMapping[singularMappings.size()]);
		}
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
