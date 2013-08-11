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

package org.batoo.jpa.core.test.derivedIdentities.e2b;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;

/**
 * 
 * @author asimarslan
 * @since 2.0.0
 */
@Entity
@IdClass(EmployeeId.class)
public class Employee {

	@Id
	private String firstName;

	@Id
	private String lastName;

	/**
	 * 
	 * @since 2.0.0
	 */
	public Employee() {
		super();
	}

	/**
	 * @param firstName
	 *            the first name
	 * @param lastName
	 *            the last name
	 * 
	 * @since 2.0.0
	 */
	public Employee(String firstName, String lastName) {
		super();

		this.firstName = firstName;
		this.lastName = lastName;
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
		if (!(obj instanceof Employee)) {
			return false;
		}
		final Employee other = (Employee) obj;
		if (this.firstName == null) {
			if (other.firstName != null) {
				return false;
			}
		}
		else if (!this.firstName.equals(other.firstName)) {
			return false;
		}
		if (this.lastName == null) {
			if (other.lastName != null) {
				return false;
			}
		}
		else if (!this.lastName.equals(other.lastName)) {
			return false;
		}
		return true;
	}

	/**
	 * 
	 * @return the firstName
	 * 
	 * @since 2.0.0
	 */
	public String getFirstName() {
		return this.firstName;
	}

	/**
	 * 
	 * @return the lastName
	 * 
	 * @since 2.0.0
	 */
	public String getLastName() {
		return this.lastName;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((this.firstName == null) ? 0 : this.firstName.hashCode());
		result = (prime * result) + ((this.lastName == null) ? 0 : this.lastName.hashCode());
		return result;
	}

	/**
	 * 
	 * @param firstName
	 *            the firstName to set
	 * 
	 * @since 2.0.0
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/**
	 * 
	 * @param lastName
	 *            the lastName to set
	 * 
	 * @since 2.0.0
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
}
