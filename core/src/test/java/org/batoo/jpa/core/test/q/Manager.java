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
