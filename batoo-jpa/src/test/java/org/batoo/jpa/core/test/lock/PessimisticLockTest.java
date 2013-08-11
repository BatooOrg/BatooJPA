/*
 * Copyright (c) 2012-2013, Batu Alp Ceylan
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
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
 * @since 2.0.0
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
	 * @since 2.0.0
	 */
	@Test
	public void testPessimisticLockRemove() {
		Foo2 foo = this.newFoo(false);

		this.persist(foo);
		this.commit();
		this.close();

		foo = this.find(Foo2.class, foo.getId(), LockModeType.PESSIMISTIC_WRITE);

		final EntityManager em2 = this.emf().createEntityManager();
		try {
			final Foo2 foo2 = em2.find(Foo2.class, foo.getId(), LockModeType.PESSIMISTIC_WRITE);

			final EntityTransaction tx2 = em2.getTransaction();
			tx2.begin();
			em2.remove(foo2);

			this.begin();
			foo.setValue("test3");

			tx2.commit();
			this.commit();
		}
		finally {
			em2.close();
		}
	}

	/**
	 * Tests the optimistic lock.
	 * 
	 * @since 2.0.0
	 */
	@Test(expected = PersistenceException.class)
	public void testPessimisticLockUpdate() {
		final Foo2 foo = this.newFoo(false);

		this.persist(foo);
		this.commit();

		final EntityManager em2 = this.emf().createEntityManager();
		try {
			final Foo2 foo2 = em2.find(Foo2.class, foo.getId());

			final EntityTransaction tx2 = em2.getTransaction();
			tx2.begin();
			foo2.setValue("test2");
			tx2.commit();

			this.begin();
			foo.setValue("test3");
			this.commit();
		}
		finally {
			em2.close();
		}
	}

	/**
	 * Tests the optimistic lock.
	 * 
	 * @since 2.0.0
	 */
	@Test(expected = PersistenceException.class)
	public void testPessimisticLockUpdateChild() {
		final Foo2 foo = this.newFoo(false);

		this.persist(foo);
		this.commit();

		final EntityManager em2 = this.emf().createEntityManager();
		try {
			final Foo2 foo2 = em2.find(Foo2.class, foo.getId());

			final EntityTransaction tx2 = em2.getTransaction();
			tx2.begin();
			foo2.getBars().get(0).setValue("barChangedValue");
			tx2.commit();

			this.begin();
			foo.getBars().get(0).setValue("barChangedValue2");
			this.commit();
		}
		finally {
			em2.close();
		}
	}

	/**
	 * Tests the optimistic lock.
	 * 
	 * @since 2.0.0
	 */
	@Test(expected = PersistenceException.class)
	public void testPessimisticLockUpdateChildren() {
		final Foo2 foo = this.newFoo(false);

		this.persist(foo);
		this.commit();

		final EntityManager em2 = this.emf().createEntityManager();
		try {
			final Foo2 foo2 = em2.find(Foo2.class, foo.getId());

			final EntityTransaction tx2 = em2.getTransaction();
			tx2.begin();
			new Bar2(foo2, "barValue3");
			tx2.commit();

			this.begin();
			new Bar2(foo, "barValue3");
			this.commit();
		}
		finally {
			em2.close();
		}
	}
}
