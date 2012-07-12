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
	 * Returns a newly created foo.
	 * 
	 * @param baseId
	 * @return
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private Foo1 createFoo(int baseId) {
		final Foo1 foo1 = new Foo1(baseId, MergeTest.VALUE);

		new Foo2(baseId + 1, MergeTest.VALUE, foo1);

		return foo1;
	}

	/**
	 * Returns a newly created parent.
	 * 
	 * @param baseId
	 *            the base id
	 * @return
	 * 
	 * @since $version
	 * @author hceylan
	 */
	protected Parent createParent(int baseId) {
		final Parent parent = new Parent(baseId, MergeTest.VALUE);

		new Child1(baseId + 1, parent, MergeTest.VALUE);
		new Child1(baseId + 2, parent, MergeTest.VALUE);

		new Child2(baseId + 3, parent, MergeTest.VALUE);
		new Child2(baseId + 4, parent, MergeTest.VALUE);

		new Child3(baseId + 5, parent, MergeTest.VALUE);
		new Child3(baseId + 6, parent, MergeTest.VALUE);

		new Child4(baseId + 7, parent, MergeTest.VALUE);
		new Child4(baseId + 8, parent, MergeTest.VALUE);

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
	private int[] getCounts1() {
		final int[] counts = new int[2];

		try {
			for (int i = 0; i < 2; i++) {
				counts[i] = new QueryRunner(this.em().unwrap(DataSource.class)).query("SELECT COUNT(*) FROM FOO" + (i + 1), //
					new SingleValueHandler<Integer>());
			}

			MergeTest.LOG.debug("Foos are: {0}, {1}", counts[0], counts[1]);
		}
		catch (final SQLException e) {
			throw new RuntimeException(e);
		}

		return counts;
	}

	/**
	 * Returns the counts
	 * 
	 * @return counts
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private int[] getCounts2() {
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
		this.merge(this.createParent(Integer.MIN_VALUE));

		this.commit();

		Assert.assertTrue(Arrays.equals(new int[] { 2, 2, 2, 2 }, this.getCounts2()));
	}

	/**
	 * Tests the simple merge
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testMergeOneToOne() {
		this.merge(this.createFoo(Integer.MIN_VALUE));

		this.commit();

		Assert.assertTrue(Arrays.equals(new int[] { 1, 1 }, this.getCounts1()));
	}

	/**
	 * Tests the simple merge
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testMergeOneToOne1() {
		final Foo1 foo = this.createFoo(1);
		this.persist(foo);

		this.commit();

		this.merge(foo);

		this.commit();

		Assert.assertTrue(Arrays.equals(new int[] { 1, 1 }, this.getCounts1()));
	}

	/**
	 * Tests the simple merge
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testMergeOneToOne2() {
		Foo1 foo = this.createFoo(1);
		this.persist(foo);

		this.commit();
		this.close();

		foo = this.createFoo(1);
		foo.setFoo2(null);
		this.merge(foo);

		this.commit();

		Assert.assertTrue(Arrays.equals(new int[] { 1, 0 }, this.getCounts1()));
	}

	/**
	 * Tests the simple merge
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testMergeOneToOne3() {
		final Foo1 foo = this.createFoo(1);
		this.persist(foo);

		this.commit();
		this.close();

		Foo2 foo2 = new Foo2(2, MergeTest.VALUE + "2", null);
		this.merge(foo2);

		this.commit();
		this.close();

		foo2 = this.find(Foo2.class, 2);

		Assert.assertNull(foo2.getFoo1());
		Assert.assertTrue(Arrays.equals(new int[] { 1, 1 }, this.getCounts1()));
	}

	/**
	 * Tests the simple merge
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testMergeOneToOne4() {
		Foo1 foo = this.createFoo(1);
		this.persist(foo);

		this.commit();
		this.close();

		foo = new Foo1(1, MergeTest.VALUE + "2");
		this.merge(foo);

		this.commit();
		this.close();

		foo = this.find(Foo1.class, 1);

		Assert.assertNull(foo.getFoo2());
		Assert.assertTrue(Arrays.equals(new int[] { 1, 0 }, this.getCounts1()));
	}

	/**
	 * Tests the simple merge
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testPersistDifferentMerge1() {
		this.persist(this.createParent(1));

		this.commit();

		Parent parent = this.createParent(1);
		parent.getChildren1().clear();
		parent = this.merge(parent);

		this.commit();

		Assert.assertTrue(Arrays.equals(new int[] { 0, 2, 2, 2 }, this.getCounts2()));
	}

	/**
	 * Tests the simple merge
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testPersistDifferentMerge2() {
		this.persist(this.createParent(1));

		this.commit();

		Parent parent = this.createParent(1);
		Child2 child = parent.getChildren2().get(0);
		parent.getChildren2().clear();
		parent = this.merge(parent);

		this.commit();

		this.close();
		child = this.find(Child2.class, child.getId());
		Assert.assertNull(child.getParent());

		Assert.assertTrue(Arrays.equals(new int[] { 2, 2, 2, 2 }, this.getCounts2()));
	}

	/**
	 * Tests the simple merge
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testPersistDifferentMerge3() {
		this.persist(this.createParent(1));

		this.commit();

		Parent parent = this.createParent(1);
		Child3 child = parent.getChildren3().get(0);
		parent.getChildren3().clear();
		parent = this.merge(parent);

		this.commit();

		this.close();
		child = this.find(Child3.class, child.getId());

		Assert.assertNotNull(child);
		Assert.assertTrue(Arrays.equals(new int[] { 2, 2, 2, 2 }, this.getCounts2()));
	}

	/**
	 * Tests the simple merge
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testPersistDifferentMerge4() {
		this.persist(this.createParent(1));

		this.commit();

		Parent parent = this.createParent(1);
		parent.getChildren4().clear();

		parent = this.merge(parent);

		this.commit();

		this.close();
		Assert.assertTrue(Arrays.equals(new int[] { 2, 2, 2, 0 }, this.getCounts2()));
	}

	/**
	 * Tests the simple merge
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testPersistMerge() {
		this.persist(this.createParent(1));

		this.commit();

		Parent parent = this.createParent(1);
		parent = this.merge(parent);

		Assert.assertTrue(Arrays.equals(new int[] { 2, 2, 2, 2 }, this.getCounts2()));
	}

	/**
	 * Tests the simple merge
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testPersistOneToOne() {
		this.persist(this.createFoo(Integer.MIN_VALUE));
		this.merge(this.createFoo(Integer.MIN_VALUE));
		this.commit();

		Assert.assertTrue(Arrays.equals(new int[] { 2, 2 }, this.getCounts1()));
	}
}
