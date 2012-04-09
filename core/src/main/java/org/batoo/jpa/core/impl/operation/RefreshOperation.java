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

import javax.persistence.PersistenceException;

import org.batoo.jpa.core.impl.EntityManagerImpl;
import org.batoo.jpa.core.impl.SessionImpl;
import org.batoo.jpa.core.impl.instance.ManagedInstance;
import org.batoo.jpa.core.impl.instance.ManagedInstance.Status;
import org.batoo.jpa.core.impl.mapping.Association;

/**
 * 
 * @author hceylan
 * @since $version
 */
public class RefreshOperation<X> extends AbstractOperation<X> {

	/**
	 * @param entityManager
	 *            the entity manager
	 * @param instance
	 *            the entity
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public RefreshOperation(EntityManagerImpl entityManager, X instance) {
		super(entityManager, instance);
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
		// noop
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	protected boolean prepare(SessionImpl session) throws InterruptedException {
		this.managedInstance = this.em.getSession().get(this.instance);

		if ((this.managedInstance == null) || (this.managedInstance.getStatus() != Status.MANAGED)) {
			throw new IllegalArgumentException("Entity is not a managed instance");
		}

		try {
			this.type.performRefresh(session, (ManagedInstance<X>) this.managedInstance);
		}
		catch (final SQLException e) {
			throw new PersistenceException("Entity cannot be refreshed", e);
		}

		return false; // NOOP
	}

}
