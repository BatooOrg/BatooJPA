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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;

import javax.persistence.EntityManager;

import org.batoo.jpa.core.BatooException;
import org.batoo.jpa.core.impl.EntityManagerImpl;
import org.batoo.jpa.core.impl.instance.ManagedInstance;

import com.google.common.collect.Lists;

/**
 * The task queue for a single {@link EntityManager}.
 * <p>
 * Main functionality is to prioritize the tasks in a fashion that the associations are satisfied.
 * 
 * @author hceylan
 * @since $version
 */
public class TaskQueue {

	// private static final BLogger LOG = BLogger.getLogger(TaskQueue.class);

	private static AtomicInteger nextNo = new AtomicInteger();

	// private final Integer no = nextNo.incrementAndGet();
	private final LinkedList<AbstractOperation<?>> queue = Lists.newLinkedList();

	// private final ExecutorService executor;

	/**
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public TaskQueue(EntityManagerImpl entityManager) {
		super();

		// this.executor = entityManager.getEntityManagerFactory().getExecutor();
	}

	/**
	 * Adds an operation to the task queue.
	 * 
	 * @param operation
	 *            the operation to add
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public synchronized void add(AbstractOperation<?> operation) {
		this.queue.addLast(operation);
	}

	/**
	 * Flushes the current tasks in the queue.
	 * 
	 * @param connection
	 *            the connection to use
	 * @throws BatooException
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void flush(final Connection connection) throws BatooException {
		// get the operations
		// final ArrayList<AbstractOperation<?>> operations = new ArrayList<AbstractOperation<?>>(this.queue);

		// sort the operations based on their dependencies
		// Collections.sort(operations);

		// final List<Future<?>> futures = Lists.newArrayList();
		for (final AbstractOperation<?> operation : this.queue) {
			// futures.add(this.executor.submit(new Runnable() {
			//
			// @Override
			// public void run() {
			operation.run(connection);
			// }
			// }));
		}
		//
		// for (final Future<?> future : futures) {
		// try {
		// future.get();
		// }
		// catch (final Throwable t) {
		// this.handleException(t);
		// }
		// }

		this.queue.clear();
	}

	/**
	 * Handles the exception.
	 * 
	 * @param t
	 *            the exception
	 * @throws BatooException
	 * 
	 * @since $version
	 * @author hceylan
	 */
	protected void handleException(Throwable t) throws BatooException {
		if (t instanceof BatooException) {
			throw (BatooException) t;
		}

		if (t.getCause() == null) {
			throw new BatooException(t.getMessage(), t);
		}

		this.handleException(t.getCause());
	}

	/**
	 * Removes the tasks for managed instance.
	 * 
	 * @param instance
	 *            the instance for which tasks will be removed.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public synchronized void removeTasksFor(ManagedInstance<?> instance) {
		for (final Iterator<AbstractOperation<?>> i = this.queue.iterator(); i.hasNext();) {
			if (i.next().getManagedInstance() == instance) {
				i.remove();
			}
		}
	}
}
