/*
 * Copyright (c) 2012-2013, Batu Alp Ceylan
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

package org.batoo.jpa.core.test.q.criteria.simple;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Selection;

import junit.framework.Assert;

import org.batoo.jpa.core.impl.criteria.CriteriaBuilderImpl;
import org.batoo.jpa.core.impl.criteria.CriteriaQueryImpl;
import org.batoo.jpa.core.impl.criteria.QueryImpl;
import org.batoo.jpa.core.impl.criteria.RootImpl;
import org.batoo.jpa.core.impl.criteria.expression.ParameterExpressionImpl;
import org.batoo.jpa.core.impl.criteria.expression.PredicateImpl;
import org.batoo.jpa.core.impl.criteria.join.AbstractJoin;
import org.batoo.jpa.core.impl.criteria.path.AbstractPath;
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
 * @author hceylan
 * 
 * @since 2.0.0
 */
public class SimpleCriteriaTest extends BaseCoreTest {

	private static final String COUNTRY_UK = "United Kingdom";
	private static final String COUNTRY_USA = "United States of America";
	private static final String COUNTRY_TR = "Turkey";

	private static final String COUNTRY_CODE_UK = "UK";
	private static final String COUNTRY_CODE_USA = "USA";
	private static final String COUNTRY_CODE_TR = "TR";

	private static final String CITY_LONDON = "London";
	private static final String CITY_NEW_YORK = "New York";
	private static final String CITY_ISTANBUL = "Istanbul";

	private static Country TR = new Country(SimpleCriteriaTest.COUNTRY_CODE_TR, SimpleCriteriaTest.COUNTRY_TR);
	private static Country USA = new Country(SimpleCriteriaTest.COUNTRY_CODE_USA, SimpleCriteriaTest.COUNTRY_USA);
	private static Country UK = new Country(SimpleCriteriaTest.COUNTRY_CODE_UK, SimpleCriteriaTest.COUNTRY_UK);

	private GregorianCalendar getStartDate() {
		final GregorianCalendar start = new GregorianCalendar();
		start.set(Calendar.YEAR, 2000);
		start.set(Calendar.MONTH, 12);
		start.set(Calendar.DAY_OF_MONTH, 31);
		return start;
	}

	private Person person() {

		final Person person = new Person("Ceylan", 38, getStartDate().getTime());

		new Address(person, SimpleCriteriaTest.CITY_ISTANBUL, SimpleCriteriaTest.TR, true);
		new Address(person, SimpleCriteriaTest.CITY_NEW_YORK, SimpleCriteriaTest.USA, false);
		new Address(person, SimpleCriteriaTest.CITY_LONDON, SimpleCriteriaTest.UK, false);

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

		this.persist(SimpleCriteriaTest.TR);
		this.persist(SimpleCriteriaTest.USA);
		this.persist(SimpleCriteriaTest.UK);

		this.commit();
	}

	/**
	 * 
	 * @since 2.0.0
	 */
	@Test
	public void testArithmeticExpression1() {
		this.persist(this.person());
		this.commit();

		this.close();

		final CriteriaBuilderImpl cb = this.em().getCriteriaBuilder();
		final CriteriaQueryImpl<Person> cq = cb.createQuery(Person.class);

		final RootImpl<Person> r = cq.from(Person.class);
		final AbstractPath<Integer> age = r.<Integer> get("age");

		final ParameterExpressionImpl<Integer> p = cb.parameter(Integer.class);
		cq.where(cb.lessThan(age, p));

		final QueryImpl<Person> q = this.em().createQuery(cq);
		q.setParameter(p, 40);

		Assert.assertEquals(1, q.getResultList().size());
	}

	/**
	 * 
	 * @since 2.0.0
	 */
	@Test
	public void testArithmeticExpression2() {
		this.persist(this.person());
		this.commit();

		this.close();

		final CriteriaBuilderImpl cb = this.em().getCriteriaBuilder();
		final CriteriaQueryImpl<Person> cq = cb.createQuery(Person.class);

		final RootImpl<Person> r = cq.from(Person.class);
		final AbstractPath<Integer> age = r.<Integer> get("age");

		final ParameterExpressionImpl<Integer> p = cb.parameter(Integer.class);
		cq.where(cb.greaterThan(age, p));

		final QueryImpl<Person> q = this.em().createQuery(cq);
		q.setParameter(p, 40);

		Assert.assertEquals(0, q.getResultList().size());
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

		final CriteriaBuilderImpl cb = this.em().getCriteriaBuilder();
		final CriteriaQueryImpl<Address> q = cb.createQuery(Address.class);
		final RootImpl<Person> r = q.from(Person.class);
		q.select(r.<Address> join("addresses"));

		final List<Address> resultList = this.em().createQuery(q).getResultList();
		Assert.assertEquals(6, resultList.size());
	}

	/**
	 * 
	 * @since 2.0.0
	 */
	@Test
	public void testAssociationJoin() {
		this.persist(this.person());
		this.persist(this.person());
		this.commit();

		this.close();

		final CriteriaBuilderImpl cb = this.em().getCriteriaBuilder();
		final CriteriaQueryImpl<Address> q = cb.createQuery(Address.class);
		final RootImpl<Person> r = q.from(Person.class);
		final Join<Person, Address> a = r.join("addresses");
		a.fetch("person");
		a.fetch("country");
		q.select(a);

		final List<Address> resultList = this.em().createQuery(q).getResultList();
		Assert.assertEquals(6, resultList.size());
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

		final CriteriaBuilderImpl cb = this.em().getCriteriaBuilder();
		final CriteriaQueryImpl<Address> q = cb.createQuery(Address.class);
		final RootImpl<Person> r = q.from(Person.class);
		final AbstractJoin<Person, Address> a = r.join("addresses");
		q.select(a);

		final AbstractPath<Boolean> p = a.<Boolean> get("primary");
		q.where(p);

		final List<Address> resultList = this.em().createQuery(q).getResultList();
		Assert.assertEquals(2, resultList.size());
	}

	/**
	 * @since 2.0.0
	 */
	@Test
	public void testConstructor() {
		this.persist(this.person());
		this.commit();
		this.close();

		final CriteriaBuilderImpl cb = this.em().getCriteriaBuilder();

		final CriteriaQueryImpl<SimpleCity> q = cb.createQuery(SimpleCity.class);
		final RootImpl<Address> r = q.from(Address.class);
		final Selection<String> city = r.<String> get("city");
		final Selection<String> country = r.<String> get("country").get("name");
		q.select(cb.construct(SimpleCity.class, city, country));

		final List<SimpleCity> resultList = this.em().createQuery(q).getResultList();
		Collections.sort(resultList);
		Assert.assertEquals(
			"[SimpleCity [city=Istanbul, country=Turkey], SimpleCity [city=London, country=United Kingdom], SimpleCity [city=New York, country=United States of America]]",
			resultList.toString());
	}

	/**
	 * 
	 * @since $version
	 */
	@Test
	public void testDateExpression() {
		this.persist(this.person());
		this.commit();

		this.close();

		final CriteriaBuilderImpl cb = this.em().getCriteriaBuilder();
		final CriteriaQueryImpl<Person> cq = cb.createQuery(Person.class);

		final RootImpl<Person> r = cq.from(Person.class);

		cq.where(cb.equal(r.<Date> get("startDate"), getStartDate().getTime()));

		cq.select(r);

		final List<Person> resultList = this.em().createQuery(cq).getResultList();

		Assert.assertEquals(1, resultList.size());
	}

	/**
	 * 
	 * @since 2.0.0
	 */
	@Test
	public void testRestriction() {
		final Person person = this.person();
		this.persist(person);
		this.persist(this.person());
		this.commit();
		this.close();

		final CriteriaBuilderImpl cb = this.em().getCriteriaBuilder();
		final CriteriaQueryImpl<Person> q = cb.createQuery(Person.class);
		final RootImpl<Person> r = q.from(Person.class);
		q.select(r);
		r.join("addresses");
		r.alias("p");
		r.fetch("addresses").fetch("country");
		r.fetch("phones");

		final AbstractPath<Object> id = r.get("id");
		final ParameterExpressionImpl<Integer> p = cb.parameter(Integer.class);

		q.where(cb.equal(id, p));

		final TypedQuery<Person> tq = this.em().createQuery(q);
		tq.setParameter(1, person.getId());

		final List<Person> resultList = tq.getResultList();
		Assert.assertEquals(18, resultList.size());
		Assert.assertEquals(3, resultList.get(0).getAddresses().size());
	}

	/**
	 * 
	 * @since 2.0.0
	 * @author asimarslan
	 */
	@Test
	public void testRootCount() {
		this.persist(this.person());
		this.persist(this.person());
		this.commit();
		//
		this.close();

		final CriteriaBuilderImpl cb = this.em().getCriteriaBuilder();
		final CriteriaQueryImpl<Long> q = cb.createQuery(Long.class);
		final RootImpl<Person> r = q.from(Person.class);

		final Long count = this.em().createQuery(q.select(cb.count(r))).getSingleResult();

		Assert.assertEquals(2, count.longValue());
	}

	/**
	 * 
	 * @since 2.0.0
	 */
	@Test
	public void testRootDistinct() {
		this.persist(this.person());
		this.persist(this.person());
		this.commit();

		this.close();

		final CriteriaBuilderImpl cb = this.em().getCriteriaBuilder();
		final CriteriaQueryImpl<Person> q = cb.createQuery(Person.class);
		final RootImpl<Person> r = q.from(Person.class);
		q.select(r);
		r.alias("p");
		r.fetch("addresses").fetch("country");
		q.distinct(true);

		final List<Person> resultList = this.em().createQuery(q).getResultList();
		Assert.assertEquals(2, resultList.size());
		Assert.assertEquals(3, resultList.get(0).getAddresses().size());
	}

	/**
	 * 
	 * @since 2.0.0
	 */
	@Test
	public void testRootFetch() {
		this.persist(this.person());
		this.persist(this.person());
		this.commit();

		this.close();

		final CriteriaBuilderImpl cb = this.em().getCriteriaBuilder();
		final CriteriaQueryImpl<Person> q = cb.createQuery(Person.class);
		final RootImpl<Person> r = q.from(Person.class);
		q.select(r);
		r.alias("p");
		r.fetch("addresses").fetch("country");
		r.fetch("phones");

		final List<Person> resultList = this.em().createQuery(q).getResultList();
		Assert.assertEquals(12, resultList.size());
		Assert.assertEquals(3, resultList.get(0).getAddresses().size());
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

		final CriteriaBuilderImpl cb = this.em().getCriteriaBuilder();
		final CriteriaQueryImpl<Person> q = cb.createQuery(Person.class);
		final RootImpl<Person> r = q.from(Person.class);
		q.select(r);
		r.alias("p");
		r.fetch("addresses").fetch("country");
		r.join("addresses");

		final List<Person> resultList = this.em().createQuery(q).getResultList();
		Assert.assertEquals(18, resultList.size());
		Assert.assertEquals(3, resultList.get(0).getAddresses().size());
	}

	/**
	 * @since 2.0.0
	 */
	@Test
	public void testSimple() {
		final CriteriaBuilderImpl cb = this.em().getCriteriaBuilder();

		final CriteriaQueryImpl<Country> q = cb.createQuery(Country.class);
		final RootImpl<Country> r = q.from(Country.class);
		q.select(r);

		final List<Country> resultList = this.em().createQuery(q).getResultList();

		Assert.assertEquals(3, resultList.size());
	}

	/**
	 * @since 2.0.0
	 */
	@Test
	public void testSimple2() {
		final CriteriaBuilderImpl cb = this.em().getCriteriaBuilder();

		final CriteriaQueryImpl<Country> q = cb.createQuery(Country.class);
		final RootImpl<Country> r = q.from(Country.class);
		q.select(r);

		final ParameterExpressionImpl<Country> p = cb.parameter(Country.class);
		q.where(cb.equal(r, p));

		final QueryImpl<Country> tq = this.em().createQuery(q);
		tq.setParameter(p, SimpleCriteriaTest.TR);

		final List<Country> resultList = tq.getResultList();

		Assert.assertEquals(1, resultList.size());
	}

	/**
	 * 
	 * @since 2.0.0
	 */
	@Test
	public void testSimple3() {
		Person person = this.person();

		this.persist(person);
		this.commit();
		this.close();

		person = this.find(Person.class, person.getId());

		final CriteriaBuilderImpl criteriaBuilder = this.em().getCriteriaBuilder();
		final CriteriaQueryImpl<Address> criteriaQuery = criteriaBuilder.createQuery(Address.class);
		final RootImpl<Address> from = criteriaQuery.from(Address.class);
		final List<PredicateImpl> predicates = new ArrayList<PredicateImpl>();

		predicates.add(criteriaBuilder.equal(from.get("person"), person));

		criteriaQuery.select(from);
		criteriaQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()])));
		final TypedQuery<Address> query = this.em().createQuery(criteriaQuery);

		query.getResultList();
	}

	/**
	 * Test the empty predicates.
	 * 
	 * @since 2.0.0
	 */
	@Test
	public void testSimple4() {
		final CriteriaBuilderImpl criteriaBuilder = this.em().getCriteriaBuilder();
		final CriteriaQueryImpl<Address> criteriaQuery = criteriaBuilder.createQuery(Address.class);
		final RootImpl<Address> from = criteriaQuery.from(Address.class);
		final List<PredicateImpl> predicates = new ArrayList<PredicateImpl>();

		criteriaQuery.select(from);
		criteriaQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()])));
		final TypedQuery<Address> query = this.em().createQuery(criteriaQuery);

		query.getResultList();
	}
}
