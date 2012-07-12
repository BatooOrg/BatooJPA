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
package org.batoo.jpa.core.test.manualid;

import javax.persistence.EntityManager;

import junit.framework.Assert;

import org.batoo.jpa.core.test.BaseCoreTest;
import org.junit.Test;

/**
 * @author hceylan
 * 
 * @since $version
 */
public class ManualIdTest extends BaseCoreTest {

	/**
	 * Tests to {@link EntityManager#persist(Object)} then {@link EntityManager#find(Class, Object)} with identity value
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testIdentiy() {
		final Foo foo = new Foo();
		foo.setKey(1);
		foo.setValue("Foo2");

		final Foo foo2 = new Foo();
		foo2.setKey(2);
		foo2.setValue("Foo2");

		this.persist(foo);
		this.persist(foo2);

		this.commit();

		this.close();

		final Foo foo3 = this.find(Foo.class, foo.getKey());
		Assert.assertEquals(foo.getKey(), foo3.getKey());
	}

}
