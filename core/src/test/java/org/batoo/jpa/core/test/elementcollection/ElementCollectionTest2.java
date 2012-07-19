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
public class ElementCollectionTest2 extends BaseCoreTest {

	/**
	 * Tests the element collections
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testElementCollection1() {
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
	}
}
