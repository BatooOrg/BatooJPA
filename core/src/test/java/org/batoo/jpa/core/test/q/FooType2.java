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
@DiscriminatorValue("FOO_TYPE_2")
public class FooType2 extends BaseFoo {

	private int valueType2;

	/**
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public FooType2() {
		super();
	}

	/**
	 * @param bar
	 *            the bar
	 * @param valueType2
	 *            the value type 2
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public FooType2(Bar bar, int valueType2) {
		super(bar);

		this.valueType2 = valueType2;
	}

	/**
	 * Returns the valueType2 of the FooType2.
	 * 
	 * @return the valueType2 of the FooType2
	 * 
	 * @since $version
	 * @author hceylan
	 */
	protected int getValueType2() {
		return this.valueType2;
	}

	/**
	 * Sets the valueType2 of the FooType2.
	 * 
	 * @param valueType2
	 *            the valueType2 to set for FooType2
	 * 
	 * @since $version
	 * @author hceylan
	 */
	protected void setValueType2(int valueType2) {
		this.valueType2 = valueType2;
	}
}
