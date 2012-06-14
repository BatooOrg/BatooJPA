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
package org.batoo.jpa.core.impl.model.mapping;

import java.sql.SQLException;

import org.batoo.jpa.core.impl.instance.ManagedInstance;
import org.batoo.jpa.core.impl.jdbc.ConnectionImpl;
import org.batoo.jpa.core.impl.jdbc.ForeignKey;
import org.batoo.jpa.core.impl.jdbc.JoinTable;
import org.batoo.jpa.core.impl.model.MetamodelImpl;
import org.batoo.jpa.core.impl.model.attribute.AssociatedSingularAttribute;
import org.batoo.jpa.core.impl.model.type.EntityTypeImpl;
import org.batoo.jpa.parser.MappingException;
import org.batoo.jpa.parser.metadata.AssociationMetadata;

/**
 * The mapping for one-to-one and many-to-one associations.
 * 
 * @param <X>
 *            the type of the entity
 * @param <Z>
 *            the inverse entity type
 * 
 * @author hceylan
 * @since $version
 */
public class SingularAssociationMapping<X, Z> extends AssociationMapping<X, Z, Z> {

	private final AssociatedSingularAttribute<X, Z> attribute;
	private AssociationMapping<Z, X, ?> inverse;
	private EntityTypeImpl<Z> type;
	private JoinTable joinTable;
	private ForeignKey foreignKey;

	/**
	 * @param parent
	 *            the parent mapping, may be <code>null</code>
	 * 
	 * @param entity
	 *            the entity
	 * @param attribute
	 *            the attribute
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public SingularAssociationMapping(EmbeddedMapping<?, ?> parent, EntityTypeImpl<X> entity, AssociatedSingularAttribute<X, Z> attribute) {
		super(parent, entity, attribute.getMetadata());

		this.attribute = attribute;
		final AssociationMetadata metadata = this.getAssociationMetadata();

		if (this.isOwner()) {
			if (metadata.getJoinTable() != null) {
				this.joinTable = new JoinTable(entity, metadata.getJoinTable());
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
	public void checkTransient(ManagedInstance<?> managedInstance) {
		final Z instance = this.get(managedInstance.getInstance());
		if (instance != null) {
			managedInstance.getSession().checkTransient(instance);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void flush(ConnectionImpl connection, ManagedInstance<?> managedInstance) throws SQLException {
		if (this.getJoinTable() != null) {
			final Z entity = this.get(managedInstance.getInstance());

			this.getJoinTable().performInsert(managedInstance.getSession(), connection, managedInstance.getInstance(), entity);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public AssociatedSingularAttribute<? super X, Z> getAttribute() {
		return this.attribute;
	}

	/**
	 * {@inheritDoc}
	 * 
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
	public AssociationMapping<Z, X, ?> getInverse() {
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
	public EntityTypeImpl<Z> getType() {
		return this.type;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void link() throws MappingException {
		final MetamodelImpl metamodel = this.getEntity().getMetamodel();

		this.type = metamodel.entity(this.attribute.getJavaType());

		if (!this.isOwner()) {
			this.inverse = (AssociationMapping<Z, X, ?>) this.type.getMapping(this.getMappedBy());

			if (this.inverse == null) {
				throw new MappingException("Cannot find the mappedBy attribute " + this.getMappedBy() + " specified on "
					+ this.attribute.getJavaMember());
			}

			this.inverse.setInverse(this);
		}
		else {
			// initialize the join table
			if (this.getJoinTable() != null) {
				this.getJoinTable().link(this.getEntity(), this.type);
			}
			// initialize the foreign key
			else {
				this.getForeignKey().link(this, this.type);
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
	public void setInverse(AssociationMapping<Z, X, ?> inverse) {
		this.inverse = inverse;
	}
}
