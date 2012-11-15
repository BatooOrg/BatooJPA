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

import java.sql.SQLException;

import javax.persistence.TypedQuery;
import javax.sql.DataSource;

import junit.framework.Assert;

import org.batoo.jpa.core.impl.jdbc.dbutils.QueryRunner;
import org.batoo.jpa.core.test.BaseCoreTest;
import org.batoo.jpa.core.test.ColumnNameListHandler;
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
	 * @since $version
	 */
	@Test
	public void test1aJPQL() {
		final Employee employee = new Employee("Sam");
		this.persist(employee);
		this.commit();

		final Dependent dependent1 = new Dependent("Joe", employee);

		this.persist(dependent1);

		this.commit();
		this.close();

		final TypedQuery<Dependent> q = this.cq("select d from Dependent d where d.name = 'Joe' AND d.emp.empName = 'Sam'", Dependent.class);
		final Dependent dependent2 = q.getSingleResult();

		Assert.assertNotNull(dependent2);

		Assert.assertEquals("Joe", dependent2.getName());
		Assert.assertEquals("Sam", dependent2.getEmp().getEmpName());
	}

	/**
	 * Tests generated DDL column names
	 * 
	 * @throws SQLException
	 *             thrown in case of an error
	 * @since $version
	 */
	@Test
	public void testColumnNames() throws SQLException {
		final Employee employee = new Employee("Sam");
		this.persist(employee);
		this.commit();

		final Dependent dependent1 = new Dependent("Joe", employee);

		this.persist(dependent1);

		this.commit();
		this.close();

		Assert.assertEquals("[emp_empid, name]", //
			new QueryRunner(this.em().unwrap(DataSource.class)).query("SELECT * FROM Dependent", new ColumnNameListHandler()));
	}
}
