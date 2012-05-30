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
package org.batoo.jpa.core.impl.manager;

import java.util.Map;

import org.batoo.jpa.core.impl.model.EntityTypeImpl;

import com.google.common.collect.Maps;

/**
 * The global repository for the session.
 * 
 * @author hceylan
 * @since $version
 */
public class Repository {

	private final Map<EntityTypeImpl<?>, SubRepository<?>> subRepositories;

	/**
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public Repository() {
		super();

		this.subRepositories = Maps.newIdentityHashMap();
	}

	/**
	 * Returns the sub repository.
	 * 
	 * @param <X>
	 *            the type of the entity
	 * @param type
	 *            the entity type
	 * @return the sub repository
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@SuppressWarnings("unchecked")
	public <X> SubRepository<X> get(EntityTypeImpl<X> type) {
		// XXX: inheritance
		// final EntityTypeImpl<? super X> supr = type.getIdentityRoot();

		SubRepository<X> subRepository = (SubRepository<X>) this.subRepositories.get(type);

		if (subRepository == null) {
			synchronized (this) {
				subRepository = (SubRepository<X>) this.subRepositories.get(type);
				if (subRepository == null) {
					subRepository = new SubRepository<X>();
					this.subRepositories.put(type, subRepository);
				}
			}
		}

		return subRepository;
	}
}
