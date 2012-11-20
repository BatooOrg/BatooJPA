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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

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

import org.apache.commons.lang.mutable.MutableLong;
import org.batoo.common.log.BLogger;
import org.batoo.common.log.BLoggerFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * @author hceylan
 * @since 2.0.0
 */
public class BenchmarkTest {

	/**
	 * The number of tests to run
	 */
	private static final int BENCHMARK_LENGTH = 1000;

	/**
	 * If the results should be summarized
	 */
	private static final boolean SUMMARIZE = true;

	/**
	 * the number of worker thread to simulate concurrency
	 */
	private static final int THREAD_COUNT = Runtime.getRuntime().availableProcessors() * 2;

	private static final BLogger LOG = BLoggerFactory.getLogger(BenchmarkTest.class);

	private static final boolean FULL_SUMMARY = System.getProperties().get("fullSummary") != null;
	private static final String SEPARATOR = "________________________________________";

	private Country country;
	private TimeElement element;
	private final HashMap<String, TimeElement> elements = Maps.newHashMap();
	private boolean running;

	private Type type;

	private long[] currentThreadTimes;
	private long[] threadIds;

	private void close(final EntityManager em) {
		em.getTransaction().commit();

		em.close();
	}

	private ExecutorService createExecutor(BlockingQueue<Runnable> workQueue) {
		final AtomicInteger nextThreadNo = new AtomicInteger(0);

		final ThreadPoolExecutor pool = new ThreadPoolExecutor(//
			BenchmarkTest.THREAD_COUNT, BenchmarkTest.THREAD_COUNT, // min max threads
			0L, TimeUnit.MILLISECONDS, // the keep alive time - hold it forever
			workQueue, new ThreadFactory() {

				@Override
				public Thread newThread(Runnable r) {
					final Thread t = new Thread(r);
					t.setDaemon(true);
					t.setPriority(Thread.NORM_PRIORITY);

					BenchmarkTest.this.threadIds[nextThreadNo.getAndIncrement()] = t.getId();

					return t;
				}
			});

		pool.prestartAllCoreThreads();

		return pool;
	}

	private Person[][] createPersons() {
		final Person[][] persons = new Person[10][10];

		for (int i = 0; i < 10; i++) {
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

				final Phone phone3 = new Phone();
				phone3.setPhoneNo("111 222-3344");
				phone3.setPerson(person);
				person.getPhones().add(phone3);

				persons[i][j] = person;
			}
		}

		return persons;
	}

	/**
	 * 
	 * @since 2.0.0
	 */
	@Test
	public void dobatoo() {
		this.doTest(Type.BATOO);
	}

	private void doBenchmarkCriteria(final EntityManagerFactory emf, Person[][] people, CriteriaQuery<Address> cq, ParameterExpression<Person> p) {
		for (final Person[] persons : people) {
			for (final Person person : persons) {
				final EntityManager em = this.open(emf);

				final TypedQuery<Address> q = em.createQuery(cq);
				q.setParameter(p, person);
				q.getResultList();

				this.close(em);
			}
		}
	}

	private void doBenchmarkFind(final EntityManagerFactory emf, Person[][] people) {
		for (final Person[] persons : people) {
			for (final Person person : persons) {
				final EntityManager em = this.open(emf);

				final Person person2 = em.find(Person.class, person.getId());
				person2.getPhones().size();

				this.close(em);
			}
		}
	}

	private void doBenchmarkJpql(final EntityManagerFactory emf, final Person[][] people) {
		for (final Person[] persons : people) {
			for (final Person person : persons) {
				final EntityManager em = this.open(emf);

				final TypedQuery<Address> q = em.createQuery(
					"select a from Person p inner join p.addresses a left join fetch a.country left join fetch a.person where p = :person", Address.class);

				q.setParameter("person", person);
				q.getResultList();

				this.close(em);
			}
		}
	}

	private void doBenchmarkPersist(final EntityManagerFactory emf, Person[][] allPersons) {
		for (final Person[] persons : allPersons) {
			for (final Person person : persons) {
				final EntityManager em = this.open(emf);

				em.persist(person);

				this.close(em);
			}
		}
	}

	private void doBenchmarkRemove(final EntityManager em, final Person person) {
		em.remove(person);

		this.close(em);
	}

	private void doBenchmarkUpdate(final EntityManager em, final Person person2) {
		person2.setName("Hasan Ceylan");

		this.close(em);
	}

	/**
	 * 
	 * @since 2.0.0
	 */
	@Test
	public void doeclipselink() {
		this.doTest(Type.ELINK);
	}

	/**
	 * 
	 * @since 2.0.0
	 */
	@Test
	public void dohibernate() {
		this.doTest(Type.HBRNT);
	}

	private void doRemove(final EntityManagerFactory emf, Person[][] people) {
		for (int i = 0; i < (people.length / 2); i++) {
			for (Person person : people[i]) {
				final EntityManager em = this.open(emf);

				person = em.find(Person.class, person.getId());

				this.doBenchmarkRemove(em, person);
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

		this.threadIds = new long[BenchmarkTest.THREAD_COUNT];
		this.currentThreadTimes = new long[BenchmarkTest.THREAD_COUNT];

		LinkedBlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<Runnable>();
		ExecutorService pool = this.createExecutor(workQueue);
		try {
			// warm mup
			this.test(type, emf, workQueue, BenchmarkTest.BENCHMARK_LENGTH / 5);
		}
		finally {
			try {
				pool.shutdown();
				pool.awaitTermination(60, TimeUnit.MINUTES);
			}
			catch (final InterruptedException e) {
				throw new RuntimeException(e);
			}
		}

		workQueue = new LinkedBlockingQueue<Runnable>();
		pool = this.createExecutor(workQueue);
		try {
			// for real
			this.test(type, emf, workQueue, BenchmarkTest.BENCHMARK_LENGTH);

			this.setRunning(true);
		}
		finally {
			pool.shutdown();
			try {
				pool.awaitTermination(60, TimeUnit.MINUTES);
				this.setRunning(false);
			}
			catch (final Exception e) {}
		}

		emf.close();
	}

	private void doUpdate(final EntityManagerFactory emf, final Person[][] people) {
		for (final Person[] persons : people) {
			for (final Person person : persons) {
				final EntityManager em = this.open(emf);

				this.doBenchmarkUpdate(em, em.find(Person.class, person.getId()));
			}
		}
	}

	private synchronized boolean isRunning() {
		return this.running;
	}

	/**
	 * 
	 * @since 2.0.0
	 */
	@After
	public void measureAfter() {
		if (BenchmarkTest.SUMMARIZE) {
			final MutableLong dbTotalTime = new MutableLong(0);
			final MutableLong jpaTotalTime = new MutableLong(0);
			this.element.dump0(this.type, dbTotalTime, jpaTotalTime, BenchmarkTest.FULL_SUMMARY);

			if (!BenchmarkTest.FULL_SUMMARY) {
				System.out.println(BenchmarkTest.SEPARATOR);
				System.out.println(//
				"TOTAL " + this.type.name() + //
					" \t" + String.format("%08d", dbTotalTime.longValue()) + //
					" \t" + String.format("%08d", jpaTotalTime.longValue()));
				System.out.println(BenchmarkTest.SEPARATOR);
				System.out.println();
			}
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
	 * @since 2.0.0
	 */
	@Before
	public void measureBefore() {
		if (BenchmarkTest.SUMMARIZE && !BenchmarkTest.FULL_SUMMARY) {
			System.out.println(BenchmarkTest.SEPARATOR);
			System.out.println("Test Name\tDB Time \tJPA Time   ");
			System.out.println(BenchmarkTest.SEPARATOR);
		}

		final Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				BenchmarkTest.this.measureTime();
			}
		});

		t.setDaemon(false);
		t.start();
	}

	/**
	 * @since 2.0.0
	 */
	protected void measureTime() {
		try {
			this.element = new TimeElement("");

			// get the MXBean
			final ThreadMXBean mxBean = ManagementFactory.getThreadMXBean();

			// wait till the warm up period is over
			while (!BenchmarkTest.this.isRunning()) {
				try {
					Thread.sleep(1);
				}
				catch (final InterruptedException e) {}
			}

			try {
				Thread.sleep(100);
			}
			catch (final InterruptedException e1) {}

			// initialize the start times of the threads
			for (int i = 0; i < this.threadIds.length; i++) {
				this.currentThreadTimes[i] = mxBean.getThreadCpuTime(this.threadIds[i]);
			}

			// profile until the benchmark is over
			while (BenchmarkTest.this.isRunning()) {
				try {
					this.measureTimes(mxBean);
					Thread.sleep(0, 10);
				}
				catch (final InterruptedException e) {}
			}
		}
		catch (final Exception e) {
			BenchmarkTest.LOG.fatal(e, "");
		}
	}

	/**
	 * @param oldTime
	 *            the oldTime
	 * @param threadInfo
	 *            the theread info
	 * @param newTime
	 *            the new time
	 * @return
	 * 
	 * @since $version
	 */
	private long measureTime(long oldTime, ThreadInfo threadInfo, long newTime) {
		TimeElement child = this.element;
		boolean gotStart = false;
		boolean last = false;

		boolean inDb = false;
		if (threadInfo == null) {
			return newTime;
		}

		for (int i = threadInfo.getStackTrace().length - 1; i >= 0; i--) {
			final StackTraceElement stElement = threadInfo.getStackTrace()[i];
			if (stElement.getClassName().startsWith("org.apache.derby") || stElement.getClassName().startsWith("com.mysql")
				|| stElement.getClassName().startsWith("org.h2") || stElement.getClassName().startsWith("org.hsqldb.")) {
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

			final long timeDiff = newTime - oldTime;
			if (timeDiff < 0) {
				BenchmarkTest.LOG.info("How about that");
			}
			if (stElement.getClassName().startsWith("org.apache.derby") || stElement.getClassName().startsWith("com.mysql") || (i == 0)) {
				child.addTime(timeDiff, true, inDb);
				child2.addTime(timeDiff, true, inDb);
				last = true;
			}
			else {
				child.addTime(timeDiff, false, inDb);
				child2.addTime(timeDiff, false, inDb);
			}

			if (last) {
				break;
			}
		}

		return newTime;
	}

	private void measureTimes(final ThreadMXBean mxBean) {
		final ThreadInfo[] threadInfos = mxBean.getThreadInfo(this.threadIds, Integer.MAX_VALUE);

		for (int ii = 0; ii < this.threadIds.length; ii++) {
			final long id = this.threadIds[ii];
			final ThreadInfo threadInfo = threadInfos[ii];

			this.currentThreadTimes[ii] = this.measureTime(this.currentThreadTimes[ii], threadInfo, mxBean.getThreadCpuTime(id));
		}
	}

	private EntityManager open(final EntityManagerFactory emf) {
		final EntityManager em = emf.createEntityManager();

		em.getTransaction().begin();

		return em;
	}

	private synchronized void setRunning(boolean running) {
		this.running = running;
	}

	private void singleTest(final EntityManagerFactory emf, Person[][] persons, CriteriaQuery<Address> cq, ParameterExpression<Person> p) {
		this.doBenchmarkPersist(emf, persons);

		this.doBenchmarkFind(emf, persons);

		this.doUpdate(emf, persons);

		this.doBenchmarkCriteria(emf, persons, cq, p);

		this.doBenchmarkJpql(emf, persons);

		this.doRemove(emf, persons);
	}

	private void test(Type type, final EntityManagerFactory emf, Queue<Runnable> workQueue, int length) {
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

		for (int i = 0; i < length; i++) {
			workQueue.add(new Runnable() {

				@Override
				public void run() {
					try {
						BenchmarkTest.this.singleTest(emf, BenchmarkTest.this.createPersons(), cq, p);
					}
					catch (final Exception e) {
						BenchmarkTest.LOG.error(e, "Error while running the test");
					}
				}
			});
		}
	}
}
