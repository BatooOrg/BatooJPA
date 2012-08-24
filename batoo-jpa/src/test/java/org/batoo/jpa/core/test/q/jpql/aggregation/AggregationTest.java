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
package org.batoo.jpa.core.test.q.jpql.aggregation;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

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
public class AggregationTest extends BaseCoreTest {

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

	private static Country TR = new Country(AggregationTest.COUNTRY_CODE_TR, AggregationTest.COUNTRY_TR);
	private static Country USA = new Country(AggregationTest.COUNTRY_CODE_USA, AggregationTest.COUNTRY_USA);
	private static Country UK = new Country(AggregationTest.COUNTRY_CODE_UK, AggregationTest.COUNTRY_UK);
	private static Country BROKEN = new Country(AggregationTest.COUNTRY_CODE_BR, null);

	private Person person(int age) {
		final GregorianCalendar start = new GregorianCalendar();
		start.set(Calendar.YEAR, 2000);
		start.set(Calendar.MONTH, 12);
		start.set(Calendar.DAY_OF_MONTH, 31);

		return this.person(age, start.getTime());
	}

	private Person person(int age, Date start) {
		final Person person = new Person("Ceylan", age, start);

		new Address(person, AggregationTest.CITY_ISTANBUL, AggregationTest.TR, true);
		new Address(person, AggregationTest.CITY_NEW_YORK, AggregationTest.USA, false);
		new Address(person, AggregationTest.CITY_LONDON, AggregationTest.UK, false);

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

		this.persist(AggregationTest.TR);
		this.persist(AggregationTest.USA);
		this.persist(AggregationTest.UK);
		this.persist(AggregationTest.BROKEN);

		this.commit();
	}

	/**
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testAgregation() {
		this.persist(this.person(40));
		this.persist(this.person(35));
		this.commit();

		this.close();

		final String testMode = System.getProperty("testMode");

		String qlString = "select avg(func(cast, '(', p.age, ' as double)')) from Person p";
		if ("mysql".equals(testMode) || "pgsql".equals(testMode)) {
			qlString = "select avg(p.age) from Person p";
		}
		else if ("mssql".equals(testMode)) {
			qlString = "select avg(func(cast, '(', p.age, ' as float)')) from Person p";
		}

		Assert.assertEquals(37.5d, this.cq(qlString, Number.class).getSingleResult().doubleValue());

		Assert.assertEquals(75, this.cq("select sum(p.age) from Person p", Number.class).getSingleResult().intValue());

		Assert.assertEquals(40, this.cq("select max(p.age) from Person p", Number.class).getSingleResult().intValue());

		Assert.assertEquals(35, this.cq("select min(p.age) from Person p", Number.class).getSingleResult().intValue());
	}

	/**
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testAgregation2() {
		this.persist(this.person(40));
		this.persist(this.person(35));
		this.persist(this.person(40));
		this.persist(this.person(35));
		this.commit();

		this.close();

		Assert.assertEquals(70,
			((Number) this.cq("select p.age, sum(p.age) from Person p group by p.age order by p.age", Object[].class).getResultList().get(0)[1]).intValue());
	}

	/**
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testCount() {
		this.persist(this.person(40));
		this.persist(this.person(40));
		this.persist(this.person(35));
		this.persist(this.person(35));
		this.commit();

		this.close();

		Assert.assertEquals((Long) 4l, this.cq("select count(p.age) from Person p", Long.class).getSingleResult());

		Assert.assertEquals((Long) 2l, this.cq("select count(distinct p.age) from Person p", Long.class).getSingleResult());
	}

	/**
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testHaving() {
		this.persist(this.person(40));
		this.persist(this.person(40));
		this.persist(this.person(35));
		this.persist(this.person(35));
		this.commit();

		this.close();

		Assert.assertEquals(2l, this.cq("select p.age, count(p.age) from Person p group by p.age having p.age > 35", Object[].class).getSingleResult()[1]);

		Assert.assertEquals(1l,
			this.cq("select p.age, count(distinct p.age) from Person p group by p.age having p.age > 35", Object[].class).getSingleResult()[1]);
	}
}
