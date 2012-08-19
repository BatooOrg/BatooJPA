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
package org.batoo.jpa.benchmark.insert;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Root;

import org.batoo.jpa.benchmark.insert.BenchmarkClassLoader.Type;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * @author hceylan
 * 
 * @since $version
 */
public class Playground {

	private static final String PU_NAME = "insert";

	/**
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@BeforeClass
	public static void boot() {
		try {
			DriverManager.getConnection("jdbc:derby:memory:testDB;create=true");
		}
		catch (final SQLException e) {}
	}

	private Country country;

	private TimeElement element;
	private final HashMap<String, TimeElement> elements = Maps.newHashMap();
	private boolean running;
	private long oldTime;

	/**
	 * @param id
	 * 
	 * @since $version
	 * @author hceylan
	 */
	protected void _measure(final long id) {
		this.element = new TimeElement("");

		final ThreadMXBean mxBean = ManagementFactory.getThreadMXBean();
		this.oldTime = mxBean.getThreadCpuTime(id);

		final ExecutorService pool = Executors.newFixedThreadPool(1);

		pool.submit(new Runnable() {

			@Override
			public void run() {
				while (Playground.this.running) {
					Playground.this._measureSingleTime(id, mxBean);
					try {
						Thread.sleep(0, 10);
					}
					catch (final InterruptedException e) {}
				}
			}
		});

		pool.shutdownNow();
	}

	/**
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@After
	public void _measureAfter() {
		this.running = false;

		this.element.dump(0, 0);

		System.out.println("\n");

		int rowNo = 0;
		final ArrayList<TimeElement> elements = Lists.newArrayList(this.elements.values());
		Collections.sort(elements, new Comparator<TimeElement>() {

			@Override
			public int compare(TimeElement o1, TimeElement o2) {
				return o1.getSelf().compareTo(o2.getSelf());
			}
		});

		for (final TimeElement element : elements) {
			rowNo++;
			element.dump2(rowNo);
		}

		System.out.println("\n");
	}

	/**
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Before
	public void _measureBefore() {
		final long id = Thread.currentThread().getId();
		new Thread(new Runnable() {

			@Override
			public void run() {
				Playground.this.running = true;
				Playground.this._measure(id);
			}
		}).start();
	}

	private void _measureSingleTime(long id, final ThreadMXBean mxBean) {
		final ThreadInfo threadInfo = mxBean.getThreadInfo(id, Integer.MAX_VALUE);

		final long newTime = mxBean.getThreadCpuTime(id);
		if (this.oldTime == 0) {
			this.oldTime = newTime;
		}
		else {
			TimeElement child = this.element;
			boolean gotStart = false;
			boolean last = false;

			boolean inDerby = false;
			for (int i = threadInfo.getStackTrace().length - 1; i >= 0; i--) {
				final StackTraceElement stElement = threadInfo.getStackTrace()[i];
				if (stElement.getClassName().startsWith("org.apache.derby")) {
					inDerby = true;
					break;
				}
			}

			for (int i = threadInfo.getStackTrace().length - 1; i >= 0; i--) {
				final StackTraceElement stElement = threadInfo.getStackTrace()[i];

				if (!gotStart && !stElement.getMethodName().startsWith("singleTest")) {
					continue;
				}

				gotStart = true;

				final String key = stElement.getClassName() + "." + stElement.getMethodName() + "." + stElement.getLineNumber();
				child = child.get(key);
				TimeElement child2 = this.elements.get(key);
				if (child2 == null) {
					this.elements.put(key, child2 = new TimeElement(key));
				}

				if (stElement.getClassName().startsWith("org.apache.derby") || (i == 0)) {
					child.addTime(newTime - this.oldTime, true, inDerby);
					child2.addTime(newTime - this.oldTime, true, inDerby);
					last = true;
				}
				else {
					child.addTime(newTime - this.oldTime, false, inDerby);
					child2.addTime(newTime - this.oldTime, false, inDerby);
				}

				if (last) {
					break;
				}
			}

		}

		this.oldTime = newTime;
	}

	@SuppressWarnings("unchecked")
	private List<Person>[] createPersons() {
		final List<Person>[] persons = new List[10];

		for (int i = 0; i < 10; i++) {
			persons[i] = Lists.newArrayList();

			for (int j = 0; j < 10; j++) {
				final Person person = new Person();

				person.setName("Hasan");

				final Address address = new Address();
				address.setCity("Istanbul");
				address.setPerson(person);
				address.setCountry(this.country);
				person.getAddresses().add(address);

				final Address address2 = new Address();
				address2.setCity("Istanbul");
				address2.setPerson(person);
				address2.setCountry(this.country);
				person.getAddresses().add(address2);

				final Phone phone = new Phone();
				phone.setPhoneNo("111 222-3344");
				phone.setPerson(person);
				person.getPhones().add(phone);

				final Phone phone2 = new Phone();
				phone2.setPhoneNo("111 222-3344");
				phone2.setPerson(person);
				person.getPhones().add(phone2);

				persons[i].add(person);
			}
		}

		return persons;
	}

	private void dobatoo() {
		this.doTest(Type.BATOO);
	}

	/**
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void doBatoo() {
		this.dobatoo();
	}

	private void doCriteria(final EntityManagerFactory emf, final Person person, CriteriaQuery<Address> cq, ParameterExpression<Person> p) {
		for (int i = 1; i < 25; i++) {
			final EntityManager em = emf.createEntityManager();

			final TypedQuery<Address> q = em.createQuery(cq);
			q.setParameter(p, person);
			q.getResultList();

			em.close();
		}
	}

	private void doFind(final EntityManagerFactory emf, final Person person) {
		for (int i = 0; i < 250; i++) {
			final EntityManager em = emf.createEntityManager();

			final Person person2 = em.find(Person.class, person.getId());
			person2.getPhones().size();
			em.close();
		}
	}

	/**
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void dohibernate() {
		this.doTest(Type.HIBERNATE);
	}

	private void doJpql(final EntityManagerFactory emf, final Person person) {
		for (int i = 0; i < 25; i++) {
			final EntityManager em = emf.createEntityManager();

			emf.getCriteriaBuilder();
			final TypedQuery<Address> q = em.createQuery(
				"select a from Person p inner join p.addresses a left join fetch a.country left join fetch a.person where p = :person", Address.class);

			q.setParameter("person", person);
			q.getResultList();

			em.close();
		}
	}

	private void doPersist(final EntityManagerFactory emf, List<Person>[] persons) {
		for (final List<Person> list : persons) {
			final EntityManager em = emf.createEntityManager();

			final EntityTransaction tx = em.getTransaction();

			tx.begin();

			for (final Person person : list) {
				em.persist(person);
			}

			tx.commit();

			em.close();
		}
	}

	private void doRemove(final EntityManagerFactory emf, final List<Person>[] persons) {
		final EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();

		for (int i = 0; i < 5; i++) {
			for (final Person person : persons[i]) {
				final Person person2 = em.find(Person.class, person.getId());
				em.remove(person2);
			}
		}

		em.getTransaction().commit();
		em.close();
	}

	private void doTest(Type type) {
		final ClassLoader old = Thread.currentThread().getContextClassLoader();
		try {
			Thread.currentThread().setContextClassLoader(new BenchmarkClassLoader(old, type, Playground.PU_NAME));
			System.currentTimeMillis();

			final EntityManagerFactory emf = Persistence.createEntityManagerFactory(Playground.PU_NAME);

			final EntityManager em = emf.createEntityManager();
			this.country = new Country();

			this.country.setName("Turkey");
			em.getTransaction().begin();
			em.persist(this.country);
			em.getTransaction().commit();

			em.close();

			System.currentTimeMillis();

			this.test(type, emf);

		}
		finally {
			Thread.currentThread().setContextClassLoader(old);
		}
	}

	private void doUpdate(final EntityManagerFactory emf, final Person person) {
		for (int i = 0; i < 100; i++) {
			final EntityManager em = emf.createEntityManager();

			final Person person2 = em.find(Person.class, person.getId());

			final EntityTransaction tx = em.getTransaction();
			tx.begin();
			person2.setName("Ceylan" + i);
			tx.commit();

			em.close();
		}
	}

	private void singleTest(final EntityManagerFactory emf, List<Person>[] persons, CriteriaQuery<Address> cq, ParameterExpression<Person> p) {
		this.doPersist(emf, persons);

		this.doFind(emf, persons[0].get(0));

		this.doUpdate(emf, persons[0].get(0));

		this.doCriteria(emf, persons[0].get(0), cq, p);

		this.doJpql(emf, persons[0].get(0));

		this.doRemove(emf, persons);
	}

	private void test(Type type, final EntityManagerFactory emf) {
		final CriteriaBuilder cb = emf.getCriteriaBuilder();
		final CriteriaQuery<Address> cq = cb.createQuery(Address.class);

		final Root<Person> r = cq.from(Person.class);
		final Join<Person, Address> a = r.join("addresses");
		a.fetch("country", JoinType.LEFT);
		a.fetch("person", JoinType.LEFT);
		cq.select(a);

		final ParameterExpression<Person> p = cb.parameter(Person.class);
		cq.where(cb.equal(r, p));

		while (!this.running) {
			try {
				Thread.sleep(1);
			}
			catch (final InterruptedException e) {}
		}

		for (int i = 0; i < 1000; i++) {
			this.singleTest(emf, this.createPersons(), cq, p);
		}
	}
}
