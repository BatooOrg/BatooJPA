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
import java.util.Collection;
import java.util.List;

import org.batoo.jpa.core.BLogger;
import org.batoo.jpa.core.impl.EntityManagerImpl;
import org.batoo.jpa.core.impl.SessionImpl;
import org.batoo.jpa.core.impl.instance.ManagedInstance.Status;
import org.batoo.jpa.core.impl.mapping.Association;

import com.google.common.collect.Lists;

/**
 * Operation to make an entity managed and persistent.
 * 
 * @author hceylan
 * @since $version
 */
public class DetachOperation<X> extends AbstractOperation<X> {

	private static final BLogger LOG = BLogger.getLogger(DetachOperation.class);

	/**
	 * @param entityManager
	 *            the entity manager
	 * @param instance
	 *            the entity
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public DetachOperation(EntityManagerImpl entityManager, X instance) {
		super(entityManager, instance);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected List<AbstractOperation<?>> cascade(Association<?, ?> association) {
		if (association.cascadeDetach()) {
			final Object value = association.getValue(this.managedInstance.getInstance());

			final List<AbstractOperation<?>> cascades = Lists.newArrayList();
			if (value != null) {
				if (value instanceof Collection) {
					final Collection<?> values = (Collection<?>) value;
					for (final Object child : values) {
						cascades.add(new DetachOperation(this.em, child));
					}

				}
				else {
					cascades.add(new DetachOperation(this.em, value));
				}

				return cascades;
			}
		}

		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void perform(Connection connection) throws SQLException {
		// noop
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	protected boolean prepare(SessionImpl session) throws InterruptedException {
		this.managedInstance = this.em.getSession().get(this.instance);

		// if it is not managed make it managed
		switch (this.managedInstance.getStatus()) {
			case MANAGED:
				this.managedInstance.setStatus(Status.DETACHED);

				if (this.managedInstance.executed()) {
					LOG.debug("{0} executed at least one DML, marking transaction as rollback only");

					this.em.getTransaction().setRollbackOnly();
				}
				else {
					this.em.getTaskQueue().removeTasksFor(this.managedInstance);
				}

				return false;
			case NEW:
			case DETACHED:
				throw new InterruptedException();
			case REMOVED:
				return false;
		}

		return false;
	}
}
