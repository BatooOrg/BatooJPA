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

	@MapsId("empPk")
	@ManyToOne
	private Employee emp; // id attribute mapped by join column default maps empPK attribute of embedded id

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
	 *            the dependent id
	 * @param employee
	 *            the employee id
	 * @author aarslan
	 * @since $version
	 */
	public Dependent(DependentId id, Employee employee) {
		super();

		this.id = id;
		this.emp = employee;
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
		if (!(obj instanceof Dependent)) {
			return false;
		}
		final Dependent other = (Dependent) obj;
		if (this.emp == null) {
			if (other.emp != null) {
				return false;
			}
		}
		else if (!this.emp.equals(other.emp)) {
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
	 * @return the emp
	 * @author aarslan
	 * @since $version
	 */
	public Employee getEmp() {
		return this.emp;
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
		result = (prime * result) + ((this.emp == null) ? 0 : this.emp.hashCode());
		result = (prime * result) + ((this.id == null) ? 0 : this.id.hashCode());
		return result;
	}

	/**
	 * @param emp
	 *            the employee to set
	 * @author aarslan
	 * @since $version
	 */
	public void setEmp(Employee emp) {
		this.emp = emp;
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

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String toString() {
		return "Dependent [id=" + this.id + ", emp=" + this.emp + "]";
	}
}
