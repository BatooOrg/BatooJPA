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
package org.batoo.jpa.core.test.manualid;

import javax.persistence.EntityManager;

import junit.framework.Assert;

import org.batoo.jpa.core.test.BaseCoreTest;
import org.junit.Test;

/**
 * @author hceylan
 * 
 * @since $version
 */
public class ManualIdTest extends BaseCoreTest {

	/**
	 * Tests to {@link EntityManager#persist(Object)} then {@link EntityManager#find(Class, Object)} with identity value
	 * 
	 * @since $version
	 */
	@Test
	public void testIdentiy() {
		final Foo foo = new Foo();
		foo.setKey(1);
		foo.setValue("Foo2");

		final Foo foo2 = new Foo();
		foo2.setKey(2);
		foo2.setValue("Foo2");

		this.persist(foo);
		this.persist(foo2);

		this.commit();

		this.close();

		final Foo foo3 = this.find(Foo.class, foo.getKey());
		Assert.assertEquals(foo.getKey(), foo3.getKey());
	}

}
