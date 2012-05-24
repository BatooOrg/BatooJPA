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
package org.batoo.jpa.core.test.manytomany;

import junit.framework.Assert;

import org.batoo.jpa.core.test.BaseCoreTest;
import org.junit.Test;

/**
 * @author hceylan
 * 
 * @since $version
 */
public class ManyToManyTest extends BaseCoreTest {

	/**
	 * Tests to persist {@link ManyToMany} relations.
	 * 
	 * @since $version
	 * @author hceylan
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
