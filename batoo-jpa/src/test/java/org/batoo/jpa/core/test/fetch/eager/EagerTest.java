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
package org.batoo.jpa.core.test.fetch.eager;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;

import junit.framework.Assert;

import org.batoo.jpa.core.test.BaseCoreTest;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author hceylan
 * 
 * @since $version
 */
public class EagerTest extends BaseCoreTest {

	private static Country TR;
	private static Country USA;
	private static Country UK;

	/**
	 * @since $version
	 */
	@BeforeClass
	public static void initCountries() {
		EagerTest.TR = new Country(1, "Turkey");
		EagerTest.USA = new Country(2, "USA");
		EagerTest.UK = new Country(3, "UK");
	}

	private Person person() {
		final Person person = new Person("Ceylan");

		new Address(person, "Istanbul", EagerTest.TR);
		new Address(person, "New York", EagerTest.USA);
		new Address(person, "London", EagerTest.UK);

		new Phone(person, "111 1111111");
		new Phone(person, "222 2222222");

		return person;
	}

	/**
	 * 
	 * @since $version
	 */
	@Before
	public void prepareCountries() {
		this.persist(EagerTest.TR);
		this.persist(EagerTest.USA);
		this.persist(EagerTest.UK);
	}

	/**
	 * Tests to {@link EntityManager#find(Class, Object)} person.
	 * 
	 * @since $version
	 */
	@Test
	public void testFind() {
		final Person person = this.person();
		this.persist(person);

		this.commit();
		this.close();

		final Person person2 = this.find(Person.class, person.getId());
		Assert.assertEquals(person.getName(), person2.getName());
		Assert.assertEquals(person.getAddresses().size(), person2.getAddresses().size());

		for (final Address address : person2.getAddresses()) {
			if ("Istanbul".equals(address.getCity())) {
				Assert.assertEquals(address.getCountry().getId(), EagerTest.TR.getId());
				break;
			}
		}
	}

	/**
	 * Tests to {@link EntityManager#find(Class, Object)} person that is in the session.
	 * 
	 * @since $version
	 */
	@Test
	public void testFindInSession() {
		this.persist(EagerTest.TR);
		this.persist(EagerTest.USA);
		this.persist(EagerTest.UK);

		final Person person = this.person();
		this.persist(person);

		this.commit();

		final Person person2 = this.find(Person.class, person.getId());
		Assert.assertSame(person, person2);
	}

	/**
	 * Tests to {@link EntityManager#persist(Object)} address which does not cascade to Parent. PersistenceException expected.
	 * 
	 * @since $version
	 */
	@Test(expected = PersistenceException.class)
	public void testPersistAddress() {
		this.persist(this.person().getAddresses().get(0));

		this.commit();
	}

	/**
	 * Tests to {@link EntityManager#persist(Object)} Parent which cascades to Child1.
	 * 
	 * @since $version
	 */
	@Test
	public void testPersistPerson() {
		Assert.assertEquals(4, this.em().getMetamodel().getEntities().size());

		this.persist(this.person());

		this.commit();
	}
}
