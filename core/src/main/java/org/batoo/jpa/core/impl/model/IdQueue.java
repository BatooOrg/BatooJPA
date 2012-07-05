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
package org.batoo.jpa.core.impl.model;

import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;

import org.batoo.jpa.common.log.BLogger;
import org.batoo.jpa.common.log.BLoggerFactory;

/**
 * 
 * @author hceylan
 * @since $version
 */
public abstract class IdQueue extends LinkedBlockingQueue<Integer> {

	/**
	 * 
	 * @author hceylan
	 * @since $version
	 */
	private final class TopUpTask implements Runnable {

		@Override
		public void run() {
			IdQueue.this.doTopUp(this);
		}
	}

	private static final long serialVersionUID = 1L;

	private static final BLogger LOG = BLoggerFactory.getLogger(IdQueue.class);

	private final String name;
	private final int allocationSize;
	private final int topupSize;

	/**
	 * @param idExecuter
	 *            the executor service to submit refill tasks
	 * @param name
	 *            the physical name of the sequence or pkvalue of the table
	 * @param allocationSize
	 *            the allocations size
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public IdQueue(ExecutorService idExecuter, String name, int allocationSize) {
		super();

		this.name = name;
		this.allocationSize = allocationSize;
		this.topupSize = allocationSize / 2;

		idExecuter.execute(new TopUpTask());
	}

	/**
	 * Tops up the queue.
	 * 
	 * @param runnable
	 *            the runnable to run to top-up
	 * 
	 * @since $version
	 * @author hceylan
	 */
	protected void doTopUp(Runnable runnable) {
		while (true) {
			if (Thread.currentThread().isInterrupted()) {
				return;
			}

			if (this.size() <= (this.topupSize)) {
				IdQueue.LOG.debug("Ids will be fetched for {0} from the database...", this.name);

				try {
					final int nextSequence = this.getNextId();
					for (int i = 0; i < this.allocationSize; i++) {
						this.put(nextSequence + i);
					}
				}
				catch (final InterruptedException e) {
					return;
				}
				catch (final Throwable e) {
					if (Thread.currentThread().isInterrupted()) {
						return;
					}

					IdQueue.LOG.fatal(e, "Cannot get next id from the database!");
				}
			}

			try {
				Thread.sleep(1);
			}
			catch (final InterruptedException e) {
				return;
			}
		}
	}

	/**
	 * Returns the next value from the database.
	 * 
	 * @return the next id
	 * @throws SQLException
	 * 
	 * @since $version
	 * @author hceylan
	 */
	protected abstract Integer getNextId() throws SQLException;

}
