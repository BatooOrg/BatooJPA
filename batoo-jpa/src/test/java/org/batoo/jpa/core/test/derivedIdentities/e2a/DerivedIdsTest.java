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

package org.batoo.jpa.core.test.derivedIdentities.e2a;

import java.sql.SQLException;
import java.util.List;

import javax.persistence.TypedQuery;
import javax.sql.DataSource;

import junit.framework.Assert;

import org.batoo.jpa.core.test.BaseCoreTest;
import org.batoo.jpa.core.test.ColumnNameListHandler;
import org.batoo.jpa.jdbc.dbutils.QueryRunner;
import org.junit.Test;

/**
 * JPA Spec 2.4.1.3 test
 * 
 * @author asimarslan
 * @since 2.0.0
 */
public class DerivedIdsTest extends BaseCoreTest {

	/**
	 * 
	 * Example-2 Case (a):
	 * <p>
	 * The parent entity uses IdClass
	 * <p>
	 * The dependent entity uses IdClass
	 * 
	 * @since 2.0.0
	 */
	@Test
	public void test2aJPQL() {
		final Employee employee = new Employee("Sam", "Doe");

		final Dependent dependent1 = new Dependent("Joe", employee);

		this.persist(employee);
		this.persist(dependent1);

		this.commit();
		this.close();

		final String qstr = "select d from Dependent d where d.name = 'Joe' and d.emp.firstName = 'Sam'";

		final TypedQuery<Dependent> q = this.cq(qstr, Dependent.class);
		final List<Dependent> resultList = q.getResultList();

		Assert.assertNotNull(resultList);
		Assert.assertEquals(1, resultList.size());

		Assert.assertEquals("Joe", resultList.get(0).getName());
		Assert.assertEquals("Sam", resultList.get(0).getEmp().getFirstName());
		Assert.assertEquals("Doe", resultList.get(0).getEmp().getLastName());

	}

	/**
	 * Tests generated DDL column names
	 * 
	 * @throws SQLException
	 *             thrown in case of an error
	 * @since 2.0.0
	 */
	@Test
	public void testColumnNames() throws SQLException {
		final Employee employee = new Employee("Sam", "Doe");

		final Dependent dependent1 = new Dependent("Joe", employee);

		this.persist(employee);
		this.persist(dependent1);

		this.commit();
		this.close();

		Assert.assertEquals("[fk1, fk2, name]",
			new QueryRunner(this.em().unwrap(DataSource.class)).query("SELECT * FROM Dependent", new ColumnNameListHandler()));
	}
}
