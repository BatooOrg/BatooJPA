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
package org.batoo.jpa.core.test.idclass;

import javax.persistence.EntityManager;

import junit.framework.Assert;

import org.batoo.jpa.core.test.BaseCoreTest;
import org.junit.Test;

/**
 * @author hceylan
 * 
 * @since $version
 */
public class IdClassTestTest extends BaseCoreTest {

	/**
	 * Tests to {@link EntityManager#persist(Object)} then {@link EntityManager#find(Class, Object)} with IdClass
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testIdClass() {
		final Foo foo1 = new Foo();
		foo1.setStrKey("key1");
		foo1.setIntKey(1);
		foo1.setValue("Foo2");

		final Foo foo2 = new Foo();
		foo2.setStrKey("key1");
		foo2.setIntKey(2);
		foo2.setValue("Foo2");

		this.persist(foo1);
		this.persist(foo2);

		this.commit();

		this.close();

		final Foo foo3 = this.find(Foo.class, new FooPk(foo1.getIntKey(), foo1.getStrKey()));
		Assert.assertEquals(foo1, foo3);
	}
}
