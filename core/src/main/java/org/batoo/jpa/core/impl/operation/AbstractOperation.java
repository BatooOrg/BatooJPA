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

import org.batoo.jpa.core.BatooException;
import org.batoo.jpa.core.impl.EntityManagerImpl;
import org.batoo.jpa.core.impl.SessionImpl;
import org.batoo.jpa.core.impl.instance.ManagedInstance;
import org.batoo.jpa.core.impl.mapping.Association;
import org.batoo.jpa.core.impl.types.EntityTypeImpl;

import com.google.common.collect.Lists;

/**
 * Abstract base class for operations.
 * 
 * @author hceylan
 * @since $version
 */
public abstract class AbstractOperation<X> {

	// private enum Status {
	// PENDING,
	// COMPLETED,
	// ERRORED
	// }

	// private static final BLogger LOG = BLogger.getLogger(AbstractOperation.class);

	private static volatile long nextOperationNo = 0;

	protected final EntityManagerImpl em;
	protected EntityTypeImpl<X> type;
	protected X instance;
	private boolean requiresFlush;
	protected ManagedInstance<? super X> managedInstance;

	private long operationNo;

	// private final List<AbstractOperation<?>> dependencies = Lists.newArrayList();
	// private Status status;

	private int h;

	/**
	 * @param entityManager
	 *            the entity manager
	 * @param managedInstance
	 *            the managed instance
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public AbstractOperation(EntityManagerImpl entityManager, ManagedInstance<X> managedInstance) {
		this(entityManager, managedInstance.getInstance());

		this.managedInstance = managedInstance;
	}

	/**
	 * @param entityManager
	 *            the entity manager
	 * @param instance
	 *            the entity
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@SuppressWarnings("unchecked")
	public AbstractOperation(EntityManagerImpl entityManager, X instance) {
		super();

		this.em = entityManager;
		this.instance = instance;
		// this.status = Status.PENDING;
		this.type = this.em.getMetamodel().entity((Class<X>) instance.getClass());

		this.operationNo = nextOperationNo++;
	}

	/**
	 * Returns a cascaded operation if the operation should be cascaded for the association.
	 * 
	 * @param association
	 *            the association
	 * @return cascade operation or null
	 * 
	 * @since $version
	 * @author hceylan
	 */
	protected abstract List<AbstractOperation<?>> cascade(Association<?, ?> association);

	/**
	 * Returns the managedInstance.
	 * 
	 * @return the managedInstance
	 * @since $version
	 */
	public ManagedInstance<? super X> getManagedInstance() {
		return this.managedInstance;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public int hashCode() {
		if (this.h != 0) {
			return this.h;
		}

		final int prime = 31;
		final int result = 1;

		return this.h = (prime * result) + (int) (this.operationNo ^ (this.operationNo >>> 32));
	}

	/**
	 * Subclasses cannot not override!
	 * 
	 * @param entityManager
	 *            the entity manager
	 * @return the array of cascaded operations
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public final List<AbstractOperation<?>> internalPrepare(EntityManagerImpl entityManager) {
		final List<AbstractOperation<?>> cascades = Lists.newArrayList();

		try {
			if (this.prepare(entityManager.getSession())) {
				this.em.getTaskQueue().add(this);

				if (this.requiresFlush) {
					entityManager.flush();
				}
			}

			if (this.managedInstance != null) {
				for (final Association<?, ?> association : this.managedInstance.getType().getAssociations()) {
					final List<AbstractOperation<?>> cascaded = this.cascade(association);
					if (cascaded != null) {
						cascades.addAll(cascaded);
					}
				}
			}
		}
		catch (final InterruptedException e) {
			// no cascading
		}

		return cascades;
	}

	/**
	 * Performs the database operation.
	 * 
	 * @param connection
	 *            the connection to use
	 * @throws SQLException
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public abstract <Y> void perform(Connection connection) throws SQLException;

	/**
	 * Subclasses implement to provide their own operations.
	 * <p>
	 * Base implementation handles cascaded operations, so subclasses must not deal with associations and embedded attributes.
	 * 
	 * @param session
	 *            the session
	 * @return true if operation needs execution
	 * @throws InterruptedException
	 *             throw if no cascading should be tried
	 * 
	 * @since $version
	 * @author hceylan
	 */
	protected abstract boolean prepare(SessionImpl session) throws InterruptedException;

	/**
	 * Returns true if the operation requires an immediate flush right after prepare.
	 * 
	 * @return true if the operation requires an immediate flush right after prepare
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public final boolean requiresFlush() {
		return this.requiresFlush;
	}

	/**
	 * Runs the operation.
	 * 
	 * @param connection
	 *            the connection to use
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public final void run(Connection connection) {
		// wait till dependencies satisfied
		try {
			// final AbstractOperation<?> failed = this.waitForDependencies();
			// if (failed != null) {
			// LOG.debug("{0}: dependency {1} failed, bailing out.", this, failed);
			//
			// this.status = Status.ERRORED;
			//
			// return;
			// }

			try {
				this.perform(connection);
			}
			catch (final SQLException e) {
				throw new BatooException("Operation failed", e);
			}
		}
		catch (final BatooException e) {
			// this.status = Status.ERRORED;

			throw new RuntimeException(e);
		}
	}

	/**
	 * Sets the requiresFlush.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public final void setRequiresFlush() {
		this.requiresFlush = true;
	}

	// private void waitForDependecy(AbstractOperation<?> dependecy) throws InterruptedException {
	// final long start = System.currentTimeMillis();
	//
	// LOG.debug("{0}: waitForDependecy(AbstractOperation), {1}...", this, dependecy);
	//
	// try {
	// while (dependecy.status == Status.PENDING) {
	// Thread.sleep(1);
	// }
	// }
	// finally {
	// final long time = System.currentTimeMillis() - start;
	//
	// if (time > BJPASettings.WARN_TIME) {
	// LOG.warn(new OperationTookLongTimeWarning(), "{0}: {1} msecs, waitForDependecy(AbstractOperation), {2}...", this, time,
	// dependecy);
	// }
	// else {
	// LOG.debug("{0}: {1} msecs, waitForDependecy(AbstractOperation), {2}...", this, time, dependecy);
	// }
	// }
	// }
	//
	// private AbstractOperation<?> waitForDependencies() throws BatooException {
	// for (final AbstractOperation<?> dependecy : this.dependencies) {
	// if (dependecy.status == Status.PENDING) {
	// try {
	// this.waitForDependecy(dependecy);
	// }
	// catch (final InterruptedException e) {
	// throw new BatooException(e);
	// }
	// }
	//
	// if (dependecy.status == Status.ERRORED) {
	// LOG.debug("{0}: Dependency {1} has status error, skipping execution", this, dependecy);
	//
	// return dependecy;
	// }
	// }
	//
	// return null;
	// }

}
