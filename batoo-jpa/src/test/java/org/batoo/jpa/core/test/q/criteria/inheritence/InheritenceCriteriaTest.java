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
