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
package org.batoo.jpa.core.test.mapsid;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;

/**
 * 
 * @author hceylan
 * @since $version
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
	 * @since $version
	 * @author hceylan
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
	 * @since $version
	 * @author hceylan
	 */
	public Phone(Person person, Integer id, String areaCode, String phoneNumber) {
		super();

		this.id = new PhonePk(id, null);
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
	 * @since $version
	 * @author hceylan
	 */
	protected String getAreaCode() {
		return this.areaCode;
	}

	/**
	 * Returns the id of the Phone.
	 * 
	 * @return the id of the Phone
	 * 
	 * @since $version
	 * @author hceylan
	 */
	protected PhonePk getId() {
		return this.id;
	}

	/**
	 * Returns the person of the Phone.
	 * 
	 * @return the person of the Phone
	 * 
	 * @since $version
	 * @author hceylan
	 */
	protected Person getPerson() {
		return this.person;
	}

	/**
	 * Returns the phoneNumber of the Phone.
	 * 
	 * @return the phoneNumber of the Phone
	 * 
	 * @since $version
	 * @author hceylan
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
	 * @since $version
	 * @author hceylan
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
	 * @since $version
	 * @author hceylan
	 */
	protected void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

}
