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
package org.batoo.jpa.core.test.manytoonetomany;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;

/**
 * 
 * @author hceylan
 * @since $version
 */
@Entity
public class Phone {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Integer id;

	@ManyToOne
	@JoinTable
	private Person person;

	private String phone;

	/**
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public Phone() {
		super();
	}

	/**
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public Phone(Person person, String phone) {
		super();

		this.person = person;
		this.phone = phone;

		person.getPhones().add(this);
	}

	/**
	 * Returns the id of the Phone.
	 * 
	 * @return the id of the Phone
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public Integer getId() {
		return this.id;
	}

	/**
	 * Returns the person of the Phone.
	 * 
	 * @return the person of the Phone
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public Person getPerson() {
		return this.person;
	}

	/**
	 * Returns the phone of the Phone.
	 * 
	 * @return the phone of the Phone
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public String getPhone() {
		return this.phone;
	}

	/**
	 * Sets the person of the Phone.
	 * 
	 * @param person
	 *            the person to set for Phone
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void setPerson(Person person) {
		this.person = person;
	}

	/**
	 * Sets the phone of the Phone.
	 * 
	 * @param phone
	 *            the phone to set for Phone
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void setPhone(String phone) {
		this.phone = phone;
	}

}
