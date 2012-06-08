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

import java.sql.SQLException;
import java.util.Collection;

import javax.persistence.CascadeType;
import javax.persistence.FetchType;

import org.apache.commons.lang.StringUtils;
import org.batoo.jpa.core.impl.instance.ManagedInstance;
import org.batoo.jpa.core.impl.jdbc.ConnectionImpl;
import org.batoo.jpa.core.impl.jdbc.ForeignKey;
import org.batoo.jpa.core.impl.jdbc.JoinTable;
import org.batoo.jpa.core.impl.manager.SessionImpl;
import org.batoo.jpa.core.impl.metamodel.MetamodelImpl;
import org.batoo.jpa.core.impl.model.EntityTypeImpl;
import org.batoo.jpa.core.impl.model.ManagedTypeImpl;
import org.batoo.jpa.parser.MappingException;
import org.batoo.jpa.parser.metadata.attribute.AssociationAttributeMetadata;

/**
 * Attribute representing persistent collection-valued association attributes.
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
public abstract class AssociatedPluralAttribute<X, C, E> extends PluralAttributeImpl<X, C, E> implements AssociatedAttribute<X, E, C> {

	private final PersistentAttributeType attributeType;
	private final String inverseName;
	private AssociatedAttribute<E, X, ?> inverse;

	// Cascades
	private final boolean cascadesDetach;
	private final boolean cascadesMerge;
	private final boolean cascadesPersist;
	private final boolean cascadesRefresh;
	private final boolean cascadesRemove;
	private final boolean removesOrphans;
	private final boolean eager;

	private EntityTypeImpl<E> type;
	private JoinTable joinTable;

	/**
	 * @param declaringType
	 *            the declaring type
	 * @param metadata
	 *            the metadata
	 * @param attributeType
	 *            attribute type
	 * @param mappedBy
	 *            the mapped by attribute
	 * @param removesOrphans
	 *            if attribute removes orphans
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public AssociatedPluralAttribute(ManagedTypeImpl<X> declaringType, PersistentAttributeType attributeType,
		AssociationAttributeMetadata metadata, String mappedBy, boolean removesOrphans) {
		super(declaringType, metadata);

		this.attributeType = attributeType;
		this.inverseName = mappedBy;
		this.removesOrphans = removesOrphans;

		this.eager = metadata.getFetchType() == FetchType.EAGER;

		this.cascadesDetach = metadata.getCascades().contains(CascadeType.ALL) || metadata.getCascades().contains(CascadeType.DETACH);
		this.cascadesMerge = metadata.getCascades().contains(CascadeType.ALL) || metadata.getCascades().contains(CascadeType.MERGE);
		this.cascadesPersist = metadata.getCascades().contains(CascadeType.ALL) || metadata.getCascades().contains(CascadeType.PERSIST);
		this.cascadesRefresh = metadata.getCascades().contains(CascadeType.ALL) || metadata.getCascades().contains(CascadeType.REFRESH);
		this.cascadesRemove = metadata.getCascades().contains(CascadeType.ALL) || metadata.getCascades().contains(CascadeType.REMOVE);

		if (StringUtils.isBlank(this.inverseName)) {
			if ((this.getPersistentAttributeType() == PersistentAttributeType.MANY_TO_MANY) || (metadata.getJoinTable() != null)) {
				this.joinTable = new JoinTable(metadata.getJoinTable());
			}
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public final boolean cascadesDetach() {
		return this.cascadesDetach;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public final boolean cascadesMerge() {
		return this.cascadesMerge;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public final boolean cascadesPersist() {
		return this.cascadesPersist;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public final boolean cascadesRefresh() {
		return this.cascadesRefresh;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public final boolean cascadesRemove() {
		return this.cascadesRemove;
	}

	/**
	 * Flushes the associates.
	 * 
	 * @param session
	 *            the session
	 * @param connection
	 *            the connection to use
	 * @param managedInstance
	 *            the managed instance
	 * @throws SQLException
	 *             thrown if there is an underlying SQL Exception
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public abstract void flush(SessionImpl session, ConnectionImpl connection, ManagedInstance<? extends X> managedInstance)
		throws SQLException;

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public final EntityTypeImpl<E> getElementType() {
		return this.type;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public ForeignKey getForeignKey() {
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public AssociatedAttribute<E, X, ?> getInverse() {
		return this.inverse;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public JoinTable getJoinTable() {
		return this.joinTable;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public PersistentAttributeType getPersistentAttributeType() {
		return this.attributeType;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public final boolean isAssociation() {
		return true;
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
		return this.inverseName == null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void link() throws MappingException {
		final MetamodelImpl metamodel = this.getDeclaringType().getMetamodel();
		this.type = metamodel.entity(this.getBindableJavaType());

		if (StringUtils.isNotBlank(this.inverseName)) {
			this.inverse = (AssociatedAttribute<E, X, ?>) this.type.getAttribute(this.inverseName);

			if (this.inverse == null) {
				throw new MappingException("Cannot find the mappedBy attribute " + this.inverseName + " specified on "
					+ this.getJavaMember());
			}

			this.inverse.setInverse(this);
		}
		else {
			if (this.joinTable != null) {
				this.joinTable.link((EntityTypeImpl<X>) this.getDeclaringType(), this.type);
			}
			else {
				this.inverse = this.type.addVirtualAttribute(this);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public final boolean references(Object instance, Object reference) {
		final C collection = this.get(instance);
		if (collection instanceof Collection) {
			return ((Collection<E>) collection).contains(reference);
		}

		return false;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void setInverse(AssociatedAttribute<E, X, ?> inverse) {
		this.inverse = inverse;
	}
}
