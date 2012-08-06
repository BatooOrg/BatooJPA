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
package org.batoo.jpa.core.test.map;

import junit.framework.Assert;

import org.batoo.jpa.core.test.BaseCoreTest;
import org.junit.Test;

/**
 * @author hceylan
 * 
 * @since $version
 */
public class MapTest extends BaseCoreTest {

	private Person person() {
		final Person person = new Person("Ceylan");

		final Address address1 = new Address(1, "Istanbul");
		final Address address2 = new Address(2, "London");
		final Address address3 = new Address(3, "New York");

		person.getAddresses1().put(address1.getId(), address1);
		person.getAddresses1().put(address2.getId(), address2);
		person.getAddresses1().put(address3.getId(), address3);

		person.getAddresses2().put(address1.getCity(), address1);
		person.getAddresses2().put(address2.getCity(), address2);
		person.getAddresses2().put(address3.getCity(), address3);

		new Phone(person, new PhoneId("111", "111-1111"));
		new Phone(person, new PhoneId("222", "222-2222"));

		return person;
	}

	/**
	 * Tests Maps with id attributes.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testMap() {
		final Person person = this.person();
		this.persist(person);

		this.commit();
		this.close();

		final Person person2 = this.find(Person.class, person.getId());
		Assert.assertEquals(person.getName(), person2.getName());
		Assert.assertEquals(person.getAddresses1().size(), person2.getAddresses1().size());
		Assert.assertEquals(new Integer(1), person.getAddresses1().get(1).getId());
	}

	/**
	 * Tests Maps with attributes.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testMap2() {
		final Person person = this.person();
		this.persist(person);

		this.commit();
		this.close();

		final Person person2 = this.find(Person.class, person.getId());
		Assert.assertEquals(person.getName(), person2.getName());
		Assert.assertEquals(person.getAddresses1().size(), person2.getAddresses1().size());
		Assert.assertEquals("Istanbul", person.getAddresses2().get("Istanbul").getCity());
	}

	/**
	 * Tests Maps with attributes.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testMap3() {
		final Person person = this.person();
		this.persist(person);

		this.commit();
		this.close();

		final Person person2 = this.find(Person.class, person.getId());
		Assert.assertEquals(person.getName(), person2.getName());
		Assert.assertEquals(person.getPhones().size(), person2.getPhones().size());
		Assert.assertEquals("111-1111", person.getPhones().get(new PhoneId("111", "111-1111")).getPhoneNumber());
	}

	/**
	 * Tests Maps with attributes.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testMap4() {
		final Person person = this.person();
		this.persist(person);

		this.commit();
		this.close();

		Person person2 = this.find(Person.class, person.getId());
		this.begin();
		person2.getPhones().remove(new PhoneId("111", "111-1111"));
		this.commit();

		person2 = this.find(Person.class, person.getId());

		Assert.assertEquals(person.getName(), person2.getName());
		Assert.assertEquals(person.getPhones().size() - 1, person2.getPhones().size());
		Assert.assertEquals("222-2222", person.getPhones().get(new PhoneId("222", "222-2222")).getPhoneNumber());
	}

	/**
	 * Tests Maps with attributes.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testMap5() {
		final Person person = this.person();
		this.persist(person);

		this.commit();
		this.close();

		Person person2 = this.find(Person.class, person.getId());
		this.begin();
		person2.getPhones().clear();
		this.commit();

		person2 = this.find(Person.class, person.getId());

		Assert.assertEquals(person.getName(), person2.getName());
		Assert.assertEquals(0, person2.getPhones().size());
	}
}
