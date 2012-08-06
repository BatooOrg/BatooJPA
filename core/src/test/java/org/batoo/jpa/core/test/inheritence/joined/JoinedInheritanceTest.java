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

import java.util.Set;

import javax.persistence.EntityManager;

import junit.framework.Assert;

import org.batoo.jpa.core.test.BaseCoreTest;
import org.junit.Test;

import com.google.common.collect.Sets;

/**
 * @author hceylan
 * 
 * @since $version
 */
public class JoinedInheritanceTest extends BaseCoreTest {

	/**
	 * Tests to {@link EntityManager#persist(Object)} then {@link EntityManager#find(Class, Object)} with root type
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testSimpleInheritence0() {
		final Foo foo1 = new Foo();
		foo1.setValue("Value");

		final Foo foo2 = new Foo();
		foo2.setValue("Value");

		final Bar bar = new Bar();
		bar.getFoos().add(foo1);
		bar.getFoos().add(foo2);

		this.persist(bar);

		this.commit();

		this.close();

		final Bar bar2 = this.em().find(Bar.class, bar.getKey());
		final Set<Foo> foos = Sets.newHashSet(foo1, foo2);
		final Set<Foo> foos2 = Sets.newHashSet(bar2.getFoos());

		Assert.assertEquals(bar2.getFoos().size(), 2);
		Assert.assertEquals(foos, foos2);
	}

	/**
	 * Tests to {@link EntityManager#persist(Object)} then {@link EntityManager#find(Class, Object)} with root type with mixed classes
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testSimpleInheritence1() {
		final Foo foo1 = new Foo();
		foo1.setValue("Value");

		final FooExt1 foo2 = new FooExt1();
		foo2.setValue("Value");
		foo2.setValueExt1("ValueExt1");

		final Bar bar = new Bar();
		bar.getFoos().add(foo1);
		bar.getFoos().add(foo2);

		this.persist(bar);

		this.commit();

		this.close();

		final Bar bar2 = this.em().find(Bar.class, bar.getKey());
		final Set<Foo> foos = Sets.newHashSet(foo1, foo2);
		final Set<Foo> foos2 = Sets.newHashSet(bar2.getFoos());

		Assert.assertEquals(bar2.getFoos().size(), 2);
		Assert.assertEquals(foos, foos2);
	}

	/**
	 * Tests to {@link EntityManager#persist(Object)} then {@link EntityManager#find(Class, Object)} with root type
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testSimpleInheritence2() {
		final FooExt1 foo1 = new FooExt1();
		foo1.setValue("Value");
		foo1.setValueExt1("ValueExt1");

		final FooExt11 foo2 = new FooExt11();
		foo2.setValue("Value");
		foo2.setValueExt1("ValueExt1");

		final Bar2 bar = new Bar2();
		bar.getFoos().add(foo1);
		bar.getFoos().add(foo2);

		this.persist(bar);

		this.commit();

		this.close();

		final Bar2 bar2 = this.em().find(Bar2.class, bar.getKey());
		final Set<FooExt1> foos = Sets.newHashSet(foo1, foo2);
		final Set<FooExt1> foos2 = Sets.newHashSet(bar2.getFoos());

		Assert.assertEquals(bar2.getFoos().size(), 2);
		Assert.assertEquals(foos, foos2);
	}

}
