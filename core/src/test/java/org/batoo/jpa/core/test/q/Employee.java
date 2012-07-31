/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
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
 * @since $version
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
	 * @since $version
	 * @author hceylan
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
	 * @since $version
	 * @author hceylan
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
	 * @since $version
	 * @author hceylan
	 */
	public Department getDepartment() {
		return this.department;
	}

	/**
	 * Returns the id of the Employee.
	 * 
	 * @return the id of the Employee
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public Integer getId() {
		return this.id;
	}

	/**
	 * Returns the manager of the Employee.
	 * 
	 * @return the manager of the Employee
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public Manager getManager() {
		return this.manager;
	}

	/**
	 * Returns the name of the Employee.
	 * 
	 * @return the name of the Employee
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Returns the salary of the Employee.
	 * 
	 * @return the salary of the Employee
	 * 
	 * @since $version
	 * @author hceylan
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
	 * @since $version
	 * @author hceylan
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
	 * @since $version
	 * @author hceylan
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
	 * @since $version
	 * @author hceylan
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
	 * @since $version
	 * @author hceylan
	 */
	public void setSalary(double salary) {
		this.salary = salary;
	}
}
