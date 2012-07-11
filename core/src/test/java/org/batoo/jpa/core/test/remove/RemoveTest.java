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
package org.batoo.jpa.core.test.remove;

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
public class RemoveTest extends BaseCoreTest {

	private static final BLogger LOG = BLoggerFactory.getLogger(RemoveTest.class);

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
		final Parent parent = new Parent(RemoveTest.VALUE);

		new Child1(parent, RemoveTest.VALUE);
		new Child1(parent, RemoveTest.VALUE);

		new Child2(parent, RemoveTest.VALUE);
		new Child2(parent, RemoveTest.VALUE);

		new Child3(parent, RemoveTest.VALUE);
		new Child3(parent, RemoveTest.VALUE);

		new Child4(parent, RemoveTest.VALUE);
		new Child4(parent, RemoveTest.VALUE);

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

			RemoveTest.LOG.debug("Remaining children are: {0}, {1}, {2}, {3}", counts[0], counts[1], counts[2], counts[3]);
		}
		catch (final SQLException e) {
			throw new RuntimeException(e);
		}

		return counts;
	}

	/**
	 * Tests the orphans removed correctly.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testFlushRemoval() {
		final Parent parent = this.createParent();
		this.persist(parent);
		this.flush();

		this.remove(parent);

		this.commit();

		Assert.assertTrue(Arrays.equals(new int[] { 0, 0, 2, 0 }, this.getCounts()));
	}

	/**
	 * Tests the orphans removed correctly.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testOrphanPersistCommitThenRemove() {
		this.persist(this.createParent());

		final Parent parent = this.createParent();
		this.persist(parent);
		this.commit();

		this.remove(parent);

		this.commit();

		Assert.assertTrue(Arrays.equals(new int[] { 2, 2, 4, 2 }, this.getCounts()));
	}

	/**
	 * Tests the orphans removed correctly.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testOrphanPersistThenRemove() {
		this.persist(this.createParent());

		final Parent parent = this.createParent();
		this.persist(parent);
		this.remove(parent);

		this.commit();

		Assert.assertTrue(Arrays.equals(new int[] { 2, 2, 4, 2 }, this.getCounts()));
	}

	/**
	 * Tests the orphans removed correctly.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testOrphanRemoval() {
		final Parent parent = this.createParent();
		this.persist(parent);

		parent.getChildren1().clear();
		parent.getChildren2().clear();
		parent.getChildren3().clear();
		parent.getChildren4().clear();

		this.commit();

		Assert.assertTrue(Arrays.equals(new int[] { 0, 2, 2, 0 }, this.getCounts()));
	}

	/**
	 * Tests the orphans removed correctly.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testOrphanRemovalPersist() {
		final Parent parent = this.createParent();
		this.persist(parent);

		this.commit();

		this.begin();

		parent.getChildren1().clear();
		parent.getChildren2().clear();
		parent.getChildren3().clear();
		parent.getChildren4().clear();

		this.commit();

		Assert.assertTrue(Arrays.equals(new int[] { 0, 2, 2, 0 }, this.getCounts()));
	}

	/**
	 * Tests the orphans removed correctly.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testRemoval() {
		final Parent parent = this.createParent();
		this.persist(parent);

		this.remove(parent);

		this.commit();

		Assert.assertTrue(Arrays.equals(new int[] { 0, 0, 2, 0 }, this.getCounts()));
	}
}
