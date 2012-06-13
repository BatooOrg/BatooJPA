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
package org.batoo.jpa.core.test.fetch.eager;

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
public class Phone {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Integer id;

	@ManyToOne
	private Person person;

	private String number;

	/**
	 * @since $version
	 * @author hceylan
	 */
	public Phone() {
		super();
	}

	/**
	 * @param person
	 *            the person
	 * @param number
	 *            the number
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public Phone(Person person, String number) {
		super();

		this.person = person;
		this.number = number;

		this.person.getPhones().add(this);
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
	 * Returns the number of the Phone.
	 * 
	 * @return the number of the Phone
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public String getNumber() {
		return this.number;
	}

	/**
	 * Returns the person.
	 * 
	 * @return the person
	 * @since $version
	 */
	public Person getPerson() {
		return this.person;
	}

	/**
	 * Sets the number of the Phone.
	 * 
	 * @param number
	 *            the number to set for Phone
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void setNumber(String number) {
		this.number = number;
	}

	/**
	 * Sets the person.
	 * 
	 * @param person
	 *            the person to set
	 * @since $version
	 */
	public void setPerson(Person person) {
		this.person = person;
	}
}
