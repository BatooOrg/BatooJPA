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
package org.batoo.jpa.common.test;

import org.batoo.jpa.common.log.BLogger;
import org.batoo.jpa.common.log.BLoggerFactory;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.mockito.MockitoAnnotations;

/**
 * Common base class for Batoo JPA Tests.
 * 
 * @author hceylan
 * @since $version
 */
public class BaseTest extends Assertable {

	private static final BLogger LOG = BLoggerFactory.getLogger(BaseTest.class);

	/**
	 * Rule to log failures to log.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Rule
	public final TestWatcher baseWatchman = new TestWatcher() {
		/**
		 * {@inheritDoc}
		 * 
		 */
		@Override
		protected void failed(Throwable e, Description description) {
			BaseTest.LOG.error(e, "Test failed {0}", description);
		}

		/**
		 * {@inheritDoc}
		 * 
		 */
		@Override
		protected void starting(Description description) {
			BaseTest.LOG.info("Starting test {0}", description);
		}

		/**
		 * {@inheritDoc}
		 * 
		 */
		@Override
		protected void succeeded(Description description) {
			BaseTest.LOG.info("Test succeded {0}", description);
		}
	};

	/**
	 * Initializes the mocks.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Before
	public void initMocks() {
		MockitoAnnotations.initMocks(this);
	}

}
