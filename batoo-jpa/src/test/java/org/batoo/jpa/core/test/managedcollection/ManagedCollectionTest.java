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
	 */
	@Test(expected = UnsupportedOperationException.class)
	public void testNoDuplicatesAfterPersist() {
		final Person person = this.person();
		this.persist(person);

		person.getAddresses().add(person.getAddresses().get(0));
	}
}
