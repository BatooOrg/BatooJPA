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
package org.batoo.jpa.core.test.enums;

import java.sql.SQLException;

import javax.persistence.EntityManager;
import javax.sql.DataSource;

import junit.framework.Assert;

import org.batoo.jpa.core.impl.jdbc.dbutils.QueryRunner;
import org.batoo.jpa.core.impl.jdbc.dbutils.SingleValueHandler;
import org.batoo.jpa.core.test.BaseCoreTest;
import org.batoo.jpa.core.test.enums.Foo.FooType;
import org.junit.Test;

/**
 * @author hceylan
 * 
 * @since $version
 */
public class EnumTest extends BaseCoreTest {

	/**
	 * Tests to {@link EntityManager#find(Class, Object)}.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testFind() {
		Foo foo = new Foo();
		this.persist(foo);

		this.commit();
		this.close();

		foo = this.find(Foo.class, foo.getId());
		Assert.assertNull(foo.getFootype());
		Assert.assertNull(foo.getFootype2());
	}

	/**
	 * Tests to {@link EntityManager#find(Class, Object)}.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testFind2() {
		Foo foo = new Foo();
		foo.setFootype(FooType.TYPE1);
		foo.setFootype2(FooType.TYPE2);
		this.persist(foo);

		this.commit();
		this.close();

		foo = this.find(Foo.class, foo.getId());
		Assert.assertEquals(FooType.TYPE1, foo.getFootype());
		Assert.assertEquals(FooType.TYPE2, foo.getFootype2());
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
		final Foo foo = new Foo();
		this.persist(foo);

		this.commit();

		Assert.assertNull(new QueryRunner(this.em().unwrap(DataSource.class)).query("SELECT FOOTYPE FROM Foo", new SingleValueHandler<String>()));
		Assert.assertNull(new QueryRunner(this.em().unwrap(DataSource.class)).query("SELECT FOOTYPE2 FROM Foo", new SingleValueHandler<Integer>()));
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
	public void testPersist2() throws SQLException {
		final Foo foo = new Foo();
		foo.setFootype(FooType.TYPE1);
		foo.setFootype2(FooType.TYPE2);
		this.persist(foo);

		this.commit();

		Assert.assertEquals(FooType.TYPE1.name(),
			new QueryRunner(this.em().unwrap(DataSource.class)).query("SELECT FOOTYPE FROM Foo", new SingleValueHandler<String>()));
		Assert.assertEquals(1,
			new QueryRunner(this.em().unwrap(DataSource.class)).query("SELECT FOOTYPE2 FROM Foo", new SingleValueHandler<Number>()).intValue());
	}

	/**
	 * Tests to update.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testUpdate() {
		Foo foo = new Foo();
		this.persist(foo);

		this.commit();
		this.close();

		this.begin();
		foo = this.find(Foo.class, foo.getId());

		foo.setFootype(FooType.TYPE1);
		foo.setFootype2(FooType.TYPE2);

		this.commit();
		this.close();

		foo = this.find(Foo.class, foo.getId());

		Assert.assertEquals(FooType.TYPE1, foo.getFootype());
		Assert.assertEquals(FooType.TYPE2, foo.getFootype2());
	}

	/**
	 * Tests to update.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testUpdate2() {
		Foo foo = new Foo();
		foo.setFootype(FooType.TYPE3);
		foo.setFootype2(FooType.TYPE3);

		this.persist(foo);

		this.commit();
		this.close();

		this.begin();
		foo = this.find(Foo.class, foo.getId());

		foo.setFootype(FooType.TYPE1);
		foo.setFootype2(FooType.TYPE2);

		this.commit();
		this.close();

		foo = this.find(Foo.class, foo.getId());

		Assert.assertEquals(FooType.TYPE1, foo.getFootype());
		Assert.assertEquals(FooType.TYPE2, foo.getFootype2());
	}
}
