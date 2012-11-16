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
package org.batoo.jpa.core.test.inheritence.joined;

import javax.persistence.EntityManager;

import junit.framework.Assert;

import org.batoo.jpa.core.test.BaseCoreTest;
import org.junit.Test;

/**
 * @author hceylan
 * 
 * @since $version
 */
public class JoinedInheritanceSimpleTest extends BaseCoreTest {

	/**
	 * Tests to {@link EntityManager#persist(Object)} then {@link EntityManager#find(Class, Object)} with root type
	 * 
	 * @since $version
	 */
	@Test
	public void testSimpleInheritance0() {
		final Foo foo = new Foo();
		foo.setValue("Value");

		this.persist(foo);

		this.commit();

		this.close();

		final Foo foo2 = this.find(Foo.class, foo.getKey());

		Assert.assertEquals(foo.getKey(), foo2.getKey());
		Assert.assertEquals(foo.getValue(), foo2.getValue());
	}

	/**
	 * Tests to {@link EntityManager#persist(Object)} then {@link EntityManager#find(Class, Object)} with extending type
	 * 
	 * @since $version
	 */
	@Test
	public void testSimpleInheritance1() {
		final FooExt1 foo = new FooExt1();
		foo.setValue("Value");
		foo.setValueExt1("ValueExt1");

		this.persist(foo);

		this.commit();

		this.close();

		final FooExt1 foo2 = this.find(FooExt1.class, foo.getKey());

		Assert.assertEquals(foo.getKey(), foo2.getKey());
		Assert.assertEquals(foo.getValue(), foo2.getValue());
		Assert.assertEquals(foo.getValueExt1(), foo2.getValueExt1());
	}

	/**
	 * Tests to {@link EntityManager#persist(Object)} then {@link EntityManager#find(Class, Object)} with further extending type
	 * 
	 * @since $version
	 */
	@Test
	public void testSimpleInheritance11() {
		final FooExt11 foo = new FooExt11();
		foo.setValue("Value");
		foo.setValueExt1("ValueExt1");
		foo.setValueExt11("ValueExt11");

		this.persist(foo);

		this.commit();

		this.close();

		final FooExt11 foo2 = this.find(FooExt11.class, foo.getKey());

		Assert.assertEquals(foo.getKey(), foo2.getKey());
		Assert.assertEquals(foo.getValue(), foo2.getValue());
		Assert.assertEquals(foo.getValueExt1(), foo2.getValueExt1());
		Assert.assertEquals(foo.getValueExt11(), foo2.getValueExt11());
	}

	/**
	 * Tests to {@link EntityManager#persist(Object)} then {@link EntityManager#find(Class, Object)} with root type
	 * 
	 * @since $version
	 */
	@Test
	public void testSimpleInheritance3() {
		final FooExt11 foo = new FooExt11();
		foo.setValue("Value");
		foo.setValueExt1("ValueExt1");
		foo.setValueExt11("ValueExt11");

		this.persist(foo);

		this.commit();

		this.close();

		final FooExt11 foo2 = (FooExt11) this.find(Foo.class, foo.getKey());

		Assert.assertEquals(foo.getKey(), foo2.getKey());
		Assert.assertEquals(foo.getValue(), foo2.getValue());
		Assert.assertEquals(foo.getValueExt1(), foo2.getValueExt1());
		Assert.assertEquals(foo.getValueExt11(), foo2.getValueExt11());
	}
}
