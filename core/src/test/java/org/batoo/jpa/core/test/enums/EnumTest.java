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
package org.batoo.jpa.core.test.enums;

import java.sql.SQLException;

import javax.persistence.EntityManager;
import javax.sql.DataSource;

import junit.framework.Assert;

import org.apache.commons.dbutils.QueryRunner;
import org.batoo.jpa.core.impl.jdbc.SingleValueHandler;
import org.batoo.jpa.core.test.BaseCoreTest;
import org.batoo.jpa.core.test.enums.Foo.FooType;
import org.junit.Test;

/**
 * @author hceylan
 * 
 * @since $version
 */
public class EnumTest extends BaseCoreTest {

	/**
	 * Tests to {@link EntityManager#find(Class, Object)}.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testFind() {
		Foo foo = new Foo();
		this.persist(foo);

		this.commit();
		this.close();

		foo = this.find(Foo.class, foo.getId());
		Assert.assertNull(foo.getFootype());
		Assert.assertNull(foo.getFootype2());
	}

	/**
	 * Tests to {@link EntityManager#find(Class, Object)}.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testFind2() {
		Foo foo = new Foo();
		foo.setFootype(FooType.TYPE1);
		foo.setFootype2(FooType.TYPE2);
		this.persist(foo);

		this.commit();
		this.close();

		foo = this.find(Foo.class, foo.getId());
		Assert.assertEquals(FooType.TYPE1, foo.getFootype());
		Assert.assertEquals(FooType.TYPE2, foo.getFootype2());
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

		Assert.assertNull(new QueryRunner(this.em().unwrap(DataSource.class)).query("SELECT FOOTYPE FROM FOO", new SingleValueHandler<String>()));
		Assert.assertNull(new QueryRunner(this.em().unwrap(DataSource.class)).query("SELECT FOOTYPE2 FROM FOO", new SingleValueHandler<Integer>()));
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
	public void testPersist2() throws SQLException {
		final Foo foo = new Foo();
		foo.setFootype(FooType.TYPE1);
		foo.setFootype2(FooType.TYPE2);
		this.persist(foo);

		this.commit();

		Assert.assertEquals(FooType.TYPE1.name(),
			new QueryRunner(this.em().unwrap(DataSource.class)).query("SELECT FOOTYPE FROM FOO", new SingleValueHandler<String>()));
		Assert.assertEquals(new Integer(1),
			new QueryRunner(this.em().unwrap(DataSource.class)).query("SELECT FOOTYPE2 FROM FOO", new SingleValueHandler<Integer>()));
	}

	/**
	 * Tests to update.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testUpdate() {
		Foo foo = new Foo();
		this.persist(foo);

		this.commit();
		this.close();

		this.begin();
		foo = this.find(Foo.class, foo.getId());

		foo.setFootype(FooType.TYPE1);
		foo.setFootype2(FooType.TYPE2);

		this.commit();
		this.close();

		foo = this.find(Foo.class, foo.getId());

		Assert.assertEquals(FooType.TYPE1, foo.getFootype());
		Assert.assertEquals(FooType.TYPE2, foo.getFootype2());
	}

	/**
	 * Tests to update.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testUpdate2() {
		Foo foo = new Foo();
		foo.setFootype(FooType.TYPE3);
		foo.setFootype2(FooType.TYPE3);

		this.persist(foo);

		this.commit();
		this.close();

		this.begin();
		foo = this.find(Foo.class, foo.getId());

		foo.setFootype(FooType.TYPE1);
		foo.setFootype2(FooType.TYPE2);

		this.commit();
		this.close();

		foo = this.find(Foo.class, foo.getId());

		Assert.assertEquals(FooType.TYPE1, foo.getFootype());
		Assert.assertEquals(FooType.TYPE2, foo.getFootype2());
	}
}
