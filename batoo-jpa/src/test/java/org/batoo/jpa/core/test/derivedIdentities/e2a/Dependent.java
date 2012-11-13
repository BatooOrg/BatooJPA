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

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;

/**
 * 
 * @author asimarslan
 * @since $version
 */
@Entity
@IdClass(DependentId.class)
public class Dependent {

	@Id
	private String name;

	@Id
	@JoinColumns({ @JoinColumn(name = "FK1", referencedColumnName = "firstName"),//
		@JoinColumn(name = "FK2", referencedColumnName = "lastName") })
	@ManyToOne
	private Employee emp;

	/**
	 * 
	 * @author asimarslan
	 * @since $version
	 */
	public Dependent() {
		super();
	}

	/**
	 * 
	 * @param name
	 * 
	 * @author asimarslan
	 * @since $version
	 */
	public Dependent(String name, Employee emp) {
		super();
		this.name = name;
		this.emp = emp;
	}

	/**
	 * 
	 * @return the emp
	 * 
	 * @author asimarslan
	 * @since $version
	 */
	public Employee getEmp() {
		return this.emp;
	}

	/**
	 * 
	 * @return the name
	 * 
	 * @author asimarslan
	 * @since $version
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * 
	 * @param emp
	 *            the emp to set
	 * 
	 * @author asimarslan
	 * @since $version
	 */
	public void setEmp(Employee emp) {
		this.emp = emp;
	}

	/**
	 * 
	 * @param name
	 *            the name to set
	 * 
	 * @author asimarslan
	 * @since $version
	 */
	public void setName(String name) {
		this.name = name;
	}

}
