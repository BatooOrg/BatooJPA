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
package org.batoo.jpa.core.test.simple;

import java.sql.SQLException;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.metamodel.EntityType;
import javax.sql.DataSource;

import junit.framework.Assert;

import org.batoo.jpa.core.test.BaseCoreTest;
import org.batoo.jpa.core.test.NullResultSetHandler;
import org.batoo.jpa.jdbc.dbutils.QueryRunner;
import org.batoo.jpa.jdbc.dbutils.SingleValueHandler;
import org.junit.Test;

/**
 * @author hceylan
 * 
 * @since 2.0.0
 */
public class SimpleTest extends BaseCoreTest {

	private Foo newFoo() {
		final Foo foo = new Foo();

		foo.setValue("test");

		return foo;
	}

	/**
	 * Tests {@link EntityManager#contains(Object)}.
	 * 
	 * @since 2.0.0
	 */
	@Test
	public void testContains() {
		final Foo foo = this.newFoo();
		final Foo foo2 = this.newFoo();

		this.persist(foo);

		Assert.assertTrue(this.contains(foo));
		Assert.assertFalse(this.contains(foo2));
	}

	/**
	 * Tests {@link EntityManagerFactory#createEntityManager()}
	 * 
	 * @throws SQLException
	 *             thrown if fails
	 * 
	 * @since 2.0.0
	 */
	@Test
	public void testCreateTable() throws SQLException {
		final Set<EntityType<?>> entities = this.em().getMetamodel().getEntities();

		Assert.assertEquals(1, entities.size());

		final DataSource dataSource = this.em().unwrap(DataSource.class);
		new QueryRunner(dataSource).query("SELECT * FROM Foo", new NullResultSetHandler());
	}

	/**
	 * Tests {@link EntityManager#detach(Object)}.
	 * 
	 * @since 2.0.0
	 */
	@Test
	public void testDetach() {
		final Foo foo = this.newFoo();
		this.persist(foo);

		Assert.assertTrue(this.em().contains(foo));
		this.detach(foo);
		this.close();

		Assert.assertFalse(this.em().contains(foo));
	}

	/**
	 * Test for find entity then detach it
	 * 
	 * @since $version
	 */
	@Test
	public void testDetachAfterFind() {
		final Foo foo = this.newFoo();
		this.persist(foo);

		this.commit();

		this.close();

		this.begin();

		final Foo foo2 = this.find(Foo.class, foo.getId());
		Assert.assertEquals(foo.getId(), foo2.getId());

		this.detach(foo2);
		this.close();

		Assert.assertFalse(this.em().contains(foo2));
	}

	/**
	 * Tests {@link EntityManager#detach(Object)} then {@link EntityTransaction#commit()}.
	 * 
	 * @throws SQLException
	 *             thrown if fails
	 * 
	 * @since 2.0.0
	 */
	@Test
	public void testDetachThenCommit() throws SQLException {
		final Foo foo = this.newFoo();
		this.persist(foo);

		this.detach(foo);

		this.commit();

		Assert.assertEquals(0,
			new QueryRunner(this.em().unwrap(DataSource.class)).query("SELECT COUNT(*) FROM Foo", new SingleValueHandler<Number>()).intValue());
	}

	/**
	 * Tests to {@link EntityManager#find(Class, Object)}
	 * 
	 * @since 2.0.0
	 */
	@Test
	public void testFind() {
		final Foo foo = this.newFoo();
		this.persist(foo);

		this.commit();

		this.close();

		final Foo foo2 = this.find(Foo.class, foo.getId());
		Assert.assertEquals(foo.getId(), foo2.getId());
	}

	/**
	 * Tests {@link EntityManager#flush()} then {@link EntityManager#detach(Object)}
	 * 
	 * @throws SQLException
	 *             thrown if test fails.
	 * 
	 * @since 2.0.0
	 */
	@Test
	public void testFlushThenDetach() throws SQLException {
		final Foo foo = this.newFoo();
		this.persist(foo);

		this.flush();

		this.detach(foo);

		this.commit();

		Assert.assertEquals(1,
			new QueryRunner(this.em().unwrap(DataSource.class)).query("SELECT COUNT(*) FROM Foo", new SingleValueHandler<Number>()).intValue());
	}

	/**
	 * Tests to {@link EntityManager#persist(Object)}.
	 * 
	 * @throws SQLException
	 *             thrown if fails
	 * @since 2.0.0
	 */
	@Test
	public void testPersist() throws SQLException {
		final Foo foo = this.newFoo();
		this.persist(foo);

		this.commit();

		Assert.assertEquals(1,
			new QueryRunner(this.em().unwrap(DataSource.class)).query("SELECT COUNT(*) FROM Foo", new SingleValueHandler<Number>()).intValue());
	}
}
