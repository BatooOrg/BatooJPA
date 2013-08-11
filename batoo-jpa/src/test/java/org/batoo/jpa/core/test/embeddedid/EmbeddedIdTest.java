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

		final FooPk fooPk1 = new FooPk("key1", 1);
		final Foo foo1 = new Foo(fooPk1, "Foo1");

		final FooPk fooPk2 = new FooPk("key2", 2);
		final Foo foo2 = new Foo(fooPk2, "Foo2");

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

	/**
	 * Test embeddedId in a MonyToOne relation
	 * 
	 * @since 2.0.1
	 */
	@Test
	public void testEmbeddedIdManyToOne() {
		final FooPk fooPk = new FooPk("key1", 1);
		final Foo foo = new Foo(fooPk, "Foo");

		final Bar bar = new Bar(1l, foo);

		this.persist(bar);

		this.commit();
		this.close();

		final Bar bar2 = this.cq("select o from Bar o", Bar.class).getSingleResult();

		Assert.assertEquals(1, bar2.getId());
		Assert.assertEquals("Foo", bar2.getFoo().getValue());
		Assert.assertEquals("key1", bar2.getFoo().getId().getStrKey());
		Assert.assertEquals(1, bar2.getFoo().getId().getIntKey().intValue());

	}
}
