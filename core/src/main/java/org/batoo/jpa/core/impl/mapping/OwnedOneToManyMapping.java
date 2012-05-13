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

import java.sql.SQLException;
import java.util.Collection;
import java.util.Deque;

import org.batoo.jpa.core.MappingException;
import org.batoo.jpa.core.impl.SessionImpl;
import org.batoo.jpa.core.impl.collections.ManagedCollection;
import org.batoo.jpa.core.impl.instance.ManagedInstance;
import org.batoo.jpa.core.impl.jdbc.AssociationSelectHelper;
import org.batoo.jpa.core.impl.metamodel.AttributeImpl;
import org.batoo.jpa.core.impl.metamodel.EntityTypeImpl;
import org.batoo.jpa.core.impl.metamodel.PluralAttributeImpl;

/**
 * Implementation of {@link AssociationType#MANY} relational attributes.
 * <p>
 * This is a {@link OneToMany} type of mapping with the declaring attribute being the owned.
 * 
 * @author hceylan
 * @since $version
 */
public class OwnedOneToManyMapping<X, C, E> extends OwnedAssociationMapping<X, C> implements CollectionMapping<X, C, E> {

	private final AssociationSelectHelper<X, C, E> selectHelper;

	/**
	 * @param declaringAttribute
	 *            the attribute which declares the mapping
	 * @param path
	 *            the path to the declaringAttribute
	 * @param column
	 *            the column definition of the mapping
	 * @param orpanRemoval
	 *            if orphans should be removed
	 * @param eager
	 *            if association is annotated with {@link FetchType#EAGER}
	 * @throws MappingException
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public OwnedOneToManyMapping(PluralAttributeImpl<X, C, E> declaringAttribute, Deque<AttributeImpl<?, ?>> path, boolean orphanRemoval,
		boolean eager) throws MappingException {
		super(AssociationType.MANY, declaringAttribute, path, orphanRemoval, eager);

		this.selectHelper = new AssociationSelectHelper<X, C, E>(this);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public PluralAttributeImpl<X, C, E> getDeclaringAttribute() {
		return (PluralAttributeImpl<X, C, E>) super.getDeclaringAttribute();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public OwnerManyToOneMapping<C, X> getOpposite() {
		return (OwnerManyToOneMapping<C, X>) super.getOpposite();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public EntityTypeImpl<C> getType() {
		return (EntityTypeImpl<C>) this.getDeclaringAttribute().getElementType();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Collection<E> performSelect(SessionImpl session, ManagedInstance<X> managedInstance) throws SQLException {
		return this.selectHelper.select(session, managedInstance);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void reset(Object instance) {
		this.getDeclaringAttribute().reset(instance);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void setCollection(Object instance, ManagedCollection<?> collection) {
		this.getDeclaringAttribute().setCollection(instance, collection);
	}

}
