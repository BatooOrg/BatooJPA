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
package org.batoo.jpa.core.impl.model.attribute;

import javax.persistence.metamodel.PluralAttribute;

import org.apache.commons.lang.StringUtils;
import org.batoo.jpa.common.reflect.ReflectHelper;
import org.batoo.jpa.core.impl.model.ManagedTypeImpl;
import org.batoo.jpa.parser.MappingException;
import org.batoo.jpa.parser.metadata.attribute.AssociationAttributeMetadata;

/**
 * Implementation of {@link PluralAttribute}.
 * 
 * @param <X>
 *            The type the represented collection belongs to
 * @param <C>
 *            The type of the represented collection
 * @param <E>
 *            The element type of the represented collection
 * 
 * @author hceylan
 * @since $version
 */
public abstract class PluralAttributeImpl<X, C, E> extends AttributeImpl<X, C> implements PluralAttribute<X, C, E> {

	private final Class<E> bindableJavaType;

	/**
	 * @param declaringType
	 *            the declaring type
	 * @param metadata
	 *            the metadata
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@SuppressWarnings("unchecked")
	public PluralAttributeImpl(ManagedTypeImpl<X> declaringType, AssociationAttributeMetadata metadata) {
		super(declaringType, metadata);

		if (StringUtils.isNotBlank(metadata.getTargetEntity())) {
			try {
				this.bindableJavaType = (Class<E>) Class.forName(metadata.getTargetEntity());
			}
			catch (final ClassNotFoundException e) {
				throw new MappingException("Target enttity class not found", metadata.getLocator());
			}
		}
		else {
			this.bindableJavaType = ReflectHelper.getGenericType(this.getJavaMember(), 0);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public final Class<E> getBindableJavaType() {
		return this.bindableJavaType;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public final BindableType getBindableType() {
		return BindableType.PLURAL_ATTRIBUTE;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public final boolean isCollection() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();

		if (this.getPersistentAttributeType() == PersistentAttributeType.ELEMENT_COLLECTION) {
			builder.append("collection");
		}

		final String declaringType = this.getDeclaringType().getJavaType().getSimpleName();

		final String type = this.getBindableJavaType().getSimpleName();
		builder.append(" ").append(declaringType).append(".").append(this.getName()).append("(").append(type).append(")");

		return builder.toString();
	}
}
