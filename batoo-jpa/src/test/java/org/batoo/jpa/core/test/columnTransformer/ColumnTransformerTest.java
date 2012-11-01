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
package org.batoo.jpa.core.test.columnTransformer;

import java.sql.SQLException;

import javax.persistence.EntityManager;
import javax.sql.DataSource;

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
public class ColumnTransformerTest extends BaseCoreTest {

	private final static String WRITE_STR = " writeValue";

	private FooRead newFooRead() {
		final FooRead foo = new FooRead();

		foo.setValue("    readValue");

		return foo;
	}

	private FooWrite newFooWrite() {
		final FooWrite foo = new FooWrite();

		foo.setValue(WRITE_STR);

		return foo;
	}

	/**
	 * Tests to {@link EntityManager#persist(Object)}.
	 * 
	 * @throws SQLException
	 *             thrown if fails
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testPersist() throws SQLException {
		final FooRead fooRead = this.newFooRead();
		this.persist(fooRead);

		final FooWrite fooWrite = this.newFooWrite();
		this.persist(fooWrite);

		this.commit();

		Assert.assertEquals(1,
			new QueryRunner(this.em().unwrap(DataSource.class)).query("SELECT COUNT(*) FROM FooRead", new SingleValueHandler<Number>()).intValue());
		Assert.assertEquals(1,
			new QueryRunner(this.em().unwrap(DataSource.class)).query("SELECT COUNT(*) FROM FooWrite", new SingleValueHandler<Number>()).intValue());
	}

	@Test
	public void testRead() throws SQLException {
		final FooRead foo = this.newFooRead();
		this.persist(foo);

		this.commit();

		this.close();

		final FooRead foo2 = this.find(FooRead.class, foo.getId());
		Assert.assertEquals(foo.getId(), foo2.getId());
		Assert.assertEquals(foo.getValue().trim(), foo2.getValue());
	}

	@Test
	public void testWrite() throws SQLException {
		final FooWrite foo = this.newFooWrite();
		this.persist(foo);

		this.commit();
		this.close();

		Assert.assertEquals(WRITE_STR.trim(),
			new QueryRunner(this.em().unwrap(DataSource.class)).query("SELECT colname FROM FooWrite", new SingleValueHandler<String>()));

	}

	@Test
	public void testWrite2() throws SQLException {
		final FooWrite foo = this.newFooWrite();
		this.persist(foo);

		this.commit();

		this.close();

		final FooWrite foo2 = this.find(FooWrite.class, foo.getId());

		this.refresh(foo2);

		Assert.assertEquals(foo.getId(), foo2.getId());
		Assert.assertEquals(WRITE_STR.trim(), foo2.getValue());

		// Assert.assertEquals(foo.getValue().trim(), foo2.getValue());
	}

}
