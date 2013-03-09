package org.batoo.jpa.community.test.i169;

import static org.junit.Assert.assertEquals;

import org.batoo.jpa.community.test.BaseCoreTest;
import org.batoo.jpa.community.test.NoDatasource;
import org.junit.Test;

public class TestI169 extends BaseCoreTest {
	@Test
	@NoDatasource
	public void test(){
		ConcreteEntity entityA = new ConcreteEntity("plop", "foo");
		persist(entityA);
		
		EntityPointToAbstractEntity mainEntity = new EntityPointToAbstractEntity();
		mainEntity.setAbstractEntity(entityA);
		persist(mainEntity);
		this.commit();

		this.close();

		EntityPointToAbstractEntity mainEntityReloaded = find(EntityPointToAbstractEntity.class, mainEntity.getId());
		assertEquals(mainEntity, mainEntityReloaded);
	}
}
