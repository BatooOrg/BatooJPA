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
package org.batoo.jpa.core.test.mappedsuperclass;

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
public class Quux {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer key;

	private Integer quuxValue;

	@ManyToOne
	private Foo foo;

	/**
	 * @since $version
	 * @author hceylan
	 */
	public Quux() {
		super();
	}

	/**
	 * Returns the foo.
	 * 
	 * @return the foo
	 * @since $version
	 */
	public Foo getFoo() {
		return this.foo;
	}

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
	 * Returns the quuxValue.
	 * 
	 * @return the quuxValue
	 * @since $version
	 */
	public Integer getQuuxValue() {
		return this.quuxValue;
	}

	/**
	 * Sets the foo.
	 * 
	 * @param foo
	 *            the foo to set
	 * @since $version
	 */
	public void setFoo(Foo foo) {
		this.foo = foo;
	}

	/**
	 * Sets the quuxValue.
	 * 
	 * @param quuxValue
	 *            the quuxValue to set
	 * @since $version
	 */
	public void setQuuxValue(Integer quuxValue) {
		this.quuxValue = quuxValue;
	}

}
