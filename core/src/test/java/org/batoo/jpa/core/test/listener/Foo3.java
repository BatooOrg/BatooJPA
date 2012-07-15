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
package org.batoo.jpa.core.test.listener;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.PostPersist;
import javax.persistence.Transient;

/**
 * 
 * @author hceylan
 * @since $version
 */
@Entity
@EntityListeners(value = FooListener.class)
public class Foo3 extends Foo implements FooType {

	@Transient
	private String value = "";

	private String fooValue;

	/**
	 * Returns the fooValue of the Foo3.
	 * 
	 * @return the fooValue of the Foo3
	 * 
	 * @since $version
	 * @author hceylan
	 */
	protected String getFooValue() {
		return this.fooValue;
	}

	/**
	 * Returns the value of the Foo3.
	 * 
	 * @return the value of the Foo3
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Override
	public String getValue() {
		return this.value;
	}

	/**
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@PostPersist
	public void postPersist() {
		this.setValue(this.getValue() + "postPersist");
	}

	/**
	 * Sets the fooValue of the Foo3.
	 * 
	 * @param fooValue
	 *            the fooValue to set for Foo3
	 * 
	 * @since $version
	 * @author hceylan
	 */
	protected void setFooValue(String fooValue) {
		this.fooValue = fooValue;
	}

	/**
	 * Sets the value of the Foo3.
	 * 
	 * @param value
	 *            the value to set for Foo3
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Override
	public void setValue(String value) {
		this.value = value;
	}
}
