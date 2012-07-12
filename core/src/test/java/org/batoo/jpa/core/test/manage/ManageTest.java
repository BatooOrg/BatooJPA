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
package org.batoo.jpa.core.test.manage;

import org.batoo.jpa.core.test.BaseCoreTest;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author hceylan
 * 
 * @since $version
 */
public class ManageTest extends BaseCoreTest {

	private Person person() {
		final Person person = new Person("Ceylan");
		new Address(person, "Istanbul");
		new Address(person, "London");
		new Address(person, "New York");

		return person;
	}

	/**
	 * Tests that a basic type changed
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testAdditionsCommitted() {
		Person person = this.person();
		this.persist(person);

		this.commit();

		this.begin();

		person = this.find(Person.class, person.getId());

		person.setName("Hasan");

		this.commit();
		this.close();

		person = this.find(Person.class, person.getId());
		Assert.assertEquals("Hasan", person.getName());
	}

	/**
	 * Tests that a basic type changed
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testAssociationsCommitted() {
		Person person1 = this.person();
		Person person2 = this.person();
		this.persist(person1);
		this.persist(person2);

		this.commit();

		this.begin();

		person1 = this.find(Person.class, person1.getId());
		person2 = this.find(Person.class, person2.getId());

		person1.setName("Hasan");
		final Address address = person1.getAddresses().remove(0);
		address.setPerson(person2);

		this.commit();
		this.close();

		person1 = this.find(Person.class, person1.getId());
		person2 = this.find(Person.class, person2.getId());

		Assert.assertEquals("Hasan", person1.getName());
		Assert.assertEquals(2, person1.getAddresses().size());
		Assert.assertEquals(4, person2.getAddresses().size());
	}

	/**
	 * Tests that a basic type changed
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testLazyInitialize() {
		Person person = this.person();
		this.persist(person);

		this.commit();
		this.close();

		person = this.find(Person.class, person.getId());

		this.begin();

		person.getName();
		person.setName("Hasan");

		this.commit();
	}
}
