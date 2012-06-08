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
package org.batoo.jpa.core.test.mappedsuperclass;

import java.sql.SQLException;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.TransactionRequiredException;
import javax.persistence.metamodel.EntityType;
import javax.sql.DataSource;

import junit.framework.Assert;

import org.apache.commons.dbutils.QueryRunner;
import org.batoo.jpa.core.impl.jdbc.SingleValueHandler;
import org.batoo.jpa.core.test.BaseCoreTest;
import org.batoo.jpa.core.test.NullResultSetHandler;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author hceylan
 * 
 * @since $version
 */
@Ignore
public class MappedSuperClassTest extends BaseCoreTest {

	/**
	 * @return new Foo
	 * 
	 * @since $version
	 * @author hceylan
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
	 * @since $version
	 * @author hceylan
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
	 * @since $version
	 * @author hceylan
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
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testDetach() {
		final Foo Foo = this.newFoo();
		this.persist(Foo);

		Assert.assertTrue(this.em().contains(Foo));
		this.detach(Foo);
		this.close();

		Assert.assertFalse(this.em().contains(Foo));
	}

	/**
	 * Tests {@link EntityManager#detach(Object)} then {@link EntityTransaction#commit()}.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testDetachThenCommit() throws SQLException {
		final Foo Foo = this.newFoo();
		this.persist(Foo);

		this.detach(Foo);

		this.commit();

		final Integer count = new QueryRunner(this.em().unwrap(DataSource.class)).query("SELECT COUNT(*) FROM Foo",
			new SingleValueHandler<Integer>());
		Assert.assertEquals(new Integer(0), count);
	}

	/**
	 * Tests to {@link EntityManager#find(Class, Object)}
	 * 
	 * @since $version
	 * @author hceylan
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
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testFlushThenDetach() throws SQLException {
		final Foo Foo = this.newFoo();
		this.persist(Foo);

		this.flush();

		this.detach(Foo);

		try {
			this.commit();

			Assert.fail("TransactionRequiredException expected");
		}
		catch (final TransactionRequiredException e) {}
	}

	/**
	 * Tests to {@link EntityManager#persist(Object)}.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testPersist() throws SQLException {
		final Foo Foo = this.newFoo();
		this.persist(Foo);

		this.commit();

		final Integer count = new QueryRunner(this.em().unwrap(DataSource.class)).query("SELECT COUNT(*) FROM FOO",
			new SingleValueHandler<Integer>());
		Assert.assertEquals(new Integer(1), count);
	}
}
