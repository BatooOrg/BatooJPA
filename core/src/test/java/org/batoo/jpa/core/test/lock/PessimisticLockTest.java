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
import javax.persistence.LockModeType;
import javax.persistence.PersistenceException;

import org.batoo.jpa.core.test.BaseCoreTest;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author hceylan
 * 
 * @since $version
 */
@Ignore
public class PessimisticLockTest extends BaseCoreTest {

	private Foo2 newFoo(boolean withChildren) {
		final Foo2 foo = new Foo2();

		foo.setValue("test");

		if (withChildren) {
			new Bar2(foo, "barValue1");
			new Bar2(foo, "barValue2");
		}

		return foo;
	}

	/**
	 * Tests the pessimistic lock.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testPessimisticLockRemove() {
		Foo2 foo = this.newFoo(false);

		this.persist(foo);
		this.commit();
		this.close();

		foo = this.find(Foo2.class, foo.getId(), LockModeType.PESSIMISTIC_WRITE);

		final EntityManager em2 = this.emf().createEntityManager();
		final Foo2 foo2 = em2.find(Foo2.class, foo.getId(), LockModeType.PESSIMISTIC_WRITE);

		final EntityTransaction tx2 = em2.getTransaction();
		tx2.begin();
		em2.remove(foo2);

		this.begin();
		foo.setValue("test3");

		tx2.commit();
		this.commit();
	}

	/**
	 * Tests the optimistic lock.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test(expected = PersistenceException.class)
	public void testPessimisticLockUpdate() {
		final Foo2 foo = this.newFoo(false);

		this.persist(foo);
		this.commit();

		final EntityManager em2 = this.emf().createEntityManager();
		final Foo2 foo2 = em2.find(Foo2.class, foo.getId());

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
	public void testPessimisticLockUpdateChild() {
		final Foo2 foo = this.newFoo(false);

		this.persist(foo);
		this.commit();

		final EntityManager em2 = this.emf().createEntityManager();
		final Foo2 foo2 = em2.find(Foo2.class, foo.getId());

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
	public void testPessimisticLockUpdateChildren() {
		final Foo2 foo = this.newFoo(false);

		this.persist(foo);
		this.commit();

		final EntityManager em2 = this.emf().createEntityManager();
		final Foo2 foo2 = em2.find(Foo2.class, foo.getId());

		final EntityTransaction tx2 = em2.getTransaction();
		tx2.begin();
		new Bar2(foo2, "barValue3");
		tx2.commit();

		this.begin();
		new Bar2(foo, "barValue3");
		this.commit();
	}
}
