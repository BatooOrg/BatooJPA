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
package org.batoo.jpa.core.test.criteria.elementcollection;

import java.util.List;

import junit.framework.Assert;

import org.batoo.jpa.core.impl.criteria.CriteriaBuilderImpl;
import org.batoo.jpa.core.impl.criteria.CriteriaQueryImpl;
import org.batoo.jpa.core.impl.criteria.RootImpl;
import org.batoo.jpa.core.test.BaseCoreTest;
import org.batoo.jpa.core.test.criteria.ElementCollectionParent;
import org.junit.Test;

/**
 * @author hceylan
 * 
 * @since $version
 */
public class ElementCollectionCriteriaTest extends BaseCoreTest {

	private ElementCollectionParent parent() {
		final ElementCollectionParent parent = new ElementCollectionParent();
		parent.setValue("Ceylan");

		parent.getCodes1().add("Value1");
		parent.getCodes1().add("Value2");

		return parent;
	}

	/**
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testCollection() {
		this.persist(this.parent());
		this.persist(this.parent());
		this.commit();

		this.close();

		final CriteriaBuilderImpl cb = (CriteriaBuilderImpl) this.em().getCriteriaBuilder();
		final CriteriaQueryImpl<ElementCollectionParent> q = cb.createQuery(ElementCollectionParent.class);
		final RootImpl<ElementCollectionParent> r = q.from(ElementCollectionParent.class);
		r.alias("p");
		r.fetch("codes1");
		r.fetch("codes2");
		r.fetch("codes3");
		r.fetch("codes4");
		q.select(r);

		final List<ElementCollectionParent> resultList = this.em().createQuery(q).getResultList();
		Assert.assertEquals(4, resultList.size());
	}
}
