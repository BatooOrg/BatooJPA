/*
 * Copyright (c) 2012-2013, Batu Alp Ceylan
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
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
 * @since 2.0.0
 */
public class SubQueryJpqlTest extends BaseCoreTest {

	/**
	 * @since 2.0.0
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
	 * @since 2.0.0
	 */
	@Test
	public void testEmpty() {
		final Department qa = new Department("QA");
		this.persist(qa);

		final Manager qaManager = new Manager("Manager1", qa, 100000);
		final Manager qaManager2 = new Manager("Manager2", qa, 100000);
		this.persist(qaManager);
		this.persist(qaManager2);

		final Employee employee1 = new Employee("Employee1", qaManager, qa, 90000);
		final Employee employee2 = new Employee("Employee2", qaManager, qa, 100000);
		this.persist(employee1);
		this.persist(employee2);

		this.commit();
		this.close();

		TypedQuery<Integer> q;

		q = this.cq("select m.id from Manager m where m.employees is empty", Integer.class);
		Assert.assertEquals(qaManager2.getId(), q.getSingleResult());

		q = this.cq("select m.id from Manager m where m.employees is not empty", Integer.class);
		Assert.assertEquals(qaManager.getId(), q.getSingleResult());
	}

	/**
	 * @since 2.0.0
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
		manager1.setName("Manager1");
		final Manager manager2 = new Manager();
		manager2.setName("Manager2");

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
		q = this.cq("select e.id from Employee e where exists (select m.id from Manager m where e.manager = m) order by e.id", Integer.class);
		Assert.assertEquals("[1, 2, 4]", q.getResultList().toString());

		q = this.cq("select e.id from Employee e where not exists (select m.id from Manager m where e.manager = m)", Integer.class);
		Assert.assertEquals("[3]", q.getResultList().toString());
	}

	/**
	 * @since 2.0.0
	 */
	@Test
	public void testMemberOf() {
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
		final Employee employee6 = new Employee("Employee2", null, qa, 90000);
		this.persist(employee3);
		this.persist(employee4);
		this.persist(employee5);
		this.persist(employee6);

		this.commit();

		TypedQuery<Integer> q;

		q = this.cq("select m.id from Manager m where :p member of m.employees", Integer.class).setParameter("p", employee1);
		Assert.assertEquals((Integer) 3, q.getSingleResult());
	}

	/**
	 * @since 2.0.0
	 */
	@Test
	public void testSize() {
		final Employee employee1 = new Employee();
		employee1.setName("Employee1");
		final Employee employee2 = new Employee();
		employee2.setName("Employee2");
		final Employee employee3 = new Employee();
		employee3.setName("Employee3");
		final Employee employee4 = new Employee();
		employee4.setName("Employee4");

		final Manager manager1 = new Manager();
		manager1.setName("Manager1");
		final Manager manager2 = new Manager();
		manager2.setName("Manager2");

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

		Assert.assertEquals((Integer) 2, this.cq("select size(m.employees) from Manager m where size(m.employees) = 2", Integer.class).getSingleResult());
	}

	/**
	 * @since 2.0.0
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

		TypedQuery<Number> q;

		q = this.cq("select e.id from Employee e where e.salary > (select m.salary from Manager m where m.department = e.department)", Number.class);
		Assert.assertEquals(employee4.getId(), q.getSingleResult());

		this.cq("select count(e) from Employee e where e.name in (select e.name from Employee e2)", Number.class).getResultList();
	}
}
