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
package org.batoo.jpa.core.test.ordercolumn;

import java.util.Arrays;

import junit.framework.Assert;

import org.batoo.jpa.core.test.BaseCoreTest;
import org.junit.Test;

/**
 * @author hceylan
 * 
 * @since $version
 */
public class OrderColumnTest extends BaseCoreTest {

	private Person person() {
		final Person person = new Person("Ceylan");
		new Address(person, "Istanbul");
		new Address(person, "London");
		new Address(person, "New York");

		return person;
	}

	/**
	 * Tests list order is maintained.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testOrder1() {
		Person person = this.person();
		this.persist(person);

		final Object[] addresses1 = person.getAddresses().toArray();

		this.commit();
		this.close();

		person = this.find(Person.class, person.getId());

		final Object[] addresses2 = person.getAddresses().toArray();

		Assert.assertEquals(Arrays.toString(addresses1), Arrays.toString(addresses2));
	}

	/**
	 * Tests list is sorted
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testOrder2() {
		Person person = this.person();
		this.persist(person);

		this.commit();
		this.close();

		person = this.find(Person.class, person.getId());

		this.begin();
		final Address address = person.getAddresses().remove(0);
		person.getAddresses().add(address);
		new Address(person, "Paris");

		final Object[] addresses1 = person.getAddresses().toArray();

		this.commit();
		this.close();

		person = this.find(Person.class, person.getId());

		final Object[] addresses2 = person.getAddresses().toArray();

		Assert.assertEquals(Arrays.toString(addresses1), Arrays.toString(addresses2));
	}
}
