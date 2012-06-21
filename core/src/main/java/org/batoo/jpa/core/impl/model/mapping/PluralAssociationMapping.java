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
import java.util.Collection;
import java.util.Map;

import javax.persistence.metamodel.Attribute.PersistentAttributeType;

import org.batoo.jpa.core.impl.collections.ManagedCollection;
import org.batoo.jpa.core.impl.instance.ManagedInstance;
import org.batoo.jpa.core.impl.jdbc.ConnectionImpl;
import org.batoo.jpa.core.impl.jdbc.ForeignKey;
import org.batoo.jpa.core.impl.jdbc.JoinTable;
import org.batoo.jpa.core.impl.manager.SessionImpl;
import org.batoo.jpa.core.impl.model.attribute.PluralAttributeImpl;
import org.batoo.jpa.core.impl.model.type.EntityTypeImpl;
import org.batoo.jpa.parser.MappingException;
import org.batoo.jpa.parser.metadata.AssociationMetadata;
import org.batoo.jpa.parser.metadata.attribute.AssociationAttributeMetadata;

/**
 * 
 * @param <Z>
 *            the source type
 * @param <E>
 *            the element type
 * @param <C>
 *            the collection type
 * 
 * @author hceylan
 * @since $version
 */
public class PluralAssociationMapping<Z, C, E> extends AssociationMapping<Z, C> {

	private final PluralAttributeImpl<? super Z, C, E> attribute;
	private final JoinTable joinTable;
	private EntityTypeImpl<E> type;
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
	public PluralAssociationMapping(ParentMapping<?, Z> parent, PluralAttributeImpl<? super Z, C, E> attribute) {
		super(parent, (AssociationAttributeMetadata) attribute.getMetadata(), attribute);

		this.attribute = attribute;

		final AssociationMetadata metadata = this.getAssociationMetadata();

		if (this.isOwner()) {
			if ((this.attribute.getPersistentAttributeType() == PersistentAttributeType.MANY_TO_MANY) || (metadata.getJoinColumns().size() == 0)) {
				this.joinTable = new JoinTable(this.getRoot().getType(), metadata.getJoinTable());
			}
			else {
				this.joinTable = null;
			}
		}
		else {
			this.joinTable = null;
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void checkTransient(ManagedInstance<?> managedInstance) {
		final Object values = this.get(managedInstance.getInstance());

		final SessionImpl session = managedInstance.getSession();

		if (values instanceof Collection) {
			for (final E entity : (Collection<E>) values) {
				session.checkTransient(entity);
			}
		}
		else if (values instanceof Map) {
			for (final E entity : ((Map<?, E>) values).values()) {
				session.checkTransient(entity);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void flush(ConnectionImpl connection, ManagedInstance<?> managedInstance) throws SQLException {
		final Object values = this.get(managedInstance.getInstance());

		final SessionImpl session = managedInstance.getSession();

		if (values instanceof Collection) {
			for (final E entity : (Collection<E>) values) {
				this.getJoinTable().performInsert(session, connection, managedInstance.getInstance(), entity);
			}
		}
		else if (values instanceof Map) {
			for (final E entity : ((Map<?, E>) values).values()) {
				this.getJoinTable().performInsert(session, connection, managedInstance.getInstance(), entity);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public PluralAttributeImpl<? super Z, C, E> getAttribute() {
		return this.attribute;
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
		entity.getMetamodel();

		this.type = (EntityTypeImpl<E>) this.attribute.getElementType();

		if (!this.isOwner()) {
			this.inverse = (AssociationMapping<?, ?>) this.type.getRootMapping().getMapping(this.getMappedBy());

			if (this.inverse == null) {
				throw new MappingException("Cannot find the mappedBy attribute " + this.getMappedBy() + " specified on " + this.attribute.getJavaMember());
			}

			this.inverse.setInverse(this);
		}
		else {
			// initialize the join table
			if (this.getJoinTable() != null) {
				this.getJoinTable().link(entity, this.type);
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
	 * Loads and returns the collection.
	 * 
	 * @param managedInstance
	 *            the managed instance owning the collection
	 * @return the loaded collection
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public Collection<? extends E> loadCollection(ManagedInstance<?> managedInstance) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean references(Object instance, Object reference) {
		final Object values = this.get(instance);

		if (values == null) {
			return false;
		}

		if (values instanceof Collection) {
			return ((Collection<?>) values).contains(reference);
		}

		return ((Map<?, ?>) values).containsValue(reference);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void set(ManagedInstance<?> managedInstance, Object instance, Object value) {
		C collection;

		if (value instanceof ManagedCollection) {
			collection = (C) value;
		}
		else {
			collection = this.attribute.newCollection(this, managedInstance, (Collection<? extends E>) value);
		}

		super.set(managedInstance, instance, collection);
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
		this.set(instance, this.attribute.newCollection(this, instance));
	}
}
