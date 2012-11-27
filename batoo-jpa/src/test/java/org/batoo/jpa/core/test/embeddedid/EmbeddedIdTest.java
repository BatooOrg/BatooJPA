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
package org.batoo.jpa.core.test.embeddedid;

import javax.persistence.EntityManager;

import junit.framework.Assert;

import org.batoo.jpa.core.test.BaseCoreTest;
import org.junit.Test;

/**
 * @author hceylan
 * 
 * @since 2.0.0
 */
public class EmbeddedIdTest extends BaseCoreTest {

	/**
	 * Tests to {@link EntityManager#persist(Object)} then {@link EntityManager#find(Class, Object)} with identity value
	 * 
	 * @since 2.0.0
	 */
	@Test
	public void testEmbeddedId() {
		final Foo foo1 = new Foo();
		final FooPk pk1 = new FooPk();
		pk1.setStrKey("key1");
		pk1.setIntKey(1);
		foo1.setId(pk1);
		foo1.setValue("Foo2");

		final Foo foo2 = new Foo();
		final FooPk pk2 = new FooPk();
		pk2.setStrKey("key1");
		pk2.setIntKey(2);
		foo2.setId(pk2);
		foo2.setValue("Foo2");

		this.persist(foo1);
		this.persist(foo2);

		this.commit();

		this.close();

		this.begin();

		final Foo foo3 = this.find(Foo.class, foo1.getId());
		final Foo foo4 = this.find(Foo.class, foo1.getId());
		Assert.assertEquals(foo1.getId(), foo3.getId());
		Assert.assertSame(foo4, foo3);

		this.rollback();
	}
}
