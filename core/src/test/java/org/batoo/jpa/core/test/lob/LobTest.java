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
package org.batoo.jpa.core.test.lob;

import junit.framework.Assert;

import org.batoo.jpa.core.test.AbstractTest;
import org.junit.Ignore;
import org.junit.Test;

import com.google.common.collect.Sets;

/**
 * @author hceylan
 * 
 * @since $version
 */
@Ignore
// FIXME: https://issues.apache.org/jira/browse/DBUTILS-90
public class LobTest extends AbstractTest {

	/**
	 * Tests to {@link EntityManager#persist(Object)} then {@link EntityManager#find(Class, Object)} with lob values
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testLob() {
		final Foo foo = new Foo();
		foo.getValues().add("Value1");
		foo.getValues().add("Value2");

		this.persist(foo);

		this.commit();

		this.close();

		final Foo foo2 = this.find(Foo.class, foo.getKey());
		Assert.assertEquals(foo.getKey(), foo2.getKey());
		Assert.assertEquals(Sets.newHashSet("Value1", "Value2"), foo2.getValues());
	}

}
