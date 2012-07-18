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
package org.batoo.jpa.core.test.mapsid;

import javax.persistence.Embeddable;

/**
 * 
 * 
 * @author hceylan
 * @since $version
 */
@Embeddable
public class PhonePk {

	Integer id;
	Integer personId;

	/**
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public PhonePk() {
		super();
	}

	/**
	 * @param id
	 *            the id
	 * @param personId
	 *            the personId
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public PhonePk(Integer id, Integer personId) {
		super();

		this.id = id;
		this.personId = personId;
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

		final PhonePk other = (PhonePk) obj;
		if (this.id == null) {
			return false;
		}
		else if (!this.id.equals(other.id)) {
			return false;
		}

		if (this.personId == null) {
			return false;
		}
		else if (!this.personId.equals(other.personId)) {
			return false;
		}

		return true;
	}

	/**
	 * Returns the id of the PhonePk.
	 * 
	 * @return the id of the PhonePk
	 * 
	 * @since $version
	 * @author hceylan
	 */
	protected Integer getId() {
		return this.id;
	}

	/**
	 * Returns the personId of the PhonePk.
	 * 
	 * @return the personId of the PhonePk
	 * 
	 * @since $version
	 * @author hceylan
	 */
	protected Integer getPersonId() {
		return this.personId;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((this.id == null) ? 0 : this.id.hashCode());
		result = (prime * result) + ((this.personId == null) ? 0 : this.personId.hashCode());
		return result;
	}

	/**
	 * Sets the id of the PhonePk.
	 * 
	 * @param id
	 *            the id to set for PhonePk
	 * 
	 * @since $version
	 * @author hceylan
	 */
	protected void setId(Integer id) {
		this.id = id;
	}

	/**
	 * Sets the personId of the PhonePk.
	 * 
	 * @param personId
	 *            the personId to set for PhonePk
	 * 
	 * @since $version
	 * @author hceylan
	 */
	protected void setPersonId(Integer personId) {
		this.personId = personId;
	}
}
