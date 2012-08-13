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
package org.batoo.jpa.core.impl.model.attribute;

import java.util.List;

import javax.persistence.metamodel.SingularAttribute;

import org.batoo.jpa.core.impl.model.mapping.SingularMapping;
import org.batoo.jpa.core.impl.model.type.EmbeddableTypeImpl;
import org.batoo.jpa.core.impl.model.type.IdentifiableTypeImpl;
import org.batoo.jpa.core.impl.model.type.ManagedTypeImpl;
import org.batoo.jpa.parser.metadata.AssociationMetadata;
import org.batoo.jpa.parser.metadata.AttributeOverrideMetadata;
import org.batoo.jpa.parser.metadata.ColumnMetadata;
import org.batoo.jpa.parser.metadata.attribute.EmbeddedAttributeMetadata;
import org.batoo.jpa.parser.metadata.attribute.EmbeddedIdAttributeMetadata;

import com.google.common.collect.Lists;

/**
 * Implementation of {@link SingularMapping} for embeddable attributes.
 * 
 * @param <X>
 *            The type containing the represented attribute
 * @param <T>
 *            The type of the represented attribute
 * 
 * @author hceylan
 * @since $version
 */
public class EmbeddedAttribute<X, T> extends SingularAttributeImpl<X, T> implements SingularAttribute<X, T> {

	private final boolean id;
	private final List<AssociationMetadata> associationOverrides;
	private final List<AttributeOverrideMetadata> attributeOverrides;
	private EmbeddableTypeImpl<T> type;

	/**
	 * Constructor for id type embedded attributes.
	 * 
	 * @param declaringType
	 *            the declaring type
	 * @param metadata
	 *            the metadata
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public EmbeddedAttribute(IdentifiableTypeImpl<X> declaringType, EmbeddedIdAttributeMetadata metadata) {
		super(declaringType, metadata);

		this.id = true;

		this.associationOverrides = Lists.newArrayList();
		this.attributeOverrides = metadata.getAttributeOverrides();
	}

	/**
	 * Constructor for regular embedded attributes.
	 * 
	 * @param declaringType
	 *            the declaring type
	 * @param metadata
	 *            the metadata
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public EmbeddedAttribute(ManagedTypeImpl<X> declaringType, EmbeddedAttributeMetadata metadata) {
		super(declaringType, metadata);

		this.id = false;

		this.associationOverrides = metadata.getAssociationOverrides();
		this.attributeOverrides = metadata.getAttributeOverrides();
	}

	/**
	 * Returns the association override if one exists for the path.
	 * 
	 * @param path
	 *            the path
	 * @return the association override if one exists for the path, otherwise <code>null</code>
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public AssociationMetadata getAssociationOverride(String path) {
		for (final AssociationMetadata override : this.associationOverrides) {
			if (override.getName().equals(path)) {
				return override;
			}
		}

		return null;
	}

	/**
	 * Returns the attribute override if one exists for the path.
	 * 
	 * @param path
	 *            the path
	 * @return the attribute override if one exists for the path, otherwise <code>null</code>
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public ColumnMetadata getAttributeOverride(String path) {
		for (final AttributeOverrideMetadata override : this.attributeOverrides) {
			if (override.getName().equals(path)) {
				return override.getColumn();
			}
		}

		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public PersistentAttributeType getPersistentAttributeType() {
		return PersistentAttributeType.EMBEDDED;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public EmbeddableTypeImpl<T> getType() {
		if (this.type != null) {
			return this.type;
		}

		return this.type = this.getMetamodel().embeddable(this.getBindableJavaType());
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
	public boolean isId() {
		return this.id;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean isOptional() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean isVersion() {
		return false;
	}

	/**
	 * Returns a new embeddable instance
	 * 
	 * @return the instance
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public T newInstance() {
		return this.getType().newInstance();
	}
}
