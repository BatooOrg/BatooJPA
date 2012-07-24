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
package org.batoo.jpa.core.test.jpql.simple;

import java.util.List;

import javax.persistence.TypedQuery;

import junit.framework.Assert;

import org.batoo.jpa.core.test.BaseCoreTest;
import org.batoo.jpa.core.test.criteria.Address;
import org.batoo.jpa.core.test.criteria.Country;
import org.batoo.jpa.core.test.criteria.HomePhone;
import org.batoo.jpa.core.test.criteria.Person;
import org.batoo.jpa.core.test.criteria.WorkPhone;
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
		final Person person = new Person("Ceylan");

		new Address(person, SimpleJpqlTest.CITY_ISTANBUL, SimpleJpqlTest.TR);
		new Address(person, SimpleJpqlTest.CITY_NEW_YORK, SimpleJpqlTest.USA);
		new Address(person, SimpleJpqlTest.CITY_LONDON, SimpleJpqlTest.UK);

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
	@Test
	public void testAssociation() {
		this.persist(this.person());
		this.persist(this.person());
		this.commit();

		this.close();

		final TypedQuery<Address> q = this.em().createQuery("select a from Person p inner join p.addresses a", Address.class);

		final List<Address> resultList = q.getResultList();
		Assert.assertEquals(6, resultList.size());
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

		final TypedQuery<Address> q = this.em().createQuery(//
			"select a from Person p\n" + //
				"left join p.addresses as a \n" + //
				"join fetch a.country \n" + //
				"join fetch a.person \n" + //
				"where p = :person", Address.class);
		q.setParameter("person", person);

		final List<Address> resultList = q.getResultList();
		Assert.assertEquals(3, resultList.size());
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

		final TypedQuery<Person> q = this.em().createQuery(//
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
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testSimple0() {
		final TypedQuery<Country> q = this.em().createQuery("select c from Country c", Country.class);
		final List<Country> resultList = q.getResultList();
		Assert.assertEquals("[Country [name=Turkey], Country [name=United States of America], Country [name=United Kingdom]]", resultList.toString());
	}

	/**
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testSimple1() {
		final TypedQuery<Country> q = this.em().createQuery("select c from Country c where c = :country", Country.class);

		q.setParameter("country", SimpleJpqlTest.TR);

		final List<Country> resultList = q.getResultList();

		Assert.assertEquals(1, resultList.size());
	}
}
