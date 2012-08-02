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
package org.batoo.jpa.core.test.q.criteria.tuple;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.persistence.Tuple;

import junit.framework.Assert;

import org.batoo.jpa.core.impl.criteria.CriteriaBuilderImpl;
import org.batoo.jpa.core.impl.criteria.CriteriaQueryImpl;
import org.batoo.jpa.core.impl.criteria.QueryImpl;
import org.batoo.jpa.core.impl.criteria.RootImpl;
import org.batoo.jpa.core.impl.criteria.path.AbstractPath;
import org.batoo.jpa.core.test.BaseCoreTest;
import org.batoo.jpa.core.test.q.Country;
import org.batoo.jpa.core.test.q.Person;
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
		final GregorianCalendar start = new GregorianCalendar();
		start.set(Calendar.YEAR, 2000);
		start.set(Calendar.MONTH, 12);
		start.set(Calendar.DAY_OF_MONTH, 31);

		final Person person = new Person("Ceylan", 38, start.getTime());

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
	public void testArithmeticExpression1() {
		this.persist(this.person());
		this.commit();

		this.close();

		final CriteriaBuilderImpl cb = this.em().getCriteriaBuilder();
		final CriteriaQueryImpl<Tuple> cq = cb.createTupleQuery();

		final RootImpl<Person> r = cq.from(Person.class);
		final AbstractPath<Integer> age = (AbstractPath<Integer>) r.<Integer> get("age").alias("age");
		final AbstractPath<String> name = (AbstractPath<String>) r.<String> get("name").alias("name");
		final AbstractPath<Date> date = (AbstractPath<Date>) r.<Date> get("startDate").alias("date");

		cq.multiselect(name, age, date);

		final QueryImpl<Tuple> q = this.em().createQuery(cq);

		final Tuple tuple = q.getSingleResult();

		Assert.assertEquals(tuple.get(0), tuple.get("name"));
		Assert.assertEquals(tuple.get(1), tuple.get("age"));
		Assert.assertEquals(tuple.get(2), tuple.get("date"));

		Assert.assertEquals(tuple.get(0), tuple.get(name));
		Assert.assertEquals(tuple.get(1), tuple.get(age));
		Assert.assertEquals(tuple.get(2), tuple.get(date));
	}
}
