/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
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

		Assert.assertEquals(37.5d, this.cq("select avg(func(cast, '(', p.age, ' as double)')) from Person p", Double.class).getSingleResult());

		Assert.assertEquals((Integer) 75, this.cq("select sum(p.age) from Person p", Integer.class).getSingleResult());

		Assert.assertEquals((Integer) 40, this.cq("select max(p.age) from Person p", Integer.class).getSingleResult());

		Assert.assertEquals((Integer) 35, this.cq("select min(p.age) from Person p", Integer.class).getSingleResult());
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

		Assert.assertEquals(70, this.cq("select p.age, sum(p.age) from Person p group by p.age", Object[].class).getResultList().get(0)[1]);
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
