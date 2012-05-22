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
package org.batoo.jpa.spec.test.simple;

import javax.persistence.AccessType;
import javax.persistence.TemporalType;

import org.batoo.jpa.parser.metadata.BasicAttributeMetadata;
import org.batoo.jpa.parser.test.BaseParserTest;
import org.junit.Test;

/**
 * Tests for enum types.
 * 
 * @author hceylan
 * @since $version
 */
public class EnumTypesTest extends BaseParserTest {

	/**
	 * Tests that the access type is consumed properly.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testAccessType() {
		this.assertEquals(AccessType.FIELD, this.e().getAccessType());
	}

	/**
	 * Tests that the temporal type is consumed properly.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testTemporalType() {
		this.assertEquals(TemporalType.TIMESTAMP, ((BasicAttributeMetadata) this.a()).getTemporalType());
	}
}
