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

public class TestUnsupportedOperationException extends BaseCoreTest{
	@Test
	@NoDatasource
	public void test(){
		Map<FieldLocale, String> translationMap = new HashMap<FieldLocale, String>();
		translationMap.put(FieldLocale.fr, "Bonjour Asim !");
		translationMap.put(FieldLocale.gb, "Hello Asim !");
		translationMap.put(FieldLocale.tr, "Merhaba Asim !");
		
		EntityA entityAPrime = new EntityA();
		entityAPrime.setName(translationMap);
		EntityA entityASecond = new EntityA();
		entityASecond.setName(translationMap);
		EntityA entityAThird = new EntityA();
		entityAThird.setName(translationMap);
		
		EntityB entityBFoo = new EntityB();
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
		
		CriteriaBuilder qBuilder = em().getCriteriaBuilder();
		CriteriaQuery<EntityA> query = qBuilder.createQuery(EntityA.class);
		Join<EntityA, EntityB> join = query.from(EntityA.class).join(EntityA_.entityB);
		query.where(qBuilder.equal(join.get(EntityB_.code), "foo"));

		em().createQuery(query).getResultList();
	}
}
