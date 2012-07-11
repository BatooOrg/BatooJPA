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
package org.batoo.jpa.core.impl.instance;

import org.batoo.jpa.core.impl.model.type.EntityTypeImpl;

/**
 * The managed id for the entity instances.
 * 
 * @param <X>
 *            the type of the id
 * 
 * @author hceylan
 * @since $version
 */
public class ManagedId<X> {

	private final EntityTypeImpl<? super X> type;
	private Object id;

	private int h;
	private final X instance;

	/**
	 * Constructor for the instances.
	 * 
	 * @param type
	 *            the type
	 * @param instance
	 *            the instance
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public ManagedId(EntityTypeImpl<X> type, X instance) {
		super();

		this.instance = instance;
		this.type = type.getRootType();
		this.id = this.type.getInstanceId(instance);
	}

	/**
	 * Constructor for the raw ids.
	 * 
	 * @param id
	 *            the id
	 * @param type
	 *            the type
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public ManagedId(Object id, EntityTypeImpl<X> type) {
		super();

		this.instance = null;
		this.type = type.getRootType();
		this.id = id;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean equals(Object obj) {
		if (this.getId() == null) {
			return false;
		}

		final ManagedId<?> other = (ManagedId<?>) obj;

		if ((other == null) || (other.id == null)) {
			return false;
		}
		if (this.type != other.type) {
			return false;
		}

		return this.id.equals(other.id);
	}

	/**
	 * Returns the id of the managed id.
	 * 
	 * @return the id
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public Object getId() {
		if (this.id == null) {
			return this.id = this.type.getInstanceId(this.instance);
		}
		return this.id;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public int hashCode() {
		if (this.h != 0) {
			return this.h;
		}

		if (this.getId() == null) {
			return 1;
		}

		final int prime = 31;
		this.h = 1;

		this.h = (prime * this.h) + this.id.hashCode();
		return this.h = (prime * this.h) + this.type.getName().hashCode();
	}
}
