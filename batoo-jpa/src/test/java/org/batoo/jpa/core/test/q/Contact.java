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
package org.batoo.jpa.core.test.q;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.OneToOne;

/**
 * 
 * 
 * @author hceylan
 * @since 2.0.0
 */
@Embeddable
public class Contact {

	@Embedded
	private Address2 address;

	@OneToOne(cascade = CascadeType.ALL)
	private WorkPhone phone;

	/**
	 * 
	 * @since 2.0.0
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
	 * @since 2.0.0
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
	 * @since 2.0.0
	 */
	protected Address2 getAddress() {
		return this.address;
	}

	/**
	 * Returns the phone of the Contact.
	 * 
	 * @return the phone of the Contact
	 * 
	 * @since 2.0.0
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
	 * @since 2.0.0
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
	 * @since 2.0.0
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
