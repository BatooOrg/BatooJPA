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
package org.batoo.jpa.core.test.managedcollection;

import org.batoo.jpa.core.test.BaseCoreTest;
import org.junit.Test;

/**
 * @author hceylan
 * 
 * @since $version
 */
public class ManagedCollectionTest extends BaseCoreTest {

	private Person person() {
		final Person person = new Person("Ceylan");
		new Address(person, "Istanbul");
		new Address(person, "London");
		new Address(person, "New York");

		return person;
	}

	/**
	 * Tests that additions to managed collection that are committed
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testAdditionsCommitted() {
		Person person = this.person();
		this.persist(person);

		this.commit();

		person = this.find(Person.class, person.getId());

		this.begin();
		new Address(person, "Paris");
		this.commit();
	}

	/**
	 * Tests that additions to existing managed collection
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testAdditionsExisting() {
		Person person = this.person();
		this.persist(person);

		this.commit();
		this.close();

		person = this.find(Person.class, person.getId());

		this.begin();
		new Address(person, "Paris");
		this.commit();
	}

	/**
	 * Tests that additions to managed collection that are committed
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testAdditionsPersist() {
		Person person = this.person();
		this.persist(person);

		person = this.find(Person.class, person.getId());

		new Address(person, "Paris");
		this.commit();
	}

	/**
	 * Tests that managed collection does not allow duplicates.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test(expected = UnsupportedOperationException.class)
	public void testNoDuplicates() {
		final Person person = this.person();
		person.getAddresses().add(person.getAddresses().get(0));

		this.persist(person);
	}

	/**
	 * Tests that managed collection does not allow duplicates.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test(expected = UnsupportedOperationException.class)
	public void testNoDuplicatesAfterPersist() {
		final Person person = this.person();
		this.persist(person);

		person.getAddresses().add(person.getAddresses().get(0));
	}
}
