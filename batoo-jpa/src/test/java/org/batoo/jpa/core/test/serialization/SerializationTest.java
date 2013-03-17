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
package org.batoo.jpa.core.test.serialization;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import junit.framework.Assert;

import org.batoo.jpa.core.test.BaseCoreTest;
import org.junit.Test;

/**
 * Test for serialization
 * 
 * @author asimarslan
 * @author ylemoigne
 * @since 2.0.1
 */
public class SerializationTest extends BaseCoreTest {

	private CompositeEntity data() {
		final Calendar testDate = Calendar.getInstance();

		final ConcreteEntity concreteEntityInst1 = new ConcreteEntity();
		concreteEntityInst1.setDate(testDate.getTime());
		concreteEntityInst1.setCode("ConcreteEntity-I1");
		concreteEntityInst1.setMyAbstractProperty("I1abstract");

		final EntityB entityBInstance1 = new EntityB();
		entityBInstance1.setCalendar(testDate);
		entityBInstance1.setCode("EntityB-I1");
		entityBInstance1.setConcreteEntity(concreteEntityInst1);

		final EntityB entityBInstance2 = new EntityB();
		entityBInstance2.setCalendar(testDate);
		entityBInstance2.setCode("EntityB-I2");
		entityBInstance2.setConcreteEntity(concreteEntityInst1);

		final EntityB entityBInstance3 = new EntityB();
		entityBInstance3.setCalendar(testDate);
		entityBInstance3.setCode("EntityB-I3");
		entityBInstance3.setConcreteEntity(concreteEntityInst1);

		final Set<EntityB> entitiesB = new HashSet<EntityB>();
		entitiesB.add(entityBInstance1);
		entitiesB.add(entityBInstance2);
		entitiesB.add(entityBInstance3);

		concreteEntityInst1.setEntitiesB(entitiesB);

		final CompositeEntity compositeEntity1 = new CompositeEntity();
		compositeEntity1.setCode("CompositeEntity-I1");
		compositeEntity1.setContreteEntity(concreteEntityInst1);

		final CompositeEntity compositeEntity2 = new CompositeEntity();
		compositeEntity2.setCode("CompositeEntity-I2");
		compositeEntity2.setContreteEntity(concreteEntityInst1);
		compositeEntity2.setNextComposite(compositeEntity1);

		final ConcreteEntity concreteEntityInst2 = new ConcreteEntity();
		concreteEntityInst2.setDate(testDate.getTime());
		concreteEntityInst2.setCode("ConcreteEntity-I2");
		concreteEntityInst2.setMyAbstractProperty("I2abstract");

		final CompositeEntity compositeEntity3 = new CompositeEntity();
		compositeEntity3.setCode("CompositeEntity-I3");
		compositeEntity3.setContreteEntity(concreteEntityInst2);
		compositeEntity3.setNextComposite(compositeEntity2);

		return compositeEntity3;
	}

	/**
	 * Test for entity serialization
	 * 
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @since 2.0.1
	 */
	@Test
	public void testSerialization() throws IOException, ClassNotFoundException {
		final CompositeEntity data = data();
		this.persist(data);
		this.commit();
		this.close();

		final CompositeEntity entity = this.find(CompositeEntity.class, data.getId());

		//

		final PipedOutputStream pos = new PipedOutputStream();
		final PipedInputStream pis = new PipedInputStream(pos);

		final ObjectOutputStream oos = new ObjectOutputStream(pos);
		final ObjectInputStream ois = new ObjectInputStream(pis);

		oos.writeObject(entity);

		final CompositeEntity transportedEntity = (CompositeEntity) ois.readObject();

		Assert.assertEquals(entity.getId(), transportedEntity.getId());
		Assert.assertEquals(entity.getTimeStamp(), transportedEntity.getTimeStamp());
		Assert.assertEquals(entity.getNextComposite(), transportedEntity.getNextComposite());

		Assert.assertEquals(entity.getNextComposite(), transportedEntity.getNextComposite());

		Assert.assertNull(transportedEntity.getContreteEntity());

	}

}
