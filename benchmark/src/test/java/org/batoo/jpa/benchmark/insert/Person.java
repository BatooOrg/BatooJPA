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
package org.batoo.jpa.benchmark.insert;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Version;

import com.google.common.collect.Lists;

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

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER, mappedBy = "person")
	private final List<Address> addresses = Lists.newArrayList();

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "person")
	private final List<Phone> phones = Lists.newArrayList();

	@Version
	private Integer version;

	private String name;

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		final Person other = (Person) obj;
		if (this.id == null) {
			return false;
		}
		else if (!this.id.equals(other.id)) {
			return false;
		}
		return true;
	}

	/**
	 * Returns the addresses.
	 * 
	 * @return the addresses
	 * @since $version
	 */
	public List<Address> getAddresses() {
		return this.addresses;
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
	 * Returns the phones.
	 * 
	 * @return the phones
	 * @since $version
	 */
	public List<Phone> getPhones() {
		return this.phones;
	}

	/**
	 * Returns the version of the Person.
	 * 
	 * @return the version of the Person
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public Integer getVersion() {
		return this.version;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((this.id == null) ? 0 : this.id.hashCode());
		return result;
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
