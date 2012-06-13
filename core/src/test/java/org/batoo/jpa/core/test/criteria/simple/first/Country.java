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
package org.batoo.jpa.core.test.criteria.simple.first;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * 
 * @author hceylan
 * @since $version
 */
@Entity
public class Country {

	@Id
	private String code;

	private String name;

	/**
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public Country() {
		super();
	}

	/**
	 * @param code
	 *            the code
	 * @param name
	 *            the name
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public Country(String code, String name) {
		super();

		this.code = code;
		this.name = name;
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
		final Country other = (Country) obj;
		if (this.code == null) {
			if (other.code != null) {
				return false;
			}
		}
		else if (!this.code.equals(other.code)) {
			return false;
		}
		if (this.name == null) {
			if (other.name != null) {
				return false;
			}
		}
		else if (!this.name.equals(other.name)) {
			return false;
		}
		return true;
	}

	/**
	 * Returns the code.
	 * 
	 * @return the code
	 * @since $version
	 */
	public String getCode() {
		return this.code;
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
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((this.code == null) ? 0 : this.code.hashCode());
		result = (prime * result) + ((this.name == null) ? 0 : this.name.hashCode());
		return result;
	}

	/**
	 * Sets the code.
	 * 
	 * @param code
	 *            the code to set
	 * @since $version
	 */
	public void setCode(String code) {
		this.code = code;
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
		return "Country" + "@" + Integer.toHexString(this.hashCode()) + " [code=" + this.code + ", name=" + this.name + "]";
	}
}
