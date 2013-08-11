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

package org.batoo.jpa.core.test.manage;

import org.batoo.jpa.core.test.BaseCoreTest;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author hceylan
 * 
 * @since 2.0.0
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
	 * @since 2.0.0
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
	 * @since 2.0.0
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
	 * @since 2.0.0
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
