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
package org.batoo.jpa.community.test.abstractJigsaw;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import org.batoo.jpa.community.test.BaseCoreTest;
import org.batoo.jpa.community.test.NoDatasource;
import org.junit.Test;

/**
 * Test for the issue abstractJigsaw
 * 
 * @author ylemoigne
 * @since 2.0.1
 */
@SuppressWarnings("javadoc")
public class TestAbstractJigsaw extends BaseCoreTest {
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
		assertThat(mainEntityReloaded.getAbstractEntity().unwrapper().unwrap(), instanceOf(ConcreteEntity.class));
	}
}
