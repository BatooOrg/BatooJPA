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
package org.batoo.jpa.core.test.embeddable.overrides2;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * 
 * @author hceylan
 * @since $version
 */
@Entity
public class Customer {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	private String name;

	@Embedded
	@AttributeOverrides({ @AttributeOverride(name = "city", column = @Column(name = "addrCity")), //
		@AttributeOverride(name = "zipcode.zip", column = @Column(name = "addrZip")) })
	private Address address;

	/**
	 * Returns the address.
	 * 
	 * @return the address
	 * @since $version
	 */
	public Address getAddress() {
		return this.address;
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
	 * Sets the address.
	 * 
	 * @param address
	 *            the address to set
	 * @since $version
	 */
	public void setAddress(Address address) {
		this.address = address;
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
