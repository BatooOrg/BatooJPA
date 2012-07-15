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

	private List<Person> createPersons() {
		final List<Person> persons = Lists.newArrayList();

		for (int i = 0; i < 10; i++) {
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

			persons.add(person);
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

	private void doFind(final EntityManagerFactory emf, final Person person) {
		for (int i = 0; i < 25; i++) {
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

	private void doPersist(final EntityManagerFactory emf, List<Person> persons) {
		final EntityManager em = emf.createEntityManager();

		final EntityTransaction tx = em.getTransaction();

		tx.begin();

		for (final Person person : persons) {
			em.persist(person);
		}

		tx.commit();

		em.close();
	}

	private void doRemove(final EntityManagerFactory emf, final List<Person> persons) {
		final EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();

		for (final Person person : persons) {

			final Person person2 = em.find(Person.class, person.getId());
			em.remove(person2);
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
		for (int i = 0; i < 25; i++) {
			final EntityManager em = emf.createEntityManager();

			final Person person2 = em.find(Person.class, person.getId());

			final EntityTransaction tx = em.getTransaction();
			tx.begin();
			person2.setName("Ceylan");
			tx.commit();

			em.close();
		}
	}

	private void singleTest(final EntityManagerFactory emf, List<Person> persons) {
		this.doPersist(emf, persons);

		this.doFind(emf, persons.get(0));

		this.doUpdate(emf, persons.get(0));

		this.doRemove(emf, persons.subList(5, 9));
	}

	private void test(Type type, final EntityManagerFactory emf) {
		while (!this.running) {
			try {
				Thread.sleep(1);
			}
			catch (final InterruptedException e) {}
		}

		for (int i = 0; i < 10000; i++) {
			this.singleTest(emf, this.createPersons());
		}
	}
}
