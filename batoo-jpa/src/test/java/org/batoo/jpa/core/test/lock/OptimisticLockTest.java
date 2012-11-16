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

import java.sql.Connection;
import java.sql.SQLException;

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
public class OptimisticLockTest extends BaseCoreTest {

	private Foo newFoo(boolean withChildren) {
		final Foo foo = new Foo();

		foo.setValue("test");

		if (withChildren) {
			new Bar(foo, "barValue1");
			new Bar(foo, "barValue2");
		}

		return foo;
	}

	/**
	 * Tests the optimistic lock.
	 * 
	 * @since $version
	 */
	@Test(expected = PersistenceException.class)
	public void testOptimisticLockRemove() {
		final Foo foo = this.newFoo(false);

		this.persist(foo);
		this.commit();

		final EntityManager em2 = this.emf().createEntityManager();
		try {
			final Foo foo2 = em2.find(Foo.class, foo.getId());

			final EntityTransaction tx2 = em2.getTransaction();

			tx2.begin();
			em2.remove(foo2);
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
	 * @since $version
	 */
	@Test(expected = PersistenceException.class)
	public void testOptimisticLockRollback() {
		Foo foo = this.newFoo(false);

		this.persist(foo);
		this.commit();
		this.close();

		this.begin();
		foo = this.find(Foo.class, foo.getId());
		foo.setValue("NewValue1");
		this.flush();

		final EntityManager em2 = this.emf().createEntityManager();
		try {
			try {
				final EntityTransaction tx2 = em2.getTransaction();
				em2.unwrap(Connection.class).setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
				tx2.begin();
				final Foo foo2 = em2.find(Foo.class, foo.getId());
				foo2.setValue("NewValue2");
				this.rollback();
				tx2.commit();
			}
			catch (final SQLException e) {
				throw new RuntimeException(e);
			}
		}
		finally {
			em2.close();
		}

	}

	/**
	 * Tests the optimistic lock.
	 * 
	 * @since $version
	 */
	@Test(expected = PersistenceException.class)
	public void testOptimisticLockUpdate() {
		final Foo foo = this.newFoo(false);

		this.persist(foo);
		this.commit();

		final EntityManager em2 = this.emf().createEntityManager();
		try {
			final Foo foo2 = em2.find(Foo.class, foo.getId());

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
	 * @since $version
	 */
	@Test(expected = PersistenceException.class)
	public void testOptimisticLockUpdateChild() {
		final Foo foo = this.newFoo(true);

		this.persist(foo);
		this.commit();

		final EntityManager em2 = this.emf().createEntityManager();
		try {
			final Foo foo2 = em2.find(Foo.class, foo.getId());

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
	 * @since $version
	 */
	@Test(expected = PersistenceException.class)
	public void testOptimisticLockUpdateChildren() {
		final Foo foo = this.newFoo(true);

		this.persist(foo);
		this.commit();

		this.begin();
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
