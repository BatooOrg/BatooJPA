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

package org.batoo.jpa.core.test.elementcollection;

import java.util.Map;

import junit.framework.Assert;

import org.batoo.jpa.core.test.BaseCoreTest;
import org.junit.Test;

/**
 * @author hceylan
 * 
 * @since 2.0.0
 */
public class ElementCollectionTest4 extends BaseCoreTest {

	/**
	 * Tests the element collections
	 * 
	 * @since 2.0.0
	 */
	@Test
	public void testElementCollection() {
		final Foo4 foo = new Foo4();

		foo.getTextMap().put(FieldLocale.TR, "Merhaba");
		foo.getTextMap().put(FieldLocale.FR, "Bonjour");
		foo.getTextMap().put(FieldLocale.EN, "Hello");
		this.persist(foo);

		this.commit();
		this.close();
		//
		final Foo4 foo2 = this.find(Foo4.class, foo.getKey());

		Assert.assertEquals(foo.getKey(), foo2.getKey());
		final Map<FieldLocale, String> textMap = foo2.getTextMap();

		for (final Object fl : textMap.keySet()) {
			Assert.assertTrue(fl instanceof FieldLocale);
		}

		Assert.assertEquals(3, textMap.size());

		Assert.assertEquals("Merhaba", textMap.get(FieldLocale.TR));
		Assert.assertEquals("Hello", textMap.get(FieldLocale.EN));
		Assert.assertEquals("Bonjour", textMap.get(FieldLocale.FR));

	}

	/**
	 * Tests the element collections with jpql
	 * 
	 * @since 2.0.0
	 */
	@Test
	public void testElementCollectionWithQuery() {
		final Foo4 foo = new Foo4();

		foo.getTextMap().put(FieldLocale.TR, "Merhaba");
		foo.getTextMap().put(FieldLocale.FR, "Bonjour");
		foo.getTextMap().put(FieldLocale.EN, "Hello");
		this.persist(foo);

		this.commit();
		this.close();

		final Foo4 foo2 = (Foo4) this.cq("select f from Foo4 f").getSingleResult();

		Assert.assertEquals(foo.getKey(), foo2.getKey());
		final Map<FieldLocale, String> textMap = foo2.getTextMap();

		for (final Object fl : textMap.keySet()) {
			Assert.assertTrue(fl instanceof FieldLocale);
		}

		Assert.assertEquals(3, textMap.size());

		Assert.assertEquals("Merhaba", textMap.get(FieldLocale.TR));
		Assert.assertEquals("Hello", textMap.get(FieldLocale.EN));
		Assert.assertEquals("Bonjour", textMap.get(FieldLocale.FR));

	}

	/**
	 * Tests the element collections
	 * 
	 * @since 2.0.0
	 */
	@Test
	public void testElementCollectionWithRemove() {
		final Foo4 foo = new Foo4();

		foo.getTextMap().put(FieldLocale.TR, "Merhaba");
		foo.getTextMap().put(FieldLocale.FR, "Bonjour");
		foo.getTextMap().put(FieldLocale.EN, "Hello");
		this.persist(foo);

		this.commit();

		this.begin();
		foo.getTextMap().remove(FieldLocale.EN);
		foo.getTextMap().put(FieldLocale.EN, "Hi");

		this.commit();

		this.close();
		//
		final Foo4 foo2 = this.find(Foo4.class, foo.getKey());

		Assert.assertEquals(foo.getKey(), foo2.getKey());
		final Map<FieldLocale, String> textMap = foo2.getTextMap();

		for (final Object fl : textMap.keySet()) {
			Assert.assertTrue(fl instanceof FieldLocale);
		}

		Assert.assertEquals(3, textMap.size());

		Assert.assertEquals("Merhaba", textMap.get(FieldLocale.TR));
		Assert.assertEquals("Hi", textMap.get(FieldLocale.EN));
		Assert.assertEquals("Bonjour", textMap.get(FieldLocale.FR));

	}
}
