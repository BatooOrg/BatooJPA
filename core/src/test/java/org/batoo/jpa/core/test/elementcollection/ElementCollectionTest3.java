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
