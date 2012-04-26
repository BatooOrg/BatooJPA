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
package org.batoo.jpa.core.test.idclass;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;

/**
 * 
 * @author hceylan
 * @since $version
 */
@Entity
@IdClass(FooPk.class)
public class Foo {

	@Id
	private String strKey;

	@Id
	private Integer intKey;

	private String value;

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
		final Foo other = (Foo) obj;
		if (this.intKey == null) {
			if (other.intKey != null) {
				return false;
			}
		}
		else if (!this.intKey.equals(other.intKey)) {
			return false;
		}
		if (this.strKey == null) {
			if (other.strKey != null) {
				return false;
			}
		}
		else if (!this.strKey.equals(other.strKey)) {
			return false;
		}
		return true;
	}

	/**
	 * Returns the intKey.
	 * 
	 * @return the intKey
	 * @since $version
	 */
	public Integer getIntKey() {
		return this.intKey;
	}

	/**
	 * Returns the strKey.
	 * 
	 * @return the strKey
	 * @since $version
	 */
	public String getStrKey() {
		return this.strKey;
	}

	/**
	 * Returns the value.
	 * 
	 * @return the value
	 * @since $version
	 */
	public String getValue() {
		return this.value;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((this.intKey == null) ? 0 : this.intKey.hashCode());
		result = (prime * result) + ((this.strKey == null) ? 0 : this.strKey.hashCode());
		return result;
	}

	/**
	 * Sets the intKey.
	 * 
	 * @param intKey
	 *            the intKey to set
	 * @since $version
	 */
	public void setIntKey(Integer intKey) {
		this.intKey = intKey;
	}

	/**
	 * Sets the strKey.
	 * 
	 * @param strKey
	 *            the strKey to set
	 * @since $version
	 */
	public void setStrKey(String strKey) {
		this.strKey = strKey;
	}

	/**
	 * Sets the value.
	 * 
	 * @param value
	 *            the value to set
	 * @since $version
	 */
	public void setValue(String value) {
		this.value = value;
	}

}
