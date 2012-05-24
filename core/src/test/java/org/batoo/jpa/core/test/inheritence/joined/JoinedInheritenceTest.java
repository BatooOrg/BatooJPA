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
package org.batoo.jpa.core.test.inheritence.joined;

import java.util.Set;

import junit.framework.Assert;

import org.batoo.jpa.core.test.BaseCoreTest;
import org.junit.Test;

import com.google.common.collect.Sets;

/**
 * @author hceylan
 * 
 * @since $version
 */
public class JoinedInheritenceTest extends BaseCoreTest {

	/**
	 * Tests to {@link EntityManager#persist(Object)} then {@link EntityManager#find(Class, Object)} with root type
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testSimpleInheritence0() {
		final Foo foo1 = new Foo();
		foo1.setValue("Value");

		final Foo foo2 = new Foo();
		foo2.setValue("Value");

		final Bar bar = new Bar();
		bar.getFoos().add(foo1);
		bar.getFoos().add(foo2);

		this.persist(bar);

		this.commit();

		this.close();

		final Bar bar2 = this.em().find(Bar.class, bar.getKey());
		final Set<Foo> foos = Sets.newHashSet(foo1, foo2);
		final Set<Foo> foos2 = Sets.newHashSet(bar2.getFoos());

		Assert.assertEquals(bar2.getFoos().size(), 2);
		Assert.assertEquals(foos, foos2);
	}

	/**
	 * Tests to {@link EntityManager#persist(Object)} then {@link EntityManager#find(Class, Object)} with root type with mixed classes
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testSimpleInheritence1() {
		final Foo foo1 = new Foo();
		foo1.setValue("Value");

		final FooExt1 foo2 = new FooExt1();
		foo2.setValue("Value");
		foo2.setValueExt1("ValueExt1");

		final Bar bar = new Bar();
		bar.getFoos().add(foo1);
		bar.getFoos().add(foo2);

		this.persist(bar);

		this.commit();

		this.close();

		final Bar bar2 = this.em().find(Bar.class, bar.getKey());
		final Set<Foo> foos = Sets.newHashSet(foo1, foo2);
		final Set<Foo> foos2 = Sets.newHashSet(bar2.getFoos());

		Assert.assertEquals(bar2.getFoos().size(), 2);
		Assert.assertEquals(foos, foos2);
	}

	/**
	 * Tests to {@link EntityManager#persist(Object)} then {@link EntityManager#find(Class, Object)} with root type
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testSimpleInheritence2() {
		final FooExt1 foo1 = new FooExt1();
		foo1.setValue("Value");
		foo1.setValueExt1("ValueExt1");

		final FooExt11 foo2 = new FooExt11();
		foo2.setValue("Value");
		foo2.setValueExt1("ValueExt1");

		final Bar2 bar = new Bar2();
		bar.getFoos().add(foo1);
		bar.getFoos().add(foo2);

		this.persist(bar);

		this.commit();

		this.close();

		final Bar2 bar2 = this.em().find(Bar2.class, bar.getKey());
		final Set<FooExt1> foos = Sets.newHashSet(foo1, foo2);
		final Set<FooExt1> foos2 = Sets.newHashSet(bar2.getFoos());

		Assert.assertEquals(bar2.getFoos().size(), 2);
		Assert.assertEquals(foos, foos2);
	}

}
