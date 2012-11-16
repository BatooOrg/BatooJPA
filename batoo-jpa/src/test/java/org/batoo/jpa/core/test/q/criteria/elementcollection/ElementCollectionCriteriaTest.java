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
package org.batoo.jpa.core.test.q.criteria.elementcollection;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Path;

import junit.framework.Assert;

import org.batoo.jpa.core.impl.criteria.CriteriaBuilderImpl;
import org.batoo.jpa.core.impl.criteria.CriteriaQueryImpl;
import org.batoo.jpa.core.impl.criteria.RootImpl;
import org.batoo.jpa.core.impl.criteria.join.MapJoinImpl;
import org.batoo.jpa.core.test.BaseCoreTest;
import org.batoo.jpa.core.test.q.ElementCollectionParent;
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

	private ElementCollectionParent parent2() {
		final ElementCollectionParent parent = new ElementCollectionParent();
		parent.setValue("Ceylan");

		parent.getCodes5().put("Key1", "Value1");
		parent.getCodes5().put("Key2", "Value2");

		return parent;
	}

	/**
	 * 
	 * @since $version
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

	/**
	 * 
	 * @since $version
	 */
	@Test
	@SuppressWarnings("rawtypes")
	public void testMapEntries() {
		this.persist(this.parent2());
		this.commit();

		this.close();

		final CriteriaBuilderImpl cb = (CriteriaBuilderImpl) this.em().getCriteriaBuilder();
		final CriteriaQueryImpl<Entry> q = cb.createQuery(Entry.class);
		final RootImpl<ElementCollectionParent> r = q.from(ElementCollectionParent.class);
		r.alias("p");

		final MapJoinImpl<ElementCollectionParent, String, String> j = (MapJoinImpl<ElementCollectionParent, String, String>) r.<String, String> joinMap("codes5");
		final Expression<Entry<String, String>> k = j.entry();
		q.select(k);

		final List<Entry> resultList = this.em().createQuery(q).getResultList();
		Collections.sort(resultList, new Comparator<Entry>() {

			@Override
			public int compare(Entry o1, Entry o2) {
				return ((String) o1.getKey()).compareTo((String) o2.getKey());
			}
		});

		Assert.assertEquals("[Key1=Value1, Key2=Value2]", resultList.toString());
	}

	/**
	 * 
	 * @since $version
	 */
	@Test
	public void testMapKey() {
		this.persist(this.parent2());
		this.commit();

		this.close();

		final CriteriaBuilderImpl cb = (CriteriaBuilderImpl) this.em().getCriteriaBuilder();
		final CriteriaQueryImpl<String> q = cb.createQuery(String.class);
		final RootImpl<ElementCollectionParent> r = q.from(ElementCollectionParent.class);
		r.alias("p");

		final MapJoinImpl<ElementCollectionParent, String, String> j = (MapJoinImpl<ElementCollectionParent, String, String>) r.<String, String> joinMap("codes5");
		final Path<String> k = j.key();
		q.select(k);

		final List<String> resultList = this.em().createQuery(q).getResultList();
		Collections.sort(resultList);

		Assert.assertEquals("[Key1, Key2]", resultList.toString());
	}

	/**
	 * 
	 * @since $version
	 */
	@Test
	public void testMapValues() {
		this.persist(this.parent2());
		this.commit();

		this.close();

		final CriteriaBuilderImpl cb = (CriteriaBuilderImpl) this.em().getCriteriaBuilder();
		final CriteriaQueryImpl<String> q = cb.createQuery(String.class);
		final RootImpl<ElementCollectionParent> r = q.from(ElementCollectionParent.class);
		r.alias("p");

		final MapJoinImpl<ElementCollectionParent, String, String> j = (MapJoinImpl<ElementCollectionParent, String, String>) r.<String, String> joinMap("codes5");
		final Path<String> k = j.value();
		q.select(k);

		final List<String> resultList = this.em().createQuery(q).getResultList();
		Collections.sort(resultList);

		Assert.assertEquals("[Value1, Value2]", resultList.toString());
	}
}
