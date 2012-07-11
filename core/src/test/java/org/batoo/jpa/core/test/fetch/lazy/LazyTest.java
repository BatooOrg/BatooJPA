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
package org.batoo.jpa.core.test.fetch.lazy;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;

import junit.framework.Assert;

import org.batoo.jpa.core.test.BaseCoreTest;
import org.junit.Before;
import org.junit.Test;

/**
 * @author hceylan
 * 
 * @since $version
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
	 * @since $version
	 * @author hceylan
	 */
	@Before
	public void prepareCountries() {
		this.persist(LazyTest.TR);
		this.persist(LazyTest.USA);
		this.persist(LazyTest.UK);
	}

	/**
	 * Tests to {@link EntityManager#find(Class, Object)} person.
	 * 
	 * @since $version
	 * @author hceylan
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
	 * Tests to {@link EntityManager#find(Class, Object)} person.
	 * 
	 * @since $version
	 * @author hceylan
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
	 * @since $version
	 * @author hceylan
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
	 * @since $version
	 * @author hceylan
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
	 * Tests to {@link EntityManager#persist(Object)} address which does not cascade to Parent. PersistenceException expected.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test(expected = PersistenceException.class)
	public void testPersistAddress() {
		this.persist(this.person().getAddresses().get(0));

		this.commit();
	}

	/**
	 * Tests to {@link EntityManager#persist(Object)} Parent which cascades to Child1.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testPersistPerson() {
		Assert.assertEquals(5, this.em().getMetamodel().getEntities().size());

		this.persist(this.person());

		this.commit();
	}
}
