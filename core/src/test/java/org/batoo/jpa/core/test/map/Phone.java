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
package org.batoo.jpa.core.test.map;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.ManyToOne;

/**
 * 
 * 
 * @author hceylan
 * @since $version
 */
@IdClass(PhoneId.class)
@Entity
public class Phone {

	@Id
	private String areaCode;

	@Id
	private String phoneNumber;

	private boolean active = true;

	@ManyToOne
	private Person person;

	/**
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public Phone() {
		super();
	}

	/**
	 * @param person
	 *            the person
	 * @param phoneId
	 *            the phone id
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public Phone(Person person, PhoneId phoneId) {
		super();

		this.person = person;
		this.areaCode = phoneId.getAreaCode();
		this.phoneNumber = phoneId.getPhoneNumber();

		this.person.getPhones().put(phoneId, this);
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
	 * Returns the active of the Phone.
	 * 
	 * @return the active of the Phone
	 * 
	 * @since $version
	 * @author hceylan
	 */
	protected boolean isActive() {
		return this.active;
	}

	/**
	 * Sets the active of the Phone.
	 * 
	 * @param active
	 *            the active to set for Phone
	 * 
	 * @since $version
	 * @author hceylan
	 */
	protected void setActive(boolean active) {
		this.active = active;
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
	 * Sets the person of the Phone.
	 * 
	 * @param person
	 *            the person to set for Phone
	 * 
	 * @since $version
	 * @author hceylan
	 */
	protected void setPerson(Person person) {
		this.person = person;
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

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String toString() {
		return "Phone [areaCode=" + this.areaCode + ", phoneNumber=" + this.phoneNumber + ", active=" + this.active + ", person=" + this.person + "]";
	}
}
