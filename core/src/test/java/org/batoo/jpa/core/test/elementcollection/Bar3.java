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
package org.batoo.jpa.core.test.elementcollection;

import javax.persistence.Embeddable;

/**
 * 
 * @author hceylan
 * @since $version
 */
@Embeddable
public class Bar3 {

	private int intValue;
	private String strValue;

	/**
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public Bar3() {
		super();
	}

	/**
	 * @param intValue
	 *            int value
	 * @param strValue
	 *            str value
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public Bar3(int intValue, String strValue) {
		super();

		this.intValue = intValue;
		this.strValue = strValue;
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
		final Bar3 other = (Bar3) obj;
		if (this.intValue != other.intValue) {
			return false;
		}
		if (this.strValue == null) {
			if (other.strValue != null) {
				return false;
			}
		}
		else if (!this.strValue.equals(other.strValue)) {
			return false;
		}
		return true;
	}

	/**
	 * Returns the intValue of the Bar3.
	 * 
	 * @return the intValue of the Bar3
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public int getIntValue() {
		return this.intValue;
	}

	/**
	 * Returns the strValue of the Bar3.
	 * 
	 * @return the strValue of the Bar3
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public String getStrValue() {
		return this.strValue;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + this.intValue;
		result = (prime * result) + ((this.strValue == null) ? 0 : this.strValue.hashCode());
		return result;
	}

	/**
	 * Sets the intValue of the Bar3.
	 * 
	 * @param intValue
	 *            the intValue to set for Bar3
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void setIntValue(int intValue) {
		this.intValue = intValue;
	}

	/**
	 * Sets the strValue of the Bar3.
	 * 
	 * @param strValue
	 *            the strValue to set for Bar3
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void setStrValue(String strValue) {
		this.strValue = strValue;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String toString() {
		return "Bar3 [intValue=" + this.intValue + ", strValue=" + this.strValue + "]";
	}
}
