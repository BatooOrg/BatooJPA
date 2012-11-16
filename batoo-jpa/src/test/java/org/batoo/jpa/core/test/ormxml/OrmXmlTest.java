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

import junit.framework.Assert;

import org.batoo.jpa.core.impl.jdbc.dbutils.QueryRunner;
import org.batoo.jpa.core.impl.jdbc.dbutils.SingleValueHandler;
import org.batoo.jpa.core.test.BaseCoreTest;
import org.junit.Test;

/**
 * @author hceylan
 * 
 * @since $version
 */
public class OrmXmlTest extends BaseCoreTest {

	/**
	 * Tests that the orm.xml is processed.
	 * 
	 * @throws SQLException
	 *             thrown in case of an error
	 * 
	 * @since $version
	 */
	@Test
	public void testOrmXmlProcessed() throws SQLException {
		Assert.assertEquals(0,
			new QueryRunner().query(this.em().getConnection(), "SELECT COUNT(*) FROM cachedpdf", new SingleValueHandler<Number>()).intValue());
	}
}
