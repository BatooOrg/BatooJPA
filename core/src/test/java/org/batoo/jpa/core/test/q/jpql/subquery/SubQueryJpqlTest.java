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
package org.batoo.jpa.core.test.q.jpql.subquery;

import javax.persistence.TypedQuery;

import org.batoo.jpa.core.test.BaseCoreTest;
import org.batoo.jpa.core.test.q.Department;
import org.batoo.jpa.core.test.q.Employee;
import org.batoo.jpa.core.test.q.Manager;
import org.junit.Assert;
import org.junit.Test;

/**
 * 
 * @author hceylan
 * @since $version
 */
public class SubQueryJpqlTest extends BaseCoreTest {

	/**
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testSubQuery() {
		final Department qa = new Department("QA");
		final Department rnd = new Department("RND");
		this.persist(qa);
		this.persist(rnd);

		final Manager qaManager = new Manager("Manager1", null, qa, 100000);
		final Manager rndManager = new Manager("Manager2", null, rnd, 100000);
		this.persist(rndManager);
		this.persist(qaManager);

		final Employee employee1 = new Employee("Employee1", rndManager, rnd, 90000);
		final Employee employee2 = new Employee("Employee2", rndManager, rnd, 100000);
		this.persist(employee1);
		this.persist(employee2);

		final Employee employee3 = new Employee("Employee1", qaManager, qa, 90000);
		final Employee employee4 = new Employee("Employee2", qaManager, qa, 110000);
		final Employee employee5 = new Employee("Employee1", qaManager, qa, 90000);
		final Employee employee6 = new Employee("Employee2", qaManager, qa, 90000);
		this.persist(employee3);
		this.persist(employee4);
		this.persist(employee5);
		this.persist(employee6);

		this.commit();

		final TypedQuery<Integer> q = this.cq(//
			"select e.id from Employee e where e.salary > ( select m.salary from Manager m where m.department = e.department)", //
			Integer.class);
		Assert.assertEquals(employee4.getId(), q.getSingleResult());
	}
}
