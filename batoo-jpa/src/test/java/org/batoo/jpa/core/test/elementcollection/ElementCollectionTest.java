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
package org.batoo.jpa.core.test.elementcollection;

import junit.framework.Assert;

import org.batoo.jpa.core.test.BaseCoreTest;
import org.batoo.jpa.core.test.enums.Foo.FooType;
import org.junit.Test;

/**
 * @author hceylan
 * 
 * @since $version
 */
public class ElementCollectionTest extends BaseCoreTest {

	/**
	 * Tests the element collections
	 * 
	 * @since $version
	 */
	@Test
	public void testElementCollection1() {
		Foo foo = new Foo();
		foo.setValue("Foo2");

		foo.getCodes().add("TR");
		foo.getCodes().add("UK");
		foo.getCodes().add("US");

		this.persist(foo);

		this.commit();

		this.begin();
		foo.getCodes().add("FR");
		this.commit();

		this.close();

		foo = this.find(Foo.class, foo.getKey());
		Assert.assertEquals(foo.getKey(), foo.getKey());
		Assert.assertEquals(4, foo.getCodes().size());
	}

	/**
	 * Tests the element collections
	 * 
	 * @since $version
	 */
	@Test
	public void testElementCollection2() {
		Foo foo = new Foo();
		foo.setValue("Foo2");

		foo.getCodes2().add("TR");
		foo.getCodes2().add("UK");
		foo.getCodes2().add("US");

		this.persist(foo);

		this.commit();

		this.begin();
		foo.getCodes2().add("FR");
		this.commit();

		this.close();

		foo = this.find(Foo.class, foo.getKey());
		Assert.assertEquals(foo.getKey(), foo.getKey());
		Assert.assertEquals(4, foo.getCodes2().size());
	}

	/**
	 * Tests the element collections
	 * 
	 * @since $version
	 */
	@Test
	public void testElementCollection3() {
		Foo foo = new Foo();
		foo.setValue("Foo2");

		foo.getCodes3().add(FooType.TYPE1);
		foo.getCodes3().add(FooType.TYPE2);

		this.persist(foo);

		this.commit();

		this.begin();
		foo.getCodes3().remove(FooType.TYPE1);
		foo.getCodes3().add(FooType.TYPE3);
		this.commit();

		this.close();

		foo = this.find(Foo.class, foo.getKey());
		Assert.assertEquals(foo.getKey(), foo.getKey());
		Assert.assertEquals(2, foo.getCodes3().size());
	}

	/**
	 * Tests the element collections
	 * 
	 * @since $version
	 */
	@Test
	public void testElementCollection4() {
		Foo foo = new Foo();
		foo.setValue("Foo2");

		foo.getCodes4().add(FooType.TYPE1);
		foo.getCodes4().add(FooType.TYPE2);

		this.persist(foo);

		this.commit();

		this.begin();
		foo.getCodes4().remove(FooType.TYPE1);
		foo.getCodes4().add(FooType.TYPE3);
		this.commit();

		this.close();

		foo = this.find(Foo.class, foo.getKey());
		Assert.assertEquals(foo.getKey(), foo.getKey());
		Assert.assertEquals(2, foo.getCodes4().size());
	}
}
