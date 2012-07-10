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
package org.batoo.jpa.core.test.refresh.simple;

import java.util.Iterator;

import javax.persistence.EntityManager;

import junit.framework.Assert;

import org.batoo.jpa.core.test.BaseCoreTest;
import org.junit.Before;
import org.junit.Test;

/**
 * @author hceylan
 * 
 * @since $version
 */
public class RefreshTest extends BaseCoreTest {

	private static final String COUNTRY_UK = "UK";
	private static final String COUNTRY_USA = "USA";
	private static final String COUNTRY_GERMANY = "Germany";
	private static final String COUNTRY_TURKEY = "Turkey";

	private static final String CITY_LONDON = "London";
	private static final String CITY_NEW_YORK = "New York";
	private static final String CITY_ISTANBUL = "Istanbul";

	private static Country TR = new Country(1, RefreshTest.COUNTRY_TURKEY);
	private static Country USA = new Country(2, RefreshTest.COUNTRY_USA);
	private static Country UK = new Country(3, RefreshTest.COUNTRY_UK);

	private Person person() {
		final Person person = new Person("Ceylan");

		new Address(person, RefreshTest.CITY_ISTANBUL, RefreshTest.TR);
		new Address(person, RefreshTest.CITY_NEW_YORK, RefreshTest.USA);
		new Address(person, RefreshTest.CITY_LONDON, RefreshTest.UK);

		new Phone(person, "111 1111111");
		new Phone(person, "222 2222222");

		return person;
	}

	/**
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Before
	public void prepareCountries() {
		this.persist(RefreshTest.TR);
		this.persist(RefreshTest.USA);
		this.persist(RefreshTest.UK);
	}

	/**
	 * Tests to {@link EntityManager#find(Class, Object)} person.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testRefresh() {
		Person person = this.person();
		this.persist(person);

		this.commit();
		this.close();

		person = this.find(Person.class, person.getId());

		Address address = null;
		Country country = null;
		for (final Iterator<Address> i = person.getAddresses().iterator(); i.hasNext();) {
			address = i.next();
			if (RefreshTest.CITY_ISTANBUL.equals(address.getCity())) {
				address.setCity("SomeCity");
				country = address.getCountry();
				country.setName(RefreshTest.COUNTRY_GERMANY);
				address.setCountry(null);
				i.remove();
				break;
			}
		}

		person.setName("Ceylan2");

		this.refresh(person);

		Assert.assertEquals("Ceylan", person.getName());
		Assert.assertEquals(RefreshTest.CITY_ISTANBUL, address.getCity());
		Assert.assertEquals(country, address.getCountry());
		Assert.assertEquals(RefreshTest.COUNTRY_GERMANY, country.getName());
		Assert.assertEquals(3, person.getAddresses().size());
	}
}
