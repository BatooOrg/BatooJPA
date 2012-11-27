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
package org.batoo.jpa.core.test.onetoonetoone;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

/**
 * 
 * @author hceylan
 * @since 2.0.0
 */
@Entity
public class Employee {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Integer id;

	@OneToOne(fetch = FetchType.EAGER)
	private Cubicle assignedCubicle;

	private String name;

	/**
	 * @since 2.0.0
	 */
	public Employee() {
		super();
	}

	/**
	 * @param name
	 *            the name
	 * 
	 * @since 2.0.0
	 */
	public Employee(String name) {
		super();

		this.name = name;
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
	 * Returns the assignedCubicle.
	 * 
	 * @return the assignedCubicle
	 * @since 2.0.0
	 */
	public Cubicle getAssignedCubicle() {
		return this.assignedCubicle;
	}

	/**
	 * Returns the id.
	 * 
	 * @return the id
	 * @since 2.0.0
	 */
	public Integer getId() {
		return this.id;
	}

	/**
	 * Returns the name.
	 * 
	 * @return the name
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
		result = (prime * result) + ((this.id == null) ? 0 : this.id.hashCode());
		return result;
	}

	/**
	 * Sets the assignedCubicle.
	 * 
	 * @param assignedCubicle
	 *            the assignedCubicle to set
	 * @since 2.0.0
	 */
	public void setAssignedCubicle(Cubicle assignedCubicle) {
		this.assignedCubicle = assignedCubicle;
	}

	/**
	 * Sets the name.
	 * 
	 * @param name
	 *            the name to set
	 * @since 2.0.0
	 */
	public void setName(String name) {
		this.name = name;
	}
}
