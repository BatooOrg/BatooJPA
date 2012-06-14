/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.batoo.jpa.core.test.embeddable.overrides2;

import java.sql.SQLException;

import javax.persistence.EntityManager;
import javax.sql.DataSource;

import junit.framework.Assert;

import org.apache.commons.dbutils.QueryRunner;
import org.batoo.jpa.core.impl.jdbc.SingleValueHandler;
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

		final Integer count = new QueryRunner(this.em().unwrap(DataSource.class)).query("SELECT COUNT(*) FROM CUSTOMER",
			new SingleValueHandler<Integer>());
		Assert.assertEquals(new Integer(1), count);
	}
}
