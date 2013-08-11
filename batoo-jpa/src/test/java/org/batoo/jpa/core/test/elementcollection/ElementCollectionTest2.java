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

import junit.framework.Assert;

import org.batoo.jpa.core.test.BaseCoreTest;
import org.junit.Test;

/**
 * @author hceylan
 * 
 * @since 2.0.0
 */
public class ElementCollectionTest2 extends BaseCoreTest {

	/**
	 * Tests the element collections
	 * 
	 * @since 2.0.0
	 */
	@Test
	public void testElementCollection() {
		Foo2 foo = new Foo2();

		foo.getImages().put("photo1", "~/home/photos/photo1.jpg");
		foo.getImages().put("photo2", "~/home/photos/photo2.jpg");
		foo.getImages().put("photo3", "~/home/photos/photo3.jpg");

		this.persist(foo);

		this.commit();

		this.begin();
		foo.getImages().remove("photo1");
		foo.getImages().put("photo4", "~/home/photos/photo4.jpg");
		this.commit();

		this.close();

		foo = this.find(Foo2.class, foo.getKey());
		Assert.assertEquals(foo.getKey(), foo.getKey());
		Assert.assertEquals(3, foo.getImages().size());

		Assert.assertNull(foo.getImages().get("photo1"));
		Assert.assertEquals("~/home/photos/photo4.jpg", foo.getImages().get("photo4"));
	}
}
