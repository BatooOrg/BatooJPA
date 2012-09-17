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
package org.batoo.jpa.core.test.cache;

import junit.framework.Assert;

import org.batoo.jpa.core.test.BaseCoreTest;
import org.junit.Test;

/**
 * @author hceylan
 * 
 * @since $version
 */
public class CacheTest extends BaseCoreTest {

	/**
	 * Tests the mix of cacheable and non-cachable puts
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testMany1() {
		final Foo foo = new Foo("value");
		Bar bar1 = new Bar(foo, 1);
		Bar bar2 = new Bar(foo, 2);

		this.persist(foo);
		this.commit();
		this.close();

		bar1 = this.find(Bar.class, bar1.getId());
		bar2 = this.find(Bar.class, bar2.getId());

		Assert.assertEquals("Global | puts:1 evicts:0 hits:1, misses:0", this.emf().getCache().getStats().toString());
	}

	/**
	 * Tests the one to many cachables
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testMany2() {
		final Foo foo = new Foo("value");
		Bar2 bar1 = new Bar2(foo, 1);
		Bar2 bar2 = new Bar2(foo, 2);

		this.persist(foo);
		this.commit();
		this.close();

		bar1 = this.find(Bar2.class, bar1.getId());
		bar2 = this.find(Bar2.class, bar2.getId());

		Assert.assertEquals("Global | puts:3 evicts:0 hits:3, misses:0", this.emf().getCache().getStats().toString());
	}

	/**
	 * Tests the one to many cachables
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testMany3() {
		final Foo foo = new Foo("value");
		Bar2 bar1 = new Bar2(foo, 1);
		Bar2 bar2 = new Bar2(foo, 2);

		this.persist(foo);
		this.commit();
		this.close();

		this.emf().getCache().evictAll();

		bar1 = this.find(Bar2.class, bar1.getId());
		bar2 = this.find(Bar2.class, bar2.getId());

		this.close();

		bar1 = this.find(Bar2.class, bar1.getId());
		bar2 = this.find(Bar2.class, bar2.getId());

		Assert.assertEquals("Global | puts:3 evicts:0 hits:3, misses:3", this.emf().getCache().getStats().toString());
	}

	/**
	 * Tests the one to many cachables
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testMany4() {
		Foo foo = new Foo("value");
		new Bar2(foo, 1);
		new Bar2(foo, 2);

		this.persist(foo);
		this.commit();
		this.close();

		this.emf().getCache().evictAll();

		foo = this.find(Foo.class, foo.getId());
		Assert.assertEquals("Global | puts:1 evicts:0 hits:0, misses:1", this.emf().getCache().getStats().toString());

		foo.getBars2().size();
		Assert.assertEquals("Global | puts:4 evicts:0 hits:1, misses:3", this.emf().getCache().getStats().toString());

		this.close();
		foo = this.find(Foo.class, foo.getId());
		Assert.assertEquals("Global | puts:4 evicts:0 hits:2, misses:3", this.emf().getCache().getStats().toString());

		foo.getBars2().size();
		Assert.assertEquals("Global | puts:4 evicts:0 hits:5, misses:3", this.emf().getCache().getStats().toString());
	}

	/**
	 * Tests the one to many cachables
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testRemoval() {
		Foo foo = new Foo("value");
		Bar2 bar1 = new Bar2(foo, 1);
		new Bar2(foo, 2);

		this.persist(foo);
		this.commit();
		this.close();

		bar1 = this.find(Bar2.class, bar1.getId());
		Assert.assertEquals("Global | puts:3 evicts:0 hits:2, misses:0", this.emf().getCache().getStats().toString());
		this.remove(bar1);
		this.commit();
		this.close();

		foo = this.find(Foo.class, foo.getId());
		Assert.assertEquals(1, foo.getBars2().size());
		Assert.assertEquals("Global | puts:5 evicts:1 hits:4, misses:1", this.emf().getCache().getStats().toString());
	}

	/**
	 * Tests the simple cache put
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testSimple() {
		Foo foo = new Foo("value");

		this.persist(foo);
		this.commit();
		this.close();

		foo = this.find(Foo.class, foo.getId());

		Assert.assertEquals("Global | puts:1 evicts:0 hits:1, misses:0", this.emf().getCache().getStats().toString());
	}
}
