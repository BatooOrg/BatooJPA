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
package org.batoo.jpa.core.test.q;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.OneToOne;

/**
 * 
 * 
 * @author hceylan
 * @since $version
 */
@Embeddable
public class Contact {

	@Embedded
	private Address2 address;

	@OneToOne(cascade = CascadeType.ALL)
	private WorkPhone phone;

	/**
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public Contact() {
		super();
	}

	/**
	 * @param city
	 *            the city
	 * @param country
	 *            the country
	 * @param phone
	 *            the phone
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public Contact(String city, Country country, String phone) {
		super();

		this.address = new Address2(city, country);
		this.phone = new WorkPhone(phone);
	}

	/**
	 * Returns the address of the Contact.
	 * 
	 * @return the address of the Contact
	 * 
	 * @since $version
	 * @author hceylan
	 */
	protected Address2 getAddress() {
		return this.address;
	}

	/**
	 * Returns the phone of the Contact.
	 * 
	 * @return the phone of the Contact
	 * 
	 * @since $version
	 * @author hceylan
	 */
	protected Phone getPhone() {
		return this.phone;
	}

	/**
	 * Sets the address of the Contact.
	 * 
	 * @param address
	 *            the address to set for Contact
	 * 
	 * @since $version
	 * @author hceylan
	 */
	protected void setAddress(Address2 address) {
		this.address = address;
	}

	/**
	 * Sets the phone of the Contact.
	 * 
	 * @param phone
	 *            the phone to set for Contact
	 * 
	 * @since $version
	 * @author hceylan
	 */
	protected void setPhone(WorkPhone phone) {
		this.phone = phone;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String toString() {
		return "Contact [address=" + this.address + ", phone=" + this.phone + "]";
	}
}
