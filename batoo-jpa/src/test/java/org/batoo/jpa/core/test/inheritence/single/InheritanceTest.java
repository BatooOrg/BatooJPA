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

package org.batoo.jpa.core.test.inheritence.single;

import javax.persistence.EntityManager;

import junit.framework.Assert;

import org.batoo.jpa.core.test.BaseCoreTest;
import org.junit.Test;

/**
 * @author hceylan
 * 
 * @since 2.0.0
 */
public class InheritanceTest extends BaseCoreTest {

	/**
	 * Tests to {@link EntityManager#persist(Object)} then {@link EntityManager#find(Class, Object)} with identity value
	 * 
	 * @since 2.0.0
	 */
	@Test
	public void testInheritance() {
		final FooExt1 foo = new FooExt1();
		foo.setValue("Foo2");
		foo.setValueExt1("Bar1");

		this.persist(foo);

		this.commit();

		this.close();

		final FooExt1 foo2 = (FooExt1) this.find(Foo.class, foo.getKey());
		Assert.assertEquals(foo.getKey(), foo2.getKey());
		Assert.assertEquals(foo.getValue(), foo2.getValue());
		Assert.assertEquals(foo.getValueExt1(), foo.getValueExt1());
	}

	/**
	 * Tests to {@link EntityManager#persist(Object)} then {@link EntityManager#find(Class, Object)} with identity value
	 * 
	 * @since 2.0.0
	 */
	@Test
	public void testSimpleInheritance2() {
		final FooExt11 foo = new FooExt11();
		foo.setValue("Foo2");

		this.persist(foo);

		this.commit();

		this.close();

		final FooExt11 foo2 = this.find(FooExt11.class, foo.getKey());
		Assert.assertEquals(foo.getKey(), foo2.getKey());
		Assert.assertEquals(foo.getValue(), foo2.getValue());
		Assert.assertEquals(foo.getValueExt1(), foo.getValueExt1());
	}

	/**
	 * Tests to {@link EntityManager#persist(Object)} then {@link EntityManager#find(Class, Object)} with identity value
	 * 
	 * @since 2.0.0
	 */
	public void testSimpleInheritance3() {
		final FooExt1 foo = new FooExt1();
		foo.setValue("Foo2");

		this.persist(foo);

		this.commit();

		this.close();

		Assert.assertNull(this.find(FooExt11.class, foo.getKey()));
	}
}
