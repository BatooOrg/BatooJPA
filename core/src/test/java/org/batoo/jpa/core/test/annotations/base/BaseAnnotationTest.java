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
package org.batoo.jpa.core.test.annotations.base;

import junit.framework.Assert;

import org.batoo.jpa.core.test.AbstractTest;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author hceylan
 * 
 * @since $version
 */
public class BaseAnnotationTest extends AbstractTest {

	/**
	 * Test if {@link Person} deployed correctly
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	@Ignore
	public void testMapping() {
		Assert.assertEquals(2, this.em().getMetamodel().getEntities().size());
	}
}
