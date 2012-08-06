/*
 * Copyright (c) 2012 - Batoo Software ve Consultancy Ltd.
 * 
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
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
public class BaseTest {

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
