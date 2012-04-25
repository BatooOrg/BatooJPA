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

import java.util.Collection;
import java.util.Deque;
import java.util.HashSet;

import javax.persistence.CascadeType;
import javax.persistence.FetchType;

import org.batoo.jpa.core.MappingException;
import org.batoo.jpa.core.impl.instance.AssociateResolver;
import org.batoo.jpa.core.impl.types.AttributeImpl;

import com.google.common.collect.Sets;

/**
 * 
 * @author hceylan
 * @since $version
 */
public abstract class OwnerAssociationMapping<X, T> extends AbstractPhysicalMapping<X, T> implements OwnerAssociation<X, T> {

	private final HashSet<CascadeType> cascadeTypes;
	private final boolean eager;

	private OwnedAssociation<T, X> opposite;

	/**
	 * @param associationType
	 *            the type of the association
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
	public OwnerAssociationMapping(AssociationType associationType, AttributeImpl<X, T> declaringAttribute,
		Deque<AttributeImpl<?, ?>> path, Collection<ColumnTemplate<X, T>> columnTemplates, boolean eager) throws MappingException {
		super(associationType, declaringAttribute, path, columnTemplates);

		this.eager = eager;
		this.cascadeTypes = Sets.newHashSet(declaringAttribute.getCascadeType());
	}

	private boolean cascadeAll() {
		return this.cascadeTypes.contains(CascadeType.ALL);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public final boolean cascadeDetach() {
		return this.cascadeAll() || this.cascadeTypes.contains(CascadeType.DETACH);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public final boolean cascadeMerge() {
		return this.cascadeAll() || this.cascadeTypes.contains(CascadeType.MERGE);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public final boolean cascadePersist() {
		return this.cascadeAll() || this.cascadeTypes.contains(CascadeType.PERSIST);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public final boolean cascadeRefresh() {
		return this.cascadeAll() || this.cascadeTypes.contains(CascadeType.REFRESH);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public final boolean cascadeRemove() {
		return this.cascadeAll() || this.cascadeTypes.contains(CascadeType.REMOVE);
	}

	/**
	 * Returns true if this association contains the instance.
	 * 
	 * @param instance
	 *            the instance
	 * @return true if this association contains the instance
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public abstract boolean contains(Object instance);

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public AssociateResolver createResolver(Object instance) {
		return new AssociateResolver(this, instance);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public OwnedAssociation<T, X> getOpposite() {
		return this.opposite;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public final boolean isEager() {
		return this.eager;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public final boolean isOwner() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void setOpposite(OwnedAssociation<T, X> opposite) {
		this.opposite = opposite;
	}
}
