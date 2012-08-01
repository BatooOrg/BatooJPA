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
package org.batoo.jpa.core.test.q.criteria.inheritence;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Path;

import org.batoo.jpa.core.impl.criteria.CriteriaBuilderImpl;
import org.batoo.jpa.core.impl.criteria.CriteriaQueryImpl;
import org.batoo.jpa.core.impl.criteria.RootImpl;
import org.batoo.jpa.core.impl.criteria.join.SetJoinImpl;
import org.batoo.jpa.core.test.BaseCoreTest;
import org.batoo.jpa.core.test.q.Bar;
import org.batoo.jpa.core.test.q.BaseFoo;
import org.batoo.jpa.core.test.q.FooType1;
import org.batoo.jpa.core.test.q.FooType2;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author hceylan
 * 
 * @since $version
 */
public class InheritenceCriteriaTest extends BaseCoreTest {

	private Object bar() {
		final Bar bar = new Bar();

		new FooType1(bar, "Value1");
		new FooType2(bar, 2);

		return bar;
	}

	/**
	 * @since $version
	 * @author hceylan
	 */
	@Test
	@SuppressWarnings("rawtypes")
	public void testType1() {
		this.persist(this.bar());
		this.commit();
		this.close();

		final CriteriaBuilderImpl cb = this.em().getCriteriaBuilder();

		final CriteriaQueryImpl<Class> q = cb.createQuery(Class.class);
		final RootImpl<Bar> r = q.from(Bar.class);
		final SetJoinImpl<Bar, BaseFoo> b = r.joinSet("foos");

		final Expression<Class<? extends BaseFoo>> type = b.type();
		q.select(type);

		final List<Class> resultList = this.em().createQuery(q).getResultList();

		Collections.sort(resultList, new Comparator<Class>() {

			@Override
			public int compare(Class o1, Class o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});

		Assert.assertEquals("[class org.batoo.jpa.core.test.q.FooType1, class org.batoo.jpa.core.test.q.FooType2]", resultList.toString());
	}

	/**
	 * @since $version
	 * @author hceylan
	 */
	@Test
	@SuppressWarnings("rawtypes")
	public void testType2() {
		this.persist(this.bar());
		this.commit();
		this.close();

		final CriteriaBuilderImpl cb = this.em().getCriteriaBuilder();

		final CriteriaQueryImpl<Class> q = cb.createQuery(Class.class);
		final RootImpl<Bar> r = q.from(Bar.class);
		final Path<Integer> path = r.join("foos").<Integer> get("id");
		final Expression<Class<? extends Integer>> type = path.type();

		q.select(type);

		final List<Class> resultList = this.em().createQuery(q).getResultList();

		Assert.assertEquals("[class java.lang.Integer, class java.lang.Integer]", resultList.toString());
	}
}
