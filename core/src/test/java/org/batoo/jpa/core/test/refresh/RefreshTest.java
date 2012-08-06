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
package org.batoo.jpa.core.test.refresh;

import java.util.Iterator;

import javax.persistence.EntityManager;

import junit.framework.Assert;

import org.batoo.jpa.core.test.BaseCoreTest;
import org.junit.Before;
import org.junit.Test;

/**
 * @author hceylan
 * 
 * @since $version
 */
public class RefreshTest extends BaseCoreTest {

	private static final String COUNTRY_UK = "UK";
	private static final String COUNTRY_USA = "USA";
	private static final String COUNTRY_GERMANY = "Germany";
	private static final String COUNTRY_TURKEY = "Turkey";

	private static final String CITY_LONDON = "London";
	private static final String CITY_NEW_YORK = "New York";
	private static final String CITY_ISTANBUL = "Istanbul";

	private static Country TR = new Country(1, RefreshTest.COUNTRY_TURKEY);
	private static Country USA = new Country(2, RefreshTest.COUNTRY_USA);
	private static Country UK = new Country(3, RefreshTest.COUNTRY_UK);

	private Person person() {
		final Person person = new Person("Ceylan");

		new Address(person, RefreshTest.CITY_ISTANBUL, RefreshTest.TR);
		new Address(person, RefreshTest.CITY_NEW_YORK, RefreshTest.USA);
		new Address(person, RefreshTest.CITY_LONDON, RefreshTest.UK);

		new Phone(person, "111 1111111");
		new Phone(person, "222 2222222");

		return person;
	}

	/**
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Before
	public void prepareCountries() {
		this.persist(RefreshTest.TR);
		this.persist(RefreshTest.USA);
		this.persist(RefreshTest.UK);
	}

	/**
	 * Tests to {@link EntityManager#find(Class, Object)} person.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testRefresh() {
		Person person = this.person();
		this.persist(person);

		this.commit();
		this.close();

		person = this.find(Person.class, person.getId());

		Address address = null;
		Country country = null;
		for (final Iterator<Address> i = person.getAddresses().iterator(); i.hasNext();) {
			address = i.next();
			if (RefreshTest.CITY_ISTANBUL.equals(address.getCity())) {
				address.setCity("SomeCity");
				country = address.getCountry();
				country.setName(RefreshTest.COUNTRY_GERMANY);
				address.setCountry(null);
				i.remove();
				break;
			}
		}

		person.setName("Ceylan2");

		this.refresh(person);

		Assert.assertEquals("Ceylan", person.getName());
		Assert.assertEquals(RefreshTest.CITY_ISTANBUL, address.getCity());
		Assert.assertEquals(country, address.getCountry());
		Assert.assertEquals(RefreshTest.COUNTRY_GERMANY, country.getName());
		Assert.assertEquals(3, person.getAddresses().size());
	}
}
