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

import org.apache.commons.lang.NotImplementedException;
import org.batoo.jpa.core.BLogger;
import org.batoo.jpa.core.jdbc.adapter.JDBCAdapter;

/**
 * A Queue that tops up the queue by {@link #allocationSize} sequences when its capacity drops to 1 / 2 of {@link #allocationSize}.
 * 
 * @author hceylan
 * @since $version
 */
public class TableIdQueue extends IdQueue {

	private static final long serialVersionUID = 1L;

	private static final BLogger LOG = BLogger.getLogger(TableIdQueue.class);

	/**
	 * @param jdbcAdapter
	 *            the jdbc adapter
	 * @param datasource
	 *            the datasource to use
	 * @param idExecuter
	 *            the executor service to submit refill tasks
	 * @param keyName
	 *            the key for the table id
	 * @param allocationSize
	 *            the allocations size
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public TableIdQueue(JDBCAdapter jdbcAdapter, DataSourceImpl datasource, ExecutorService idExecuter, String keyName, int allocationSize) {
		super(jdbcAdapter, datasource, idExecuter, keyName, allocationSize);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	protected Integer getNextId() throws SQLException {
		throw new NotImplementedException();
	}
}
