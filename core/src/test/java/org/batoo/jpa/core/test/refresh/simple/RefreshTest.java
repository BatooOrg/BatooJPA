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

import junit.framework.Assert;

import org.batoo.jpa.core.test.AbstractTest;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author hceylan
 * 
 * @since $version
 */
@Ignore
public class RefreshTest extends AbstractTest {

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

		for (final Iterator<Address> i = person.getAddresses().iterator(); i.hasNext();) {
			if (CITY_ISTANBUL.equals(i.next().getCity())) {
				i.remove();
				break;
			}
		}

		person.setName("Ceylan2");

		this.refresh(person);

		Assert.assertEquals("Ceylan", person.getName());
		Assert.assertEquals(3, person.getAddresses().size());
	}

}
