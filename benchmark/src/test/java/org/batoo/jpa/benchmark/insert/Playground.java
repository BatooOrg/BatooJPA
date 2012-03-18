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

import java.sql.DriverManager;
import java.sql.SQLException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.batoo.jpa.benchmark.insert.BenchmarkClassLoader.Type;
import org.batoo.jpa.core.BLogger;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author hceylan
 * 
 * @since $version
 */
public class Playground {

	private static final BLogger LOG = BLogger.getLogger(Playground.class);

	private static final String PU_NAME = "insert";

	@BeforeClass
	public static void boot() throws SQLException {
		DriverManager.getConnection("jdbc:derby:memory:testDB;create=true");
	}

	private Person createPerson() {
		final Person person = new Person();

		person.setName("Hasan Ceylan");

		final Address address = new Address();
		address.setCity("Istanbul");
		address.setPerson(person);

		final Country country = new Country();
		country.setName("Turkey");
		address.setCountry(country);

		person.getAddresses().add(address);

		return person;
	}

	private void dobatoo() {
		this.doTest(Type.BATOO);
	}

	private void dofind(final EntityManagerFactory emf, final Person person) {
		final EntityManager em = emf.createEntityManager();

		Person person2 = em.find(Person.class, person.getId());

		em.close();
	}

	private void dohibernate() {
		this.doTest(Type.HIBERNATE);
	}

	private Person dopersist(final EntityManagerFactory emf) {
		final EntityManager em = emf.createEntityManager();
		final EntityTransaction tx = em.getTransaction();

		tx.begin();

		final Person person = this.createPerson();
		em.persist(person);

		tx.commit();
		em.close();

		return person;
	}

	private void doTest(Type type) {
		final ClassLoader old = Thread.currentThread().getContextClassLoader();
		try {
			Thread.currentThread().setContextClassLoader(new BenchmarkClassLoader(old, type, PU_NAME));
			long start = System.currentTimeMillis();

			final EntityManagerFactory emf = Persistence.createEntityManagerFactory(PU_NAME);
			LOG.info("{0} - deploy {1}", type, System.currentTimeMillis() - start);

			start = System.currentTimeMillis();

			this.test(type, emf);
		}
		finally {
			Thread.currentThread().setContextClassLoader(old);
		}
	}

	@Test
	public void insertALotInOneSession() throws InterruptedException {
		this.dohibernate();

		this.dobatoo();
	}

	private void singleTest(final EntityManagerFactory emf) {
		final Person person = this.dopersist(emf);

		this.dofind(emf, person);
	}

	private void test(Type type, final EntityManagerFactory emf) {
		final long start = System.currentTimeMillis();

		for (int i = 0; i < 100000; i++) {
			this.singleTest(emf);
		}

		LOG.info("{0} - execute {1}", type, System.currentTimeMillis() - start);
	}
}
