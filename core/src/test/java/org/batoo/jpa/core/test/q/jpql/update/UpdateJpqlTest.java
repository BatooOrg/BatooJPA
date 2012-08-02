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
 * @since $version
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
	 * @since $version
	 * @author hceylan
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
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testUpdate() {
		Assert.assertEquals((Long) 4l, this.cq("select count(c) from Country c", Long.class).getSingleResult());

		this.cu("delete Country c where c = :code").setParameter("code", UpdateJpqlTest.TR).executeUpdate();

		Assert.assertEquals((Long) 3l, this.cq("select count(c) from Country c", Long.class).getSingleResult());
	}
}
