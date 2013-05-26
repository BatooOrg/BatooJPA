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
import org.batoo.jpa.core.impl.model.ManagedTypeImpl;
import org.batoo.jpa.core.impl.model.mapping.SingularMappingEx;
import org.batoo.jpa.parser.MappingException;
import org.batoo.jpa.parser.metadata.attribute.AttributeMetadata;
import org.batoo.jpa.parser.metadata.attribute.AssociationAttributeMetadata;

/**
 * Implementation of {@link SingularMappingEx}.
 * 
 * @param <X>
 *            The type containing the represented attribute
 * @param <T>
 *            The type of the represented attribute
 * 
 * @author hceylan
 * @since 2.0.0
 */
public abstract class SingularAttributeImpl<X, T> extends AttributeImpl<X, T> implements SingularAttribute<X, T> {
	private final Class<T> bindableJavaType;

	/**
	 * @param declaringType
	 *            the declaring type
	 * @param metadata
	 *            the metadata
	 * 
	 * @since 2.0.0
	 */
	public SingularAttributeImpl(ManagedTypeImpl<X> declaringType, AttributeMetadata metadata) {
		super(declaringType, metadata);

		if (metadata instanceof AssociationAttributeMetadata && StringUtils.isNotBlank(((AssociationAttributeMetadata) metadata).getTargetEntity())) {
			try {
				this.bindableJavaType = (Class<T>) declaringType.getMetamodel().getEntityManagerFactory().getClassloader().loadClass(((AssociationAttributeMetadata) metadata).getTargetEntity());
			}
			catch (final ClassNotFoundException e) {
				throw new MappingException("Target enttity class not found", metadata.getLocator());
			}
		}
		else {
			this.bindableJavaType = this.getJavaType();
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public final Class<T> getBindableJavaType() {
		return this.bindableJavaType;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public final BindableType getBindableType() {
		return BindableType.SINGULAR_ATTRIBUTE;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public final boolean isCollection() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();

		if (this.isId()) {
			builder.append("id");
		}
		else if (this.isVersion()) {
			builder.append("version");
		}
		else if (this.getPersistentAttributeType() == PersistentAttributeType.EMBEDDED) {
			builder.append("embedded");
		}
		else {
			builder.append("basic");
		}

		final String declaringType = this.getDeclaringType().getJavaType().getSimpleName();

		final String type = this.getBindableJavaType().getSimpleName();
		builder.append(" ").append(declaringType).append(".").append(this.getName()).append("(").append(type).append(")");

		return builder.toString();
	}
}
