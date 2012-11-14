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

import java.sql.SQLException;
import java.util.List;

import javax.persistence.TypedQuery;
import javax.sql.DataSource;

import junit.framework.Assert;

import org.batoo.jpa.core.impl.jdbc.dbutils.QueryRunner;
import org.batoo.jpa.core.test.BaseCoreTest;
import org.batoo.jpa.core.test.ColumnNameListHandler;
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
	public void test1bJPQL() {

		final Employee employee = new Employee("Sam");

		this.persist(employee);
		this.commit();

		final DependentId dependentId1 = new DependentId("Joe", employee.getEmpId());
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
		Assert.assertEquals("Sam", resultList.get(0).getEmp().getEmpName());

	}

	/**
	 * Tests generated DDL column names
	 * 
	 * @throws SQLException
	 * @since $version
	 */
	@Test
	public void testColumnNames() throws SQLException {
		final Employee employee = new Employee("Sam");

		this.persist(employee);
		this.commit();

		final DependentId dependentId1 = new DependentId("Joe", employee.getEmpId());
		final Dependent dependent1 = new Dependent(dependentId1, employee);

		this.persist(dependent1);

		this.commit();
		this.close();

		final List<String> columnNames = new QueryRunner(this.em().unwrap(DataSource.class)).query("SELECT * FROM Dependent",
			new ColumnNameListHandler<List<String>>());

		Assert.assertEquals(2, columnNames.size());
		Assert.assertTrue(columnNames.contains("NAME"));
		Assert.assertTrue(columnNames.contains("EMPPK"));

	}
}
