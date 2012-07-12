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
package org.batoo.jpa.core.test.merge;

import java.sql.SQLException;
import java.util.Arrays;

import javax.sql.DataSource;

import org.apache.commons.dbutils.QueryRunner;
import org.batoo.jpa.common.log.BLogger;
import org.batoo.jpa.common.log.BLoggerFactory;
import org.batoo.jpa.core.impl.jdbc.SingleValueHandler;
import org.batoo.jpa.core.test.BaseCoreTest;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author hceylan
 * 
 * @since $version
 */
public class MergeTest extends BaseCoreTest {

	private static final BLogger LOG = BLoggerFactory.getLogger(MergeTest.class);

	private static final String VALUE = "value";

	/**
	 * Returns a newly created parent.
	 * 
	 * @return
	 * 
	 * @since $version
	 * @author hceylan
	 */
	protected Parent createParent() {
		final Parent parent = new Parent(MergeTest.VALUE);

		new Child1(parent, MergeTest.VALUE);
		new Child1(parent, MergeTest.VALUE);

		new Child2(parent, MergeTest.VALUE);
		new Child2(parent, MergeTest.VALUE);

		new Child3(parent, MergeTest.VALUE);
		new Child3(parent, MergeTest.VALUE);

		new Child4(parent, MergeTest.VALUE);
		new Child4(parent, MergeTest.VALUE);

		return parent;
	}

	/**
	 * Returns the counts
	 * 
	 * @return counts
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private int[] getCounts() {
		final int[] counts = new int[4];

		try {
			for (int i = 0; i < 4; i++) {
				counts[i] = new QueryRunner(this.em().unwrap(DataSource.class)).query("SELECT COUNT(*) FROM CHILD" + (i + 1), //
					new SingleValueHandler<Integer>());
			}

			MergeTest.LOG.debug("Remaining children are: {0}, {1}, {2}, {3}", counts[0], counts[1], counts[2], counts[3]);
		}
		catch (final SQLException e) {
			throw new RuntimeException(e);
		}

		return counts;
	}

	/**
	 * Tests the simple merge
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testMerge() {
		this.merge(this.createParent());

		this.commit();

		Assert.assertTrue(Arrays.equals(new int[] { 2, 2, 0, 0 }, this.getCounts()));
	}

	/**
	 * Tests the simple merge
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testPersistMerge() {
		this.persist(this.createParent());

		this.commit();

		Assert.assertTrue(Arrays.equals(new int[] { 2, 2, 0, 0 }, this.getCounts()));
	}
}
