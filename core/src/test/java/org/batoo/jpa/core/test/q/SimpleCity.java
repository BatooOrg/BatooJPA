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
package org.batoo.jpa.core.test.q;

/**
 * 
 * @author hceylan
 * @since $version
 */
public class SimpleCity implements Comparable<SimpleCity> {

	private String city;
	private String country;

	/**
	 * @param city
	 *            the city
	 * @param country
	 *            the country
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public SimpleCity(String city, String country) {
		super();

		this.city = city;
		this.country = country;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public int compareTo(SimpleCity o) {
		if (this.country.compareTo(o.country) != 0) {
			return this.country.compareTo(o.country);
		}

		if (this.city.compareTo(o.city) != 0) {
			return this.city.compareTo(o.city);
		}

		return 0;
	}

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
		final SimpleCity other = (SimpleCity) obj;
		if (this.city == null) {
			if (other.city != null) {
				return false;
			}
		}
		else if (!this.city.equals(other.city)) {
			return false;
		}
		if (this.country == null) {
			if (other.country != null) {
				return false;
			}
		}
		else if (!this.country.equals(other.country)) {
			return false;
		}
		return true;
	}

	/**
	 * Returns the city of the SimpleCity.
	 * 
	 * @return the city of the SimpleCity
	 * 
	 * @since $version
	 * @author hceylan
	 */
	protected String getCity() {
		return this.city;
	}

	/**
	 * Returns the country of the SimpleCity.
	 * 
	 * @return the country of the SimpleCity
	 * 
	 * @since $version
	 * @author hceylan
	 */
	protected String getCountry() {
		return this.country;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((this.city == null) ? 0 : this.city.hashCode());
		result = (prime * result) + ((this.country == null) ? 0 : this.country.hashCode());
		return result;
	}

	/**
	 * Sets the city of the SimpleCity.
	 * 
	 * @param city
	 *            the city to set for SimpleCity
	 * 
	 * @since $version
	 * @author hceylan
	 */
	protected void setCity(String city) {
		this.city = city;
	}

	/**
	 * Sets the country of the SimpleCity.
	 * 
	 * @param country
	 *            the country to set for SimpleCity
	 * 
	 * @since $version
	 * @author hceylan
	 */
	protected void setCountry(String country) {
		this.country = country;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String toString() {
		return "SimpleCity [city=" + this.city + ", country=" + this.country + "]";
	}

}
