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
package org.batoo.jpa.core.test.q.criteria.simple;

import java.util.Collections;
import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Selection;

import junit.framework.Assert;

import org.batoo.jpa.core.impl.criteria.CriteriaBuilderImpl;
import org.batoo.jpa.core.impl.criteria.CriteriaQueryImpl;
import org.batoo.jpa.core.impl.criteria.RootImpl;
import org.batoo.jpa.core.impl.criteria.TypedQueryImpl;
import org.batoo.jpa.core.impl.criteria.expression.ParameterExpressionImpl;
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
 * @since $version
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

	private Person person() {
		final Person person = new Person("Ceylan");

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
	 * @since $version
	 * @author hceylan
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
	 * @since $version
	 * @author hceylan
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
		q.select(r.<Address> get("addresses"));

		final List<Address> resultList = this.em().createQuery(q).getResultList();
		Assert.assertEquals(6, resultList.size());
	}

	/**
	 * 
	 * @since $version
	 * @author hceylan
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
	 * @since $version
	 * @author hceylan
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
	 * @since $version
	 * @author hceylan
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
	 * @author hceylan
	 */
	@Test
	public void testRestriction() {
		this.persist(this.person());
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
		tq.setParameter(0, 9);

		final List<Person> resultList = tq.getResultList();
		Assert.assertEquals(18, resultList.size());
		Assert.assertEquals(3, resultList.get(0).getAddresses().size());
	}

	/**
	 * 
	 * @since $version
	 * @author hceylan
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
	 * @since $version
	 * @author hceylan
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
	 * @since $version
	 * @author hceylan
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
	 * @since $version
	 * @author hceylan
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
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testSimple2() {
		final CriteriaBuilderImpl cb = this.em().getCriteriaBuilder();

		final CriteriaQueryImpl<Country> q = cb.createQuery(Country.class);
		final RootImpl<Country> r = q.from(Country.class);
		q.select(r);

		final ParameterExpressionImpl<Country> p = cb.parameter(Country.class);
		q.where(cb.equal(r, p));

		final TypedQueryImpl<Country> tq = this.em().createQuery(q);
		tq.setParameter(p, SimpleCriteriaTest.TR);

		final List<Country> resultList = tq.getResultList();

		Assert.assertEquals(1, resultList.size());
	}
}
