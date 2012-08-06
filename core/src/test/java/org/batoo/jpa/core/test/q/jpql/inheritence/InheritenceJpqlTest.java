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
package org.batoo.jpa.core.test.q.jpql.inheritence;

import javax.persistence.TypedQuery;

import org.batoo.jpa.core.test.BaseCoreTest;
import org.batoo.jpa.core.test.q.Contractor;
import org.batoo.jpa.core.test.q.Employee;
import org.batoo.jpa.core.test.q.Exempt;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author hceylan
 * 
 * @since $version
 */
public class InheritenceJpqlTest extends BaseCoreTest {

	/**
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testCaseType() {
		this.persist(new Employee());
		this.persist(new Exempt());
		this.persist(new Exempt());
		this.persist(new Contractor());
		this.commit();
		this.close();

		Assert.assertEquals("[Employee, Exempt, Exempt, Contractor]", //
			this.cq("select case type(e) \n" + //
				"    when Exempt then 'Exempt'\n" + //
				"    when Contractor then 'Contractor'\n" + //
				"    else 'Employee'\n" + //
				"  end\n" + //
				"from Employee e order by type(e)", String.class).getResultList().toString());
	}

	/**
	 * @since $version
	 * @author hceylan
	 */
	@Test
	@SuppressWarnings("rawtypes")
	public void testType() {
		this.persist(new Employee());
		this.persist(new Exempt());
		this.persist(new Exempt());
		this.persist(new Contractor());
		this.commit();
		this.close();

		TypedQuery<Employee> q;
		TypedQuery<Class> q2;

		q = this.cq("select e from Employee e where type(e) = Exempt", Employee.class);
		Assert.assertEquals(2, q.getResultList().size());

		q = this.cq("select object(e) from Employee e where type(e) = :p", Employee.class).setParameter("p", Exempt.class);
		Assert.assertEquals(2, q.getResultList().size());

		q2 = this.cq("select type(e) from Employee e where type(e) = :p", Class.class).setParameter("p", Exempt.class);
		Assert.assertEquals(Exempt.class, q2.getResultList().get(0));

		q2 = this.cq("select type(e.id) from Employee e where type(e) = :p", Class.class).setParameter("p", Exempt.class);
		Assert.assertEquals(Integer.class, q2.getResultList().get(0));
	}
}
