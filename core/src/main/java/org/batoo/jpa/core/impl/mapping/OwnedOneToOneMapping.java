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

import org.batoo.jpa.core.MappingException;
import org.batoo.jpa.core.impl.metamodel.AttributeImpl;
import org.batoo.jpa.core.impl.metamodel.EntityTypeImpl;
import org.batoo.jpa.core.impl.metamodel.SingularAttributeImpl;

/**
 * Implementation of {@link AssociationType#ONE} with owned relational attributes.
 * <p>
 * This is a {@link ManyToMany} type of mapping with the declaring attribute being the owned.
 * 
 * @author hceylan
 * @since $version
 */
public class OwnedOneToOneMapping<X, T> extends OwnedAssociationMapping<X, T> {

	/**
	 * @param declaringAttribute
	 *            the attribute which declares the mapping
	 * @param path
	 *            the path to the declaringAttribute
	 * @param orpanRemoval
	 *            if orphans should be removed
	 * @param eager
	 *            if association is annotated with {@link FetchType#EAGER}
	 * @throws MappingException
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public OwnedOneToOneMapping(AttributeImpl<X, T> declaringAttribute, Deque<AttributeImpl<?, ?>> path, boolean orpanRemoval, boolean eager)
		throws MappingException {
		super(AssociationType.ONE, declaringAttribute, path, orpanRemoval, eager);
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
	public OwnerOneToOneMapping<T, X> getOpposite() {
		return (OwnerOneToOneMapping<T, X>) super.getOpposite();
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
