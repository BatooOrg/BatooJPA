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

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;

/**
 * 
 * @author asimarslan
 * @since $version
 */
@Entity
public class Dependent {

	@EmbeddedId
	private DependentId id;

	// id attribute mapped by join column default
	@MapsId("empPK")
	// maps empPK attribute of embedded id
	@ManyToOne
	private Employee employee;

	/**
	 * 
	 * @author aarslan
	 * @since $version
	 */
	public Dependent() {
		super();
	}

	/**
	 * @param id
	 * @param employee
	 * @author aarslan
	 * @since $version
	 */
	public Dependent(DependentId id, Employee employee) {
		super();
		this.id = id;
		this.employee = employee;
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
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Dependent other = (Dependent) obj;
		if (this.employee == null) {
			if (other.employee != null) {
				return false;
			}
		}
		else if (!this.employee.equals(other.employee)) {
			return false;
		}
		if (this.id == null) {
			if (other.id != null) {
				return false;
			}
		}
		else if (!this.id.equals(other.id)) {
			return false;
		}
		return true;
	}

	/**
	 * @return the employee
	 * @author aarslan
	 * @since $version
	 */
	public Employee getEmployee() {
		return this.employee;
	}

	/**
	 * @return the id
	 * @author aarslan
	 * @since $version
	 */
	public DependentId getId() {
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
		result = prime * result + ((this.employee == null) ? 0 : this.employee.hashCode());
		result = prime * result + ((this.id == null) ? 0 : this.id.hashCode());
		return result;
	}

	/**
	 * @param employee
	 *            the employee to set
	 * @author aarslan
	 * @since $version
	 */
	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	/**
	 * @param id
	 *            the id to set
	 * @author aarslan
	 * @since $version
	 */
	public void setId(DependentId id) {
		this.id = id;
	}

}
