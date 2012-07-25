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
package org.batoo.jpa.core.test.q.criteria.embedded;

import java.util.List;

import javax.persistence.criteria.Fetch;

import junit.framework.Assert;

import org.batoo.jpa.core.impl.criteria.CriteriaBuilderImpl;
import org.batoo.jpa.core.impl.criteria.CriteriaQueryImpl;
import org.batoo.jpa.core.impl.criteria.RootImpl;
import org.batoo.jpa.core.test.BaseCoreTest;
import org.batoo.jpa.core.test.q.criteria.Address2;
import org.batoo.jpa.core.test.q.criteria.Contact;
import org.batoo.jpa.core.test.q.criteria.Country;
import org.batoo.jpa.core.test.q.criteria.Person2;
import org.junit.Before;
import org.junit.Test;

/**
 * @author hceylan
 * 
 * @since $version
 */
public class EmbeddedCriteriaTest extends BaseCoreTest {

	private static final String COUNTRY_UK = "United Kingdom";
	private static final String COUNTRY_USA = "United States of America";
	private static final String COUNTRY_TR = "Turkey";

	private static final String COUNTRY_CODE_UK = "UK";
	private static final String COUNTRY_CODE_USA = "USA";
	private static final String COUNTRY_CODE_TR = "TR";

	private static final String CITY_LONDON = "London";
	private static final String CITY_NEW_YORK = "New York";
	private static final String CITY_ISTANBUL = "Istanbul";

	private static Country TR = new Country(EmbeddedCriteriaTest.COUNTRY_CODE_TR, EmbeddedCriteriaTest.COUNTRY_TR);
	private static Country USA = new Country(EmbeddedCriteriaTest.COUNTRY_CODE_USA, EmbeddedCriteriaTest.COUNTRY_USA);
	private static Country UK = new Country(EmbeddedCriteriaTest.COUNTRY_CODE_UK, EmbeddedCriteriaTest.COUNTRY_UK);

	private Person2 person1() {
		return new Person2("Michael Jackson", EmbeddedCriteriaTest.CITY_NEW_YORK, EmbeddedCriteriaTest.USA, "111 111-1111");
	}

	private Person2 person2() {
		return new Person2("Sting", EmbeddedCriteriaTest.CITY_LONDON, EmbeddedCriteriaTest.UK, "222 222-22222");
	}

	private Person2 person3() {
		return new Person2("Tarkan", EmbeddedCriteriaTest.CITY_ISTANBUL, EmbeddedCriteriaTest.TR, "333 333-3333");
	}

	/**
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Before
	public void prepareCountries() {
		this.begin();

		this.persist(EmbeddedCriteriaTest.TR);
		this.persist(EmbeddedCriteriaTest.USA);
		this.persist(EmbeddedCriteriaTest.UK);

		this.commit();
	}

	/**
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testEmbedded() {
		this.persist(this.person1());
		this.persist(this.person2());
		this.persist(this.person3());
		this.commit();

		this.close();

		final CriteriaBuilderImpl cb = this.em().getCriteriaBuilder();
		final CriteriaQueryImpl<Person2> q = cb.createQuery(Person2.class);
		final RootImpl<Person2> r = q.from(Person2.class);
		q.select(r);
		final Fetch<Person2, Contact> c = r.<Contact> fetch("contact");
		final Fetch<Contact, Address2> a = c.<Address2> fetch("address");
		a.fetch("country");
		c.fetch("phone");

		final List<Person2> resultList = this.em().createQuery(q).getResultList();
		Assert.assertEquals(
			"[[name=Michael Jackson, contact=Contact [address=Address2 [country=Country [name=United States of America], city=New York], phone=Phone [phoneNo=111 111-1111]]], "
				+ "[name=Sting, contact=Contact [address=Address2 [country=Country [name=United Kingdom], city=London], phone=Phone [phoneNo=222 222-22222]]], "
				+ "[name=Tarkan, contact=Contact [address=Address2 [country=Country [name=Turkey], city=Istanbul], phone=Phone [phoneNo=333 333-3333]]]]",
			resultList.toString());
	}

	/**
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testEmbedded0() {
		final Person2 person1 = this.person1();
		final Person2 person2 = this.person2();
		final Person2 person3 = this.person3();

		this.persist(person1);
		this.persist(person2);
		this.persist(person3);
		this.commit();

		this.close();

		final Person2 person = this.find(Person2.class, person1.getId());

		Assert.assertEquals(
			"[name=Michael Jackson, contact=Contact [address=Address2 [country=Country [name=United States of America], city=New York], phone=Phone [phoneNo=111 111-1111]]]",
			person.toString());
	}
}
