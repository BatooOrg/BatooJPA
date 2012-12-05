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
package org.batoo.jpa.core.test.q.jpql.simple;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
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
 * @since 2.0.0
 */
public class SimpleJpqlTest extends BaseCoreTest {

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

	private static Country TR = new Country(SimpleJpqlTest.COUNTRY_CODE_TR, SimpleJpqlTest.COUNTRY_TR);
	private static Country USA = new Country(SimpleJpqlTest.COUNTRY_CODE_USA, SimpleJpqlTest.COUNTRY_USA);
	private static Country UK = new Country(SimpleJpqlTest.COUNTRY_CODE_UK, SimpleJpqlTest.COUNTRY_UK);
	private static Country BROKEN = new Country(SimpleJpqlTest.COUNTRY_CODE_BR, null);

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
	 * @since 2.0.0
	 */
	@Before
	public void prepareCountries() {
		this.begin();

		this.persist(SimpleJpqlTest.TR);
		this.persist(SimpleJpqlTest.USA);
		this.persist(SimpleJpqlTest.UK);
		this.persist(SimpleJpqlTest.BROKEN);

		this.commit();
	}

	/**
	 * 
	 * @since 2.0.0
	 */
	@Test(expected = PersistenceException.class)
	public void testAliasNotBound() {
		final TypedQuery<Country> q = this.cq("select d from Country c", Country.class);
		q.getResultList();
	}

	/**
	 * 
	 * @since 2.0.0
	 */
	@Test
	public void testArtimeticOperands() {
		this.persist(this.person(40));
		this.persist(this.person(35));
		this.commit();

		this.close();

		TypedQuery<Number> q1;
		int total;

		q1 = this.cq("select -p.age from Person p", Number.class);
		total = 0;
		for (final Number i : q1.getResultList()) {
			total += i.intValue();
		}
		Assert.assertEquals(-75, total);

		q1 = this.cq("select p.age + p.age from Person p", Number.class);
		total = 0;
		for (final Number i : q1.getResultList()) {
			total += i.intValue();
		}
		Assert.assertEquals(150, total);

		q1 = this.cq("select -p.age + -p.age from Person p", Number.class);
		total = 0;
		for (final Number i : q1.getResultList()) {
			total += i.intValue();
		}
		Assert.assertEquals(-150, total);

		q1 = this.cq("select 22 + p.age from Person p", Number.class);
		total = 0;
		for (final Number i : q1.getResultList()) {
			total += i.intValue();
		}
		Assert.assertEquals(119, total);

		q1 = this.cq("select p.age * 2 from Person p", Number.class);
		total = 0;
		for (final Number i : q1.getResultList()) {
			total += i.intValue();
		}
		Assert.assertEquals(150, total);
	}

	/**
	 * 
	 * @since 2.0.0
	 */
	@Test
	public void testAssociation() {
		this.persist(this.person());
		this.persist(this.person());
		this.commit();

		this.close();

		Assert.assertEquals(6, this.cq("select a from Person p inner join p.addresses a", Address.class).getResultList().size());

		Assert.assertEquals(6, this.cq("select a from Person p, IN(p.addresses) a", Address.class).getResultList().size());
	}

	/**
	 * 
	 * @since 2.0.0
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
				"left join fetch a.country \n" + //
				"left join fetch a.person \n" + //
				"where p = :person", Address.class);
		q.setParameter("person", person);

		Assert.assertEquals(3, q.getResultList().size());
	}

	/**
	 * 
	 * @since 2.0.0
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
	 * 
	 * @since 2.0.0
	 */
	@Test
	public void testComparisonOperands() {
		this.persist(this.person(40));
		this.persist(this.person(35));
		this.commit();

		this.close();

		TypedQuery<Person> q;

		q = this.cq("select p from Person p where p.age > :age", Person.class).setParameter("age", 40);
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

		q = this.cq("select p from Person p where p.age in (:age1, :age2)", Person.class).setParameter("age1", 35).setParameter("age2", 40);
		Assert.assertEquals(2, q.getResultList().size());

		q = this.cq("select p from Person p where p.age in (:p1, :p2)", Person.class).setParameter("p1", 35).setParameter("p2", 45);
		Assert.assertEquals(1, q.getResultList().size());

		q = this.cq("select p from Person p where (p.age < 10 or p.age > :age1) and (p.age > 100 or p.age < :age2)", Person.class).setParameter("age1", 36).setParameter(
			"age2", 50);
		Assert.assertEquals(1, q.getResultList().size());
	}

	/**
	 * @since 2.0.0
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
	 * @since 2.0.0
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

		TypedQuery<Person> q;

		q = this.cq("select p from Person p where p.startDate > :start", Person.class).setParameter("start", start1.getTime(), TemporalType.DATE);
		Assert.assertEquals(1, q.getResultList().size());

		q = this.cq("select p from Person p where p.startDate < :start", Person.class).setParameter("start", start2.getTime(), TemporalType.DATE);
		Assert.assertEquals(1, q.getResultList().size());

		q = this.cq("select p from Person p where p.startDate >= :start", Person.class).setParameter("start", start1.getTime(), TemporalType.DATE);
		Assert.assertEquals(2, q.getResultList().size());

		q = this.cq("select p from Person p where p.startDate <= :start", Person.class).setParameter("start", start1.getTime(), TemporalType.DATE);
		Assert.assertEquals(1, q.getResultList().size());

		q = this.cq("select p from Person p where p.startDate <= current_date", Person.class);
		Assert.assertEquals(2, q.getResultList().size());

		final TypedQuery<java.sql.Date> q2 = this.cq("select current_date from Person p", java.sql.Date.class);
		Assert.assertEquals(2, q2.getResultList().size());

		final TypedQuery<Time> q3 = this.cq("select current_time from Person p", Time.class);
		Assert.assertEquals(2, q3.getResultList().size());

		final TypedQuery<Timestamp> q4 = this.cq("select current_timestamp from Person p", Timestamp.class);
		Assert.assertEquals(2, q4.getResultList().size());
	}

	/**
	 * 
	 * @since 2.0.0
	 */
	@Test
	public void testDateExpression2() {
		final GregorianCalendar day1 = new GregorianCalendar();

		final Person person1 = new Person("person1", 35, day1.getTime());
		person1.setValidTo(day1.getTime());

		day1.add(Calendar.DAY_OF_YEAR, 10);

		final Person person2 = new Person("person2", 45, day1.getTime());
		person2.setValidTo(day1.getTime());

		final GregorianCalendar day2 = new GregorianCalendar();
		day2.add(Calendar.DAY_OF_YEAR, 1);

		this.persist(person1);
		this.persist(person2);
		this.commit();

		this.close();

		TypedQuery<Person> q;

		q = this.cq("SELECT bean FROM Person bean where (bean.validFrom is null or bean.validFrom <= :now) and (bean.validTo is null or bean.validTo >= :now)",
			Person.class).setParameter("now", day2.getTime(), TemporalType.DATE);

		Assert.assertEquals(1, q.getResultList().size());

	}

	/**
	 * 
	 * @since 2.0.0
	 */
	@Test
	public void testFunction() {
		final String testMode = System.getProperty("testMode");
		// oracle doesn't have pi function
		if ("oracle".equals(testMode)) {
			return;
		}

		this.persist(this.person(100));

		this.commit();

		double expected = 314.159265;
		if ("hsql".equals(testMode) || "derby".equals(testMode) || "h2".equals(testMode) || "mssql".equals(testMode) || "saw".equals(testMode)
			|| "pgsql".equals(testMode)) {
			expected = 314.1592653589793;
		}

		Assert.assertEquals(expected, this.cq("select pp.age * func(pi, '()') from Person pp", Double.class).getSingleResult());
	}

	/**
	 * 
	 * @since 2.0.0
	 */
	@Test
	public void testIndex() {
		this.persist(this.person());
		this.commit();

		this.close();

		final TypedQuery<Number> q = this.cq(//
			"select index(wp) from Person p\n" + //
				"    left join p.workPhones wp\n" + //
				"    order by wp.id", Number.class);

		Assert.assertEquals("[0, 1]", q.getResultList().toString());
	}

	/**
	 * 
	 * @since 2.0.0
	 */
	@Test
	public void testLike() {
		final Person person = this.person(40);
		person.setName("%Ceylan");
		this.persist(person);
		this.persist(this.person(35));
		this.commit();

		this.close();

		TypedQuery<Person> q;
		q = this.cq("select p from Person p where p.name like :name", Person.class).setParameter("name", "Ce%");
		Assert.assertEquals(1, q.getResultList().size());

		q = this.cq("select p from Person p where p.name like :name", Person.class).setParameter("name", "De%");
		Assert.assertEquals(0, q.getResultList().size());

		q = this.cq("select p from Person p where p.name not like :name escape '^'", Person.class).setParameter("name", "^%Ce%");
		Assert.assertEquals(1, q.getResultList().size());
	}

	/**
	 * 
	 * @since 2.0.0
	 */
	@Test
	public void testNamedQuery() {
		this.persist(this.person(40));
		this.persist(this.person(35));
		this.commit();

		Assert.assertEquals(40, this.em().createNamedQuery("theOldestGuys", Person.class).setMaxResults(1).getSingleResult().getAge());
	}

	/**
	 * 
	 * @since 2.0.0
	 */
	@Test
	public void testNumericFunctions() {
		this.persist(this.person(-49));
		this.commit();

		this.close();

		TypedQuery<Number> q;

		q = this.cq("select abs(p.age) from Person p where p.id = 1", Number.class);
		Assert.assertEquals(49, q.getSingleResult().intValue());

		q = this.cq("select sqrt(abs(p.age)) from Person p where p.id = 1", Number.class);
		Assert.assertEquals(7.0, q.getSingleResult().doubleValue());

		q = this.cq("select mod(abs(p.age), 10) from Person p where p.id = 1", Number.class);
		Assert.assertEquals(9, q.getSingleResult().intValue());

		q = this.cq("select length(p.name) from Person p where p.id = 1", Number.class);
		Assert.assertEquals(6, q.getSingleResult().intValue());
	}

	/**
	 * 
	 * @since 2.0.0
	 */
	@Test
	public void testOrderBy() {
		final String testMode = System.getProperty("testMode");
		String expected = null;

		if ("mysql".equals(testMode) || "hsql".equals(testMode) || "h2".equals(testMode) || "mssql".equals(testMode) || "saw".equals(testMode)) {
			expected = SimpleJpqlTest.COUNTRY_USA;
		}

		Assert.assertEquals(expected, this.cq("select c.name from Country c order by c.name desc", String.class).setMaxResults(1).getSingleResult());

		expected = SimpleJpqlTest.COUNTRY_TR;
		if ("mysql".equals(testMode) || "hsql".equals(testMode) || "h2".equals(testMode) || "mssql".equals(testMode) || "saw".equals(testMode)) {
			expected = null;
		}

		Assert.assertEquals(expected, this.cq("select c.name from Country c order by c.name asc", String.class).setMaxResults(1).getSingleResult());
	}

	/**
	 * 
	 * @since 2.0.0
	 */
	@Test
	public void testPagination() {
		Assert.assertEquals(2l, this.cq("select c from Country c", Country.class).setFirstResult(2).getResultList().size());

		Assert.assertEquals(2l, this.cq("select c from Country c", Country.class).setMaxResults(2).getResultList().size());

		Assert.assertEquals(1l, this.cq("select c from Country c", Country.class).setFirstResult(3).setMaxResults(2).getResultList().size());
	}

	/**
	 * 
	 * @since 2.0.0
	 */
	@Test
	public void testQuery() {
		this.persist(this.person(40));
		this.persist(this.person(35));

		this.commit();
		this.close();

		Assert.assertEquals(75, ((Number) this.cq("select sum(p.age) from Person p").getSingleResult()).intValue());
	}

	/**
	 * 
	 * @since 2.0.0
	 */
	@Test
	public void testRootJoin() {
		this.persist(this.person());
		this.persist(this.person());
		this.commit();

		this.close();

		final TypedQuery<Person> q = this.cq(//
			"select p from Person p\n" + //
				"    left join fetch p.addresses\n" + //
				"    left join fetch p.addresses.country\n" + //
				"    left join p.addresses a", //
			Person.class);

		final List<Person> resultList = q.getResultList();

		Assert.assertEquals(18, resultList.size());
		Assert.assertEquals(3, resultList.get(0).getAddresses().size());
	}

	/**
	 * @since 2.0.0
	 */
	@Test
	public void testSimple() {
		TypedQuery<Country> q = this.cq("select c from Country c where c = :country", Country.class).setParameter("country", SimpleJpqlTest.TR);
		Assert.assertEquals(1, q.getResultList().size());

		q = this.cq("select c from Country c where c.name is not null", Country.class);
		final List<Country> rs = q.getResultList();
		Collections.sort(rs, new Comparator<Country>() {

			@Override
			public int compare(Country o1, Country o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});

		Assert.assertEquals("[Country [name=Turkey], Country [name=United Kingdom], Country [name=United States of America]]", rs.toString());

		q = this.cq("select c from Country as c where c.name is null", Country.class);
		Assert.assertEquals(1, q.getResultList().size());
	}

	/**
	 * 
	 * @since 2.0.0
	 */
	@Test
	public void testStringExpression() {
		this.persist(this.person(40));
		this.persist(this.person(35));
		this.commit();

		this.close();

		TypedQuery<Person> q;
		TypedQuery<String> q2;

		final String testMode = System.getProperty("testMode");

		q = this.cq("select p.name from Person p where p.name <= :name", Person.class).setParameter("name", "Ceylan");
		Assert.assertEquals(2, q.getResultList().size());

		q = this.cq("select p from Person p where p.name > :name", Person.class).setParameter("name", "Ceylan");
		Assert.assertEquals(0, q.getResultList().size());

		q2 = this.cq("select lower(c.name) from Country c where upper(c.name) = :name", String.class).setParameter("name",
			SimpleJpqlTest.COUNTRY_TR.toUpperCase());
		Assert.assertEquals("turkey", q2.getResultList().get(0));

		q2 = this.cq("select upper(c.name) from Country c where lower(c.name) = :name", String.class).setParameter("name",
			SimpleJpqlTest.COUNTRY_TR.toLowerCase());
		Assert.assertEquals("TURKEY", q2.getResultList().get(0));

		q2 = this.cq("select concat(c.code, '_', c.name) from Country c order by c.code", String.class);
		String expected = "[null, TR_Turkey, UK_United Kingdom, USA_United States of America]";
		if (testMode.equals("oracle") || "saw".equals(testMode)) {
			expected = "[BR_, TR_Turkey, UK_United Kingdom, USA_United States of America]";
		}
		Assert.assertEquals(expected, q2.getResultList().toString());

		q2 = this.cq("select substring(c.name, 2) from Country c order by c.code", String.class);
		Assert.assertEquals("[null, urkey, nited Kingdom, nited States of America]", q2.getResultList().toString());

		q2 = this.cq("select substring(c.name, 2, 3) from Country c order by c.code", String.class);
		Assert.assertEquals("[null, urk, nit, nit]", q2.getResultList().toString());

		q2 = this.cq("select trim(' a ') from Country c where c = :country", String.class).setParameter("country", SimpleJpqlTest.TR);
		Assert.assertEquals("a", q2.getSingleResult());

		q2 = this.cq("select trim(leading from ' a ') from Country c where c = :country", String.class).setParameter("country", SimpleJpqlTest.TR);
		Assert.assertEquals("a ", q2.getSingleResult());

		q2 = this.cq("select trim(trailing from ' a ') from Country c where c = :country", String.class).setParameter("country", SimpleJpqlTest.TR);
		Assert.assertEquals(" a", q2.getSingleResult());

		q2 = this.cq("select trim(both from ' a ') from Country c where c = :country", String.class).setParameter("country", SimpleJpqlTest.TR);
		Assert.assertEquals("a", q2.getSingleResult());

		if (!"mssql".equals(testMode) && !"saw".equals(testMode)) {
			q2 = this.cq("select trim(both 'c' from 'cac') from Country c where c = :country", String.class).setParameter("country", SimpleJpqlTest.TR);
			Assert.assertEquals("a", q2.getSingleResult());
		}
	}
}
