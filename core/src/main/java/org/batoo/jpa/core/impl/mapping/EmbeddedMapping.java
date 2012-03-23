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
package org.batoo.jpa.core.impl.mapping;

import java.util.Deque;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.metamodel.Attribute;

import org.apache.commons.lang.NotImplementedException;
import org.batoo.jpa.core.MappingException;
import org.batoo.jpa.core.impl.instance.AbstractResolver;
import org.batoo.jpa.core.impl.types.AttributeImpl;
import org.batoo.jpa.core.impl.types.EmbeddableTypeImpl;
import org.batoo.jpa.core.impl.types.SingularAttributeImpl;

/**
 * Implementation of {@link AssociationType#ONE} with embeddable attributes.
 * 
 * @author hceylan
 * @since $version
 */
public class EmbeddedMapping<X, T> extends AbstractMapping<X, T> {

	/**
	 * @param declaringAttribute
	 *            the attribute which declares the mapping
	 * @param path
	 *            the path to the declaringAttribute
	 * @param column
	 *            the column definition of the mapping
	 * @throws MappingException
	 * 
	 * @since $version
	 * @author hceylan
	 * @param attributeOverrides
	 */
	public EmbeddedMapping(AttributeImpl<X, T> declaringAttribute, Deque<AttributeImpl<?, ?>> path, Map<String, Column> attributeOverrides)
		throws MappingException {
		super(AssociationType.ONE, declaringAttribute, path);

		this.linkAttributes(attributeOverrides);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <Y> AbstractResolver<Y> createResolver(Y instance) {
		throw new NotImplementedException();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public SingularAttributeImpl<X, T> getDeclaringAttribute() {
		return (SingularAttributeImpl<X, T>) super.getDeclaringAttribute();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public EmbeddableTypeImpl<T> getType() {
		return (EmbeddableTypeImpl<T>) this.getDeclaringAttribute().getType();
	}

	private void linkAttributes(Map<String, Column> attributeOverrides) throws MappingException {
		for (final Attribute<?, ?> attribute : this.getType().getDeclaredAttributes()) {
			((AttributeImpl<?, ?>) attribute).link(this.getPath(), attributeOverrides);
		}
	}
}
