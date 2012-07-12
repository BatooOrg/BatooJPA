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
package org.batoo.jpa.core.test.merge;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * 
 * @author hceylan
 * @since $version
 */
@Entity
public class Child3 {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Integer id;

	private String value;

	/**
	 * @since $version
	 * @author hceylan
	 */
	public Child3() {
		super();
	}

	/**
	 * @param id
	 *            the id
	 * @param parent
	 *            the person
	 * @param value
	 *            the value
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public Child3(Integer id, Parent parent, String value) {
		super();

		this.id = id < 0 ? null : id;
		this.value = value;

		parent.getChildren3().add(this);
	}

	/**
	 * Returns the id of the Child1.
	 * 
	 * @return the id of the Child1
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public Integer getId() {
		return this.id;
	}

	/**
	 * Returns the value of the Child1.
	 * 
	 * @return the value of the Child1
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public String getValue() {
		return this.value;
	}

	/**
	 * Sets the value of the Child1.
	 * 
	 * @param value
	 *            the value to set for Child1
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void setValue(String value) {
		this.value = value;
	}
}
