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
package org.batoo.jpa.core.impl;

import java.util.HashMap;

import javax.persistence.PersistenceException;

import org.batoo.jpa.core.impl.instance.ManagedInstance;

import com.google.common.collect.Maps;

/**
 * Sub section of the repository for a specific entity type.
 * 
 * @param <X>
 *            the type of the repository
 * 
 * @author hceylan
 * @since $version
 */
public class SubRepository<X> {

	private final HashMap<ManagedInstance<? extends X>, ManagedInstance<? extends X>> repository = Maps.newHashMap();

	/**
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public SubRepository() {
		super();
	}

	/**
	 * Returns the managed instance with the id.
	 * 
	 * @param id
	 *            the id of the managed instance
	 * @return the managed instance with the id
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public ManagedInstance<? extends X> get(Object id) {
		try {
			return this.repository.get(id);
		}
		catch (final NullPointerException e) {
			// at least one of the attributes is null, therefore cannot be located in the session
			return null;
		}
	}

	/**
	 * Puts the managed instance.
	 * 
	 * @param instance
	 *            the managed instance
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void put(ManagedInstance<? extends X> instance) {
		if (this.repository.containsKey(instance)) {
			throw new PersistenceException("Type " + instance.getType().getName() + " with id " + instance.getId()
				+ " already exists in session");
		}

		this.repository.put(instance, instance);
	}

	/**
	 * Removes the managed instance.
	 * 
	 * @param instance
	 *            the managed instance
	 * @return returns the actual removed instance
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public ManagedInstance<? extends X> remove(ManagedInstance<? extends X> instance) {
		return this.repository.remove(instance);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String toString() {
		return "SubRepository [repository=" + this.repository + "]";
	}
}
