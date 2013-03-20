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
package org.batoo.jpa.community.test.unsupportedOperationException;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;

import org.batoo.jpa.community.test.BaseCoreTest;
import org.batoo.jpa.community.test.NoDatasource;
import org.junit.Test;

import com.google.common.collect.Sets;

@SuppressWarnings("javadoc")
public class TestUnsupportedOperationException extends BaseCoreTest {
	@Test
	@NoDatasource
	public void test() {
		final Map<FieldLocale, String> translationMap = new HashMap<FieldLocale, String>();
		translationMap.put(FieldLocale.fr, "Bonjour Asim !");
		translationMap.put(FieldLocale.gb, "Hello Asim !");
		translationMap.put(FieldLocale.tr, "Merhaba Asim !");

		final EntityA entityAPrime = new EntityA();
		entityAPrime.setName(translationMap);
		final EntityA entityASecond = new EntityA();
		entityASecond.setName(translationMap);
		final EntityA entityAThird = new EntityA();
		entityAThird.setName(translationMap);

		final EntityB entityBFoo = new EntityB();
		entityBFoo.setCode("foo");
		entityBFoo.setEntitiesA(Sets.newHashSet(entityAPrime, entityASecond, entityAThird));

		entityAPrime.setEntityB(entityBFoo);
		entityASecond.setEntityB(entityBFoo);
		entityAThird.setEntityB(entityBFoo);

		this.begin();
		persist(entityBFoo);
		this.commit();
		this.close();
		em().clear();

		final CriteriaBuilder qBuilder = em().getCriteriaBuilder();
		final CriteriaQuery<EntityA> query = qBuilder.createQuery(EntityA.class);
		final Join<EntityA, EntityB> join = query.from(EntityA.class).join(EntityA_.entityB);
		query.where(qBuilder.equal(join.get(EntityB_.code), "foo"));

		em().createQuery(query).getResultList();
	}
}
