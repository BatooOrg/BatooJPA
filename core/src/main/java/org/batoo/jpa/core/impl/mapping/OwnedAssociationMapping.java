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
import java.util.HashSet;

import javax.persistence.CascadeType;
import javax.persistence.FetchType;

import org.batoo.jpa.core.MappingException;
import org.batoo.jpa.core.impl.instance.OwnedAssociateResolver;
import org.batoo.jpa.core.impl.types.AttributeImpl;

import com.google.common.collect.Sets;

/**
 * 
 * @author hceylan
 * @since $version
 */
public abstract class OwnedAssociationMapping<X, T> extends AbstractMapping<X, T> implements OwnedAssociation<X, T> {

	private final HashSet<CascadeType> cascadeTypes;
	private final boolean orpanRemoval;
	private final boolean eager;
	private OwnerAssociation<T, X> opposite;

	/**
	 * @param associationType
	 *            the type of the association
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
	public OwnedAssociationMapping(AssociationType associationType, AttributeImpl<X, T> declaringAttribute,
		Deque<AttributeImpl<?, ?>> path, boolean orpanRemoval, boolean eager) throws MappingException {
		super(associationType, declaringAttribute, path);

		this.cascadeTypes = Sets.newHashSet(declaringAttribute.getCascadeType());
		this.orpanRemoval = orpanRemoval;
		this.eager = eager;

		this.getOwner().addMapping(this);
	}

	private boolean cascadeAll() {
		return this.cascadeTypes.contains(CascadeType.ALL);
	};

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
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public final <Y> OwnedAssociateResolver<Y> createResolver(Y instance) {
		return new OwnedAssociateResolver<Y>(this, instance);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public OwnerAssociation<T, X> getOpposite() {
		final String mappedBy = this.getDeclaringAttribute().getMappedBy();
		return (OwnerAssociation<T, X>) this.getType().getMaping(mappedBy);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean isEager() {
		return this.eager;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	public final boolean isOwner() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void link() {
		final String mappedBy = this.getDeclaringAttribute().getMappedBy();
		this.opposite = (OwnerAssociation<T, X>) this.getType().getMaping(mappedBy);

		if (this.opposite != null) {
			this.opposite.setOpposite(this);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public final boolean orphanRemoval() {
		return this.orpanRemoval;
	}
}
