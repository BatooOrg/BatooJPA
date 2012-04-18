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
package org.batoo.jpa.core.test.mappedsuperclass;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * 
 * @author hceylan
 * @since $version
 */
@MappedSuperclass
public class Bar {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer key;

	private String fooValue;

	private String barValue;

	/**
	 * Returns the barValue.
	 * 
	 * @return the barValue
	 * @since $version
	 */
	public String getBarValue() {
		return this.barValue;
	}

	/**
	 * Returns the fooValue.
	 * 
	 * @return the fooValue
	 * @since $version
	 */
	public String getFooValue() {
		return this.fooValue;
	}

	/**
	 * Returns the key.
	 * 
	 * @return the key
	 * @since $version
	 */
	public Integer getKey() {
		return this.key;
	}

	/**
	 * Sets the barValue.
	 * 
	 * @param barValue
	 *            the barValue to set
	 * @since $version
	 */
	public void setBarValue(String barValue) {
		this.barValue = barValue;
	}

	/**
	 * Sets the fooValue.
	 * 
	 * @param fooValue
	 *            the fooValue to set
	 * @since $version
	 */
	public void setFooValue(String fooValue) {
		this.fooValue = fooValue;
	}

}
