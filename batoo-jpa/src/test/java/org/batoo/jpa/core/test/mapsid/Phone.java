/*
 * Copyright (c) 2012-2013, Batu Alp Ceylan
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

package org.batoo.jpa.core.test.mapsid;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;

/**
 * 
 * @author hceylan
 * @since 2.0.0
 */
@Entity
public class Phone {

	@EmbeddedId
	private PhonePk id;

	@MapsId("personId")
	@ManyToOne
	private Person person;

	private String areaCode;
	private String phoneNumber;

	/**
	 * @since 2.0.0
	 */
	public Phone() {
		super();
	}

	/**
	 * @param person
	 *            the person
	 * @param id
	 *            the phone id
	 * @param areaCode
	 *            the area code
	 * @param phoneNumber
	 *            the phone number
	 * 
	 * @since 2.0.0
	 */
	public Phone(Person person, Integer id, String areaCode, String phoneNumber) {
		super();

		this.id = new PhonePk(id, person.getId());
		this.areaCode = areaCode;
		this.phoneNumber = phoneNumber;
		this.person = person;

		person.getPhones().add(this);
	}

	/**
	 * Returns the areaCode of the Phone.
	 * 
	 * @return the areaCode of the Phone
	 * 
	 * @since 2.0.0
	 */
	protected String getAreaCode() {
		return this.areaCode;
	}

	/**
	 * Returns the id of the Phone.
	 * 
	 * @return the id of the Phone
	 * 
	 * @since 2.0.0
	 */
	protected PhonePk getId() {
		return this.id;
	}

	/**
	 * Returns the person of the Phone.
	 * 
	 * @return the person of the Phone
	 * 
	 * @since 2.0.0
	 */
	protected Person getPerson() {
		return this.person;
	}

	/**
	 * Returns the phoneNumber of the Phone.
	 * 
	 * @return the phoneNumber of the Phone
	 * 
	 * @since 2.0.0
	 */
	protected String getPhoneNumber() {
		return this.phoneNumber;
	}

	/**
	 * Sets the areaCode of the Phone.
	 * 
	 * @param areaCode
	 *            the areaCode to set for Phone
	 * 
	 * @since 2.0.0
	 */
	protected void setAreaCode(String areaCode) {
		this.areaCode = areaCode;
	}

	/**
	 * Sets the phoneNumber of the Phone.
	 * 
	 * @param phoneNumber
	 *            the phoneNumber to set for Phone
	 * 
	 * @since 2.0.0
	 */
	protected void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

}
