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
package org.batoo.jpa.core.test.criteria.simple.first;

import org.batoo.jpa.common.log.BLogger;
import org.batoo.jpa.common.log.BLoggerFactory;
import org.batoo.jpa.core.impl.criteria2.CriteriaBuilderImpl;
import org.batoo.jpa.core.impl.criteria2.CriteriaQueryImpl;
import org.batoo.jpa.core.impl.criteria2.TypedQueryImpl;
import org.batoo.jpa.core.test.BaseCoreTest;
import org.batoo.jpa.core.test.criteria.Address;
import org.batoo.jpa.core.test.criteria.Country;
import org.batoo.jpa.core.test.criteria.HomePhone;
import org.batoo.jpa.core.test.criteria.Person;
import org.batoo.jpa.core.test.criteria.WorkPhone;
import org.junit.Before;
import org.junit.Test;

/**
 * @author hceylan
 * 
 * @since $version
 */
public class FirstCriteriaTest extends BaseCoreTest {

	private static final BLogger LOG = BLoggerFactory.getLogger(FirstCriteriaTest.class);

	private static final String COUNTRY_UK = "United Kingdom";
	private static final String COUNTRY_USA = "United States of America";
	private static final String COUNTRY_TR = "Turkey";

	private static final String COUNTRY_CODE_UK = "UK";
	private static final String COUNTRY_CODE_USA = "USA";
	private static final String COUNTRY_CODE_TR = "TR";

	private static final String CITY_LONDON = "London";
	private static final String CITY_NEW_YORK = "New York";
	private static final String CITY_ISTANBUL = "Istanbul";

	private static Country TR = new Country(FirstCriteriaTest.COUNTRY_CODE_TR, FirstCriteriaTest.COUNTRY_TR);
	private static Country USA = new Country(FirstCriteriaTest.COUNTRY_CODE_USA, FirstCriteriaTest.COUNTRY_USA);
	private static Country UK = new Country(FirstCriteriaTest.COUNTRY_CODE_UK, FirstCriteriaTest.COUNTRY_UK);

	private Person person() {
		final Person person = new Person("Ceylan");

		new Address(person, FirstCriteriaTest.CITY_ISTANBUL, FirstCriteriaTest.TR);
		new Address(person, FirstCriteriaTest.CITY_NEW_YORK, FirstCriteriaTest.USA);
		new Address(person, FirstCriteriaTest.CITY_LONDON, FirstCriteriaTest.UK);

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
		this.persist(FirstCriteriaTest.TR);
		this.persist(FirstCriteriaTest.USA);
		this.persist(FirstCriteriaTest.UK);
		this.commit();
	}

	/**
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testFirstCriteria() {
		try {
			final CriteriaBuilderImpl cb = (CriteriaBuilderImpl) this.em().getCriteriaBuilder();
			final CriteriaQueryImpl<Country> q = cb.createQuery(Country.class);
			q.from(Country.class);

			// cb.parameter(String.class);
			// final AbstractPath<String> p = r1.get("code");
			// final ParameterExpressionImpl<String> pe = cb.parameter(String.class);
			// q = q.where(cb.equal(p, pe));
			//
			((TypedQueryImpl<Country>) this.em().createQuery(q)).getResultList();

			FirstCriteriaTest.LOG.info("\n{0}", q);
		}
		catch (final Exception e) {
			FirstCriteriaTest.LOG.error(e, "");
		}
	}
}
