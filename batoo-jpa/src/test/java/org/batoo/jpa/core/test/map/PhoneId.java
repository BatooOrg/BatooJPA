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
