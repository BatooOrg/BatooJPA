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
package org.batoo.jpa.core.impl.operation;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.batoo.jpa.core.impl.EntityManagerImpl;
import org.batoo.jpa.core.impl.SessionImpl;
import org.batoo.jpa.core.impl.instance.ManagedInstance;
import org.batoo.jpa.core.impl.mapping.Association;
import org.batoo.jpa.core.impl.mapping.PersistableAssociation;

/**
 * @author hceylan
 * @since $version
 */
public class PersistAssociationOperation<X> extends AbstractOperation<X> {

	private final PersistableAssociation association;

	/**
	 * @param entityManager
	 *            the entity manager
	 * @param managedInstance
	 *            the instance
	 * @param association
	 *            the persistable association
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public PersistAssociationOperation(EntityManagerImpl entityManager, ManagedInstance<X> managedInstance,
		PersistableAssociation association) {
		super(entityManager, managedInstance);

		this.association = association;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	protected List<AbstractOperation<?>> cascade(Association<?, ?> association) {
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <Y> void perform(Connection connection) throws SQLException {
		this.association.performInsert(connection, this.managedInstance);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	protected boolean prepare(SessionImpl session) throws InterruptedException {
		return true; // always perform
	}

}
