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
package org.batoo.jpa.core.test.listener;

import junit.framework.Assert;

import org.batoo.jpa.core.test.BaseCoreTest;
import org.junit.Test;

/**
 * @author hceylan
 * 
 * @since 2.0.0
 */
public class ListenerTest extends BaseCoreTest {

	/**
	 * 
	 * @since 2.0.0
	 */
	@Test
	public void testListener() {
		Foo1 foo = new Foo1();
		this.persist(foo);

		this.commit();
		this.close();

		Assert.assertEquals("prePersistpostPersist", foo.getValue());
		Assert.assertEquals("masterPrePersistmasterPostPersist", foo.getParentValue());

		foo = this.find(Foo1.class, foo.getId());

		Assert.assertEquals("", foo.getValue());
		Assert.assertEquals("masterPostLoad", foo.getParentValue());

		this.remove(foo);
		this.commit();

		Assert.assertEquals("", foo.getValue());
		Assert.assertEquals("masterPostLoadmasterPreRemovemasterPostRemove", foo.getParentValue());
	}

	/**
	 * 
	 * @since 2.0.0
	 */
	@Test
	public void testListener2() {
		Foo2 foo = new Foo2();
		this.persist(foo);

		this.commit();
		this.close();

		Assert.assertEquals("postPersist", foo.getValue());
		Assert.assertEquals("", foo.getParentValue());

		foo = this.find(Foo2.class, foo.getId());

		Assert.assertEquals("postLoad", foo.getValue());
		Assert.assertEquals("", foo.getParentValue());

		this.remove(foo);
		this.commit();

		Assert.assertEquals("postLoad", foo.getValue());
		Assert.assertEquals("", foo.getParentValue());
	}

	/**
	 * 
	 * @since 2.0.0
	 */
	@Test
	public void testListener3() {
		Foo3 foo = new Foo3();
		this.persist(foo);

		this.commit();
		this.close();

		Assert.assertEquals("listener1PrePersistlistener1PostPersistpostPersist", foo.getValue());
		Assert.assertEquals("masterPrePersistmasterPostPersist", foo.getParentValue());

		foo = this.find(Foo3.class, foo.getId());

		Assert.assertEquals("listener1PostLoad", foo.getValue());
		Assert.assertEquals("masterPostLoad", foo.getParentValue());

		this.begin();
		foo.setFooValue("FooValue");
		this.commit();

		Assert.assertEquals("listener1PostLoadlistener1PreUpdatelistener1PostUpdate", foo.getValue());
		Assert.assertEquals("masterPostLoadmasterPreUpdatemasterPostUpdate", foo.getParentValue());

		this.begin();
		this.remove(foo);
		this.commit();

		Assert.assertEquals("listener1PostLoadlistener1PreUpdatelistener1PostUpdatelistener1PreRemovelistener1PostRemove", foo.getValue());
		Assert.assertEquals("masterPostLoadmasterPreUpdatemasterPostUpdatemasterPreRemovemasterPostRemove", foo.getParentValue());
	}
}
