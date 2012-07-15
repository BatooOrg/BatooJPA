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
package org.batoo.jpa.core.test.enums;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * 
 * @author hceylan
 * @since $version
 */
@Entity
public class Foo {

	@SuppressWarnings("javadoc")
	public static enum FooType {
		TYPE1,
		TYPE2,
		TYPE3,
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@Enumerated(EnumType.STRING)
	private FooType footype;

	private FooType footype2;

	/**
	 * Returns the footype of the Foo.
	 * 
	 * @return the footype of the Foo
	 * 
	 * @since $version
	 * @author hceylan
	 */
	protected FooType getFootype() {
		return this.footype;
	}

	/**
	 * Returns the footype2 of the Foo.
	 * 
	 * @return the footype2 of the Foo
	 * 
	 * @since $version
	 * @author hceylan
	 */
	protected FooType getFootype2() {
		return this.footype2;
	}

	/**
	 * Returns the id of the Foo1.
	 * 
	 * @return the id of the Foo1
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public Integer getId() {
		return this.id;
	}

	/**
	 * Sets the footype of the Foo.
	 * 
	 * @param footype
	 *            the footype to set for Foo
	 * 
	 * @since $version
	 * @author hceylan
	 */
	protected void setFootype(FooType footype) {
		this.footype = footype;
	}

	/**
	 * Sets the footype2 of the Foo.
	 * 
	 * @param footype2
	 *            the footype2 to set for Foo
	 * 
	 * @since $version
	 * @author hceylan
	 */
	protected void setFootype2(FooType footype2) {
		this.footype2 = footype2;
	}

}
