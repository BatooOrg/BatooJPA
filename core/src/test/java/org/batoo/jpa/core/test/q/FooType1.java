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
package org.batoo.jpa.core.test.q;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * 
 * 
 * @author hceylan
 * @since $version
 */
@Entity
@DiscriminatorValue("FOO_TYPE_1")
public class FooType1 extends BaseFoo {

	private String valueType1;

	/**
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public FooType1() {
		super();
	}

	/**
	 * @param bar
	 *            the bar
	 * @param valueType1
	 *            the value type 1
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public FooType1(Bar bar, String valueType1) {
		super(bar);

		this.valueType1 = valueType1;
	}

	/**
	 * Returns the valueType1 of the FooType1.
	 * 
	 * @return the valueType1 of the FooType1
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public String getValueType1() {
		return this.valueType1;
	}

	/**
	 * Sets the valueType1 of the FooType1.
	 * 
	 * @param valueType1
	 *            the valueType1 to set for FooType1
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void setValueType1(String valueType1) {
		this.valueType1 = valueType1;
	}
}
