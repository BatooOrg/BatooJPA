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
package org.batoo.jpa.core.test.map;

import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;

import com.google.common.collect.Maps;

/**
 * 
 * @author hceylan
 * @since $version
 */
@Entity
public class Person {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Integer id;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	private final Map<Integer, Address> addresses1 = Maps.newHashMap();

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinTable(name = "Person_Address2")
	@MapKey(name = "city")
	private final Map<String, Address> addresses2 = Maps.newHashMap();

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER, mappedBy = "person")
	private final Map<PhoneId, Phone> phones = Maps.newHashMap();

	private String name;

	/**
	 * @since $version
	 * @author hceylan
	 */
	public Person() {
		super();
	}

	/**
	 * @param name
	 *            the name
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public Person(String name) {
		super();

		this.name = name;
	}

	/**
	 * Returns the addresses1 of the Person.
	 * 
	 * @return the addresses1 of the Person
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public Map<Integer, Address> getAddresses1() {
		return this.addresses1;
	}

	/**
	 * Returns the addresses2 of the Person.
	 * 
	 * @return the addresses2 of the Person
	 * 
	 * @since $version
	 * @author hceylan
	 */
	protected Map<String, Address> getAddresses2() {
		return this.addresses2;
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
	 * Returns the phones of the Person.
	 * 
	 * @return the phones of the Person
	 * 
	 * @since $version
	 * @author hceylan
	 */
	protected Map<PhoneId, Phone> getPhones() {
		return this.phones;
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
