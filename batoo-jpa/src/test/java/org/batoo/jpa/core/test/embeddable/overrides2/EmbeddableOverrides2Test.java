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
package org.batoo.jpa.core.test.embeddable.overrides2;

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
public class EmbeddableOverrides2Test extends BaseCoreTest {

	private Customer customer() {
		final Customer customer = new Customer();
		customer.setName("Hasan Ceylan");

		final Address address = new Address();
		address.setCity("Istanbul");

		final Zipcode zipcode = new Zipcode();
		zipcode.setZip("11111");
		zipcode.setPlusfour("4444");
		address.setZipcode(zipcode);

		address.setCity("Istanbul");
		customer.setAddress(address);

		return customer;
	}

	/**
	 * Tests to {@link EntityManager#find(Class, Object)} with two level deep override.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testFind() {
		final Customer customer = this.customer();
		this.persist(customer);

		this.commit();

		this.close();

		final Customer customer2 = this.find(Customer.class, customer.getId());
		Assert.assertEquals(customer.getId(), customer2.getId());
		Assert.assertEquals(customer.getName(), customer2.getName());
		Assert.assertEquals(customer.getAddress().getCity(), customer2.getAddress().getCity());
		Assert.assertEquals(customer.getAddress().getZipcode().getZip(), customer2.getAddress().getZipcode().getZip());
		Assert.assertEquals(customer.getAddress().getZipcode().getPlusfour(), customer2.getAddress().getZipcode().getPlusfour());
	}

	/**
	 * Tests to {@link EntityManager#persist(Object)} with two level deep override.
	 * 
	 * @throws SQLException
	 *             thrown if SQL fails
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testPersist() throws SQLException {
		final Customer customer = this.customer();
		this.persist(customer);

		this.commit();

		final Number count = new QueryRunner(this.em().unwrap(DataSource.class)).query("SELECT COUNT(*) FROM Customer", new SingleValueHandler<Number>());
		Assert.assertEquals(1, count.intValue());
	}
}
