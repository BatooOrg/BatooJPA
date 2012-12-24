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
package org.batoo.jpa.core.test.ormxml;

import java.sql.SQLException;

import javax.persistence.EntityManager;
import javax.sql.DataSource;

import junit.framework.Assert;

import org.batoo.jpa.core.test.BaseCoreTest;
import org.batoo.jpa.jdbc.dbutils.QueryRunner;
import org.batoo.jpa.jdbc.dbutils.SingleValueHandler;
import org.junit.Test;

/**
 * @author hceylan
 * 
 * @since 2.0.0
 */
public class OrmXmlTest extends BaseCoreTest {

	/**
	 * Tests to {@link EntityManager#find(Class, Object)}
	 * 
	 * @throws SQLException
	 * 
	 * @since 2.0.0
	 */
	@Test
	public void testFind() throws SQLException {
		this.persist(new E4("test-E1"));
		this.persist(new E4("test-E2"));

		this.commit();
		this.close();

		Assert.assertEquals(2,
			new QueryRunner(this.em().unwrap(DataSource.class)).query("SELECT COUNT(*) FROM E4", new SingleValueHandler<Number>()).intValue());

		// final E1 e = this.find(E1.class, e1.getId());
		// Assert.assertEquals(e.getId(), e1.getId());
	}

	/**
	 * Tests that the orm.xml is processed.
	 * 
	 * @throws SQLException
	 *             thrown in case of an error
	 * 
	 * @since 2.0.0
	 */
	@Test
	public void testOrmXmlProcessed() throws SQLException {
		Assert.assertEquals(0,
			new QueryRunner().query(this.em().getConnection(), "SELECT COUNT(*) FROM cachedpdf", new SingleValueHandler<Number>()).intValue());
	}
}
