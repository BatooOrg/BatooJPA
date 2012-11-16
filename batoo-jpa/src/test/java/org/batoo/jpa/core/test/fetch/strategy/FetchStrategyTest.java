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
package org.batoo.jpa.core.test.fetch.strategy;

import javax.persistence.EntityManager;

import junit.framework.Assert;

import org.batoo.jpa.core.test.BaseCoreTest;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author hceylan
 * 
 * @since 2.0.0
 */
public class FetchStrategyTest extends BaseCoreTest {

	private static Country TR;
	private static Country USA;
	private static Country UK;

	private static City ISTANBUL;
	private static City NEWYORK;
	private static City LONDON;

	/**
	 * @since 2.0.0
	 */
	@BeforeClass
	public static void init() {
		FetchStrategyTest.TR = new Country(1, "Turkey");
		FetchStrategyTest.USA = new Country(2, "USA");
		FetchStrategyTest.UK = new Country(3, "UK");

		FetchStrategyTest.ISTANBUL = new City(1, "ISTANBUL", FetchStrategyTest.TR);
		FetchStrategyTest.NEWYORK = new City(2, "NEWYORK", FetchStrategyTest.USA);
		FetchStrategyTest.LONDON = new City(3, "LONDON", FetchStrategyTest.UK);
	}

	private Person person() {
		final Person person = new Person("Ceylan");

		new Address(person, FetchStrategyTest.LONDON);
		new Address(person, FetchStrategyTest.LONDON);
		new Address(person, FetchStrategyTest.LONDON);

		new Phone(person, "111 1111111");
		new Phone(person, "222 2222222");

		return person;
	}

	/**
	 * 
	 * @since 2.0.0
	 */
	@Before
	public void prepareCountries() {
		this.persist(FetchStrategyTest.TR);
		this.persist(FetchStrategyTest.USA);
		this.persist(FetchStrategyTest.UK);

		this.persist(FetchStrategyTest.ISTANBUL);
		this.persist(FetchStrategyTest.NEWYORK);
		this.persist(FetchStrategyTest.LONDON);
	}

	/**
	 * Tests to {@link EntityManager#find(Class, Object)} person.
	 * 
	 * @since 2.0.0
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
			if ("ISTANBUL".equals(address.getCity().getName())) {
				Assert.assertEquals(address.getCity().getCountry().getId(), FetchStrategyTest.TR.getId());
				break;
			}
		}
	}

}
