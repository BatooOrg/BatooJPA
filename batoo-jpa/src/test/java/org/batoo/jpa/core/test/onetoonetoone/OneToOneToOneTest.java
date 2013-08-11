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

package org.batoo.jpa.core.test.onetoonetoone;

import javax.persistence.EntityManager;

import junit.framework.Assert;

import org.batoo.jpa.core.test.BaseCoreTest;
import org.junit.Test;

/**
 * @author hceylan
 * 
 * @since 2.0.0
 */
public class OneToOneToOneTest extends BaseCoreTest {

	/**
	 * Tests to {@link EntityManager#find(Class, Object)} person.
	 * 
	 * @since 2.0.0
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
	 * @since 2.0.0
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
	 * Tests to {@link EntityManager#persist(Object)} Parent which cascades to Child1.
	 * 
	 * @since 2.0.0
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
