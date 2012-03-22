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
package org.batoo.jpa.core.test.secondarytable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SecondaryTable;

/**
 * 
 * @author hceylan
 * @since $version
 */
@Entity
@SecondaryTable(name = "FooExtra")
public class Foo {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer key;

	private String value1;

	@Column(table = "FooExtra")
	private String value2;

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
	 * Returns the value1.
	 * 
	 * @return the value1
	 * @since $version
	 */
	public String getValue1() {
		return this.value1;
	}

	/**
	 * Returns the value2.
	 * 
	 * @return the value2
	 * @since $version
	 */
	public String getValue2() {
		return this.value2;
	}

	/**
	 * Sets the value1.
	 * 
	 * @param value1
	 *            the value1 to set
	 * @since $version
	 */
	public void setValue1(String value1) {
		this.value1 = value1;
	}

	/**
	 * Sets the value2.
	 * 
	 * @param value2
	 *            the value2 to set
	 * @since $version
	 */
	public void setValue2(String value2) {
		this.value2 = value2;
	}
}
