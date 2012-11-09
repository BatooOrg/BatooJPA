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
package org.batoo.jpa.core.test.q.jpql.compliance;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.persistence.PersistenceUnitUtil;

import junit.framework.Assert;

import org.batoo.jpa.core.test.BaseCoreTest;
import org.batoo.jpa.core.test.q.Address;
import org.batoo.jpa.core.test.q.Country;
import org.batoo.jpa.core.test.q.HomePhone;
import org.batoo.jpa.core.test.q.Person;
import org.batoo.jpa.core.test.q.WorkPhone;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * @author hceylan
 * @since $version
 */
public class ComplianceTest extends BaseCoreTest {

	private static final String COUNTRY_UK = "United Kingdom";
	private static final String COUNTRY_USA = "United States of America";
	private static final String COUNTRY_TR = "Turkey";

	private static final String COUNTRY_CODE_UK = "UK";
	private static final String COUNTRY_CODE_USA = "USA";
	private static final String COUNTRY_CODE_TR = "TR";
	private static final String COUNTRY_CODE_BR = "BR";

	private static final String CITY_LONDON = "London";
	private static final String CITY_NEW_YORK = "New York";
	private static final String CITY_ISTANBUL = "Istanbul";

	private static Country TR = new Country(ComplianceTest.COUNTRY_CODE_TR, ComplianceTest.COUNTRY_TR);
	private static Country USA = new Country(ComplianceTest.COUNTRY_CODE_USA, ComplianceTest.COUNTRY_USA);
	private static Country UK = new Country(ComplianceTest.COUNTRY_CODE_UK, ComplianceTest.COUNTRY_UK);
	private static Country BROKEN = new Country(ComplianceTest.COUNTRY_CODE_BR, null);

	private Person person(int age) {
		final GregorianCalendar start = new GregorianCalendar();
		start.set(Calendar.YEAR, 2000);
		start.set(Calendar.MONTH, 12);
		start.set(Calendar.DAY_OF_MONTH, 31);

		return this.person(age, start.getTime());
	}

	private Person person(int age, Date start) {
		final Person person = new Person("Ceylan", age, start);

		new Address(person, ComplianceTest.CITY_ISTANBUL, ComplianceTest.TR, true);
		new Address(person, ComplianceTest.CITY_NEW_YORK, ComplianceTest.USA, false);
		new Address(person, ComplianceTest.CITY_LONDON, ComplianceTest.UK, false);

		new HomePhone(person, "111 1111111");
		new HomePhone(person, "222 2222222");

		new WorkPhone(person, "333 3333333");
		new WorkPhone(person, "444 4444444");

		return person;
	}

	/**
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Before
	public void prepareCountries() {
		this.begin();

		this.persist(ComplianceTest.TR);
		this.persist(ComplianceTest.USA);
		this.persist(ComplianceTest.UK);
		this.persist(ComplianceTest.BROKEN);

		this.commit();
	}

	/**
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testCast() {
		this.persist(this.person(35));
		this.commit();

		this.close();

		Assert.assertEquals(Byte.class, this.cq("select cast(p.age as byte) from Person p", Number.class).getSingleResult().getClass());
		Assert.assertEquals(Short.class, this.cq("select cast(p.age as short) from Person p", Number.class).getSingleResult().getClass());
		Assert.assertEquals(Integer.class, this.cq("select cast(p.age as int) from Person p", Number.class).getSingleResult().getClass());
		Assert.assertEquals(Integer.class, this.cq("select cast(p.age as integer) from Person p", Number.class).getSingleResult().getClass());
		Assert.assertEquals(Long.class, this.cq("select cast(p.age as long) from Person p", Number.class).getSingleResult().getClass());
		Assert.assertEquals(Float.class, this.cq("select cast(p.age as float) from Person p", Number.class).getSingleResult().getClass());
		Assert.assertEquals(Double.class, this.cq("select cast(p.age as double) from Person p", Number.class).getSingleResult().getClass());
		Assert.assertEquals(String.class, this.cq("select cast(p.age as string) from Person p", String.class).getSingleResult().getClass());
		Assert.assertEquals(String.class, this.cq("select cast(p.age as varchar) from Person p", String.class).getSingleResult().getClass());
	}

	/**
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testFetchAllProperties() {
		this.persist(this.person(35));
		this.commit();

		this.close();

		final Person person = this.cq("select distinct p from Person p fetch all properties inner join p.workPhones w", Person.class).getSingleResult();
		final PersistenceUnitUtil util = this.em().getEntityManagerFactory().getPersistenceUnitUtil();

		Assert.assertTrue(util.isLoaded(person, "addresses"));
		Assert.assertTrue(util.isLoaded(person, "phones"));
		Assert.assertTrue(util.isLoaded(person, "workPhones"));
	}
}
