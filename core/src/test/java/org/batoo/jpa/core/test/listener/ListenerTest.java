/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.batoo.jpa.core.test.listener;

import junit.framework.Assert;

import org.batoo.jpa.core.test.BaseCoreTest;
import org.junit.Test;

/**
 * @author hceylan
 * 
 * @since $version
 */
public class ListenerTest extends BaseCoreTest {

	/**
	 * 
	 * @since $version
	 * @author hceylan
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
	 * @since $version
	 * @author hceylan
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
	 * @since $version
	 * @author hceylan
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
