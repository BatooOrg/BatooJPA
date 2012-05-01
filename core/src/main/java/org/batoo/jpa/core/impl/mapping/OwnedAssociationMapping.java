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

import javax.persistence.CascadeType;
import javax.persistence.FetchType;

import org.apache.commons.lang.StringUtils;
import org.batoo.jpa.core.MappingException;
import org.batoo.jpa.core.impl.instance.OwnedAssociateResolver;
import org.batoo.jpa.core.impl.types.AttributeImpl;
import org.batoo.jpa.core.jdbc.adapter.JDBCAdapter;

import com.google.common.collect.Sets;

/**
 * 
 * @author hceylan
 * @since $version
 */
public abstract class OwnedAssociationMapping<X, T> extends AbstractMapping<X, T> implements OwnedAssociation<X, T> {

	private final boolean orpanRemoval;

	private final boolean eager;
	private final boolean cascadeDetach;
	private final boolean cascadeMerge;
	private final boolean cascadePersist;
	private final boolean cascadeRefresh;
	private final boolean cascadeRemove;

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

		this.orpanRemoval = orpanRemoval;
		this.eager = eager;

		this.getOwner().addMapping(this, false);

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
	public final boolean cascadeDetach() {
		return this.cascadeDetach;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public final boolean cascadeMerge() {
		return this.cascadeMerge;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public final boolean cascadePersist() {
		return this.cascadePersist;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public final boolean cascadeRefresh() {
		return this.cascadeRefresh;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public final boolean cascadeRemove() {
		return this.cascadeRemove;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public final OwnedAssociateResolver createResolver(Object instance) {
		return new OwnedAssociateResolver(this, instance);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public OwnerAssociation<T, X> getOpposite() {
		final String mappedBy = this.getDeclaringAttribute().getMappedBy();
		return (OwnerAssociation<T, X>) this.getType().getMapping(mappedBy);
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
	@Override
	public final boolean isOwner() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void link(JDBCAdapter jdbcAdapter) throws MappingException {
		final String mappedBy = this.getDeclaringAttribute().getMappedBy();
		AbstractMapping<?, ?> mapping = null;

		if (StringUtils.isNotEmpty(mappedBy)) {
			mapping = this.getType().getMapping(mappedBy);

			if (mapping == null) {
				final String thisName = this.getOwner().getJavaType().getCanonicalName() + "." + this.getDeclaringAttribute().getName();
				throw new MappingException("Mappedby property not found: " + thisName + "(" + mappedBy + ")");
			}
		}

		if (mapping != null) {
			if (!(mapping instanceof OwnerAssociation)) {
				final String thisName = this.getOwner().getJavaType().getCanonicalName() + "." + this.getDeclaringAttribute().getName();
				final String otherName = mapping.getOwner().getJavaType().getCanonicalName() + "."
					+ mapping.getDeclaringAttribute().getName();
				throw new MappingException("Both sides of the OneToOne mapping is marked with mappedBy, only one side can be the owner: "
					+ thisName + " - " + otherName);
			}

			this.opposite = (OwnerAssociation<T, X>) mapping;
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
