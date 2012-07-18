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
package org.batoo.jpa.core.test.elementcollection;

import junit.framework.Assert;

import org.batoo.jpa.core.test.BaseCoreTest;
import org.batoo.jpa.core.test.enums.Foo.FooType;
import org.junit.Test;

/**
 * @author hceylan
 * 
 * @since $version
 */
public class ElementCollectionTest extends BaseCoreTest {

	/**
	 * Tests the element collections
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testElementCollection1() {
		Foo foo = new Foo();
		foo.setValue("Foo2");

		foo.getCodes().add("TR");
		foo.getCodes().add("UK");
		foo.getCodes().add("US");

		this.persist(foo);

		this.commit();

		this.begin();
		foo.getCodes().add("FR");
		this.commit();

		this.close();

		foo = this.find(Foo.class, foo.getKey());
		Assert.assertEquals(foo.getKey(), foo.getKey());
		Assert.assertEquals(4, foo.getCodes().size());
	}

	/**
	 * Tests the element collections
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testElementCollection2() {
		Foo foo = new Foo();
		foo.setValue("Foo2");

		foo.getCodes2().add("TR");
		foo.getCodes2().add("UK");
		foo.getCodes2().add("US");

		this.persist(foo);

		this.commit();

		this.begin();
		foo.getCodes2().add("FR");
		this.commit();

		this.close();

		foo = this.find(Foo.class, foo.getKey());
		Assert.assertEquals(foo.getKey(), foo.getKey());
		Assert.assertEquals(4, foo.getCodes2().size());
	}

	/**
	 * Tests the element collections
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testElementCollection3() {
		Foo foo = new Foo();
		foo.setValue("Foo2");

		foo.getCodes3().add(FooType.TYPE1);
		foo.getCodes3().add(FooType.TYPE2);

		this.persist(foo);

		this.commit();

		this.begin();
		foo.getCodes3().remove(FooType.TYPE1);
		foo.getCodes3().add(FooType.TYPE3);
		this.commit();

		this.close();

		foo = this.find(Foo.class, foo.getKey());
		Assert.assertEquals(foo.getKey(), foo.getKey());
		Assert.assertEquals(2, foo.getCodes3().size());
	}

	/**
	 * Tests the element collections
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testElementCollection4() {
		Foo foo = new Foo();
		foo.setValue("Foo2");

		foo.getCodes4().add(FooType.TYPE1);
		foo.getCodes4().add(FooType.TYPE2);

		this.persist(foo);

		this.commit();

		this.begin();
		foo.getCodes4().remove(FooType.TYPE1);
		foo.getCodes4().add(FooType.TYPE3);
		this.commit();

		this.close();

		foo = this.find(Foo.class, foo.getKey());
		Assert.assertEquals(foo.getKey(), foo.getKey());
		Assert.assertEquals(2, foo.getCodes4().size());
	}
}
