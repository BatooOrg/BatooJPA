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
package org.batoo.jpa.core.test.listener;

import javax.persistence.PostLoad;
import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;

/**
 * 
 * 
 * @author hceylan
 * @since $version
 */
public class FooListener {

	/**
	 * @param instance
	 *            the instance
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@PostLoad
	public void postLoad(Object instance) {
		final String string = "listener1PostLoad";
		this.update(instance, string);
	}

	/**
	 * @param instance
	 *            the instance
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@PostPersist
	public void postPersist(Object instance) {
		final String string = "listener1PostPersist";
		this.update(instance, string);
	}

	/**
	 * @param instance
	 *            the instance
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@PostRemove
	public void postRemove(Object instance) {
		final String string = "listener1PostRemove";
		this.update(instance, string);
	}

	/**
	 * @param instance
	 *            the instance
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@PostUpdate
	public void preLoad(Object instance) {
		final String string = "listener1PostUpdate";
		this.update(instance, string);
	}

	/**
	 * @param instance
	 *            the instance
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@PrePersist
	public void prePersist(Object instance) {
		final String string = "listener1PrePersist";
		this.update(instance, string);
	}

	/**
	 * @param instance
	 *            the instance
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@PreRemove
	public void preRemove(Object instance) {
		final String string = "listener1PreRemove";
		this.update(instance, string);
	}

	/**
	 * @param instance
	 *            the instance
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@PreUpdate
	public void preUpdate(Object instance) {
		final String string = "listener1PreUpdate";
		this.update(instance, string);
	}

	private void update(Object instance, String string) {
		final FooType foo = (FooType) instance;
		foo.setValue(foo.getValue() + string);
	}
}
