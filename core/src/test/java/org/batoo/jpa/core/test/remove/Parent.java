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
package org.batoo.jpa.core.test.remove;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import com.google.common.collect.Lists;

/**
 * 
 * @author hceylan
 * @since $version
 */
@Entity
public class Parent {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Integer id;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "parent")
	private final List<Child1> children1 = Lists.newArrayList();

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "parent")
	private final List<Child2> children2 = Lists.newArrayList();

	@OneToMany(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
	private final List<Child3> children3 = Lists.newArrayList();

	@OneToMany(cascade = CascadeType.PERSIST, orphanRemoval = true)
	private final List<Child4> children4 = Lists.newArrayList();

	private String value;

	/**
	 * @since $version
	 * @author hceylan
	 */
	public Parent() {
		super();
	}

	/**
	 * @param value
	 *            the value
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public Parent(String value) {
		super();

		this.value = value;
	}

	/**
	 * Returns the children1 of the Parent.
	 * 
	 * @return the children1 of the Parent
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public List<Child1> getChildren1() {
		return this.children1;
	}

	/**
	 * Returns the children2 of the Parent.
	 * 
	 * @return the children2 of the Parent
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public List<Child2> getChildren2() {
		return this.children2;
	}

	/**
	 * Returns the children3 of the Parent.
	 * 
	 * @return the children3 of the Parent
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public List<Child3> getChildren3() {
		return this.children3;
	}

	/**
	 * Returns the children4 of the Parent.
	 * 
	 * @return the children4 of the Parent
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public List<Child4> getChildren4() {
		return this.children4;
	}

	/**
	 * Returns the id of the Parent.
	 * 
	 * @return the id of the Parent
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public Integer getId() {
		return this.id;
	}

	/**
	 * Returns the value of the Parent.
	 * 
	 * @return the value of the Parent
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public String getValue() {
		return this.value;
	}

	/**
	 * Sets the value of the Parent.
	 * 
	 * @param value
	 *            the value to set for Parent
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void setValue(String value) {
		this.value = value;
	}

}
