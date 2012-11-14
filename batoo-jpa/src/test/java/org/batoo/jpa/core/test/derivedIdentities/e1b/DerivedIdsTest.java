/*
 * Copyright (c) 2012 - Batoo Software ve Consultancy Ltd.
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
package org.batoo.jpa.core.test.derivedIdentities.e1b;

import java.util.List;

import javax.persistence.TypedQuery;

import junit.framework.Assert;

import org.batoo.jpa.core.test.BaseCoreTest;
import org.junit.Test;

/**
 * JPA Spec 2.4.1.3 test
 * 
 * @author asimarslan
 * @since $version
 */
public class DerivedIdsTest extends BaseCoreTest {

	/**
	 * 
	 * Example-1 Case (b):
	 * <p>
	 * The parent entity has a simple primary key
	 * <p>
	 * The dependent entity uses EmbeddedId to represent a composite key:
	 * 
	 * @author asimarslan
	 * @since $version
	 */
	@Test
	public void test1b() {
		final Employee employee = new Employee("employee1");
		this.persist(employee);
		this.commit();

		final DependentId dependentId1 = new DependentId("dep-id-name1", employee.getId());
		final DependentId dependentId2 = new DependentId("dep-id-name2", employee.getId());

		final Dependent dependent1 = new Dependent(dependentId1, employee);
		final Dependent dependent2 = new Dependent(dependentId2, employee);

		this.persist(dependent1);
		this.persist(dependent2);

		this.commit();
		this.close();

		final Dependent dependent3 = this.find(Dependent.class, dependentId1);

		Assert.assertEquals(dependent1, dependent3);
	}

	@Test
	public void test2aJPQL() {

		final Employee employee = new Employee("Sam");

		this.persist(employee);
		this.commit();

		final DependentId dependentId1 = new DependentId("Joe", employee.getId());
		final Dependent dependent1 = new Dependent(dependentId1, employee);

		this.persist(dependent1);

		this.commit();
		this.close();

		final String qstr = "SELECT d FROM Dependent d WHERE d.id.name = 'Joe' AND d.employee.empName = 'Sam'";

		final TypedQuery<Dependent> q = this.cq(qstr, Dependent.class);
		final List<Dependent> resultList = q.getResultList();

		Assert.assertNotNull(resultList);
		Assert.assertEquals(1, resultList.size());

		Assert.assertEquals("Joe", resultList.get(0).getId().getName());
		Assert.assertEquals("Sam", resultList.get(0).getEmployee().getEmpName());

	}
}
