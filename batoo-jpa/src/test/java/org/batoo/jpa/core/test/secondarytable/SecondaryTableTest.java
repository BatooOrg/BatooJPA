/*
 * Copyright (c) 2012-2013, Batu Alp Ceylan
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

package org.batoo.jpa.core.test.secondarytable;

import java.sql.SQLException;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.SecondaryTables;
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
public class SecondaryTableTest extends BaseCoreTest {

	private Person person() {
		final Person person = new Person("Ceylan");
		new Address(person, "Istanbul");
		new Address(person, "London");
		new Address(person, "New York");

		return person;
	}

	/**
	 * Tests {@link EntityManagerFactory#createEntityManager()}
	 * 
	 * @throws SQLException
	 *             thrown if underlying SQL fails
	 * 
	 * @since 2.0.0
	 */
	@Test
	public void testCreateTable() throws SQLException {
		final Set<EntityType<?>> entities = this.em().getMetamodel().getEntities();

		Assert.assertEquals(4, entities.size());

		final DataSource dataSource = this.em().unwrap(DataSource.class);
		new QueryRunner(dataSource).query("SELECT * FROM Foo", new NullResultSetHandler());
		new QueryRunner(dataSource).query("SELECT * FROM FooExtra", new NullResultSetHandler());
		new QueryRunner(dataSource).query("SELECT * FROM Person", new NullResultSetHandler());
		new QueryRunner(dataSource).query("SELECT * FROM PersonExtra", new NullResultSetHandler());
		new QueryRunner(dataSource).query("SELECT * FROM Address", new NullResultSetHandler());
		new QueryRunner(dataSource).query("SELECT * FROM AddressExtra", new NullResultSetHandler());
	}

	/**
	 * Tests to {@link EntityManager#find(Class, Object)}
	 * 
	 * @since 2.0.0
	 */
	@Test
	public void testFind() {
		final Foo foo = new Foo();
		foo.setValue1("Value1");
		foo.setValue2("Value2");

		this.persist(foo);

		this.commit();

		this.close();

		final Foo foo2 = this.find(Foo.class, foo.getKey());

		Assert.assertEquals(foo.getKey(), foo2.getKey());
		Assert.assertEquals(foo.getValue1(), foo2.getValue1());
		Assert.assertEquals(foo.getValue2(), foo2.getValue2());
	}

	/**
	 * Tests to {@link EntityManager#find(Class, Object)} with {@link SecondaryTables}
	 * 
	 * @since 2.0.0
	 */
	@Test
	public void testFind2() {
		final Foo2 foo = new Foo2();
		foo.setValue1("Value1");
		foo.setValue2("Value2");

		this.persist(foo);

		this.commit();

		this.close();

		final Foo2 foo2 = this.find(Foo2.class, foo.getKey());

		Assert.assertEquals(foo.getKey(), foo2.getKey());
		Assert.assertEquals(foo.getValue1(), foo2.getValue1());
		Assert.assertEquals(foo.getValue2(), foo2.getValue2());
	}

	/**
	 * Tests to {@link EntityManager#find(Class, Object)} with one-many-one.
	 * 
	 * @since 2.0.0
	 */
	@Test
	public void testFindOneToManyToOne() {
		final Person person = this.person();

		this.persist(person);

		this.commit();

		this.close();

		final Person person2 = this.find(Person.class, person.getId());

		Assert.assertEquals(person.getId(), person2.getId());
		Assert.assertEquals(person.getAddresses().size(), person2.getAddresses().size());
	}

	/**
	 * Tests to {@link EntityManager#persist(Object)}.
	 * 
	 * @throws SQLException
	 *             thrown if underlying SQL fails
	 * 
	 * @since 2.0.0
	 */
	@Test
	public void testPersist() throws SQLException {
		final Foo foo = new Foo();
		this.persist(foo);

		this.commit();

		Assert.assertEquals(1,
			new QueryRunner(this.em().unwrap(DataSource.class)).query("SELECT COUNT(*) FROM Foo", new SingleValueHandler<Number>()).intValue());

		Assert.assertEquals(1,
			new QueryRunner(this.em().unwrap(DataSource.class)).query("SELECT COUNT(*) FROM FooExtra", new SingleValueHandler<Number>()).intValue());
	}

	/**
	 * Tests to {@link EntityManager#persist(Object)} with one-many-one.
	 * 
	 * @throws SQLException
	 *             thrown if underlying SQL fails
	 * 
	 * @since 2.0.0
	 */
	@Test
	public void testPersistOneToManyToOne() throws SQLException {
		final Person person = this.person();
		this.persist(person);

		this.commit();

		Assert.assertEquals(1,
			new QueryRunner(this.em().unwrap(DataSource.class)).query("SELECT COUNT(*) FROM Person", new SingleValueHandler<Number>()).intValue());

		Assert.assertEquals(1,
			new QueryRunner(this.em().unwrap(DataSource.class)).query("SELECT COUNT(*) FROM PersonExtra", new SingleValueHandler<Number>()).intValue());

		Assert.assertEquals(3,
			new QueryRunner(this.em().unwrap(DataSource.class)).query("SELECT COUNT(*) FROM Address", new SingleValueHandler<Number>()).intValue());

		Assert.assertEquals(3,
			new QueryRunner(this.em().unwrap(DataSource.class)).query("SELECT COUNT(*) FROM AddressExtra", new SingleValueHandler<Number>()).intValue());
	}

	/**
	 * Tests to update
	 * 
	 * @throws SQLException
	 *             thrown if underlying SQL fails
	 * 
	 * @since 2.0.0
	 */
	@Test
	public void testRemove() throws SQLException {
		Foo foo = new Foo();
		this.persist(foo);

		this.commit();

		this.begin();
		foo = this.find(Foo.class, foo.getKey());
		this.remove(foo);
		this.commit();
	}

	/**
	 * Tests to update
	 * 
	 * @throws SQLException
	 *             thrown if underlying SQL fails
	 * 
	 * @since 2.0.0
	 */
	@Test
	public void testUpdate() throws SQLException {
		Foo foo = new Foo();
		this.persist(foo);

		this.commit();

		this.begin();
		foo = this.find(Foo.class, foo.getKey());
		foo.setValue1("111");
		this.commit();
	}
}
