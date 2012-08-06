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

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.google.common.collect.Sets;

/**
 * 
 * 
 * @author hceylan
 * @since $version
 */
@Entity
public class Manager {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Integer id;

	private double salary;

	private String name;

	@ManyToOne
	private Department department;

	@OneToMany(mappedBy = "manager")
	private final Set<Employee> employees = Sets.newHashSet();

	/**
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public Manager() {
		super();
	}

	/**
	 * @param name
	 *            the name
	 * @param department
	 *            the department
	 * @param salary
	 *            the salary
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public Manager(String name, Department department, double salary) {
		super();

		this.name = name;
		this.department = department;
		this.salary = salary;
	}

	/**
	 * Returns the department of the Manager.
	 * 
	 * @return the department of the Manager
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public Department getDepartment() {
		return this.department;
	}

	/**
	 * Returns the employees of the Manager.
	 * 
	 * @return the employees of the Manager
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public Set<Employee> getEmployees() {
		return this.employees;
	}

	/**
	 * Returns the id of the Manager.
	 * 
	 * @return the id of the Manager
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public Integer getId() {
		return this.id;
	}

	/**
	 * Returns the name of the Manager.
	 * 
	 * @return the name of the Manager
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Returns the salary of the Manager.
	 * 
	 * @return the salary of the Manager
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public double getSalary() {
		return this.salary;
	}

	/**
	 * Sets the department of the Manager.
	 * 
	 * @param department
	 *            the department to set for Manager
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void setDepartment(Department department) {
		this.department = department;
	}

	/**
	 * Sets the name of the Manager.
	 * 
	 * @param name
	 *            the name to set for Manager
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Sets the salary of the Manager.
	 * 
	 * @param salary
	 *            the salary to set for Manager
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void setSalary(double salary) {
		this.salary = salary;
	}
}
