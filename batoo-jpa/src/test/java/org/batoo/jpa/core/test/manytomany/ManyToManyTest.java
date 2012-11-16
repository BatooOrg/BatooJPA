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
package org.batoo.jpa.core.test.manytomany;

import javax.persistence.ManyToMany;

import junit.framework.Assert;

import org.batoo.jpa.core.test.BaseCoreTest;
import org.junit.Test;

/**
 * @author hceylan
 * 
 * @since 2.0.0
 */
public class ManyToManyTest extends BaseCoreTest {

	/**
	 * Tests to persist {@link ManyToMany} relations.
	 * 
	 * @since 2.0.0
	 */
	@Test
	public void testPersist() {
		final Customer customer1 = new Customer("Ceylan");
		final Customer customer2 = new Customer("Ceylanson");

		final PhoneNumber number1 = new PhoneNumber("111 111-1111");
		final PhoneNumber number2 = new PhoneNumber("222 222-2222");

		customer1.getPhoneNumbers().add(number1);
		customer1.getPhoneNumbers().add(number2);

		customer2.getPhoneNumbers().add(number1);
		customer2.getPhoneNumbers().add(number2);

		this.persist(customer1);
		this.persist(customer2);

		this.commit();
		this.close();

		final Customer customer1_2 = this.find(Customer.class, customer1.getId());
		final Customer customer2_2 = this.find(Customer.class, customer2.getId());

		Assert.assertTrue(customer2_2.getPhoneNumbers().size() == 2);
		Assert.assertTrue(customer2_2.getPhoneNumbers().size() == 2);

		Assert.assertEquals(customer1_2.getPhoneNumbers(), customer2_2.getPhoneNumbers());
	}
}
