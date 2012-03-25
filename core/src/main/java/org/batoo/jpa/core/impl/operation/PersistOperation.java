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

import javax.persistence.EntityExistsException;

import org.batoo.jpa.core.impl.EntityManagerImpl;
import org.batoo.jpa.core.impl.SessionImpl;
import org.batoo.jpa.core.impl.instance.ManagedInstance.Status;
import org.batoo.jpa.core.impl.mapping.Association;
import org.batoo.jpa.core.impl.mapping.PersistableAssociation;

import com.google.common.collect.Lists;

/**
 * Operation to make an entity managed and persistent.
 * 
 * @author hceylan
 * @since $version
 */
public class PersistOperation<X> extends AbstractOperation<X> {

	/**
	 * @param entityManager
	 *            the entity manager
	 * @param instance
	 *            the entity
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public PersistOperation(EntityManagerImpl entityManager, X instance) {
		super(entityManager, instance);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected List<AbstractOperation<?>> cascade(Association<?, ?> association) {
		final List<AbstractOperation<?>> cascades = Lists.newArrayList();

		if (association.cascadePersist()) {
			final Object value = association.getValue(this.managedInstance.getInstance());

			if (value != null) {
				if (value instanceof Collection) {
					final Collection<?> values = (Collection<?>) value;
					for (final Object child : values) {
						cascades.add(new PersistOperation(this.em, child));
					}

				}
				else {
					cascades.add(new PersistOperation(this.em, value));
				}
			}
		}

		if (association instanceof PersistableAssociation) {
			final PersistableAssociation persistableAssociation = (PersistableAssociation) association;
			if (persistableAssociation.hasJoin()) {
				cascades.add(new PersistAssociationOperation(this.em, this.managedInstance, persistableAssociation));
			}
		}

		return cascades;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void perform(Connection connection) throws SQLException {
		this.managedInstance.performInsert(connection);

		// Entities with identity id types are put to session after their id is fetched from the database post to an insert statement.
		if (this.requiresFlush()) {
			this.em.getSession().put(this.managedInstance);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	protected boolean prepare(SessionImpl session) {
		this.managedInstance = this.em.getSession().get(this.instance);

		if (this.managedInstance == null) {
			this.managedInstance = this.type.getManagedInstance(session, this.instance);
			this.managedInstance.setStatus(Status.NEW);
		}

		switch (this.managedInstance.getStatus()) {
			case MANAGED: // entities already managed is ignored by cascades run as usual
				return false;

			case REMOVED: // removed entities become managed again
				this.managedInstance.setStatus(Status.MANAGED);
				return false;

			case NEW: // new entities become managed
				this.managedInstance.fillIdValues();

				// if the entity has identity id type, then a flush is required to put the entity into the session.
				if (this.managedInstance.getType().hasIdentityAttribute()) {
					this.setRequiresFlush(); // Force flush
				}
				else {
					this.managedInstance.setStatus(Status.MANAGED);
					this.em.getSession().put(this.managedInstance); // put into the session immediately
				}

				return true;

			default: // DETACHED, for detached entities exception thrown
				throw new EntityExistsException("Entity has been previously detached");
		}
	}

}
