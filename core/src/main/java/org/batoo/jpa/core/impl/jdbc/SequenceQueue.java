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

import org.batoo.jpa.core.jdbc.adapter.JDBCAdapter;

/**
 * A Queue that tops up the queue by {@link #allocationSize} sequences when its capacity drops to 1 / 2 of {@link #allocationSize}.
 * 
 * @author hceylan
 * @since $version
 */
public class SequenceQueue extends IdQueue {

	private static final long serialVersionUID = 1L;

	/**
	 * @param jdbcAdapter
	 *            the jdbc adapter
	 * @param datasource
	 *            the datasource to use
	 * @param idExecuter
	 *            the executor service to submit refill tasks
	 * @param sequenceName
	 *            the physical name of the sequence
	 * @param allocationSize
	 *            the allocations size
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public SequenceQueue(JDBCAdapter jdbcAdapter, DataSourceImpl datasource, ExecutorService idExecuter, String sequenceName,
		int allocationSize) {
		super(jdbcAdapter, datasource, idExecuter, sequenceName, allocationSize);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	protected Integer getNextId() throws SQLException {
		return this.jdbcAdapter.getNextSequence(this.datasource, this.name);
	}
}
