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
package org.batoo.jpa.core.test.identity;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

/**
 * 
 * @author hceylan
 * @since $version
 */
@Entity
public class Foo {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer key;

	private String value;

	@OneToOne(cascade = CascadeType.ALL)
	private Foo other;

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
	 * Returns the other of the Foo.
	 * 
	 * @return the other of the Foo
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public Foo getOther() {
		return this.other;
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
	 * Sets the other of the Foo.
	 * 
	 * @param other
	 *            the other to set for Foo
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void setOther(Foo other) {
		this.other = other;
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
