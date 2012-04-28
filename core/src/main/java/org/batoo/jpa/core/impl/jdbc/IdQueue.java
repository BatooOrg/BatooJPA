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
package org.batoo.jpa.core.impl.jdbc;

import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;

import org.batoo.jpa.core.BLogger;
import org.batoo.jpa.core.jdbc.adapter.JDBCAdapter;

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

	private static final BLogger LOG = BLogger.getLogger(IdQueue.class);

	protected final JDBCAdapter jdbcAdapter;
	protected final DataSourceImpl datasource;
	private final ExecutorService idExecuter;
	protected final String name;
	private final int allocationSize;

	private final int topupSize;

	private TopUpTask topUpTask;

	/**
	 * /**
	 * 
	 * @param jdbcAdapter
	 *            the jdbc adapter
	 * @param datasource
	 *            the datasource to use
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
	public IdQueue(JDBCAdapter jdbcAdapter, DataSourceImpl datasource, ExecutorService idExecuter, String name, int allocationSize) {
		super();

		this.jdbcAdapter = jdbcAdapter;
		this.datasource = datasource;
		this.idExecuter = idExecuter;
		this.name = name;
		this.allocationSize = allocationSize;
		this.topupSize = allocationSize / 2;

		idExecuter.execute(this.topUpTask = new TopUpTask());
	}

	protected void doTopUp(Runnable runnable) {
		while (this.size() <= (this.topupSize)) {
			LOG.debug("Ids will be fetched for {0} from the database...", this.name);

			try {
				final int nextSequence = this.getNextId();
				for (int i = 0; i < this.allocationSize; i++) {
					this.put(nextSequence + i);
				}
			}
			catch (final Throwable e) {
				LOG.fatal(e, "Cannot get next id from the database!");
			}
		}

		try {
			Thread.sleep(1);
		}
		catch (final InterruptedException e) {}
		if (!this.idExecuter.isShutdown()) {
			this.idExecuter.execute(this.topUpTask);
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
