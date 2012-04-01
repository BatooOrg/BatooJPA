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

import org.batoo.jpa.core.test.AbstractTest;
import org.junit.Before;
import org.junit.Test;

/**
 * @author hceylan
 * 
 * @since $version
 */
public class LazyTest extends AbstractTest {

	private static final String COUNTRY_UK = "UK";
	private static final String COUNTRY_USA = "USA";
	private static final String COUNTRY_TURKEY = "Turkey";

	private static final String CITY_LONDON = "London";
	private static final String CITY_NEW_YORK = "New York";
	private static final String CITY_ISTANBUL = "Istanbul";

	private static Country TR = new Country(1, COUNTRY_TURKEY);
	private static Country USA = new Country(2, COUNTRY_USA);
	private static Country UK = new Country(3, COUNTRY_UK);

	private Person person() {
		final Person person = new Person("Ceylan");

		new Address(person, CITY_ISTANBUL, TR);
		new Address(person, CITY_NEW_YORK, USA);
		new Address(person, CITY_LONDON, UK);

		new Phone(person, "111 1111111");
		new Phone(person, "222 2222222");

		return person;
	}

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
			if (CITY_ISTANBUL.equals(address.getCity())) {
				Assert.assertEquals(TR.getId(), address.getCountry().getId());
				Assert.assertEquals(COUNTRY_TURKEY, address.getCountry().getName());
				break;
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
			if (CITY_ISTANBUL.equals(address.getCity())) {
				Assert.assertEquals(address.getCountry().getId(), TR.getId());
				Assert.assertEquals(address.getCountry().getName(), COUNTRY_TURKEY);
				break;
			}
		}

		this.close();
	}

	/**
	 * Tests to {@link EntityManager#persist(Object)} address which does not cascade to Person. PersistenceException expected.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testPersistAddress() {
		this.persist(this.person().getAddresses().get(0));

		try {
			this.commit();

			Assert.fail("No PersistenceException thrown");
		}
		catch (final PersistenceException e) {
			// expected
		}
	}

	/**
	 * Tests to {@link EntityManager#persist(Object)} Person which cascades to Address.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testPersistPerson() {
		Assert.assertEquals(4, this.em().getMetamodel().getEntities().size());

		this.persist(this.person());

		this.commit();
	}
}
