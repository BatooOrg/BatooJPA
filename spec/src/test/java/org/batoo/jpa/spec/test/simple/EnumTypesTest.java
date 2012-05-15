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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import junit.framework.Assert;

import org.batoo.jpa.spec.orm.Entity;
import org.batoo.jpa.spec.test.BaseOrmTest;
import org.junit.Test;

/**
 * 
 * @author hceylan
 * @since $version
 */
public class EnumTypesTest extends BaseOrmTest {

	@Test
	public void testAccessType() throws Exception {
		Assert.assertEquals(AccessType.FIELD, this.em().getAccess());
	}

	@Test
	public void testTemporalType() throws Exception {
		final Entity entity = this.em().getEntities().get(0);
		final Temporal temporal = entity.getAttributes().getVersions().get(0).getTemporal();

		Assert.assertEquals(TemporalType.TIMESTAMP, temporal.value());
	}
}
