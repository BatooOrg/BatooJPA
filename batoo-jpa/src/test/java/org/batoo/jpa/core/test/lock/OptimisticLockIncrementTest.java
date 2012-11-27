/*
 * Copyright (c) 2012 - Batoo Software ve Consultancy Ltd.
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
import javax.persistence.PersistenceException;

import org.batoo.jpa.core.test.BaseCoreTest;
import org.junit.Test;

/**
 * @author hceylan
 * 
 * @since 2.0.0
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
	 * @since 2.0.0
	 */
	@Test(expected = PersistenceException.class)
	public void testOptimisticLockRemove() {
		Foo foo = this.newFoo(false);

		this.persist(foo);
		this.commit();

		foo = this.merge(foo);

		final EntityManager em2 = this.emf().createEntityManager();
		try {
			final EntityTransaction tx2 = em2.getTransaction();
			tx2.begin();

			final Foo foo2 = em2.find(Foo.class, foo.getId());

			em2.remove(foo2);
			tx2.commit();

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
	public void testOptimisticLockUpdate() {
		Foo foo = this.newFoo(false);

		this.persist(foo);
		this.commit();

		foo = this.merge(foo);

		final EntityManager em2 = this.emf().createEntityManager();
		try {
			final EntityTransaction tx2 = em2.getTransaction();
			tx2.begin();

			final Foo foo2 = em2.find(Foo.class, foo.getId());

			foo2.setValue("test2");
			tx2.commit();

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
	// TODO check with the spec
	// @Test(expected = PersistenceException.class)
	public void testOptimisticLockUpdateChild() {
		Foo foo = this.newFoo(true);

		this.persist(foo);
		this.commit();

		foo = this.merge(foo);

		final EntityManager em2 = this.emf().createEntityManager();
		try {
			final EntityTransaction tx2 = em2.getTransaction();
			tx2.begin();

			final Foo foo2 = em2.find(Foo.class, foo.getId());

			foo2.getBars().get(0).setValue("barChangedValue");
			tx2.commit();

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
	public void testOptimisticLockUpdateChildren() {
		final Foo foo = this.newFoo(true);

		this.persist(foo);
		this.commit();

		final EntityManager em2 = this.emf().createEntityManager();
		try {
			final EntityTransaction tx2 = em2.getTransaction();
			tx2.begin();

			final Foo foo2 = em2.find(Foo.class, foo.getId());

			new Bar(foo2, "barValue3");
			tx2.commit();

			this.begin();
			new Bar(foo, "barValue3");
			this.commit();
		}
		finally {
			em2.close();
		}
	}
}
