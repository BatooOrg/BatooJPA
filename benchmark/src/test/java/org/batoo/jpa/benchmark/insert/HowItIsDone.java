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
public class HowItIsDone {

	private static final BLogger LOG = BLogger.getLogger(HowItIsDone.class);

	private static final String PU_NAME = "insert";

	@BeforeClass
	public static void boot() throws SQLException, InterruptedException {
		DriverManager.getConnection("jdbc:derby:memory:testDB;create=true");
	}

	private Country country;

	private Person createPerson() {
		final Person person = new Person();

		person.setName("Hasan Ceylan");

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

		return person;
	}

	@Test
	public void doTest() {
		final ClassLoader old = Thread.currentThread().getContextClassLoader();
		try {
			Thread.currentThread().setContextClassLoader(new BenchmarkClassLoader(old, Type.HIBERNATE, PU_NAME));

			Persistence.createEntityManagerFactory(PU_NAME);

			this.testImpl();
		}
		finally {
			Thread.currentThread().setContextClassLoader(old);
		}
	}

	private void testImpl() {
		final EntityManagerFactory emf = Persistence.createEntityManagerFactory(PU_NAME);

		EntityManager em = emf.createEntityManager();

		final EntityTransaction tx = em.getTransaction();

		tx.begin();

		Person person = this.createPerson();
		em.persist(person);
		tx.commit();

		em.close();

		em = emf.createEntityManager();
		person = em.find(Person.class, person.getId());

		em.refresh(person);
	}

}
