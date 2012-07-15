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
package org.batoo.jpa.core.test.orderby;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 * 
 * @author hceylan
 * @since $version
 */
@Entity
public class Address2 {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Integer id;

	@ManyToOne
	private Person person;

	private String street;

	private String city;

	/**
	 * @since $version
	 * @author hceylan
	 */
	public Address2() {
		super();
	}

	/**
	 * @param person
	 *            the person
	 * @param city
	 *            the city
	 * @param street
	 *            the street
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public Address2(Person person, String city, String street) {
		super();

		this.city = city;
		this.street = street;

		this.setPerson(person);
	}

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
	 * Returns the id.
	 * 
	 * @return the id
	 * @since $version
	 */
	public Integer getId() {
		return this.id;
	}

	/**
	 * Returns the person of the Address.
	 * 
	 * @return the person of the Address
	 * 
	 * @since $version
	 * @author hceylan
	 */
	protected Person getPerson() {
		return this.person;
	}

	/**
	 * Returns the street of the Address2.
	 * 
	 * @return the street of the Address2
	 * 
	 * @since $version
	 * @author hceylan
	 */
	protected String getStreet() {
		return this.street;
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
	 * Sets the person of the Address.
	 * 
	 * @param person
	 *            the person to set for Address
	 * 
	 * @since $version
	 * @author hceylan
	 */
	protected void setPerson(Person person) {
		if (this.person != null) {
			this.person.getAddresses().remove(this);
		}

		this.person = person;

		if (this.person != null) {
			this.person.getAddresses2().add(this);
		}
	}

	/**
	 * Sets the street of the Address2.
	 * 
	 * @param street
	 *            the street to set for Address2
	 * 
	 * @since $version
	 * @author hceylan
	 */
	protected void setStreet(String street) {
		this.street = street;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String toString() {
		return "Address2 [city=" + this.city + ", street=" + this.street + "]";
	}
}
