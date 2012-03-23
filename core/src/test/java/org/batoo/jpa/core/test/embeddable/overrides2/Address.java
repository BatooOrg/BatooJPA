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

import javax.persistence.Embeddable;
import javax.persistence.Embedded;

/**
 * 
 * @author hceylan
 * @since $version
 */
@Embeddable
public class Address {

	@Embedded
	private Zipcode zipcode;

	private String city;

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
	 * Returns the zipcode.
	 * 
	 * @return the zipcode
	 * @since $version
	 */
	public Zipcode getZipcode() {
		return this.zipcode;
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
	 * Sets the zipcode.
	 * 
	 * @param zipcode
	 *            the zipcode to set
	 * @since $version
	 */
	public void setZipcode(Zipcode zipcode) {
		this.zipcode = zipcode;
	}

}
