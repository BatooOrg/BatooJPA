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
package org.batoo.jpa.core.test.q.jpql.simple;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.persistence.PersistenceException;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;

import junit.framework.Assert;

import org.batoo.jpa.core.test.BaseCoreTest;
import org.batoo.jpa.core.test.q.Address;
import org.batoo.jpa.core.test.q.Country;
import org.batoo.jpa.core.test.q.HomePhone;
import org.batoo.jpa.core.test.q.Person;
import org.batoo.jpa.core.test.q.SimpleCity;
import org.batoo.jpa.core.test.q.WorkPhone;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * 
 * @author hceylan
 * @since $version
 */
public class SimpleJpqlTest extends BaseCoreTest {

	private static final String COUNTRY_UK = "United Kingdom";
	private static final String COUNTRY_USA = "United States of America";
	private static final String COUNTRY_TR = "Turkey";

	private static final String COUNTRY_CODE_UK = "UK";
	private static final String COUNTRY_CODE_USA = "USA";
	private static final String COUNTRY_CODE_TR = "TR";

	private static final String CITY_LONDON = "London";
	private static final String CITY_NEW_YORK = "New York";
	private static final String CITY_ISTANBUL = "Istanbul";

	private static Country TR = new Country(SimpleJpqlTest.COUNTRY_CODE_TR, SimpleJpqlTest.COUNTRY_TR);
	private static Country USA = new Country(SimpleJpqlTest.COUNTRY_CODE_USA, SimpleJpqlTest.COUNTRY_USA);
	private static Country UK = new Country(SimpleJpqlTest.COUNTRY_CODE_UK, SimpleJpqlTest.COUNTRY_UK);

	private Person person() {
		final GregorianCalendar start = new GregorianCalendar();
		start.set(Calendar.YEAR, 2000);
		start.set(Calendar.MONTH, 12);
		start.set(Calendar.DAY_OF_MONTH, 31);

		return this.person(35, start.getTime());
	}

	private Person person(int age) {
		final GregorianCalendar start = new GregorianCalendar();
		start.set(Calendar.YEAR, 2000);
		start.set(Calendar.MONTH, 12);
		start.set(Calendar.DAY_OF_MONTH, 31);

		return this.person(age, start.getTime());
	}

	private Person person(int age, Date start) {
		final Person person = new Person("Ceylan", age, start);

		new Address(person, SimpleJpqlTest.CITY_ISTANBUL, SimpleJpqlTest.TR, true);
		new Address(person, SimpleJpqlTest.CITY_NEW_YORK, SimpleJpqlTest.USA, false);
		new Address(person, SimpleJpqlTest.CITY_LONDON, SimpleJpqlTest.UK, false);

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

		this.persist(SimpleJpqlTest.TR);
		this.persist(SimpleJpqlTest.USA);
		this.persist(SimpleJpqlTest.UK);

		this.commit();
	}

	/**
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test(expected = PersistenceException.class)
	public void testAliasNotBound() {
		final TypedQuery<Country> q = this.cq("select d from Country c", Country.class);
		q.getResultList();
	}

	/**
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testArithmeticExpression() {
		Person p1, p2;

		this.persist(p1 = this.person(40));
		this.persist(p2 = this.person(35));
		this.commit();

		this.close();

		TypedQuery<Integer> q2;
		int total;

		TypedQuery<Person> q = this.cq("select p from Person p where p.age > :age", Person.class).setParameter("age", 40);
		Assert.assertEquals(0, q.getResultList().size());

		q = this.cq("select p from Person p where p.age < :age", Person.class).setParameter("age", 40);
		Assert.assertEquals(1, q.getResultList().size());

		q = this.cq("select p from Person p where p.age >= :age", Person.class).setParameter("age", 40);
		Assert.assertEquals(1, q.getResultList().size());

		q = this.cq("select p from Person p where p.age >= :age1 or p.age < :age2", Person.class).setParameter("age1", 40).setParameter("age2", 50);
		Assert.assertEquals(2, q.getResultList().size());

		q = this.cq("select p from Person p where p.age <= :age", Person.class).setParameter("age", 40);
		Assert.assertEquals(2, q.getResultList().size());

		q = this.cq("select p from Person p where p.age between :age1 and :age2", Person.class).setParameter("age1", 38).setParameter("age2", 41);
		Assert.assertEquals(1, q.getResultList().size());

		q = this.cq("select p from Person p where p.age between :age1 and :age2", Person.class).setParameter("age1", 30).setParameter("age2", 50);
		Assert.assertEquals(2, q.getResultList().size());

		q = this.cq("select p from Person p where p.age between :age1 and :age2", Person.class).setParameter("age1", 10).setParameter("age2", 20);
		Assert.assertEquals(0, q.getResultList().size());

		q = this.cq("select p from Person p where p.age not between :age1 and :age2", Person.class).setParameter("age1", 10).setParameter("age2", 20);
		Assert.assertEquals(2, q.getResultList().size());

		q2 = this.cq("select -p.age from Person p", Integer.class);
		total = 0;
		for (final Integer i : q2.getResultList()) {
			total += i;
		}
		Assert.assertEquals(-75, total);

		q2 = this.cq("select p.age + p.age from Person p", Integer.class);
		total = 0;
		for (final Integer i : q2.getResultList()) {
			total += i;
		}
		Assert.assertEquals(150, total);

		q2 = this.cq("select -p.age + -p.age from Person p", Integer.class);
		total = 0;
		for (final Integer i : q2.getResultList()) {
			total += i;
		}
		Assert.assertEquals(-150, total);

		q2 = this.cq("select 22 + p.age from Person p", Integer.class);
		total = 0;
		for (final Integer i : q2.getResultList()) {
			total += i;
		}
		Assert.assertEquals(119, total);

		q2 = this.cq("select p.age * 2 from Person p", Integer.class);
		total = 0;
		for (final Integer i : q2.getResultList()) {
			total += i;
		}
		Assert.assertEquals(150, total);

		q = this.cq("select p from Person p where p.age in (:age1, :age2)", Person.class).setParameter("age1", 35).setParameter("age2", 40);
		Assert.assertEquals(2, q.getResultList().size());

		q = this.cq("select p from Person p where p in (:p1, :p2)", Person.class).setParameter("p1", p1).setParameter("p2", p2);
		Assert.assertEquals(2, q.getResultList().size());
	}

	/**
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testAssociation() {
		this.persist(this.person());
		this.persist(this.person());
		this.commit();

		this.close();

		final TypedQuery<Address> q = this.cq("select a from Person p inner join p.addresses a", Address.class);

		Assert.assertEquals(6, q.getResultList().size());
	}

	/**
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testAssociationJoin() {
		final Person person = this.person();
		this.persist(person);
		this.persist(this.person());
		this.commit();

		this.close();

		final TypedQuery<Address> q = this.cq(//
			"select a from Person p\n" + //
				"left join p.addresses as a \n" + //
				"join fetch a.country \n" + //
				"join fetch a.person \n" + //
				"where p = :person", Address.class);
		q.setParameter("person", person);

		Assert.assertEquals(3, q.getResultList().size());
	}

	/**
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testBooleanExpression() {
		this.persist(this.person());
		this.persist(this.person());
		this.commit();

		this.close();

		final TypedQuery<Address> q = this.cq(//
			"select a as a1 from Person p\n" + //
				"    left join p.addresses a\n" + //
				"    where a.primary", //
			Address.class);

		Assert.assertEquals(2, q.getResultList().size());
	}

	/**
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testConstructor() {
		this.persist(this.person());
		this.commit();
		this.close();

		final TypedQuery<SimpleCity> q = this.cq("select new org.batoo.jpa.core.test.q.SimpleCity(a.city, a.country.name) from Address a", SimpleCity.class);

		final List<SimpleCity> resultList = q.getResultList();
		Collections.sort(resultList);

		Assert.assertEquals(
			"[SimpleCity [city=Istanbul, country=Turkey], SimpleCity [city=London, country=United Kingdom], SimpleCity [city=New York, country=United States of America]]",
			resultList.toString());
	}

	/**
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testDateExpression() {
		final GregorianCalendar start1 = new GregorianCalendar();
		start1.set(Calendar.YEAR, 2000);
		start1.set(Calendar.MONTH, 12);
		start1.set(Calendar.DAY_OF_MONTH, 31);

		final GregorianCalendar start2 = new GregorianCalendar();
		start2.set(Calendar.YEAR, 2001);
		start2.set(Calendar.MONTH, 12);
		start2.set(Calendar.DAY_OF_MONTH, 31);

		this.persist(this.person(40, start1.getTime()));
		this.persist(this.person(35, start2.getTime()));
		this.commit();

		this.close();

		TypedQuery<Person> q = this.cq("select p from Person p where p.startDate > :start", Person.class).setParameter("start", start1.getTime(),
			TemporalType.DATE);
		Assert.assertEquals(1, q.getResultList().size());

		q = this.cq("select p from Person p where p.startDate < :start", Person.class).setParameter("start", start2.getTime(), TemporalType.DATE);
		Assert.assertEquals(1, q.getResultList().size());

		q = this.cq("select p from Person p where p.startDate >= :start", Person.class).setParameter("start", start1.getTime(), TemporalType.DATE);
		Assert.assertEquals(2, q.getResultList().size());

		q = this.cq("select p from Person p where p.startDate <= :start", Person.class).setParameter("start", start1.getTime(), TemporalType.DATE);
		Assert.assertEquals(1, q.getResultList().size());
	}

	/**
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testRootJoin() {
		this.persist(this.person());
		this.persist(this.person());
		this.commit();

		this.close();

		final TypedQuery<Person> q = this.cq(//
			"select p from Person p\n" + //
				"    join fetch p.addresses\n" + //
				"    join fetch p.addresses.country\n" + //
				"    left join p.addresses a", //
			Person.class);

		final List<Person> resultList = q.getResultList();

		Assert.assertEquals(18, resultList.size());
		Assert.assertEquals(3, resultList.get(0).getAddresses().size());
	}

	/**
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testSimple() {
		TypedQuery<Country> q = this.cq("select c from Country c", Country.class);
		Assert.assertEquals("[Country [name=Turkey], Country [name=United States of America], Country [name=United Kingdom]]", q.getResultList().toString());

		q = this.cq("select c from Country c where c = :country", Country.class);
		q.setParameter("country", SimpleJpqlTest.TR);

		Assert.assertEquals(1, q.getResultList().size());
	}

	/**
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testStringExpression() {
		this.persist(this.person(40));
		this.persist(this.person(35));
		this.commit();

		this.close();

		TypedQuery<Person> q;

		q = this.cq("select p.name from Person p where p.name <= :name", Person.class).setParameter("name", "Ceylan");
		Assert.assertEquals(2, q.getResultList().size());

		q = this.cq("select p from Person p where p.name > :name", Person.class).setParameter("name", "Ceylan");
		Assert.assertEquals(0, q.getResultList().size());

		q = this.cq("select p from Person p where p.name like :name", Person.class).setParameter("name", "Ce%");
		Assert.assertEquals(2, q.getResultList().size());

		q = this.cq("select p from Person p where p.name like :name", Person.class).setParameter("name", "De%");
		Assert.assertEquals(0, q.getResultList().size());

		q = this.cq("select p from Person p where p.name not like :name", Person.class).setParameter("name", "De%");
		Assert.assertEquals(2, q.getResultList().size());

		// q = this.cq("select length(p.name) from Person p", Person.class);
		// Assert.assertEquals(2, q.getResultList().size());
	}
}
