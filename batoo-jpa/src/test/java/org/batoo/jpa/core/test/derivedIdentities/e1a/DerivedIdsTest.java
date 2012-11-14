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
package org.batoo.jpa.core.test.derivedIdentities.e1a;

import junit.framework.Assert;

import org.batoo.jpa.core.test.BaseCoreTest;
import org.junit.Ignore;
import org.junit.Test;

/**
 * JPA Spec 2.4.1.3 test.
 * 
 * @author asimarslan
 * @since $version
 */
public class DerivedIdsTest extends BaseCoreTest {

	/**
	 * Example-1 Case (a):
	 * <p>
	 * The parent entity has a simple primary key.
	 * <p>
	 * The dependent entity uses IdClass to represent a composite key.
	 * 
	 * @author asimarslan
	 * @since $version
	 */
	@Test
	public void test1a() {
		final Employee employee = new Employee("employee1");
		this.persist(employee);
		this.commit();

		final Dependent dependent1 = new Dependent("theName1", employee);
		final Dependent dependent2 = new Dependent("theName2", employee);

		this.persist(dependent1);
		this.persist(dependent2);

		this.commit();
		this.close();

		final Dependent dependent3 = this.find(Dependent.class, new DependentId(dependent1.getName(), dependent1.getEmp().getEmpId()));
		final Dependent dependent4 = this.find(Dependent.class, new DependentId(dependent1.getName(), dependent1.getEmp().getEmpId()));

		// final Dependent dependent3 = this.cq("select d from Dependent d where d = ?", Dependent.class).setParameter(1, new
		// DependentId(dependent1.getName(), dependent1.getEmp().getEmpId())).getSingleResult();
		// final Dependent dependent4 = this.cq("select d from Dependent d where d = ?", Dependent.class).setParameter(1, new
		// DependentId(dependent1.getName(), dependent1.getEmp().getEmpId())).getSingleResult();

		Assert.assertEquals(dependent1, dependent3);
		Assert.assertEquals(employee, dependent3.getEmp());
		Assert.assertSame(dependent3, dependent4);
	}

	/**
	 * Example-1 Case (a) - Modified form:
	 * <p>
	 * The parent entity has a simple primary key.
	 * <p>
	 * The dependent entity uses IdClass to represent a composite key.
	 * 
	 * @author asimarslan
	 * @since $version
	 */
	@Test
	@Ignore
	public void test1a2() {
		final Employee employee = new Employee("employee1");
		this.persist(employee);
		this.commit();

		final Dependent dependent1 = new Dependent("theName1", employee);
		final Dependent dependent2 = new Dependent("theName2", employee);

		this.persist(dependent1);
		this.persist(dependent2);

		this.commit();
		this.close();

		final Dependent dependent3 = this.cq("select d from Dependent d where d.name = ? and d.emp = ?", Dependent.class) //
		.setParameter(1, dependent1.getName()) //
		.setParameter(2, dependent1.getEmp())//
		.getSingleResult();

		final Dependent dependent4 = this.cq("select d from Dependent d where d.name = ? and d.emp = ?", Dependent.class) //
		.setParameter(1, dependent1.getName()) //
		.setParameter(2, dependent1.getEmp().getEmpId())//
		.getSingleResult();

		Assert.assertEquals(dependent1, dependent3);
		Assert.assertEquals(employee, dependent3.getEmp());
		Assert.assertSame(dependent3, dependent4);
	}
}
