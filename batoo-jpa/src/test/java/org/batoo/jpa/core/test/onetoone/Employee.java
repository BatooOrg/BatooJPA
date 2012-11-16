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
package org.batoo.jpa.core.test.onetoone;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

/**
 * 
 * @author hceylan
 * @since $version
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
	 * @since $version
	 */
	public Employee() {
		super();
	}

	/**
	 * @param name
	 *            the name
	 * 
	 * @since $version
	 */
	public Employee(String name) {
		super();

		this.name = name;
	}

	/**
	 * Returns the assignedCubicle.
	 * 
	 * @return the assignedCubicle
	 * @since $version
	 */
	public Cubicle getAssignedCubicle() {
		return this.assignedCubicle;
	}

	/**
	 * Returns the id.
	 * 
	 * @return the id
	 * @since $version
	 */
	public Integer getId() {
		return this.id;
	}

	/**
	 * Returns the name.
	 * 
	 * @return the name
	 * @since $version
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Sets the assignedCubicle.
	 * 
	 * @param assignedCubicle
	 *            the assignedCubicle to set
	 * @since $version
	 */
	public void setAssignedCubicle(Cubicle assignedCubicle) {
		this.assignedCubicle = assignedCubicle;
	}

	/**
	 * Sets the name.
	 * 
	 * @param name
	 *            the name to set
	 * @since $version
	 */
	public void setName(String name) {
		this.name = name;
	}
}
