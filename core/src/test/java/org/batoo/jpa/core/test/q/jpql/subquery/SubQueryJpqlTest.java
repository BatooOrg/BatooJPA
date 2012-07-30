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
	public void testAllAnySome() {
		final Department qa = new Department("QA");
		final Department rnd = new Department("RND");
		this.persist(qa);
		this.persist(rnd);

		final Manager qaManager = new Manager("Manager1", qa, 100000);
		final Manager rndManager = new Manager("Manager2", rnd, 100000);
		final Manager qaManager2 = new Manager("Manager3", qa, 80000);
		this.persist(rndManager);
		this.persist(qaManager);
		this.persist(qaManager2);

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

		final TypedQuery<Integer> q;
		final TypedQuery<String> q2;

		q = this.cq("select e.id from Employee e where e.salary > all (select m.salary from Manager m where m.department = e.department)", Integer.class);
		Assert.assertEquals(employee4.getId(), q.getSingleResult());

		q2 = this.cq("select e.name from Employee e where e.salary > any (select m.salary from Manager m where m.department = e.department)", String.class);
		Assert.assertEquals(4, q2.getResultList().size());
	}

	/**
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testExists() {
		final Employee employee1 = new Employee();
		employee1.setName("Employee1");
		final Employee employee2 = new Employee();
		employee2.setName("Employee2");
		final Employee employee3 = new Employee();
		employee3.setName("Employee3");
		final Employee employee4 = new Employee();
		employee4.setName("Employee4");

		final Manager manager1 = new Manager();
		employee3.setName("Manager1");
		final Manager manager2 = new Manager();
		employee3.setName("Manager2");

		employee1.setManager(manager1);
		employee2.setManager(manager1);
		employee4.setManager(manager2);

		this.persist(employee1);
		this.persist(employee2);
		this.persist(employee3);
		this.persist(employee4);
		this.persist(manager1);
		this.persist(manager2);

		this.commit();

		TypedQuery<Integer> q;
		q = this.cq("select e.id from Employee e where exists (select m.id from Manager m where e.manager = m)", Integer.class);
		Assert.assertEquals("[1, 2, 4]", q.getResultList().toString());

		q = this.cq("select e.id from Employee e where not exists (select m.id from Manager m where e.manager = m)", Integer.class);
		Assert.assertEquals("[3]", q.getResultList().toString());
	}

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

		final Manager qaManager = new Manager("Manager1", qa, 100000);
		final Manager rndManager = new Manager("Manager2", rnd, 100000);
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

		TypedQuery<Integer> q;

		q = this.cq("select e.id from Employee e where e.salary > (select m.salary from Manager m where m.department = e.department)", Integer.class);
		Assert.assertEquals(employee4.getId(), q.getSingleResult());
	}
}
