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
package org.batoo.jpa.core.test.lock;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 * 
 * @author hceylan
 * @since $version
 */
@Entity
public class Bar2 {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	private String value;

	@ManyToOne
	private Foo2 foo;

	/**
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public Bar2() {
		super();
	}

	/**
	 * @param foo
	 *            the foo
	 * @param value
	 *            the value
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public Bar2(Foo2 foo, String value) {
		super();

		this.foo = foo;
		this.value = value;

		this.foo.getBars().add(this);
	}

	/**
	 * Returns the foo of the Bar.
	 * 
	 * @return the foo of the Bar
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public Foo2 getFoo() {
		return this.foo;
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
	 * Returns the value of the Foo1.
	 * 
	 * @return the value of the Foo1
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public String getValue() {
		return this.value;
	}

	/**
	 * Sets the foo of the Bar.
	 * 
	 * @param foo
	 *            the foo to set for Bar
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void setFoo(Foo2 foo) {
		this.foo = foo;
	}

	/**
	 * Sets the value of the Foo1.
	 * 
	 * @param value
	 *            the value to set for Foo1
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String toString() {
		return "Bar [id=" + this.id + ", value=" + this.value + ", foo=" + this.foo + "]";
	}
}
