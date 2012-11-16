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
package org.batoo.jpa.core.test.remove;

import java.sql.SQLException;
import java.util.Arrays;

import javax.sql.DataSource;

import org.batoo.common.log.BLogger;
import org.batoo.common.log.BLoggerFactory;
import org.batoo.jpa.core.impl.jdbc.dbutils.QueryRunner;
import org.batoo.jpa.core.impl.jdbc.dbutils.SingleValueHandler;
import org.batoo.jpa.core.test.BaseCoreTest;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author hceylan
 * 
 * @since 2.0.0
 */
public class RemoveTest extends BaseCoreTest {

	private static final BLogger LOG = BLoggerFactory.getLogger(RemoveTest.class);

	private static final String VALUE = "value";

	/**
	 * Returns a newly created parent.
	 * 
	 * @return
	 * 
	 * @since 2.0.0
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
	 * @since 2.0.0
	 */
	private int[] getCounts() {
		final int[] counts = new int[4];

		try {
			for (int i = 0; i < 4; i++) {
				counts[i] = new QueryRunner(this.em().unwrap(DataSource.class)).query("SELECT COUNT(*) FROM Child" + (i + 1), //
					new SingleValueHandler<Number>()).intValue();
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
	 * @since 2.0.0
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
	 * @since 2.0.0
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
	 * @since 2.0.0
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
	 * @since 2.0.0
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
	 * @since 2.0.0
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
	 * @since 2.0.0
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
