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
package org.batoo.jpa.core.test.fetch.lazy;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

/**
 * 
 * @author hceylan
 * @since $version
 */
@Entity
public class HomePhone extends Phone {

	@ManyToOne(fetch = FetchType.LAZY)
	private Person person;

	/**
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public HomePhone() {
		super();
	}

	/**
	 * @param person
	 *            the person
	 * @param phone
	 *            the phone
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public HomePhone(Person person, String phone) {
		super(phone);

		this.person = person;

		this.person.getPhones().add(this);
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
