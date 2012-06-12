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
package org.batoo.jpa.core.test.inheritence.single;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import junit.framework.Assert;

import org.batoo.jpa.core.test.BaseCoreTest;
import org.junit.Test;

/**
 * @author hceylan
 * 
 * @since $version
 */
public class InheritanceTest extends BaseCoreTest {

	/**
	 * Tests to {@link EntityManager#persist(Object)} then {@link EntityManager#find(Class, Object)} with identity value
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testInheritance() {
		final FooExt1 foo = new FooExt1();
		foo.setValue("Bar");
		foo.setValueExt1("Bar1");

		this.persist(foo);

		this.commit();

		this.close();

		final FooExt1 foo2 = (FooExt1) this.find(Foo.class, foo.getKey());
		Assert.assertEquals(foo.getKey(), foo2.getKey());
		Assert.assertEquals(foo.getValue(), foo2.getValue());
		Assert.assertEquals(foo.getValueExt1(), foo.getValueExt1());
	}

	/**
	 * Tests to {@link EntityManager#persist(Object)} then {@link EntityManager#find(Class, Object)} with identity value
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testSimpleInheritance2() {
		final FooExt11 foo = new FooExt11();
		foo.setValue("Bar");

		this.persist(foo);

		this.commit();

		this.close();

		final FooExt11 foo2 = this.find(FooExt11.class, foo.getKey());
		Assert.assertEquals(foo.getKey(), foo2.getKey());
		Assert.assertEquals(foo.getValue(), foo2.getValue());
		Assert.assertEquals(foo.getValueExt1(), foo.getValueExt1());
	}

	/**
	 * Tests to {@link EntityManager#persist(Object)} then {@link EntityManager#find(Class, Object)} with identity value
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test(expected = NoResultException.class)
	public void testSimpleInheritance3() {
		final FooExt1 foo = new FooExt1();
		foo.setValue("Bar");

		this.persist(foo);

		this.commit();

		this.close();

		this.find(FooExt11.class, foo.getKey());
	}

}
