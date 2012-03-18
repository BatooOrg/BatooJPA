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
import java.util.Set;

import javax.persistence.metamodel.BasicType;

import org.batoo.jpa.core.MappingException;
import org.batoo.jpa.core.impl.instance.BasicResolver;
import org.batoo.jpa.core.impl.types.AttributeImpl;
import org.batoo.jpa.core.impl.types.BasicTypeImpl;
import org.batoo.jpa.core.impl.types.SingularAttributeImpl;

/**
 * Implementation for attributes with {@link BasicType} declarations.
 * 
 * @author hceylan
 * @since $version
 */
public final class BasicMapping<X, T> extends AbstractPhysicalMapping<X, T> {

	/**
	 * Sanitizes the column definitions.
	 * 
	 * @param attribute
	 * @param templates
	 * @return
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private static <X, T> Set<ColumnTemplate<X, T>> sanitize(SingularAttributeImpl<X, T> attribute, Set<ColumnTemplate<X, T>> templates) {
		if (templates.isEmpty()) {
			templates.add(new BasicColumnTemplate<X, T>(attribute, attribute.getIdType(), attribute.getGeneratorName()));
		}

		return templates;
	}

	/**
	 * @param declaringAttribute
	 *            the attribute which declares the mapping
	 * @param path
	 *            the path to the declaringAttribute
	 * @param columnTemplates
	 *            the set of column templates of the mapping
	 * @throws MappingException
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public BasicMapping(SingularAttributeImpl<X, T> declaringAttribute, Deque<AttributeImpl<?, ?>> path,
		Set<ColumnTemplate<X, T>> columnTemplates) throws MappingException {
		super(AssociationType.BASIC, declaringAttribute, path, BasicMapping.sanitize(declaringAttribute, columnTemplates));
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <Y> BasicResolver<Y> createResolver(Y instance) {
		return new BasicResolver<Y>(this, instance);
	}

	/**
	 * Fills the sequence / table generated value.
	 * 
	 * @param instance
	 *            the instance
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void fillValue(Object instance) {
		this.getDeclaringAttribute().fillValue(instance);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public final SingularAttributeImpl<X, T> getDeclaringAttribute() {
		return (SingularAttributeImpl<X, T>) super.getDeclaringAttribute();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public BasicTypeImpl<T> getType() {
		return (BasicTypeImpl<T>) this.getDeclaringAttribute().getType();
	}

}
