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
package org.batoo.jpa.core.test.embeddable.overrides;

import java.sql.SQLException;

import javax.sql.DataSource;

import junit.framework.Assert;

import org.apache.commons.dbutils.QueryRunner;
import org.batoo.jpa.core.impl.manager.jdbc.SingleValueHandler;
import org.batoo.jpa.core.test.BaseCoreTest;
import org.junit.Test;

/**
 * @author hceylan
 * 
 * @since $version
 */
public class EmbeddableOverridesTest extends BaseCoreTest {

	private Foo foo() {
		final Foo foo = new Foo();
		foo.setValue("Value");

		final Address homeAddress = new Address();
		homeAddress.setCity("Istanbul");
		homeAddress.setStreet("Sweat home street");
		foo.setHomeAddress(homeAddress);

		final Address workAddress = new Address();
		workAddress.setCity("London");
		workAddress.setStreet("Big Money Avenue");
		foo.setWorkAddress(workAddress);

		return foo;
	}

	/**
	 * Tests to {@link EntityManager#find(Class, Object)}
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testFind() {
		final Foo foo = this.foo();
		this.persist(foo);

		this.commit();

		this.close();

		final Foo foo2 = this.find(Foo.class, foo.getId());
		Assert.assertEquals(foo.getId(), foo2.getId());
		Assert.assertEquals(foo.getValue(), foo2.getValue());
		Assert.assertEquals(foo.getHomeAddress().getCity(), foo2.getHomeAddress().getCity());
		Assert.assertEquals(foo.getHomeAddress().getStreet(), foo2.getHomeAddress().getStreet());
		Assert.assertEquals(foo.getWorkAddress().getCity(), foo2.getWorkAddress().getCity());
		Assert.assertEquals(foo.getWorkAddress().getStreet(), foo2.getWorkAddress().getStreet());
	}

	/**
	 * Tests to {@link EntityManager#persist(Object)}.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testPersist() throws SQLException {
		final Foo foo = this.foo();
		this.persist(foo);

		this.commit();

		final Integer count = new QueryRunner(this.em().unwrap(DataSource.class)).query("SELECT COUNT(*) FROM FOO",
			new SingleValueHandler<Integer>());
		Assert.assertEquals(new Integer(1), count);
	}
}
