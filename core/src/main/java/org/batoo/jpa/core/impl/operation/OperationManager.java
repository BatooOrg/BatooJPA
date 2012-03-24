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

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.batoo.jpa.core.BLogger;
import org.batoo.jpa.core.impl.EntityManagerImpl;

import com.google.common.collect.Lists;

/**
 * Manager for the persistence operations.
 * 
 * @author hceylan
 * @since $version
 */
public class OperationManager {

	private static final BLogger LOG = BLogger.getLogger(OperationManager.class);

	private static final AtomicInteger counter = new AtomicInteger();

	private static void prepare0(EntityManagerImpl entityManager, List<AbstractOperation<?>> operations) {
		for (final AbstractOperation<?> operation : operations) {
			OperationManager.prepare0(entityManager, operation.internalPrepare(entityManager));
		}
	}

	/**
	 * Replays the list of operations.
	 * 
	 * @param entityManager
	 *            the entity manager
	 * @param operations
	 *            the operations to replay
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public static void replay(EntityManagerImpl entityManager, AbstractOperation<?>... operations) {
		if ((operations == null) || (operations.length == 0)) {
			return;
		}

		if (LOG.isTraceEnabled()) {

			final int callNo = counter.incrementAndGet();

			LOG.trace("{0}: replay() {1} atomic operations", callNo, operations.length);

			final long start = System.currentTimeMillis();
			try {
				OperationManager.prepare0(entityManager, Lists.newArrayList(operations));
			}
			finally {
				LOG.trace("{0}: {1} msecs, replay() {2} atomic operations", callNo, System.currentTimeMillis() - start, operations.length);
			}
		}
		else {
			OperationManager.prepare0(entityManager, Lists.newArrayList(operations));
		}
	}

	private OperationManager() {
		// no instantiations.
	}
}
