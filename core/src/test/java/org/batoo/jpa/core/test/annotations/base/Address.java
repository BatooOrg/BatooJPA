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
package org.batoo.jpa.core.test.annotations.base;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 * @author hceylan
 * @since $version
 */
@Entity
public class Address implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	private Integer id;

	@Basic
	private String streetAddress;

	@ManyToOne
	private Employee employee;

	private String town;

	private String city;

	private boolean valid = true;

	/**
	 * Returns the city.
	 * 
	 * @return the city
	 * @since $version
	 */
	public String getCity() {
		return this.city;
	}

	/**
	 * Returns the employee.
	 * 
	 * @return the employee
	 * @since $version
	 */
	public Employee getEmployee() {
		return this.employee;
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
	 * Returns the streetAddress.
	 * 
	 * @return the streetAddress
	 * @since $version
	 */
	public String getStreetAddress() {
		return this.streetAddress;
	}

	/**
	 * Returns the town.
	 * 
	 * @return the town
	 * @since $version
	 */
	public String getTown() {
		return this.town;
	}

	/**
	 * Returns the valid.
	 * 
	 * @return the valid
	 * @since $version
	 */
	public boolean isValid() {
		return this.valid;
	}

	/**
	 * Sets the city.
	 * 
	 * @param city
	 *            the city to set
	 * @since $version
	 */
	public void setCity(String city) {
		this.city = city;
	}

	/**
	 * Sets the employee.
	 * 
	 * @param employee
	 *            the employee to set
	 * @since $version
	 */
	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	/**
	 * Sets the id.
	 * 
	 * @param id
	 *            the id to set
	 * @since $version
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * Sets the streetAddress.
	 * 
	 * @param streetAddress
	 *            the streetAddress to set
	 * @since $version
	 */
	public void setStreetAddress(String streetAddress) {
		this.streetAddress = streetAddress;
	}

	/**
	 * Sets the town.
	 * 
	 * @param town
	 *            the town to set
	 * @since $version
	 */
	public void setTown(String town) {
		this.town = town;
	}

	/**
	 * Sets the valid.
	 * 
	 * @param valid
	 *            the valid to set
	 * @since $version
	 */
	public void setValid(boolean valid) {
		this.valid = valid;
	}
}
