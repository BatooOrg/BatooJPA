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
package org.batoo.jpa.core.test.derivedIdentities.e3b;

import java.sql.SQLException;
import java.util.List;

import javax.persistence.TypedQuery;
import javax.sql.DataSource;

import junit.framework.Assert;

import org.batoo.jpa.core.impl.jdbc.dbutils.QueryRunner;
import org.batoo.jpa.core.test.BaseCoreTest;
import org.batoo.jpa.core.test.ColumnNameListHandler;
import org.batoo.jpa.core.test.derivedIdentities.e2b.Dependent;
import org.batoo.jpa.core.test.derivedIdentities.e2b.DependentId;
import org.batoo.jpa.core.test.derivedIdentities.e2b.Employee;
import org.batoo.jpa.core.test.derivedIdentities.e2b.EmployeeId;
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
	 * Example-3 Case (b):
	 * <p>
	 * The parent entity uses EmbeddedId:
	 * <p>
	 * The dependent entity uses EmbeddedId
	 * 
	 * @since $version
	 */
	@Test
	public void test3bJPQL() {
		final Employee employee = new Employee("Sam", "Doe");

		this.persist(employee);
		this.commit();

		final Dependent dependent1 = new Dependent(new DependentId("Joe", new EmployeeId(employee.getFirstName(), employee.getLastName())), employee);

		this.persist(dependent1);

		this.commit();
		this.close();

		final String qstr = "SELECT d FROM Dependent d WHERE d.id.name = 'Joe' and d.emp.empId.firstName = 'Sam'";

		final TypedQuery<Dependent> q = this.cq(qstr, Dependent.class);
		final List<Dependent> resultList = q.getResultList();

		Assert.assertNotNull(resultList);
		Assert.assertEquals(1, resultList.size());

		Assert.assertEquals("Joe", resultList.get(0).getId().getName());
		Assert.assertEquals("Sam", resultList.get(0).getEmp().getFirstName());
		Assert.assertEquals("Doe", resultList.get(0).getEmp().getLastName());

	}

	/**
	 * Tests generated DDL column names
	 * 
	 * @throws SQLException
	 * @since $version
	 */
	@Test
	public void testColumnNames() throws SQLException {
		final Employee employee = new Employee("Sam", "Doe");

		this.persist(employee);
		this.commit();

		final Dependent dependent1 = new Dependent(new DependentId("Joe", new EmployeeId(employee.getFirstName(), employee.getLastName())), employee);

		this.persist(dependent1);

		this.commit();
		this.close();

		final List<String> columnNames = new QueryRunner(this.em().unwrap(DataSource.class)).query("SELECT * FROM Dependent",
			new ColumnNameListHandler<List<String>>());

		Assert.assertEquals(3, columnNames.size());
		Assert.assertTrue(columnNames.contains("DEP_NAME"));
		Assert.assertTrue(columnNames.contains("FK1"));
		Assert.assertTrue(columnNames.contains("FK2"));

	}
}
