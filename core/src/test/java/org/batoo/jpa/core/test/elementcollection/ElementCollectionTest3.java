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
import org.junit.Test;

/**
 * @author hceylan
 * 
 * @since $version
 */
public class ElementCollectionTest3 extends BaseCoreTest {

	/**
	 * Tests the embeddable element collections
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testElementCollection1() {
		Foo3 foo = new Foo3();

		foo.getImages().add(new Bar3(1, "~/home/photos/photo1.jpg"));
		foo.getImages().add(new Bar3(2, "~/home/photos/photo2.jpg"));
		foo.getImages().add(new Bar3(3, "~/home/photos/photo3.jpg"));

		this.persist(foo);

		this.commit();

		this.begin();
		foo.getImages().remove(new Bar3(1, "~/home/photos/photo1.jpg"));
		foo.getImages().add(new Bar3(4, "~/home/photos/photo4.jpg"));
		this.commit();

		this.close();

		foo = this.find(Foo3.class, foo.getKey());
		Assert.assertEquals(foo.getKey(), foo.getKey());
		Assert.assertEquals(3, foo.getImages().size());
	}

	/**
	 * Tests the embeddable element collections
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testElementCollection2() {
		Foo3 foo = new Foo3();

		foo.getImages2().put(1, new Bar3(1, "~/home/photos/photo1.jpg"));
		foo.getImages2().put(2, new Bar3(2, "~/home/photos/photo2.jpg"));
		foo.getImages2().put(3, new Bar3(3, "~/home/photos/photo3.jpg"));

		this.persist(foo);

		this.commit();

		this.begin();
		foo.getImages2().remove(1);
		foo.getImages2().put(4, new Bar3(4, "~/home/photos/photo4.jpg"));
		this.commit();

		this.close();

		foo = this.find(Foo3.class, foo.getKey());
		Assert.assertEquals(foo.getKey(), foo.getKey());
		Assert.assertEquals(3, foo.getImages2().size());
	}
}
