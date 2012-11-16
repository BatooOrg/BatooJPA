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
package org.batoo.jpa.core.test.derivedIdentities.e3b;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

/**
 * 
 * @author asimarslan
 * @since 2.0.0
 */
@Entity
public class Employee {

	@EmbeddedId
	private EmployeeId empId;

	/**
	 * 
	 * @since 2.0.0
	 */
	public Employee() {
		super();
	}

	/**
	 * @param empId
	 *            the employee id
	 * 
	 * @since 2.0.0
	 */
	public Employee(EmployeeId empId) {
		super();

		this.empId = empId;
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
		if (this.empId == null) {
			if (other.empId != null) {
				return false;
			}
		}
		else if (!this.empId.equals(other.empId)) {
			return false;
		}
		return true;
	}

	/**
	 * 
	 * @return the empId
	 * 
	 * @since 2.0.0
	 */
	public EmployeeId getEmpId() {
		return this.empId;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((this.empId == null) ? 0 : this.empId.hashCode());
		return result;
	}

	/**
	 * 
	 * @param empId
	 *            the empId to set
	 * 
	 * @since 2.0.0
	 */
	public void setEmpId(EmployeeId empId) {
		this.empId = empId;
	}
}
