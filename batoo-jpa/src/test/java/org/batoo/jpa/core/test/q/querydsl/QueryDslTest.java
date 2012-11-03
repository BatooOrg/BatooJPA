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
package org.batoo.jpa.core.test.q.querydsl;

import junit.framework.Assert;

import org.batoo.jpa.core.test.BaseCoreTest;
import org.batoo.jpa.core.test.q.Country;
import org.batoo.jpa.core.test.q.QCountry;
import org.junit.Before;
import org.junit.Test;

import com.mysema.query.jpa.impl.JPAQuery;

/**
 * Tests for QueryDSL - http://www.querydsl.com
 * 
 * @author hceylan
 * @since $version
 */
public class QueryDslTest extends BaseCoreTest {

	private static final String COUNTRY_UK = "United Kingdom";
	private static final String COUNTRY_USA = "United States of America";
	private static final String COUNTRY_TR = "Turkey";

	private static final String COUNTRY_CODE_UK = "UK";
	private static final String COUNTRY_CODE_USA = "USA";
	private static final String COUNTRY_CODE_TR = "TR";
	private static final String COUNTRY_CODE_BR = "BR";

	private static Country TR = new Country(QueryDslTest.COUNTRY_CODE_TR, QueryDslTest.COUNTRY_TR);
	private static Country USA = new Country(QueryDslTest.COUNTRY_CODE_USA, QueryDslTest.COUNTRY_USA);
	private static Country UK = new Country(QueryDslTest.COUNTRY_CODE_UK, QueryDslTest.COUNTRY_UK);
	private static Country BROKEN = new Country(QueryDslTest.COUNTRY_CODE_BR, null);

	/**
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public QueryDslTest() {
		super();
	}

	/**
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Before
	public void prepareCountries() {
		this.begin();

		this.persist(QueryDslTest.TR);
		this.persist(QueryDslTest.USA);
		this.persist(QueryDslTest.UK);
		this.persist(QueryDslTest.BROKEN);

		this.commit();
	}

	/**
	 * Tests the query dsl.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testQueryDsl() {
		final JPAQuery q = new JPAQuery(this.em());

		final QCountry c = QCountry.country;
		final Country country = q.from(c).where(c.name.eq("Turkey")).uniqueResult(c);

		Assert.assertEquals(QueryDslTest.TR, country);
	}
}
