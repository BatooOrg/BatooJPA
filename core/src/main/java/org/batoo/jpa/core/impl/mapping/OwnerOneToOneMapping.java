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

import javax.persistence.FetchType;
import javax.persistence.OneToOne;

import org.batoo.jpa.core.MappingException;
import org.batoo.jpa.core.impl.types.AttributeImpl;
import org.batoo.jpa.core.impl.types.EntityTypeImpl;
import org.batoo.jpa.core.impl.types.SingularAttributeImpl;

/**
 * Implementation of {@link AssociationType#ONE} with owning relational attributes.
 * <p>
 * This is a {@link OneToOne} type of mapping with the declaring attribute being the owner.
 * 
 * @author hceylan
 * @since $version
 */
public class OwnerOneToOneMapping<X, T> extends OwnerAssociationMapping<X, T> implements Association<X, T> {

	/**
	 * @param declaringAttribute
	 *            the attribute which declares the mapping
	 * @param path
	 *            the path to the declaringAttribute
	 * @param columnTemplates
	 *            the set of column templates of the mapping
	 * @param eager
	 *            if association is annotated with {@link FetchType#EAGER}
	 * @throws MappingException
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public OwnerOneToOneMapping(AttributeImpl<X, T> declaringAttribute, Deque<AttributeImpl<?, ?>> path,
		Set<ColumnTemplate<X, T>> columnTemplates, boolean eager) throws MappingException {
		super(AssociationType.ONE, declaringAttribute, path, columnTemplates, eager);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean contains(Object instance) {
		return this.getValue(instance) == instance;
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
	public OwnedOneToOneMapping<T, X> getOpposite() {
		return (OwnedOneToOneMapping<T, X>) super.getOpposite();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public EntityTypeImpl<T> getType() {
		return (EntityTypeImpl<T>) this.getDeclaringAttribute().getType();
	}

}
