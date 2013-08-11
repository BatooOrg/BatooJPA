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

package org.batoo.jpa.core.test.fetch.lazy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;

import junit.framework.Assert;

import org.batoo.jpa.core.test.BaseCoreTest;
import org.junit.Before;
import org.junit.Test;

/**
 * @author hceylan
 * 
 * @since 2.0.0
 */
public class LazyTest extends BaseCoreTest {

	private static final String COUNTRY_UK = "UK";
	private static final String COUNTRY_USA = "USA";
	private static final String COUNTRY_TURKEY = "Turkey";

	private static final String CITY_LONDON = "London";
	private static final String CITY_NEW_YORK = "New York";
	private static final String CITY_ISTANBUL = "Istanbul";

	private static Country TR = new Country(1, LazyTest.COUNTRY_TURKEY);
	private static Country USA = new Country(2, LazyTest.COUNTRY_USA);
	private static Country UK = new Country(3, LazyTest.COUNTRY_UK);

	private Person person() {
		final Person person = new Person("Ceylan");

		new Address(person, LazyTest.CITY_ISTANBUL, LazyTest.TR);
		new Address(person, LazyTest.CITY_NEW_YORK, LazyTest.USA);
		new Address(person, LazyTest.CITY_LONDON, LazyTest.UK);

		new HomePhone(person, "111 1111111");
		new HomePhone(person, "222 2222222");

		new WorkPhone(person, "333 3333333");
		new WorkPhone(person, "444 4444444");

		return person;
	}

	/**
	 * 
	 * @since 2.0.0
	 */
	@Before
	public void prepareCountries() {
		this.persist(LazyTest.TR);
		this.persist(LazyTest.USA);
		this.persist(LazyTest.UK);
	}

	private Person serializePerson(Person person) throws IOException, ClassNotFoundException {
		final ByteArrayOutputStream s = new ByteArrayOutputStream();
		// serialize
		final ObjectOutputStream os = new ObjectOutputStream(s);
		try {
			os.writeObject(person);
		}
		finally {
			os.close();
		}

		// unserialize
		final ObjectInputStream is = new ObjectInputStream(new ByteArrayInputStream(s.toByteArray()));

		final Person person2 = (Person) is.readObject();
		return person2;
	}

	/**
	 * Tests to {@link EntityManager#find(Class, Object)} person.
	 * 
	 * @since 2.0.0
	 */
	@Test
	public void testFind() {
		final Person person = this.person();
		this.persist(person);

		this.commit();
		this.close();

		final Person person2 = this.find(Person.class, person.getId());
		Assert.assertEquals(person.getName(), person2.getName());
		Assert.assertEquals(person.getAddresses().size(), person2.getAddresses().size());

		for (final Address address : person2.getAddresses()) {
			if (LazyTest.CITY_ISTANBUL.equals(address.getCity())) {
				Assert.assertEquals(LazyTest.TR.getId(), address.getCountry().getId());
				Assert.assertEquals(LazyTest.COUNTRY_TURKEY, address.getCountry().getName());
			}
		}
	}

	/**
	 * Tests to {@link EntityManager#persist(Object)} load address then person as lazy.
	 * 
	 * @since 2.0.0
	 */
	@Test
	public void testFindAddress() {
		final Person person = this.person();
		this.persist(person);

		this.commit();
		this.close();

		final Address address = this.find(Address.class, person.getAddresses().get(0).getId());
		Assert.assertEquals("Ceylan", address.getPerson().getName());
	}

	/**
	 * Tests to {@link EntityManager#find(Class, Object)} person.
	 * 
	 * @since 2.0.0
	 */
	@Test
	public void testFindInSession() {
		final Person person = this.person();
		this.persist(person);

		this.commit();

		final Person person2 = this.find(Person.class, person.getId());
		Assert.assertEquals(person.getName(), person2.getName());
		Assert.assertEquals(person.getAddresses().size(), person2.getAddresses().size());

		for (final Address address : person2.getAddresses()) {
			if (LazyTest.CITY_ISTANBUL.equals(address.getCity())) {
				Assert.assertEquals(address.getCountry().getId(), LazyTest.TR.getId());
				Assert.assertEquals(address.getCountry().getName(), LazyTest.COUNTRY_TURKEY);
				break;
			}
		}

		this.close();
	}

	/**
	 * Tests to {@link EntityManager#find(Class, Object)} person with phones as lazy many type association.
	 * 
	 * @since 2.0.0
	 */
	@Test
	public void testFindMany() {
		final Person person = this.person();
		this.persist(person);

		this.commit();
		this.close();

		final Person person2 = this.find(Person.class, person.getId());
		Assert.assertEquals(person.getName(), person2.getName());
		Assert.assertEquals(2, person2.getWorkPhones().size());
	}

	/**
	 * Tests to {@link EntityManager#find(Class, Object)} person with lazy OneToMany association.
	 * 
	 * @since 2.0.0
	 */
	@Test
	public void testFindOneToMany() {
		final Person person = this.person();
		this.persist(person);

		this.commit();
		this.close();

		final Person person2 = this.find(Person.class, person.getId());
		Assert.assertEquals(person.getName(), person2.getName());
		Assert.assertEquals(person.getWorkPhones().size(), person2.getWorkPhones().size());

		for (final Phone phone : person2.getWorkPhones()) {
			if ("333 3333333".equals(phone.getPhoneNo())) {
				return;
			}
		}

		Assert.fail("Phone 333 3333333 not found");
	}

	/**
	 * Tests no lazy exception when lazy collection is initialized
	 * 
	 * @throws Exception
	 *             in case of an error
	 * @since 2.0.0
	 */
	@Test(expected = PersistenceException.class)
	public void testLazyCollection() throws Exception {
		final Person person = this.person();
		this.persist(person);

		this.commit();
		this.close();

		Person person2 = this.find(Person.class, person.getId());

		this.em().close();

		Assert.assertEquals(person.getPhones().size(), person2.getPhones().size());

		this.close();

		person2 = this.find(Person.class, person.getId());
		person2 = this.serializePerson(person2);

		person2.getWorkPhones().size();
	}

	/**
	 * Tests to {@link EntityManager#persist(Object)} address which does not cascade to Parent. PersistenceException expected.
	 * 
	 * @since 2.0.0
	 */
	@Test(expected = PersistenceException.class)
	public void testPersistAddress() {
		this.persist(this.person().getAddresses().get(0));

		this.commit();
	}

	/**
	 * Tests to {@link EntityManager#persist(Object)} Parent which cascades to Child1.
	 * 
	 * @since 2.0.0
	 */
	@Test
	public void testPersistPerson() {
		Assert.assertEquals(5, this.em().getMetamodel().getEntities().size());

		this.persist(this.person());

		this.commit();
	}

	/**
	 * Tests to {@link EntityManager#persist(Object)} Parent which cascades to Child1.
	 * 
	 * @throws Exception
	 *             in case of an error
	 * @since 2.0.0
	 */
	@Test
	public void testSerializePerson() throws Exception {
		Person person = this.person();
		this.persist(person);

		this.commit();
		this.close();

		person = this.find(Person.class, person.getId());

		final Person person2 = this.serializePerson(person);

		Assert.assertEquals(person.getId(), person2.getId());
		Assert.assertEquals(person.getAddresses().size(), person2.getAddresses().size());
		Assert.assertTrue(this.emf().getPersistenceUnitUtil().isLoaded(person2, "addresses"));
		Assert.assertFalse(this.emf().getPersistenceUnitUtil().isLoaded(person2, "phones"));
	}
}
