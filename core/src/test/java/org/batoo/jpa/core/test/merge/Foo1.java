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
package org.batoo.jpa.core.test.merge;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

/**
 * 
 * 
 * @author hceylan
 * @since $version
 */
@Entity
public class Foo1 {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Integer id;

	private String value;

	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "foo1")
	private Foo2 foo2;

	/**
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public Foo1() {
		super();
	}

	/**
	 * @param id
	 *            the id
	 * @param value
	 *            the value
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public Foo1(Integer id, String value) {
		super();

		this.id = id < 0 ? null : id;
		this.value = value;
	}

	/**
	 * Returns the foo2 of the Foo1.
	 * 
	 * @return the foo2 of the Foo1
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public Foo2 getFoo2() {
		return this.foo2;
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
	 * Sets the foo2 of the Foo1.
	 * 
	 * @param foo2
	 *            the foo2 to set for Foo1
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void setFoo2(Foo2 foo2) {
		if (this.foo2 != null) {
			this.foo2.setFoo1(null);
		}

		this.foo2 = foo2;

		if (this.foo2 != null) {
			this.foo2.setFoo1(this);
		}
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

}
