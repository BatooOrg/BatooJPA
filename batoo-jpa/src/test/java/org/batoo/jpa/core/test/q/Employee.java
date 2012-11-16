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
package org.batoo.jpa.core.test.q;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.ManyToOne;

/**
 * 
 * 
 * @author hceylan
 * @since 2.0.0
 */
@Entity
@Inheritance
public class Employee {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Integer id;

	private double salary;

	private String name;

	@ManyToOne
	private Department department;

	@ManyToOne
	private Manager manager;

	/**
	 * 
	 * @since 2.0.0
	 */
	public Employee() {
		super();
	}

	/**
	 * @param name
	 *            the name
	 * @param manager
	 *            the manager
	 * @param department
	 *            the department
	 * @param salary
	 *            the salary
	 * 
	 * @since 2.0.0
	 */
	public Employee(String name, Manager manager, Department department, double salary) {
		super();

		this.name = name;
		this.department = department;
		this.salary = salary;

		this.setManager(manager);
	}

	/**
	 * Returns the department of the Employee.
	 * 
	 * @return the department of the Employee
	 * 
	 * @since 2.0.0
	 */
	public Department getDepartment() {
		return this.department;
	}

	/**
	 * Returns the id of the Employee.
	 * 
	 * @return the id of the Employee
	 * 
	 * @since 2.0.0
	 */
	public Integer getId() {
		return this.id;
	}

	/**
	 * Returns the manager of the Employee.
	 * 
	 * @return the manager of the Employee
	 * 
	 * @since 2.0.0
	 */
	public Manager getManager() {
		return this.manager;
	}

	/**
	 * Returns the name of the Employee.
	 * 
	 * @return the name of the Employee
	 * 
	 * @since 2.0.0
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Returns the salary of the Employee.
	 * 
	 * @return the salary of the Employee
	 * 
	 * @since 2.0.0
	 */
	public double getSalary() {
		return this.salary;
	}

	/**
	 * Sets the department of the Employee.
	 * 
	 * @param department
	 *            the department to set for Employee
	 * 
	 * @since 2.0.0
	 */
	public void setDepartment(Department department) {
		this.department = department;
	}

	/**
	 * Sets the manager of the Employee.
	 * 
	 * @param manager
	 *            the manager to set for Employee
	 * 
	 * @since 2.0.0
	 */
	public void setManager(Manager manager) {
		if (this.manager != null) {
			this.manager.getEmployees().remove(this);
		}

		this.manager = manager;

		if (this.manager != null) {
			this.manager.getEmployees().add(this);
		}
	}

	/**
	 * Sets the name of the Employee.
	 * 
	 * @param name
	 *            the name to set for Employee
	 * 
	 * @since 2.0.0
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Sets the salary of the Employee.
	 * 
	 * @param salary
	 *            the salary to set for Employee
	 * 
	 * @since 2.0.0
	 */
	public void setSalary(double salary) {
		this.salary = salary;
	}
}
