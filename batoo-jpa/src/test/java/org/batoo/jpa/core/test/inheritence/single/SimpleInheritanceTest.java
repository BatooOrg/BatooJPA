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
public class SimpleInheritanceTest extends BaseCoreTest {

	/**
	 * Tests to {@link EntityManager#persist(Object)} then {@link EntityManager#find(Class, Object)} with root type
	 * 
	 * @since 2.0.0
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
	 * @since 2.0.0
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
	 * @since 2.0.0
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
	 * @since 2.0.0
	 */
	@Test
	public void testSimpleInheritance3() {
		final FooExt11 foo = new FooExt11();
		foo.setValue("Value");
		foo.setValueExt1("ValueExt1");

		this.persist(foo);

		this.commit();

		this.close();

		final FooExt11 foo2 = (FooExt11) this.find(Foo.class, foo.getKey());

		Assert.assertEquals(foo.getKey(), foo2.getKey());
		Assert.assertEquals(foo.getValue(), foo2.getValue());
		Assert.assertEquals(foo.getValueExt1(), foo2.getValueExt1());
		Assert.assertEquals(foo.getValueExt11(), foo2.getValueExt11());
	}

	/**
	 * Test inheritance of mappedSuperClass
	 * 
	 * @since 2.0.1
	 */
	@Test
	public void testSimpleInheritance4() {
		final FooExt11 foo = new FooExt11();
		foo.setValue("FooExt11 Value");
		foo.setValueExt1("ValueExt1");

		final Bar bar = new Bar(1, foo);

		this.persist(bar);

		this.commit();

		this.close();

		final Bar bar2 = this.find(Bar.class, 1l);

		Assert.assertEquals(1, bar2.getId());
		Assert.assertEquals(1, bar2.getFoo().getKey().intValue());
		Assert.assertEquals("FooExt11 Value", bar2.getFoo().getValue());
		Assert.assertEquals(FooExt11.class, bar2.getFoo().getClass().getSuperclass());
	}

}
