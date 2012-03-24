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
	 * @author hceylan
	 */
	public Employee() {
		super();
	}

	/**
	 * @param name
	 * 
	 * @since $version
	 * @author hceylan
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
