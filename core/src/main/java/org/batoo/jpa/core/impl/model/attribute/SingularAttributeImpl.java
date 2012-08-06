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

import org.batoo.jpa.core.impl.model.mapping.SingularMapping;
import org.batoo.jpa.core.impl.model.type.ManagedTypeImpl;
import org.batoo.jpa.parser.metadata.attribute.AttributeMetadata;

/**
 * Implementation of {@link SingularMapping}.
 * 
 * @param <X>
 *            The type containing the represented attribute
 * @param <T>
 *            The type of the represented attribute
 * 
 * @author hceylan
 * @since $version
 */
public abstract class SingularAttributeImpl<X, T> extends AttributeImpl<X, T> implements SingularAttribute<X, T> {

	/**
	 * @param declaringType
	 *            the declaring type
	 * @param metadata
	 *            the metadata
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public SingularAttributeImpl(ManagedTypeImpl<X> declaringType, AttributeMetadata metadata) {
		super(declaringType, metadata);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public final Class<T> getBindableJavaType() {
		return this.getJavaType();
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
