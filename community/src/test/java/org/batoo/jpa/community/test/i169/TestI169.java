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
package org.batoo.jpa.community.test.i169;

import static org.junit.Assert.assertEquals;

import org.batoo.jpa.community.test.BaseCoreTest;
import org.batoo.jpa.community.test.NoDatasource;
import org.batoo.jpa.core.impl.instance.EnhancedInstance;
import org.junit.Test;

/**
 * Test for the issue 169
 * 
 * @author ylemoigne
 * @since $version
 */
@SuppressWarnings("javadoc")
public class TestI169 extends BaseCoreTest {
	@Test
	@NoDatasource
	public void test() {
		final ConcreteEntity entityA = new ConcreteEntity("plop", "foo");
		persist(entityA);

		final EntityPointToAbstractEntity mainEntity = new EntityPointToAbstractEntity();
		mainEntity.setAbstractEntity(entityA);
		persist(mainEntity);
		this.commit();

		this.close();

		final EntityPointToAbstractEntity mainEntityReloaded = find(EntityPointToAbstractEntity.class, mainEntity.getId());
		assertEquals(mainEntity.getId(), mainEntityReloaded.getId());
		assertEquals(mainEntity.getAbstractEntity().getId(), mainEntityReloaded.getAbstractEntity().getId());
		assertEquals(mainEntityReloaded.getAbstractEntity().getClass().getInterfaces()[0], EnhancedInstance.class);
	}
}
