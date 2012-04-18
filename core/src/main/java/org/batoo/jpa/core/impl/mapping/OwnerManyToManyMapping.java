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

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Deque;

import javax.persistence.FetchType;
import javax.persistence.OneToOne;

import org.batoo.jpa.core.MappingException;
import org.batoo.jpa.core.impl.SessionImpl;
import org.batoo.jpa.core.impl.collections.ManagedCollection;
import org.batoo.jpa.core.impl.instance.ManagedId;
import org.batoo.jpa.core.impl.instance.ManagedInstance;
import org.batoo.jpa.core.impl.jdbc.AssociationSelectHelper;
import org.batoo.jpa.core.impl.jdbc.JoinTable;
import org.batoo.jpa.core.impl.jdbc.PhysicalColumn;
import org.batoo.jpa.core.impl.types.AttributeImpl;
import org.batoo.jpa.core.impl.types.EntityTypeImpl;
import org.batoo.jpa.core.impl.types.PluralAttributeImpl;
import org.batoo.jpa.core.jdbc.adapter.JDBCAdapter;

/**
 * Implementation of {@link AssociationType#MANY} relational attributes.
 * <p>
 * This is a {@link OneToOne} type of mapping with the declaring attribute being the owner.
 * 
 * @author hceylan
 * @since $version
 */
public class OwnerManyToManyMapping<X, C, E> extends OwnerAssociationMapping<X, C> implements Association<X, C>,
	CollectionMapping<X, C, E>, PersistableAssociation<X, C> {

	private JoinTable joinTable;

	private final AssociationSelectHelper<X, C, E> selectHelper;

	/**
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
	public OwnerManyToManyMapping(PluralAttributeImpl<X, C, E> declaringAttribute, Deque<AttributeImpl<?, ?>> path,
		Collection<ColumnTemplate<X, C>> columnTemplates, boolean eager) throws MappingException {
		super(AssociationType.MANY, declaringAttribute, path, columnTemplates, eager);

		this.selectHelper = new AssociationSelectHelper<X, C, E>(this);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean contains(Object instance) {
		return ((Collection<?>) this.getValue(instance)).contains(instance);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public PluralAttributeImpl<X, C, E> getDeclaringAttribute() {
		return (PluralAttributeImpl<X, C, E>) super.getDeclaringAttribute();
	}

	/**
	 * Returns the joinTable.
	 * 
	 * @return the joinTable
	 * @since $version
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
	@SuppressWarnings("unchecked")
	public OwnedManyToManyMapping<C, X, E> getOpposite() {
		return (OwnedManyToManyMapping<C, X, E>) super.getOpposite();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Collection<PhysicalColumn> getPhysicalColumns() {
		return this.joinTable.getColumns();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public EntityTypeImpl<C> getType() {
		return (EntityTypeImpl<C>) this.getDeclaringAttribute().getElementType();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean hasJoin() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean isCollection() {
		return true;
	}

	/**
	 * Creates the join table for the association.
	 * 
	 * @param jdbcAdapter
	 * @throws MappingException
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void link(JDBCAdapter jdbcAdapter) throws MappingException {
		this.joinTable = new JoinTable(this, jdbcAdapter);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void performInsert(Connection connection, ManagedInstance<X> managedInstance) throws SQLException {
		this.joinTable.performInsert(connection, managedInstance);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Collection<E> performSelect(SessionImpl session, ManagedId<X> managedId) throws SQLException {
		return this.selectHelper.select(session, managedId);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void reset(Object instance) {
		this.getDeclaringAttribute().reset(instance);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void setCollection(Object instance, ManagedCollection<?> collection) {
		this.getDeclaringAttribute().setCollection(instance, collection);
	}
}
