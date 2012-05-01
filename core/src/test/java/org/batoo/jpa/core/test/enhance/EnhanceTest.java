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
package org.batoo.jpa.core.test.enhance;

import java.lang.reflect.Constructor;

import javax.persistence.metamodel.EntityType;

import org.batoo.jpa.core.impl.SessionImpl;
import org.batoo.jpa.core.impl.instance.Enhancer;
import org.batoo.jpa.core.test.AbstractTest;
import org.junit.Test;

/**
 * 
 * @author hceylan
 * @since $version
 */
public class EnhanceTest extends AbstractTest {

	@Test
	public void testEnhance() throws Exception {
		final EntityType<Person> type = this.em().getMetamodel().entity(Person.class);

		final Class<? extends Person> enhanced = Enhancer.enhance(type);

		final Constructor<? extends Person> constructor = enhanced.getConstructor(Class.class, SessionImpl.class, Object.class,
			boolean.class);
		final Person newInstance = constructor.newInstance(null, null, null, true);

		System.out.println(newInstance);
	}
}
