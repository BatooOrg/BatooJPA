/*
 * Copyright (c) 2012-2013, Batu Alp Ceylan
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

package org.batoo.jpa.core.test.enhance.abstractclass;

import java.lang.reflect.Constructor;

import javax.persistence.metamodel.EntityType;

import junit.framework.Assert;

import org.batoo.jpa.core.impl.instance.Enhancer;
import org.batoo.jpa.core.impl.manager.SessionImpl;
import org.batoo.jpa.core.test.BaseCoreTest;
import org.junit.Test;

/**
 * 
 * @author asimarslan
 * @since 2.0.1
 */
@SuppressWarnings("javadoc")
public class EnhanceTest extends BaseCoreTest {

	/**
	 * Test enhance for abstract class
	 * 
	 * @throws Exception
	 */
	@Test
	public void testEnhanceAbstractClass() throws Exception {
		final EntityType<AbstractEntity> type = this.em().getMetamodel().entity(AbstractEntity.class);

		final Class<? extends AbstractEntity> enhanced = Enhancer.enhance(type);

		Assert.assertEquals(AbstractEntity.class, enhanced.getSuperclass());

		final Constructor<? extends AbstractEntity> constructor = enhanced.getConstructor(Class.class, SessionImpl.class, Object.class, boolean.class);
		constructor.newInstance(null, null, null, true);
	}

	/**
	 * Test enhance for a class that extends an abstract class
	 * 
	 * @throws Exception
	 */
	@Test
	public void testEnhanceClassExtendsAbstractClass() throws Exception {
		final EntityType<ConcreteEntity> type = this.em().getMetamodel().entity(ConcreteEntity.class);

		final Class<? extends ConcreteEntity> enhanced = Enhancer.enhance(type);

		Assert.assertEquals(ConcreteEntity.class, enhanced.getSuperclass());

		final Constructor<? extends ConcreteEntity> constructor = enhanced.getConstructor(Class.class, SessionImpl.class, Object.class, boolean.class);
		constructor.newInstance(null, null, null, true);
	}
}
