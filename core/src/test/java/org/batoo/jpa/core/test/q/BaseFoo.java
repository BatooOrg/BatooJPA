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

import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.ManyToOne;

/**
 * 
 * @author hceylan
 * @since $version
 */
@Entity
@Inheritance
@DiscriminatorColumn(name = "FOO_TYPE")
@DiscriminatorValue("BASE_FOO")
public class BaseFoo {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Integer id;

	@ManyToOne
	private Bar bar;

	/**
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public BaseFoo() {
		super();
	}

	/**
	 * @param bar
	 *            the bar
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public BaseFoo(Bar bar) {
		super();

		this.bar = bar;

		this.bar.getFoos().add(this);
	}

	/**
	 * Returns the bar of the BaseFoo.
	 * 
	 * @return the bar of the BaseFoo
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public Bar getBar() {
		return this.bar;
	}

	/**
	 * Returns the id of the BasepublicFoo.
	 * 
	 * @return the id of the BaseFoo
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public Integer getId() {
		return this.id;
	}

	/**
	 * Sets the bar of the BaseFoo.
	 * 
	 * @param bar
	 *            the bar to set for BaseFoo
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void setBar(Bar bar) {
		this.bar = bar;
	}
}
