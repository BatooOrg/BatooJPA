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
package org.batoo.jpa.core.test.simple;

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
import org.junit.Test;

/**
 * @author hceylan
 * 
 * @since $version
 */
public class SimpleTest extends BaseCoreTest {

	/**
	 * Tests {@link EntityManager#contains(Object)}.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testContains() {
		final Foo foo = new Foo();
		final Foo foo2 = new Foo();

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
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testCreateTable() throws SQLException {
		final Set<EntityType<?>> entities = this.em().getMetamodel().getEntities();

		Assert.assertEquals(1, entities.size());

		final DataSource dataSource = this.em().unwrap(DataSource.class);
		new QueryRunner(dataSource).query("SELECT * FROM FOO", new NullResultSetHandler());
	}

	/**
	 * Tests {@link EntityManager#detach(Object)}.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testDetach() {
		// FIXME it still contains but the status is detached
		final Foo foo = new Foo();
		this.persist(foo);

		Assert.assertTrue(this.em().contains(foo));
		this.detach(foo);
		this.close();

		Assert.assertFalse(this.em().contains(foo));
	}

	/**
	 * Tests {@link EntityManager#detach(Object)} then {@link EntityTransaction#commit()}.
	 * 
	 * @throws SQLException
	 *             thrown if fails
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testDetachThenCommit() throws SQLException {
		final Foo foo = new Foo();
		this.persist(foo);

		this.detach(foo);

		this.commit();

		final Integer count = new QueryRunner(this.em().unwrap(DataSource.class)).query("SELECT COUNT(*) FROM FOO",
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
		final Foo foo = new Foo();
		this.persist(foo);

		this.commit();

		this.close();

		final Foo foo2 = this.find(Foo.class, foo.getKey());
		Assert.assertEquals(foo.getKey(), foo2.getKey());
	}

	/**
	 * Tests {@link EntityManager#flush()} then {@link EntityManager#detach(Object)}
	 * 
	 * @throws SQLException
	 *             thrown if fails
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testFlushThenDetach() throws SQLException {
		final Foo foo = new Foo();
		this.persist(foo);

		this.flush();

		this.detach(foo);

		try {
			this.commit();

			Assert.fail("TransactionRequiredException expected");
		}
		catch (final TransactionRequiredException e) {}
	}

	/**
	 * Tests to {@link EntityManager#persist(Object)}.
	 * 
	 * @throws SQLException
	 *             thrown if fails
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testPersist() throws SQLException {
		final Foo foo = new Foo();
		this.persist(foo);

		this.commit();

		final Integer count = new QueryRunner(this.em().unwrap(DataSource.class)).query("SELECT COUNT(*) FROM FOO",
			new SingleValueHandler<Integer>());
		Assert.assertEquals(new Integer(1), count);
	}
}
