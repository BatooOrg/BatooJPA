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

import java.sql.DriverManager;
import java.sql.SQLException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.batoo.jpa.benchmark.insert.BenchmarkClassLoader.Type;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author hceylan
 * 
 * @since $version
 */
public class HowItIsDone {

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

	/**
	 * The actual test.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void doTest() {
		final ClassLoader old = Thread.currentThread().getContextClassLoader();
		try {
			Thread.currentThread().setContextClassLoader(new BenchmarkClassLoader(old, Type.HIBERNATE, HowItIsDone.PU_NAME));

			Persistence.createEntityManagerFactory(HowItIsDone.PU_NAME);

			this.testImpl();
		}
		finally {
			Thread.currentThread().setContextClassLoader(old);
		}
	}

	private void testImpl() {
		final EntityManagerFactory emf = Persistence.createEntityManagerFactory(HowItIsDone.PU_NAME);

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
