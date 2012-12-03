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
package org.batoo.jpa.core.test.q.jpql.update;

import junit.framework.Assert;

import org.batoo.jpa.core.test.BaseCoreTest;
import org.batoo.jpa.core.test.q.Country;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * 
 * @author hceylan
 * @since 2.0.0
 */
public class UpdateJpqlTest extends BaseCoreTest {

	private static final String COUNTRY_UK = "United Kingdom";
	private static final String COUNTRY_USA = "United States of America";
	private static final String COUNTRY_TR = "Turkey";

	private static final String COUNTRY_CODE_UK = "UK";
	private static final String COUNTRY_CODE_USA = "USA";
	private static final String COUNTRY_CODE_TR = "TR";
	private static final String COUNTRY_CODE_BR = "BR";

	private static Country TR = new Country(UpdateJpqlTest.COUNTRY_CODE_TR, UpdateJpqlTest.COUNTRY_TR);
	private static Country USA = new Country(UpdateJpqlTest.COUNTRY_CODE_USA, UpdateJpqlTest.COUNTRY_USA);
	private static Country UK = new Country(UpdateJpqlTest.COUNTRY_CODE_UK, UpdateJpqlTest.COUNTRY_UK);
	private static Country BROKEN = new Country(UpdateJpqlTest.COUNTRY_CODE_BR, null);

	/**
	 * 
	 * @since 2.0.0
	 */
	@Before
	public void prepareCountries() {
		this.begin();

		this.persist(UpdateJpqlTest.TR);
		this.persist(UpdateJpqlTest.USA);
		this.persist(UpdateJpqlTest.UK);
		this.persist(UpdateJpqlTest.BROKEN);

		this.commit();
	}

	/**
	 * 
	 * @since 2.0.0
	 */
	@Test
	public void testDelete() {
		Assert.assertEquals((Long) 4l, this.cq("select count(c) from Country c", Long.class).getSingleResult());

		this.begin();

		this.cu("delete from Country c where c = :code").setParameter("code", UpdateJpqlTest.TR).executeUpdate();

		this.commit();

		Assert.assertEquals((Long) 3l, this.cq("select count(c) from Country c", Long.class).getSingleResult());
	}

	/**
	 * 
	 * @since 2.0.0
	 */
	@Test
	public void testUpdate() {
		this.begin();

		this.cu("update Country c set c.name = :name where c = :code").setParameter("code", UpdateJpqlTest.TR).setParameter("name", "TURKEY").executeUpdate();

		this.commit();
	}

	/**
	 * 
	 * @since 2.0.0
	 */
	@Test
	public void testUpdate2() {
		this.begin();

		this.cu("update Country c set name = :name where c = :code").setParameter("code", UpdateJpqlTest.TR).setParameter("name", "TURKEY").executeUpdate();

		this.commit();
	}
}
