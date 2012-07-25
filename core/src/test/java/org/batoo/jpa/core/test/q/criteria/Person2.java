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
package org.batoo.jpa.core.test.q.criteria;

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
public class Person2 {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Integer id;

	@Embedded
	private Contact contact;

	private String name;

	/**
	 * @since $version
	 * @author hceylan
	 */
	public Person2() {
		super();
	}

	/**
	 * @param name
	 *            the name
	 * @param city
	 *            the city
	 * @param country
	 *            the country
	 * @param phone
	 *            the phone
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public Person2(String name, String city, Country country, String phone) {
		super();

		this.name = name;
		this.contact = new Contact(city, country, phone);
	}

	/**
	 * Returns the contact of the Person2.
	 * 
	 * @return the contact of the Person2
	 * 
	 * @since $version
	 * @author hceylan
	 */
	protected Contact getContact() {
		return this.contact;
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
	 * Sets the contact of the Person2.
	 * 
	 * @param contact
	 *            the contact to set for Person2
	 * 
	 * @since $version
	 * @author hceylan
	 */
	protected void setContact(Contact contact) {
		this.contact = contact;
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
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String toString() {
		return "[name=" + this.name + ", contact=" + this.contact + "]";
	}
}
