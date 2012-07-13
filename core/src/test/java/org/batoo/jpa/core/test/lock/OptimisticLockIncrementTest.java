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
package org.batoo.jpa.core.test.lock;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceException;

import org.batoo.jpa.core.test.BaseCoreTest;
import org.junit.Test;

/**
 * @author hceylan
 * 
 * @since $version
 */
public class OptimisticLockIncrementTest extends BaseCoreTest {

	private Foo newFoo(boolean withChildren) {
		final Foo foo = new Foo();

		foo.setValue("test");

		new Bar(foo, "barValue1");
		new Bar(foo, "barValue2");

		return foo;
	}

	/**
	 * Tests the optimistic lock.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test(expected = PersistenceException.class)
	public void testOptimisticLockRemove() {
		final Foo foo = this.newFoo(false);

		this.persist(foo);
		this.commit();

		final EntityManager em2 = this.emf().createEntityManager();
		final Foo foo2 = em2.find(Foo.class, foo.getId());

		final EntityTransaction tx2 = em2.getTransaction();
		tx2.begin();
		em2.remove(foo2);
		tx2.commit();

		this.begin();
		foo.setValue("test3");
		this.commit();
	}

	/**
	 * Tests the optimistic lock.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test(expected = PersistenceException.class)
	public void testOptimisticLockUpdate() {
		final Foo foo = this.newFoo(false);

		this.persist(foo);
		this.commit();

		final EntityManager em2 = this.emf().createEntityManager();
		final Foo foo2 = em2.find(Foo.class, foo.getId());

		final EntityTransaction tx2 = em2.getTransaction();
		tx2.begin();
		foo2.setValue("test2");
		tx2.commit();

		this.begin();
		foo.setValue("test3");
		this.commit();
	}

	/**
	 * Tests the optimistic lock.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test(expected = PersistenceException.class)
	public void testOptimisticLockUpdateChild() {
		final Foo foo = this.newFoo(true);

		this.persist(foo);
		this.commit();

		final EntityManager em2 = this.emf().createEntityManager();
		final Foo foo2 = em2.find(Foo.class, foo.getId());

		final EntityTransaction tx2 = em2.getTransaction();
		tx2.begin();
		foo2.getBars().get(0).setValue("barChangedValue");
		tx2.commit();

		this.begin();
		foo.getBars().get(0).setValue("barChangedValue2");
		this.commit();
	}

	/**
	 * Tests the optimistic lock.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test(expected = PersistenceException.class)
	public void testOptimisticLockUpdateChildren() {
		final Foo foo = this.newFoo(true);

		this.persist(foo);
		this.commit();

		final EntityManager em2 = this.emf().createEntityManager();
		final Foo foo2 = em2.find(Foo.class, foo.getId());

		final EntityTransaction tx2 = em2.getTransaction();
		tx2.begin();
		new Bar(foo2, "barValue3");
		tx2.commit();

		this.begin();
		new Bar(foo, "barValue3");
		this.commit();
	}
}
