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

/**
 * @author hceylan
 * @since $version
 */
public class FooPk {

	private String strKey;

	private Integer intKey;

	public FooPk() {
		super();
	}

	/**
	 * @param intKey
	 * @param strKey
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public FooPk(Integer intKey, String strKey) {
		super();

		this.intKey = intKey;
		this.strKey = strKey;
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
		final FooPk other = (FooPk) obj;
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
}
