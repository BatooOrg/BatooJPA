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
package org.batoo.jpa.benchmark;

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
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Root;

import org.junit.After;
import org.junit.AfterClass;
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
public class BenchmarkTest {

	// The number of tests to run
	private static final int BENCHMARK_LENGTH = 1000;

	// If the results should be summarized
	private static final boolean SUMMARIZE = true;

	/**
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@BeforeClass
	public static void boot() {
		try {
			DriverManager.getConnection("jdbc:mysql://localhost/test");
		}
		catch (final SQLException e) {}

		if (BenchmarkTest.SUMMARIZE) {
			System.out.println("Single Entity Operations  ====================================");
			System.out.println("Prvdr | Total Time | JPA Time   | DB Time   | Name Of The Test");
		}
	}

	/**
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@AfterClass
	public static void finish() {
		if (BenchmarkTest.SUMMARIZE) {
			System.out.println("==============================================================");
		}
	}

	private Country country;
	private TimeElement element;
	private final HashMap<String, TimeElement> elements = Maps.newHashMap();
	private boolean running;

	private long oldTime;

	private Type type;

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
				while (BenchmarkTest.this.running) {
					BenchmarkTest.this._measureSingleTime(id, mxBean);
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

		if (BenchmarkTest.SUMMARIZE) {
			this.element.dump0(this.type);
		}
		else {
			this.element.dump1(0, 0);

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
				BenchmarkTest.this.running = true;
				BenchmarkTest.this._measure(id);
			}
		}).start();

		if (BenchmarkTest.SUMMARIZE) {
			System.out.println("______________________________________________________________");
		}
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

			boolean inDb = false;
			for (int i = threadInfo.getStackTrace().length - 1; i >= 0; i--) {
				final StackTraceElement stElement = threadInfo.getStackTrace()[i];
				if (stElement.getClassName().startsWith("org.apache.derby") || stElement.getClassName().startsWith("com.mysql")) {
					inDb = true;
					break;
				}
			}

			for (int i = threadInfo.getStackTrace().length - 1; i >= 0; i--) {
				final StackTraceElement stElement = threadInfo.getStackTrace()[i];

				if (!gotStart && !stElement.getMethodName().startsWith("singleTest")) {
					continue;
				}

				gotStart = true;

				final String key = BenchmarkTest.SUMMARIZE ? //
					stElement.getClassName() + "." + stElement.getMethodName() : //
					stElement.getClassName() + "." + stElement.getMethodName() + "." + stElement.getLineNumber();

				child = child.get(key);
				TimeElement child2 = this.elements.get(key);
				if (child2 == null) {
					this.elements.put(key, child2 = new TimeElement(key));
				}

				if (stElement.getClassName().startsWith("org.apache.derby") || stElement.getClassName().startsWith("com.mysql") || (i == 0)) {
					child.addTime(newTime - this.oldTime, true, inDb);
					child2.addTime(newTime - this.oldTime, true, inDb);
					last = true;
				}
				else {
					child.addTime(newTime - this.oldTime, false, inDb);
					child2.addTime(newTime - this.oldTime, false, inDb);
				}

				if (last) {
					break;
				}
			}

		}

		this.oldTime = newTime;
	}

	private void close(final EntityManager em) {
		em.getTransaction().commit();

		em.close();
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

	/**
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void dobatoo() {
		this.doTest(Type.BATOO);
	}

	private void doBenchmarkCriteria(final EntityManagerFactory emf, final Person person, CriteriaQuery<Address> cq, ParameterExpression<Person> p) {
		for (int i = 1; i < 250; i++) {
			final EntityManager em = this.open(emf);

			final TypedQuery<Address> q = em.createQuery(cq);
			q.setParameter(p, person);
			q.getResultList();

			this.close(em);
		}
	}

	private void doBenchmarkFind(final EntityManagerFactory emf, final Person person) {
		for (int i = 0; i < 250; i++) {
			final EntityManager em = this.open(emf);

			final Person person2 = em.find(Person.class, person.getId());
			person2.getPhones().size();

			this.close(em);
		}
	}

	private void doBenchmarkJpql(final EntityManagerFactory emf, final Person person) {
		for (int i = 0; i < 250; i++) {
			final EntityManager em = this.open(emf);

			final TypedQuery<Address> q = em.createQuery(
				"select a from Person p inner join p.addresses a left join fetch a.country left join fetch a.person where p = :person", Address.class);

			q.setParameter("person", person);
			q.getResultList();

			this.close(em);
		}
	}

	private void doBenchmarkPersist(final EntityManagerFactory emf, List<Person>[] persons) {
		for (final List<Person> list : persons) {
			for (int i = 0; i < list.size(); i++) {
				final EntityManager em = this.open(emf);

				em.persist(list.get(i));

				this.close(em);
			}
		}
	}

	private void doBenchmarkRemove(final EntityManager em, final Person person) {
		em.remove(person);

		this.close(em);
	}

	private void doBenchmarkUpdate(int i, final EntityManager em, final Person person2) {
		person2.setName("Ceylan" + i);

		this.close(em);
	}

	/**
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void doeclipselink() {
		try {
			this.doTest(Type.ELINK);
		}
		catch (final Exception e) {
			System.err.println("To benchmark EclipseLink, please uncomment EclipseLink dependency in pom.xml and \n"
				+ "deploy EclipseLink jar file to your local repository...");
		}
	}

	/**
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void dohibernate() {
		this.doTest(Type.HBRNT);
	}

	private void doRemove(final EntityManagerFactory emf, final List<Person>[] persons) {
		for (int i = 0; i < 5; i++) {
			for (final Person person : persons[i]) {
				final EntityManager em = this.open(emf);

				this.doBenchmarkRemove(em, em.find(Person.class, person.getId()));
			}
		}
	}

	private void doTest(Type type) {
		final EntityManagerFactory emf = Persistence.createEntityManagerFactory(type.name().toLowerCase());

		final EntityManager em = this.open(emf);
		this.country = new Country();

		this.country.setName("Turkey");
		em.persist(this.country);

		this.close(em);

		this.test(type, emf);
	}

	private void doUpdate(final EntityManagerFactory emf, final Person person) {
		for (int i = 0; i < 100; i++) {
			final EntityManager em = this.open(emf);

			this.doBenchmarkUpdate(i, em, em.find(Person.class, person.getId()));
		}
	}

	private EntityManager open(final EntityManagerFactory emf) {
		final EntityManager em = emf.createEntityManager();

		em.getTransaction().begin();

		return em;
	}

	private void singleTest(final EntityManagerFactory emf, List<Person>[] persons, CriteriaQuery<Address> cq, ParameterExpression<Person> p) {
		this.doBenchmarkPersist(emf, persons);

		this.doBenchmarkFind(emf, persons[0].get(0));

		this.doUpdate(emf, persons[0].get(0));

		this.doBenchmarkCriteria(emf, persons[0].get(0), cq, p);

		this.doBenchmarkJpql(emf, persons[0].get(0));

		this.doRemove(emf, persons);
	}

	private void test(Type type, final EntityManagerFactory emf) {
		this.type = type;

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

		for (int i = 0; i < BenchmarkTest.BENCHMARK_LENGTH; i++) {
			this.singleTest(emf, this.createPersons(), cq, p);
		}
	}
}
