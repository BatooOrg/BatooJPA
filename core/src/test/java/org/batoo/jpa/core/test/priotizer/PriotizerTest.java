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
package org.batoo.jpa.core.test.priotizer;

import org.batoo.jpa.core.test.BaseCoreTest;
import org.junit.Test;

/**
 * @author hceylan
 * 
 * @since $version
 */
public class PriotizerTest extends BaseCoreTest {

	private Parent parent() {
		final Parent parent = new Parent();

		parent.getChildren1().add(new Child(parent));
		parent.getChildren1().add(new Child(parent));

		parent.getChildren2().add(new Child());
		parent.getChildren2().add(new Child());

		return parent;
	}

	/**
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testPersist() {
		final Parent parent = this.parent();
		this.persist(parent);

		this.commit();
	}

	/**
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testPersistAndRemove() {
		final Parent parent = this.parent();
		this.persist(parent);

		this.commit();

		this.persist(this.parent());
		this.remove(parent);

		this.commit();
	}
}
