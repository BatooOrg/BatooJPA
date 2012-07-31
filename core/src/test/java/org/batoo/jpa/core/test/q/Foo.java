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
public class Foo {

	@SuppressWarnings("javadoc")
	public enum FooType {
		TYPE1,
		TYPE2,
		TYPE3
	}

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Integer id;

	private Integer number;
	private Integer number2;
	private String string;
	private FooType type;

	/**
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public Foo() {
		super();
	}

	/**
	 * @param number
	 *            the number
	 * @param number2
	 *            the number 2
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public Foo(Integer number, Integer number2) {
		super();

		this.number = number;
		this.number2 = number2;
	}

	/**
	 * @param number
	 *            the number
	 * @param string
	 *            the string
	 * @param type
	 *            the type
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public Foo(Integer number, String string, FooType type) {
		super();

		this.number = number;
		this.string = string;
		this.type = type;
	}

	/**
	 * Returns the id of the Foo.
	 * 
	 * @return the id of the Foo
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public Integer getId() {
		return this.id;
	}

	/**
	 * Returns the number of the Foo.
	 * 
	 * @return the number of the Foo
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public Integer getNumber() {
		return this.number;
	}

	/**
	 * Returns the number2 of the Foo.
	 * 
	 * @return the number2 of the Foo
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public Integer getNumber2() {
		return this.number2;
	}

	/**
	 * Returns the string of the Foo.
	 * 
	 * @return the string of the Foo
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public String getString() {
		return this.string;
	}

	/**
	 * Returns the type of the Foo.
	 * 
	 * @return the type of the Foo
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public FooType getType() {
		return this.type;
	}

	/**
	 * Sets the number of the Foo.
	 * 
	 * @param number
	 *            the number to set for Foo
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void setNumber(Integer number) {
		this.number = number;
	}

	/**
	 * Sets the number2 of the Foo.
	 * 
	 * @param number2
	 *            the number2 to set for Foo
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void setNumber2(Integer number2) {
		this.number2 = number2;
	}

	/**
	 * Sets the string of the Foo.
	 * 
	 * @param string
	 *            the string to set for Foo
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void setString(String string) {
		this.string = string;
	}

	/**
	 * Sets the type of the Foo.
	 * 
	 * @param type
	 *            the type to set for Foo
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void setType(FooType type) {
		this.type = type;
	}
}
