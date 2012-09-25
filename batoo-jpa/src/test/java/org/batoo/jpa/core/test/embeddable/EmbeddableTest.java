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
package org.batoo.jpa.core.test.embeddable;

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
public class EmbeddableTest extends BaseCoreTest {

	private Foo foo() {
		final Foo foo = new Foo();
		foo.setValue("Value");

		final Address homeAddress = new Address();
		homeAddress.setCity("Istanbul");
		homeAddress.setStreet("Sweat home street");
		foo.setAddress(homeAddress);

		return foo;
	}

	/**
	 * Tests to {@link EntityManager#find(Class, Object)}
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testFind() {
		final Foo foo = this.foo();
		this.persist(foo);

		this.commit();

		this.close();

		final Foo foo2 = this.find(Foo.class, foo.getId());
		Assert.assertEquals(foo.getId(), foo2.getId());
		Assert.assertEquals(foo.getValue(), foo2.getValue());
		Assert.assertEquals(foo.getAddress().getCity(), foo2.getAddress().getCity());
		Assert.assertEquals(foo.getAddress().getStreet(), foo2.getAddress().getStreet());
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
		final Foo foo = this.foo();
		this.persist(foo);

		this.commit();

		Assert.assertEquals(1,
			new QueryRunner(this.em().unwrap(DataSource.class)).query("SELECT COUNT(*) FROM Foo", new SingleValueHandler<Number>()).intValue());
	}
}
