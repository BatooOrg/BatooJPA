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

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import com.google.common.collect.Lists;

/**
 * @author hceylan
 * 
 * @since $version
 */
@Entity
public class Employee extends Person {

	private static final long serialVersionUID = 1L;

	private String name;

	@OneToOne
	private Address workAddress;

	@OneToMany
	private final List<Address> otherAddresses = Lists.newArrayList();

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
	 * Returns the otherAddresses.
	 * 
	 * @return the otherAddresses
	 * @since $version
	 */
	public List<Address> getOtherAddresses() {
		return this.otherAddresses;
	}

	/**
	 * Returns the workAddress.
	 * 
	 * @return the workAddress
	 * @since $version
	 */
	public Address getWorkAddress() {
		return this.workAddress;
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

	/**
	 * Sets the workAddress.
	 * 
	 * @param workAddress
	 *            the workAddress to set
	 * @since $version
	 */
	public void setWorkAddress(Address workAddress) {
		this.workAddress = workAddress;
	}

}
