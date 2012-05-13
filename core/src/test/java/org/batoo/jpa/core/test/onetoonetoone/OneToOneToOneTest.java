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
package org.batoo.jpa.core.test.onetoonetoone;

import junit.framework.Assert;

import org.batoo.jpa.core.test.AbstractTest;
import org.junit.Test;

/**
 * @author hceylan
 * 
 * @since $version
 */
public class OneToOneToOneTest extends AbstractTest {

	/**
	 * Tests to {@link EntityManager#find(Class, Object)} person.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testFind() {
		final Employee employee = new Employee("Ceylan");
		this.persist(employee);

		this.commit();
		this.close();

		final Employee employee2 = this.find(Employee.class, employee.getId());

		Assert.assertEquals(employee.getId(), employee2.getId());
	}

	/**
	 * Tests to {@link EntityManager#find(Class, Object)} person that is in the session.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testFindInSession() {
		final Employee person = new Employee("Ceylan");
		this.persist(person);

		this.commit();

		final Employee person2 = this.find(Employee.class, person.getId());
		Assert.assertSame(person, person2);
	}

	/**
	 * Tests to {@link EntityManager#persist(Object)} Person which cascades to Address.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testPersist() {
		Assert.assertEquals(2, this.em().getMetamodel().getEntities().size());

		final Cubicle cubicle = new Cubicle();
		this.persist(cubicle);

		final Employee employee = new Employee("Ceylan");
		employee.setAssignedCubicle(cubicle);
		this.persist(employee);

		this.commit();
	}
}
