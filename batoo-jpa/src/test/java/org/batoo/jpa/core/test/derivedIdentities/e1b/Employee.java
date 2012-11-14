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
package org.batoo.jpa.core.test.derivedIdentities.e1b;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * 
 * @author asimarslan
 * @since $version
 */
@Entity
public class Employee {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Integer id;

	private String empName;

	/**
	 * 
	 * @author asimarslan
	 * @since $version
	 */
	public Employee() {
		super();
	}

	/**
	 * @param empName
	 *            the name of the employee
	 * 
	 * @author asimarslan
	 * @since $version
	 */
	public Employee(String empName) {
		super();
		this.empName = empName;
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
		final Employee other = (Employee) obj;
		if (this.id == null) {
			if (other.id != null) {
				return false;
			}
		}
		else if (!this.id.equals(other.id)) {
			return false;
		}
		if (this.empName == null) {
			if (other.empName != null) {
				return false;
			}
		}
		else if (!this.empName.equals(other.empName)) {
			return false;
		}
		return true;
	}

	/**
	 * 
	 * @return the empName
	 * 
	 * @author asimarslan
	 * @since $version
	 */
	public String getEmpName() {
		return this.empName;
	}

	/**
	 * 
	 * @return the empId
	 * 
	 * @author asimarslan
	 * @since $version
	 */
	public Integer getId() {
		return this.id;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((this.id == null) ? 0 : this.id.hashCode());
		result = (prime * result) + ((this.empName == null) ? 0 : this.empName.hashCode());
		return result;
	}

	/**
	 * 
	 * @param empName
	 *            the empName to set
	 * 
	 * @author asimarslan
	 * @since $version
	 */
	public void setEmpName(String empName) {
		this.empName = empName;
	}

	/**
	 * 
	 * @param empId
	 *            the empId to set
	 * 
	 * @author asimarslan
	 * @since $version
	 */
	public void setId(Integer empId) {
		this.id = empId;
	}

}
