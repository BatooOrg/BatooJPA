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
import java.util.Set;

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

	private final boolean eager;

	private OwnedAssociation<T, X> opposite;
	private final boolean cascadeDetach;
	private final boolean cascadeMerge;
	private final boolean cascadePersist;
	private final boolean cascadeRefresh;
	private final boolean cascadeRemove;

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

		final Set<CascadeType> cascadeTypes = Sets.newHashSet(declaringAttribute.getCascadeType());
		final boolean cascadeAll = cascadeTypes.contains(CascadeType.ALL);
		this.cascadeDetach = cascadeAll || cascadeTypes.contains(CascadeType.DETACH);
		this.cascadeMerge = cascadeAll || cascadeTypes.contains(CascadeType.MERGE);
		this.cascadePersist = cascadeAll || cascadeTypes.contains(CascadeType.PERSIST);
		this.cascadeRefresh = cascadeAll || cascadeTypes.contains(CascadeType.REFRESH);
		this.cascadeRemove = cascadeAll || cascadeTypes.contains(CascadeType.REMOVE);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean cascadeDetach() {
		return this.cascadeDetach;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean cascadeMerge() {
		return this.cascadeMerge;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean cascadePersist() {
		return this.cascadePersist;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean cascadeRefresh() {
		return this.cascadeRefresh;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean cascadeRemove() {
		return this.cascadeRemove;
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
	 * Returns the cascadeRemove.
	 * 
	 * @return the cascadeRemove
	 * @since $version
	 */
	public boolean isCascadeRemove() {
		return this.cascadeRemove;
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
