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

import javax.persistence.metamodel.SingularAttribute;

import org.apache.commons.lang.StringUtils;
import org.batoo.jpa.core.impl.model.EntityTypeImpl;
import org.batoo.jpa.core.impl.model.ManagedTypeImpl;
import org.batoo.jpa.core.impl.model.mapping.SingularMappingEx;
import org.batoo.jpa.parser.metadata.attribute.AssociationAttributeMetadata;
import org.batoo.jpa.parser.metadata.attribute.OneToOneAttributeMetadata;
import org.batoo.jpa.parser.metadata.attribute.OptionalAssociationAttributeMetadata;

/**
 * Implementation of {@link SingularMappingEx} representing types of ManyToOne and OneToOne
 * 
 * @param <X>
 *            The type containing the represented attribute
 * @param <T>
 *            The type of the represented attribute
 * @author hceylan
 * @since 2.0.0
 */
public class AssociatedSingularAttribute<X, T> extends SingularAttributeImpl<X, T> implements SingularAttribute<X, T> {

	private final PersistentAttributeType attributeType;
	private final boolean optional;
	private final String mapsId;
	private final boolean id;

	private EntityTypeImpl<T> type;
	private final boolean owner;
	private final boolean joined;

	/**
	 * @param declaringType
	 *            the declaring type
	 * @param metadata
	 *            the metadata
	 * @param attributeType
	 *            the type of the attribute
	 * 
	 * @since 2.0.0
	 */
	public AssociatedSingularAttribute(ManagedTypeImpl<X> declaringType, PersistentAttributeType attributeType, AssociationAttributeMetadata metadata) {
		super(declaringType, metadata);

		this.attributeType = attributeType;
		final OptionalAssociationAttributeMetadata optionalAssociationMetadata = (OptionalAssociationAttributeMetadata) metadata;
		this.optional = optionalAssociationMetadata.isOptional();
		this.id = optionalAssociationMetadata.isId();
		this.mapsId = optionalAssociationMetadata.getMapsId();

		if (metadata instanceof OneToOneAttributeMetadata) {
			this.owner = StringUtils.isBlank(((OneToOneAttributeMetadata) metadata).getMappedBy());
		}
		else {
			this.owner = true;
		}

		this.joined = metadata.getJoinTable() == null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <Z extends X> AttributeImpl<Z, T> clone(EntityTypeImpl<Z> type) {
		return new AssociatedSingularAttribute<Z, T>(type, this.attributeType, this.getMetadata());
	}

	/**
	 * Returns the mapsId of the AssociatedSingularAttribute.
	 * 
	 * @return the mapsId of the AssociatedSingularAttribute
	 * 
	 * @since 2.0.0
	 */
	public String getMapsId() {
		return this.mapsId;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public AssociationAttributeMetadata getMetadata() {
		return (AssociationAttributeMetadata) super.getMetadata();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public PersistentAttributeType getPersistentAttributeType() {
		return this.attributeType;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public EntityTypeImpl<T> getType() {
		if (this.type == null) {
			this.type = this.getMetamodel().entity(this.getBindableJavaType());
		}

		return this.type;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean isAssociation() {
		return true;
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
	 * Returns if the association is joined.
	 * 
	 * @return true if the association is joined.
	 * 
	 * @since 2.0.0
	 */
	public boolean isJoined() {
		return this.joined;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean isOptional() {
		return this.optional;
	}

	/**
	 * Returns if the attribute is the owner.
	 * 
	 * @return true if the attribute is the owner, false otherwise
	 * 
	 * @since 2.0.0
	 */
	public boolean isOwner() {
		return this.owner;
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
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder("association").append(super.toString());

		if (this.getPersistentAttributeType() == PersistentAttributeType.MANY_TO_ONE) {
			if (this.isOptional()) {
				builder.append(" <*..0>");
			}
			else {
				builder.append(" <*..1>");
			}
		}
		else {
			if (this.isOptional()) {
				builder.append(" <0..1>");
			}
			else {
				builder.append(" <1..1>");
			}
		}

		return builder.toString();
	}
}
