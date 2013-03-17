package org.batoo.jpa.community.test.serializationTestModel;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import org.batoo.jpa.community.test.BaseCoreTest;
import org.batoo.jpa.community.test.NoDatasource;
import org.junit.Test;

public class TestSerializationModel extends BaseCoreTest {
	@Test
	@NoDatasource
	public void test() {
		Calendar testDate = Calendar.getInstance();
		
		ConcreteEntity concreteEntityInst1 = new ConcreteEntity();
		concreteEntityInst1.setDate(testDate.getTime());
		concreteEntityInst1.setCode("ConcreteEntity-I1");
		concreteEntityInst1.setMyAbstractProperty("I1abstract");
		
		EntityB entityBInstance1 = new EntityB();
		entityBInstance1.setCalendar(testDate);
		entityBInstance1.setCode("EntityB-I1");
		entityBInstance1.setConcreteEntity(concreteEntityInst1);
		
		EntityB entityBInstance2 = new EntityB();
		entityBInstance2.setCalendar(testDate);
		entityBInstance2.setCode("EntityB-I2");
		entityBInstance2.setConcreteEntity(concreteEntityInst1);
		
		EntityB entityBInstance3 = new EntityB();
		entityBInstance3.setCalendar(testDate);
		entityBInstance3.setCode("EntityB-I3");
		entityBInstance3.setConcreteEntity(concreteEntityInst1);
		
		Set<EntityB> entitiesB = new HashSet<EntityB>();
		entitiesB.add(entityBInstance1);
		entitiesB.add(entityBInstance2);
		entitiesB.add(entityBInstance3);
		
		concreteEntityInst1.setEntitiesB(entitiesB);
		
		CompositeEntity compositeEntity1 = new CompositeEntity();
		compositeEntity1.setCode("CompositeEntity-I1");
		compositeEntity1.setContreteEntity(concreteEntityInst1);
		
		CompositeEntity compositeEntity2 = new CompositeEntity();
		compositeEntity2.setCode("CompositeEntity-I2");
		compositeEntity2.setContreteEntity(concreteEntityInst1);
		compositeEntity2.setNextComposite(compositeEntity1);

		ConcreteEntity concreteEntityInst2 = new ConcreteEntity();
		concreteEntityInst2.setDate(testDate.getTime());
		concreteEntityInst2.setCode("ConcreteEntity-I2");
		concreteEntityInst2.setMyAbstractProperty("I2abstract");
		
		CompositeEntity compositeEntity3 = new CompositeEntity();
		compositeEntity3.setCode("CompositeEntity-I3");
		compositeEntity3.setContreteEntity(concreteEntityInst2);
		compositeEntity3.setNextComposite(compositeEntity2);
		
		persist(compositeEntity3);
		
		this.commit();
		this.close();
	}
}
