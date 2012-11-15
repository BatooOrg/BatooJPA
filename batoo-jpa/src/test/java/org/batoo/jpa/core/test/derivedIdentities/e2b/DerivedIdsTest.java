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
package org.batoo.jpa.core.test.derivedIdentities.e2b;

import java.sql.SQLException;

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
// FIXME: Temporarily ignored
public class DerivedIdsTest extends BaseCoreTest {

	/**
	 * 
	 * Example-2 Case (b):
	 * <p>
	 * The parent entity uses IdClass
	 * <p>
	 * The dependent entity uses EmbeddedId to represent a composite key:
	 * 
	 * @author asimarslan
	 * @since $version
	 */
	@Test
	public void test2bJPQL() {
		final Employee employee = new Employee("Sam", "Doe");

		this.persist(employee);
		this.commit();

		final Dependent dependent1 = new Dependent(new DependentId("Joe", new EmployeeId(employee.getFirstName(), employee.getLastName())), employee);

		this.persist(dependent1);

		this.commit();
		this.close();

		final TypedQuery<Dependent> q = this.cq("select d from Dependent d where d.id.name = 'Joe' and d.emp.firstName = 'Sam'", Dependent.class);
		final Dependent dependent2 = q.getSingleResult();

		Assert.assertEquals(dependent1, dependent2);
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
		final Employee employee = new Employee("Sam", "Doe");

		this.persist(employee);
		this.commit();

		final Dependent dependent1 = new Dependent(new DependentId("Joe", new EmployeeId(employee.getFirstName(), employee.getLastName())), employee);

		this.persist(dependent1);

		this.commit();
		this.close();

		Assert.assertEquals("[firstname, lastname, name]",
			new QueryRunner(this.em().unwrap(DataSource.class)).query("SELECT * FROM Dependent", new ColumnNameListHandler()));
	}
}
