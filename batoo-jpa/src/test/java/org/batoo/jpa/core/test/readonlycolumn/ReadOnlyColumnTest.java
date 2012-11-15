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
package org.batoo.jpa.core.test.readonlycolumn;

import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import junit.framework.Assert;

import org.batoo.jpa.core.impl.jdbc.dbutils.QueryRunner;
import org.batoo.jpa.core.test.BaseCoreTest;
import org.batoo.jpa.core.test.ColumnNameListHandler;
import org.junit.Test;

/**
 * @author asimarslan
 * @since $version
 * 
 */
public class ReadOnlyColumnTest extends BaseCoreTest {

	/**
	 * Tests generated DDL column names
	 * 
	 * @throws SQLException
	 *             thrown in case of an error
	 * @since $version
	 */
	@Test
	public void testColumnNames() throws SQLException {
		Person person = new Person(1, "Ceylan", new Address(1, "Istanbul"));
		this.persist(person);

		this.commit();
		this.close();

		final List<String> columnNames = new QueryRunner(this.em().unwrap(DataSource.class)).query("SELECT * FROM Person",
			new ColumnNameListHandler<List<String>>());

		person = this.find(Person.class, person.getId());

		Assert.assertFalse(this.emf().getPersistenceUnitUtil().isLoaded(person.getHomeAddress()));
		person.getHomeAddress().getId();
		Assert.assertTrue(this.emf().getPersistenceUnitUtil().isLoaded(person.getHomeAddress()));

		Assert.assertEquals(3, columnNames.size());
		Assert.assertTrue(columnNames.contains("id") || columnNames.contains("ID"));
		Assert.assertTrue(columnNames.contains("name") || columnNames.contains("NAME"));
		Assert.assertTrue(columnNames.contains("address_id") || columnNames.contains("ADDRESS_ID"));
		Assert.assertFalse(columnNames.contains("homeAddress") || columnNames.contains("homeaddress") || columnNames.contains("HOMEADDRESS"));
	}
}
