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
package org.batoo.jpa.core.test.inheritence;

import javax.persistence.Entity;

/**
 * 
 * @author hceylan
 * @since $version
 */
@Entity
public class FooExt11 extends FooExt1 {

	private String valueExt11;

	/**
	 * Returns the valueExt11.
	 * 
	 * @return the valueExt11
	 * @since $version
	 */
	public String getValueExt11() {
		return this.valueExt11;
	}

	/**
	 * Sets the valueExt1.
	 * 
	 * @param valueExt1
	 *            the valueExt1 to set
	 * @since $version
	 */
	public void setValueExt11(String valueExt11) {
		this.valueExt11 = valueExt11;
	}
}
