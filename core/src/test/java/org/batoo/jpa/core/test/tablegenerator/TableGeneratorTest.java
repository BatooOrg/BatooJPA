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
package org.batoo.jpa.core.test.tablegenerator;

import java.sql.SQLException;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.metamodel.EntityType;
import javax.sql.DataSource;

import junit.framework.Assert;

import org.apache.commons.dbutils.QueryRunner;
import org.batoo.jpa.core.impl.jdbc.SingleValueHandler;
import org.batoo.jpa.core.test.BaseCoreTest;
import org.batoo.jpa.core.test.NullResultSetHandler;
import org.junit.Test;

/**
 * @author hceylan
 * 
 * @since $version
 */
public class TableGeneratorTest extends BaseCoreTest {

	/**
	 * Tests {@link EntityManager#contains(Object)}.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testContains() {
		final Foo foo = new Foo();
		final Foo foo2 = new Foo();

		this.persist(foo);

		Assert.assertTrue(this.contains(foo));
		Assert.assertFalse(this.contains(foo2));
	}

	/**
	 * Tests {@link EntityManagerFactory#createEntityManager()}
	 * 
	 * @throws SQLException
	 *             thrown if SQL fails
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testCreateTables() throws SQLException {
		final Set<EntityType<?>> entities = this.em().getMetamodel().getEntities();

		Assert.assertEquals(1, entities.size());

		final DataSource dataSource = this.em().unwrap(DataSource.class);
		new QueryRunner(dataSource).query("SELECT * FROM FOO", new NullResultSetHandler());
	}

	/**
	 * Tests to {@link EntityManager#find(Class, Object)}
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testFind() {
		final Foo foo = new Foo();
		this.persist(foo);

		this.commit();

		this.close();

		final Foo foo2 = this.find(Foo.class, foo.getId());
		Assert.assertEquals(foo.getId(), foo2.getId());
	}

	/**
	 * Tests to {@link EntityManager#persist(Object)}.
	 * 
	 * @throws SQLException
	 *             thrown if SQL fails
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testPersist() throws SQLException {
		final Foo foo = new Foo();
		this.persist(foo);

		this.commit();

		final Integer count = new QueryRunner(this.em().unwrap(DataSource.class)).query("SELECT COUNT(*) FROM FOO", new SingleValueHandler<Integer>());
		Assert.assertEquals(new Integer(1), count);
	}
}
