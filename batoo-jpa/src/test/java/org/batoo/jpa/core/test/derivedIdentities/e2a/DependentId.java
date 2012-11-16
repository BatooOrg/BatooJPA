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
package org.batoo.jpa.core.test.derivedIdentities.e2a;

/**
 * 
 * @author asimarslan
 * @since 2.0.0
 */
public class DependentId {

	private String name; // matches name of attribute
	private EmployeeId emp; // matches name of attribute and type of Employee PK

	/**
	 * 
	 * @since 2.0.0
	 */
	public DependentId() {
		super();
	}

	/**
	 * 
	 * @param name
	 *            the name
	 * @param emp
	 *            the employee
	 * @since 2.0.0
	 */
	public DependentId(String name, EmployeeId emp) {
		super();

		this.name = name;
		this.emp = emp;
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
		if (!(obj instanceof DependentId)) {
			return false;
		}
		final DependentId other = (DependentId) obj;
		if (this.emp == null) {
			if (other.emp != null) {
				return false;
			}
		}
		else if (!this.emp.equals(other.emp)) {
			return false;
		}
		if (this.name == null) {
			if (other.name != null) {
				return false;
			}
		}
		else if (!this.name.equals(other.name)) {
			return false;
		}
		return true;
	}

	/**
	 * 
	 * @return the emp
	 * 
	 * @since 2.0.0
	 */
	public EmployeeId getEmp() {
		return this.emp;
	}

	/**
	 * 
	 * @return the name
	 * 
	 * @since 2.0.0
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((this.emp == null) ? 0 : this.emp.hashCode());
		result = (prime * result) + ((this.name == null) ? 0 : this.name.hashCode());
		return result;
	}

	/**
	 * 
	 * @param emp
	 *            the emp to set
	 * 
	 * @since 2.0.0
	 */
	public void setEmp(EmployeeId emp) {
		this.emp = emp;
	}

	/**
	 * 
	 * @param name
	 *            the name to set
	 * 
	 * @since 2.0.0
	 */
	public void setName(String name) {
		this.name = name;
	}
}
