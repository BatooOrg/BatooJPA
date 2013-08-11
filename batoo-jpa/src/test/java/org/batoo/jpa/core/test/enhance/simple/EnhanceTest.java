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

package org.batoo.jpa.core.test.enhance.simple;

import java.lang.reflect.Constructor;

import javax.persistence.metamodel.EntityType;

import org.batoo.jpa.core.impl.instance.Enhancer;
import org.batoo.jpa.core.impl.manager.SessionImpl;
import org.batoo.jpa.core.test.BaseCoreTest;
import org.junit.Test;

/**
 * 
 * @author hceylan
 * @since 2.0.0
 */
public class EnhanceTest extends BaseCoreTest {

	/**
	 * Tests the enhancement
	 * 
	 * @throws Exception
	 *             thrown in case of failure
	 * 
	 * @since 2.0.0
	 */
	@Test
	public void testEnhance() throws Exception {
		final EntityType<Person> type = this.em().getMetamodel().entity(Person.class);

		final Class<? extends Person> enhanced = Enhancer.enhance(type);
		final Constructor<? extends Person> constructor = enhanced.getConstructor(Class.class, SessionImpl.class, Object.class, boolean.class);
		constructor.newInstance(null, null, null, true);
	}
}
