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

import javax.persistence.Embeddable;

/**
 * 
 * @author hceylan
 * @since $version
 */
@Embeddable
public class PhoneId {

	private String areaCode;
	private String phoneNumber;

	/**
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public PhoneId() {
		super();
	}

	/**
	 * @param areaCode
	 *            the area code
	 * @param phoneNumber
	 *            the phone number
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public PhoneId(String areaCode, String phoneNumber) {
		super();

		this.areaCode = areaCode;
		this.phoneNumber = phoneNumber;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		final PhoneId other = (PhoneId) obj;
		if (this.areaCode == null) {
			if (other.areaCode != null) {
				return false;
			}
		}
		else if (!this.areaCode.equals(other.areaCode)) {
			return false;
		}
		if (this.phoneNumber == null) {
			if (other.phoneNumber != null) {
				return false;
			}
		}
		else if (!this.phoneNumber.equals(other.phoneNumber)) {
			return false;
		}
		return true;
	}

	/**
	 * Returns the areaCode of the PhoneId.
	 * 
	 * @return the areaCode of the PhoneId
	 * 
	 * @since $version
	 * @author hceylan
	 */
	protected String getAreaCode() {
		return this.areaCode;
	}

	/**
	 * Returns the phoneNumber of the PhoneId.
	 * 
	 * @return the phoneNumber of the PhoneId
	 * 
	 * @since $version
	 * @author hceylan
	 */
	protected String getPhoneNumber() {
		return this.phoneNumber;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((this.areaCode == null) ? 0 : this.areaCode.hashCode());
		result = (prime * result) + ((this.phoneNumber == null) ? 0 : this.phoneNumber.hashCode());
		return result;
	}

	/**
	 * Sets the areaCode of the PhoneId.
	 * 
	 * @param areaCode
	 *            the areaCode to set for PhoneId
	 * 
	 * @since $version
	 * @author hceylan
	 */
	protected void setAreaCode(String areaCode) {
		this.areaCode = areaCode;
	}

	/**
	 * Sets the phoneNumber of the PhoneId.
	 * 
	 * @param phoneNumber
	 *            the phoneNumber to set for PhoneId
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
		return "PhoneId [areaCode=" + this.areaCode + ", phoneNumber=" + this.phoneNumber + "]";
	}

}
