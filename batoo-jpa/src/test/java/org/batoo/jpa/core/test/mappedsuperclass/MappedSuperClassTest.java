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
package org.batoo.jpa.core.test.mappedsuperclass;

import java.sql.SQLException;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.metamodel.EntityType;
import javax.sql.DataSource;

import junit.framework.Assert;

import org.batoo.jpa.core.impl.jdbc.dbutils.QueryRunner;
import org.batoo.jpa.core.impl.jdbc.dbutils.SingleValueHandler;
import org.batoo.jpa.core.test.BaseCoreTest;
import org.batoo.jpa.core.test.NullResultSetHandler;
import org.junit.Test;

/**
 * @author hceylan
 * 
 * @since 2.0.0
 */
public class MappedSuperClassTest extends BaseCoreTest {

	/**
	 * @return new Foo
	 * 
	 * @since 2.0.0
	 */
	private Foo newFoo() {
		final Foo foo = new Foo();

		foo.setFooValue("FooValue");
		foo.setBarValue("BarValue");

		Quux quux;

		quux = new Quux();
		quux.setFoo(foo);
		quux.setQuuxValue(1);
		foo.getFooQuuxes().add(quux);

		quux = new Quux();
		quux.setFoo(foo);
		quux.setQuuxValue(2);
		foo.getFooQuuxes().add(quux);

		quux = new Quux();
		quux.setFoo(foo);
		quux.setQuuxValue(3);
		foo.getQuuxes().add(quux);

		quux = new Quux();
		quux.setFoo(foo);
		quux.setQuuxValue(4);
		foo.getQuuxes().add(quux);

		return foo;
	}

	/**
	 * Tests {@link EntityManager#contains(Object)}.
	 * 
	 * @since 2.0.0
	 */
	@Test
	public void testContains() {
		final Foo Foo = this.newFoo();
		final Foo Foo2 = this.newFoo();

		this.persist(Foo);

		Assert.assertTrue(this.contains(Foo));
		Assert.assertFalse(this.contains(Foo2));
	}

	/**
	 * Tests {@link EntityManagerFactory#createEntityManager()}
	 * 
	 * @throws SQLException
	 *             thrown if test fails.
	 * 
	 * @since 2.0.0
	 */
	@Test
	public void testCreateTable() throws SQLException {
		final Set<EntityType<?>> entities = this.em().getMetamodel().getEntities();

		Assert.assertEquals(2, entities.size());

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
		final Foo Foo = this.newFoo();
		this.persist(Foo);

		Assert.assertTrue(this.em().contains(Foo));
		this.detach(Foo);

		Assert.assertFalse(this.em().contains(Foo));
	}

	/**
	 * Tests {@link EntityManager#detach(Object)} then {@link EntityTransaction#commit()}.
	 * 
	 * @throws SQLException
	 *             thrown if test fails.
	 * 
	 * @since 2.0.0
	 */
	@Test
	public void testDetachThenCommit() throws SQLException {
		final Foo Foo = this.newFoo();
		this.persist(Foo);

		this.detach(Foo);

		this.commit();

		Assert.assertEquals(0,
			(Number) new QueryRunner(this.em().unwrap(DataSource.class)).query("SELECT COUNT(*) FROM Foo", new SingleValueHandler<Number>()).intValue());
	}

	/**
	 * Tests to {@link EntityManager#find(Class, Object)}
	 * 
	 * @since 2.0.0
	 */
	@Test
	public void testFind() {
		final Foo Foo = this.newFoo();
		this.persist(Foo);

		this.commit();

		this.close();

		final Foo Foo2 = this.find(Foo.class, Foo.getKey());
		Assert.assertEquals(Foo.getKey(), Foo2.getKey());
	}

	/**
	 * Tests {@link EntityManager#flush()} then {@link EntityManager#detach(Object)}
	 * 
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
	 *             thrown if test fails.
	 * 
	 * @since 2.0.0
	 */
	@Test
	public void testPersist() throws SQLException {
		final Foo Foo = this.newFoo();
		this.persist(Foo);

		this.commit();

		Assert.assertEquals(1,
			new QueryRunner(this.em().unwrap(DataSource.class)).query("SELECT COUNT(*) FROM Foo", new SingleValueHandler<Number>()).intValue());
	}
}
