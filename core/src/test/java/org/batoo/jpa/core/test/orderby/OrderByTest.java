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
