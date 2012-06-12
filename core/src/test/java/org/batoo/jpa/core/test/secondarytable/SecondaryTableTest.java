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
package org.batoo.jpa.core.test.secondarytable;

import java.sql.SQLException;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.SecondaryTables;
import javax.persistence.metamodel.EntityType;
import javax.sql.DataSource;

import junit.framework.Assert;

import org.apache.commons.dbutils.QueryRunner;
import org.batoo.jpa.core.impl.jdbc.SingleValueHandler;
import org.batoo.jpa.core.test.BaseCoreTest;
import org.batoo.jpa.core.test.NullResultSetHandler;
import org.junit.Test;

/**
 * @author hceylan
 * 
 * @since $version
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
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testCreateTable() throws SQLException {
		final Set<EntityType<?>> entities = this.em().getMetamodel().getEntities();

		Assert.assertEquals(4, entities.size());

		final DataSource dataSource = this.em().unwrap(DataSource.class);
		new QueryRunner(dataSource).query("SELECT * FROM FOO", new NullResultSetHandler());
		new QueryRunner(dataSource).query("SELECT * FROM FOOEXTRA", new NullResultSetHandler());
		new QueryRunner(dataSource).query("SELECT * FROM PERSON", new NullResultSetHandler());
		new QueryRunner(dataSource).query("SELECT * FROM PERSONEXTRA", new NullResultSetHandler());
		new QueryRunner(dataSource).query("SELECT * FROM ADDRESS", new NullResultSetHandler());
		new QueryRunner(dataSource).query("SELECT * FROM ADDRESSEXTRA", new NullResultSetHandler());
	}

	/**
	 * Tests to {@link EntityManager#find(Class, Object)}
	 * 
	 * @since $version
	 * @author hceylan
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
	 * @since $version
	 * @author hceylan
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
	 * @since $version
	 * @author hceylan
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
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testPersist() throws SQLException {
		final Foo foo = new Foo();
		this.persist(foo);

		this.commit();

		Integer count = new QueryRunner(this.em().unwrap(DataSource.class)).query("SELECT COUNT(*) FROM FOO",
			new SingleValueHandler<Integer>());
		Assert.assertEquals(new Integer(1), count);

		count = new QueryRunner(this.em().unwrap(DataSource.class)).query("SELECT COUNT(*) FROM FOOEXTRA",
			new SingleValueHandler<Integer>());

		Assert.assertEquals(new Integer(1), count);
	}

	/**
	 * Tests to {@link EntityManager#persist(Object)} with one-many-one.
	 * 
	 * @throws SQLException
	 *             thrown if underlying SQL fails
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testPersistOneToManyToOne() throws SQLException {
		final Person person = this.person();
		this.persist(person);

		this.commit();

		Integer count = new QueryRunner(this.em().unwrap(DataSource.class)).query("SELECT COUNT(*) FROM PERSON",
			new SingleValueHandler<Integer>());
		Assert.assertEquals(new Integer(1), count);

		count = new QueryRunner(this.em().unwrap(DataSource.class)).query("SELECT COUNT(*) FROM PERSONEXTRA",
			new SingleValueHandler<Integer>());

		Assert.assertEquals(new Integer(1), count);

		count = new QueryRunner(this.em().unwrap(DataSource.class)).query("SELECT COUNT(*) FROM ADDRESS", new SingleValueHandler<Integer>());
		Assert.assertEquals(new Integer(3), count);

		count = new QueryRunner(this.em().unwrap(DataSource.class)).query("SELECT COUNT(*) FROM ADDRESSEXTRA",
			new SingleValueHandler<Integer>());

		Assert.assertEquals(new Integer(3), count);
	}
}
