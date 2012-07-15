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
package org.batoo.jpa.core.test.orderby;

import java.util.Arrays;

import junit.framework.Assert;

import org.batoo.jpa.core.test.BaseCoreTest;
import org.junit.Test;

/**
 * @author hceylan
 * 
 * @since $version
 */
public class OrderByTest extends BaseCoreTest {

	private Person person() {
		final Person person = new Person("Ceylan");
		new Address(person, "Istanbul");
		new Address(person, "London");
		new Address(person, "New York");

		new Address2(person, "Istanbul", "Street1");
		new Address2(person, "Istanbul", "Street2");
		new Address2(person, "Istanbul", "Street3");
		new Address2(person, "London", "Street5");
		new Address2(person, "London", "Street4");
		new Address2(person, "New York", "Broadway");

		return person;
	}

	/**
	 * Tests list is sorted
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testOrderBy1() {
		final Person person = this.person();
		this.persist(person);

		this.commit();

		final Address[] addresses = person.getAddresses().toArray(new Address[person.getAddresses().size()]);
		for (int i = 1; i < addresses.length; i++) {
			Assert.assertTrue(addresses[i].getId().intValue() > addresses[i - 1].getId().intValue());
		}

		final String addresses2 = Arrays.toString(person.getAddresses2().toArray());
		Assert.assertEquals(
			"[Address2 [city=New York, street=Broadway], Address2 [city=Istanbul, street=Street1], Address2 [city=Istanbul, street=Street2], Address2 [city=Istanbul, street=Street3], Address2 [city=London, street=Street4], Address2 [city=London, street=Street5]]",
			addresses2);
	}

	/**
	 * Tests list is sorted
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testOrderBy2() {
		Person person = this.person();
		this.persist(person);

		this.commit();

		person = this.find(Person.class, person.getId());

		this.begin();
		new Address(person, "Paris");
		this.commit();

		person = this.find(Person.class, person.getId());

		final Address[] addresses = person.getAddresses().toArray(new Address[person.getAddresses().size()]);
		for (int i = 1; i < addresses.length; i++) {
			Assert.assertTrue(addresses[i].getId().intValue() > addresses[i - 1].getId().intValue());
		}

		final String addresses2 = Arrays.toString(person.getAddresses2().toArray());
		Assert.assertEquals(
			"[Address2 [city=New York, street=Broadway], Address2 [city=Istanbul, street=Street1], Address2 [city=Istanbul, street=Street2], Address2 [city=Istanbul, street=Street3], Address2 [city=London, street=Street4], Address2 [city=London, street=Street5]]",
			addresses2);
	}
}
