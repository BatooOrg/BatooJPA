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
package org.batoo.jpa.core.test.duplicatecolumn;

import junit.framework.Assert;

import org.batoo.jpa.core.test.BaseCoreTest;
import org.junit.Test;

/**
 * @author hceylan
 * 
 * @since $version
 */
public class DuplicateColumnTest extends BaseCoreTest {

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	protected boolean lazySetup() {
		return true;
	}

	/**
	 * Tests the duplicate columns in the same entity.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testDuplicateColumn() {
		try {
			this.em();

			Assert.fail("Exception expected");
		}
		catch (final IllegalArgumentException e) {
			Assert.assertTrue(e.getCause().getCause().getMessage().startsWith(
				"Duplicate column on entity org.batoo.jpa.core.test.duplicatecolumn.Foo"));
		}
	}
}
