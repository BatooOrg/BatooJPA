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

import junit.framework.Assert;

import org.batoo.jpa.core.test.AbstractTest;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author hceylan
 * 
 * @since $version
 */
@Ignore
public class SingleInheritenceTest extends AbstractTest {

	/**
	 * Tests to {@link EntityManager#persist(Object)} then {@link EntityManager#find(Class, Object)} with extending type
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testSimpleInheritence() {
		final FooExt1 foo = new FooExt1();
		foo.setValue("Value");
		foo.setValueExt1("ValueExt1");

		this.persist(foo);

		this.commit();

		this.close();

		final FooExt1 foo2 = this.find(FooExt1.class, foo.getKey());
		Assert.assertEquals(foo.getKey(), foo2.getKey());
		Assert.assertEquals(foo.getValue(), foo2.getValue());
		Assert.assertEquals(foo.getValueExt1(), foo2.getValueExt1());
	}

	/**
	 * Tests to {@link EntityManager#persist(Object)} then {@link EntityManager#find(Class, Object)} with further extending type
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testSimpleInheritence2() {
		final FooExt11 foo = new FooExt11();
		foo.setValue("Value");
		foo.setValueExt1("ValueExt1");
		foo.setValueExt11("ValueExt11");

		this.persist(foo);

		this.commit();

		this.close();

		final FooExt11 foo2 = this.find(FooExt11.class, foo.getKey());
		Assert.assertEquals(foo.getKey(), foo2.getKey());
		Assert.assertEquals(foo.getValue(), foo2.getValue());
		Assert.assertEquals(foo.getValueExt1(), foo2.getValueExt1());
		Assert.assertEquals(foo.getValueExt11(), foo2.getValueExt11());
	}

	/**
	 * Tests to {@link EntityManager#persist(Object)} then {@link EntityManager#find(Class, Object)} with root type
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testSimpleInheritence3() {
		final FooExt11 foo = new FooExt11();
		foo.setValue("Bar");

		this.persist(foo);

		this.commit();

		this.close();

		final Foo foo2 = this.find(Foo.class, foo.getKey());
		Assert.assertTrue(foo2.getClass() != foo.getClass());
		Assert.assertTrue(foo.getClass().isAssignableFrom(foo2.getClass()));

		Assert.assertEquals(foo.getKey(), foo2.getKey());
	}
}
