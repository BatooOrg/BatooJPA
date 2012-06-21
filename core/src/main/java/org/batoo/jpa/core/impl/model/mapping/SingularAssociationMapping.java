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
 * Mappings for one-to-one and many-to-one associations.
 * 
 * @param <Z>
 *            the source type
 * @param <X>
 *            the destination type
 * 
 * @author hceylan
 * @since $version
 */
public class SingularAssociationMapping<Z, X> extends AssociationMapping<Z, X> {

	private final AssociatedSingularAttribute<? super Z, X> attribute;
	private final JoinTable joinTable;
	private final ForeignKey foreignKey;
	private EntityTypeImpl<X> type;
	private AssociationMapping<?, ?> inverse;

	/**
	 * @param parent
	 *            the parent mapping
	 * @param attribute
	 *            the attribute
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public SingularAssociationMapping(ParentMapping<?, Z> parent, AssociatedSingularAttribute<? super Z, X> attribute) {
		super(parent, attribute.getMetadata(), attribute);

		this.attribute = attribute;

		final AssociationMetadata metadata = this.getAssociationMetadata();

		if (this.isOwner()) {
			if (metadata.getJoinTable() != null) {
				this.joinTable = new JoinTable(this.getRoot().getType(), metadata.getJoinTable());
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
		final X instance = this.get(managedInstance.getInstance());
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
			final X entity = this.get(managedInstance.getInstance());

			this.getJoinTable().performInsert(managedInstance.getSession(), connection, managedInstance.getInstance(), entity);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public AssociatedSingularAttribute<? super Z, X> getAttribute() {
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
	public AssociationMapping<?, ?> getInverse() {
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
	public EntityTypeImpl<?> getType() {
		return this.type;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void link() throws MappingException {
		final EntityTypeImpl<?> entity = this.getRoot().getType();
		final MetamodelImpl metamodel = entity.getMetamodel();

		this.type = metamodel.entity(this.attribute.getJavaType());

		if (!this.isOwner()) {
			this.inverse = (AssociationMapping<?, ?>) this.type.getRootMapping().getMapping(this.getMappedBy());

			if (this.inverse == null) {
				throw new MappingException("Cannot find the mappedBy attribute " + this.getMappedBy() + " specified on " + this.attribute.getJavaMember());
			}

			this.inverse.setInverse(this);
		}
		else {
			// initialize the join table
			if (this.joinTable != null) {
				this.joinTable.link(entity, this.type);
			}
			// initialize the foreign key
			else {
				this.foreignKey.link(this, this.type);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void load(ManagedInstance<?> instance) {
		// TODO Auto-generated method stub
	}

	/**
	 * @param managedInstance
	 *            the managed instance
	 * @param mappingId
	 *            the id of the mapping
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void load(ManagedInstance<?> managedInstance, Object mappingId) {
		// TODO Auto-generated method stub

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
	public void setInverse(AssociationMapping<?, ?> inverse) {
		this.inverse = inverse;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void setLazy(ManagedInstance<?> instance) {
		final EntityTypeImpl<X> type = this.attribute.getType();
		final ManagedInstance<X> value = type.getManagedInstanceById(instance.getSession(), this, instance.getId(), null, true);
		this.set(instance, value.getInstance());
	}
}
