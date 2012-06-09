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

import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.metamodel.SingularAttribute;

import org.apache.commons.lang.StringUtils;
import org.batoo.jpa.core.impl.instance.ManagedInstance;
import org.batoo.jpa.core.impl.jdbc.ConnectionImpl;
import org.batoo.jpa.core.impl.jdbc.ForeignKey;
import org.batoo.jpa.core.impl.jdbc.JoinTable;
import org.batoo.jpa.core.impl.manager.SessionImpl;
import org.batoo.jpa.core.impl.metamodel.MetamodelImpl;
import org.batoo.jpa.core.impl.model.EntityTypeImpl;
import org.batoo.jpa.core.impl.model.ManagedTypeImpl;
import org.batoo.jpa.core.jdbc.adapter.JdbcAdaptor;
import org.batoo.jpa.parser.MappingException;
import org.batoo.jpa.parser.metadata.attribute.AssociationAttributeMetadata;

/**
 * Implementation of {@link SingularAttribute} representing types of ManyToOne and OneToOne
 * 
 * @param <X>
 *            The type containing the represented attribute
 * @param <T>
 *            The type of the represented attribute
 * @author hceylan
 * @since $version
 */
public class AssociatedSingularAttribute<X, T> extends SingularAttributeImpl<X, T> implements SingularAttribute<X, T>,
	AssociatedAttribute<X, T, T> {

	private final PersistentAttributeType attributeType;
	private final String inverseName;
	private final ForeignKey foreignKey;
	private final JoinTable joinTable;

	private final boolean eager;
	private final boolean optional;
	private final boolean cascadesDetach;
	private final boolean cascadesMerge;
	private final boolean cascadesPersist;
	private final boolean cascadesRefresh;
	private final boolean cascadesRemove;

	private EntityTypeImpl<T> type;
	private AssociatedAttribute<T, X, ?> inverse;

	/**
	 * @param declaringType
	 *            the declaring type
	 * @param metadata
	 *            the metadata
	 * @param attributeType
	 *            the type of the attribute
	 * @param mappedBy
	 *            the mapped by attribute
	 * @param optional
	 *            if the attribute is optional
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public AssociatedSingularAttribute(ManagedTypeImpl<X> declaringType, PersistentAttributeType attributeType,
		AssociationAttributeMetadata metadata, String mappedBy, boolean optional) {
		super(declaringType, metadata);

		this.attributeType = attributeType;
		this.inverseName = mappedBy;
		this.optional = optional;

		this.eager = metadata.getFetchType() == FetchType.EAGER;

		this.cascadesDetach = metadata.getCascades().contains(CascadeType.ALL) || metadata.getCascades().contains(CascadeType.DETACH);
		this.cascadesMerge = metadata.getCascades().contains(CascadeType.ALL) || metadata.getCascades().contains(CascadeType.MERGE);
		this.cascadesPersist = metadata.getCascades().contains(CascadeType.ALL) || metadata.getCascades().contains(CascadeType.PERSIST);
		this.cascadesRefresh = metadata.getCascades().contains(CascadeType.ALL) || metadata.getCascades().contains(CascadeType.REFRESH);
		this.cascadesRemove = metadata.getCascades().contains(CascadeType.ALL) || metadata.getCascades().contains(CascadeType.REMOVE);

		if (StringUtils.isBlank(this.inverseName)) {
			if (metadata.getJoinTable() != null) {
				this.joinTable = new JoinTable(metadata.getJoinTable());
				this.foreignKey = null;
			}
			else {
				this.foreignKey = new ForeignKey(metadata.getJoinColumns());
				this.joinTable = null;
			}
		}
		else {
			this.joinTable = null;
			this.foreignKey = null;
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean cascadesDetach() {
		return this.cascadesDetach;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean cascadesMerge() {
		return this.cascadesMerge;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean cascadesPersist() {
		return this.cascadesPersist;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean cascadesRefresh() {
		return this.cascadesRefresh;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean cascadesRemove() {
		return this.cascadesRemove;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void checkTransient(ManagedInstance<? extends X> managedInstance) {
		final T instance = this.get(managedInstance.getInstance());

		managedInstance.getSession().checkTransient(instance);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String describe() {
		return super.toString();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void flush(SessionImpl session, ConnectionImpl connection, ManagedInstance<? extends X> managedInstance) throws SQLException {
		if (this.joinTable != null) {
			final T entity = this.get(managedInstance.getInstance());

			this.getJoinTable().performInsert(session, connection, managedInstance.getInstance(), entity);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public EntityTypeImpl<T> getAssociationType() {
		return this.getType();
	}

	/**
	 * Returns the foreign key of the attribute.
	 * 
	 * @return the foreign key of the attribute
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Override
	public ForeignKey getForeignKey() {
		return this.foreignKey;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public AssociatedAttribute<T, X, ?> getInverse() {
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
	public EntityTypeImpl<T> getType() {
		return this.type;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean isAssociation() {
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
	public boolean isId() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean isOptional() {
		return this.optional;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean isOwner() {
		return this.inverseName == null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean isVersion() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void link() throws MappingException {
		final MetamodelImpl metamodel = this.getDeclaringType().getMetamodel();

		this.type = metamodel.entity(this.getJavaType());

		if (StringUtils.isNotBlank(this.inverseName)) {
			this.inverse = (AssociatedAttribute<T, X, ?>) this.type.getAttribute(this.inverseName);

			if (this.inverse == null) {
				throw new MappingException("Cannot find the mappedBy attribute " + this.inverseName + " specified on "
					+ this.getJavaMember());
			}

			this.inverse.setInverse(this);
		}
		else {
			// initialize the foreign key
			final JdbcAdaptor jdbcAdaptor = this.getDeclaringType().getMetamodel().getJdbcAdaptor();
			if (this.joinTable != null) {
				this.joinTable.link((EntityTypeImpl<X>) this.getDeclaringType(), this.type);
			}
			else {
				this.foreignKey.link(jdbcAdaptor, this, this.type);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean references(Object instance, Object reference) {
		return this.get(instance) == reference;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void setInverse(AssociatedAttribute<T, X, ?> inverse) {
		this.inverse = inverse;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder("association").append(super.toString());

		if (this.getPersistentAttributeType() == PersistentAttributeType.MANY_TO_ONE) {
			if (this.isOptional()) {
				builder.append(" <0..*>");
			}
			else {
				builder.append(" <1..*>");
			}
		}
		else {
			if (this.isOptional()) {
				builder.append(" <0..1>");
			}
			else {
				builder.append(" <1..1>");
			}
		}

		if (this.inverse != null) {
			builder.append(this.inverse.describe());
		}

		return builder.toString();
	}
}
