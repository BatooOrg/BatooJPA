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
