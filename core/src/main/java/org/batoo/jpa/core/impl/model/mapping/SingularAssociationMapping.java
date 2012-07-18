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
import java.util.IdentityHashMap;

import javax.persistence.PersistenceException;
import javax.persistence.metamodel.Attribute.PersistentAttributeType;

import org.apache.commons.lang.mutable.MutableBoolean;
import org.batoo.jpa.core.impl.instance.ManagedInstance;
import org.batoo.jpa.core.impl.jdbc.ConnectionImpl;
import org.batoo.jpa.core.impl.jdbc.ForeignKey;
import org.batoo.jpa.core.impl.jdbc.JoinTable;
import org.batoo.jpa.core.impl.manager.EntityManagerImpl;
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
public class SingularAssociationMapping<Z, X> extends AssociationMapping<Z, X, X> {

	private final AssociatedSingularAttribute<? super Z, X> attribute;
	private final JoinTable joinTable;
	private final ForeignKey foreignKey;
	private EntityTypeImpl<X> type;
	private AssociationMapping<?, ?, ?> inverse;

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
			final Object instanceId = this.type.getInstanceId(instance);
			if (instanceId == null) {
				throw new PersistenceException("Instance " + instance + " is not managed");
			}
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void flush(ConnectionImpl connection, ManagedInstance<?> managedInstance, boolean removals, boolean force) throws SQLException {
		if (this.getTable() != null) {
			if (!removals) {
				final X entity = this.get(managedInstance.getInstance());
				this.getTable().performInsert(connection, managedInstance.getInstance(), entity, -1);
			}
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
	public AssociationMapping<?, ?, ?> getInverse() {
		return this.inverse;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public MappingType getMappingType() {
		return MappingType.SINGULAR_ASSOCIATION;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public JoinTable getTable() {
		return this.joinTable;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public EntityTypeImpl<X> getType() {
		return this.type;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void initialize(ManagedInstance<?> instance) {
		// noop
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
	public void link() throws MappingException {
		final EntityTypeImpl<?> entity = this.getRoot().getType();
		final MetamodelImpl metamodel = entity.getMetamodel();

		this.type = metamodel.entity(this.attribute.getJavaType());

		if (!this.isOwner()) {
			this.inverse = (AssociationMapping<?, ?, ?>) this.type.getRootMapping().getMapping(this.getMappedBy());

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
	public void mergeWith(EntityManagerImpl entityManager, ManagedInstance<?> instance, Object entity, MutableBoolean requiresFlush,
		IdentityHashMap<Object, Object> processed) {
		// get the new value as merged
		final X newEntity = entityManager.mergeImpl(this.get(entity), requiresFlush, processed, this.cascadesMerge());

		// get the old value
		final X oldEntity = this.get(instance.getInstance());

		// if no change nothing to do here
		if (oldEntity == newEntity) {
			return;
		}

		// handle the remove orphans and inverse
		if ((oldEntity != null) && (this.removesOrphans() || (this.inverse != null))) {
			// handle orphan removal
			if (this.removesOrphans()) {
				entityManager.remove(oldEntity);
			}

			// update the other side of the relation
			if ((this.inverse != null) && (this.inverse.getAttribute().getPersistentAttributeType() == PersistentAttributeType.ONE_TO_ONE)) {
				final ManagedInstance<X> oldInstance = instance.getSession().get(oldEntity);
				this.inverse.set(oldInstance, null);
			}
		}

		// set the new value
		this.set(instance, newEntity);
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
	public void setInverse(AssociationMapping<?, ?, ?> inverse) {
		this.inverse = inverse;
	}
}
