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
package org.batoo.jpa.core.test.ordercolumn;

import java.util.Arrays;

import junit.framework.Assert;

import org.batoo.jpa.core.test.BaseCoreTest;
import org.junit.Test;

/**
 * @author hceylan
 * 
 * @since 2.0.0
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
	 * @since 2.0.0
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
	 * @since 2.0.0
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
