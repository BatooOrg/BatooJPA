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

import org.batoo.jpa.core.BLogger;
import org.batoo.jpa.core.test.BaseCoreTest;
import org.junit.Test;

/**
 * @author hceylan
 * 
 * @since $version
 */
public class FirstCriteriaTest extends BaseCoreTest {

	private static final BLogger LOG = BLogger.getLogger(FirstCriteriaTest.class);

	/**
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testFirstCriteria() {
		try {

			// final Country c1 = new Country("TR", "Turkey");
			// final Country c2 = new Country("UK", "United Kingdom");
			// final Country c3 = new Country("US", "United States Of America");
			//
			// this.begin();
			//
			// this.em().persist(c1);
			// this.em().persist(c2);
			// this.em().persist(c3);
			//
			// this.commit();
			//
			// final CriteriaBuilderImpl cb = (CriteriaBuilderImpl) this.em().getCriteriaBuilder();
			// CriteriaQueryImpl<Country> q = cb.createQuery(Country.class);
			// final ParameterExpression<String> pe = cb.parameter(String.class);
			//
			// final RootImpl<Country> r1 = q.from(Country.class);
			// q = q.select(r1);
			//
			// final BasicAttributeImpl<? super Country, ?>[] idAttributes = r1.getModel().getIdAttributes();
			//
			// cb.equal(r1.get(idAttributes[0]), pe);
			//
			// final TypedQueryImpl<Country> tq = (TypedQueryImpl<Country>) this.em().createQuery(q);
			// final List<Country> resultList = tq.getResultList();
			//
			// LOG.info("{0}", resultList);

		}
		catch (final Exception e) {
			LOG.error(e, "");
		}
	}
}
