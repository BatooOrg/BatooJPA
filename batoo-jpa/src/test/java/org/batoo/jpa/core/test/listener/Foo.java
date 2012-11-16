/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"; you may not use this file except in compliance
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
package org.batoo.jpa.core.test.listener;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PostLoad;
import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;
import javax.persistence.Transient;

/**
 * 
 * @author hceylan
 * @since 2.0.0
 */
@MappedSuperclass
public class Foo {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@Transient
	private String parentValue = "";

	/**
	 * Returns the id of the Foo1.
	 * 
	 * @return the id of the Foo1
	 * 
	 * @since 2.0.0
	 */
	public Integer getId() {
		return this.id;
	}

	/**
	 * Returns the parentValue of the Foo.
	 * 
	 * @return the parentValue of the Foo
	 * 
	 * @since 2.0.0
	 */
	public String getParentValue() {
		return this.parentValue;
	}

	/**
	 * 
	 * @since 2.0.0
	 */
	@PostLoad
	public void postLoadFoo() {
		this.parentValue = this.parentValue + "masterPostLoad";
	}

	/**
	 * 
	 * @since 2.0.0
	 */
	@PostPersist
	public void postPersistFoo() {
		this.parentValue = this.parentValue + "masterPostPersist";
	}

	/**
	 * 
	 * @since 2.0.0
	 */
	@PostRemove
	public void postRemoveFoo() {
		this.parentValue = this.parentValue + "masterPostRemove";
	}

	/**
	 * 
	 * @since 2.0.0
	 */
	@PostUpdate
	public void postUpdateFoo() {
		this.parentValue = this.parentValue + "masterPostUpdate";
	}

	/**
	 * 
	 * @since 2.0.0
	 */
	@PrePersist
	public void prePersistFoo() {
		this.parentValue = this.parentValue + "masterPrePersist";
	}

	/**
	 * 
	 * @since 2.0.0
	 */
	@PreRemove
	public void preRemoveFoo() {
		this.parentValue = this.parentValue + "masterPreRemove";
	}

	/**
	 * 
	 * @since 2.0.0
	 */
	@PreUpdate
	public void preUpdateFoo() {
		this.parentValue = this.parentValue + "masterPreUpdate";
	}

	/**
	 * Sets the parentValue of the Foo.
	 * 
	 * @param parentValue
	 *            the parenValue to set for Foo
	 * 
	 * @since 2.0.0
	 */
	public void setParentValue(String parentValue) {
		this.parentValue = parentValue;
	}
}
