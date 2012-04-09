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
package org.batoo.jpa.core.impl.collections;

import java.util.Collection;
import java.util.Set;

import org.batoo.jpa.core.impl.SessionImpl;
import org.batoo.jpa.core.impl.instance.ManagedInstance;
import org.batoo.jpa.core.impl.mapping.CollectionMapping;

import com.google.common.collect.Sets;

/**
 * 
 * @author hceylan
 * @since $version
 */
public class ManagedSet<E> extends AbstractManagedCollection<E> implements Set<E> {

	private final Set<E> set = Sets.newHashSet();

	/**
	 * @param session
	 *            the session
	 * @param managedInstance
	 *            the owner managed instance
	 * @param mapping
	 *            the mapping
	 * @param existing
	 *            the existing set may be null
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public ManagedSet(SessionImpl session, ManagedInstance<?> managedInstance, CollectionMapping<?, Set<E>, E> mapping, Set<E> existing) {
		super(session, managedInstance, mapping, existing);

		if (existing != null) {
			this.snapshot = existing;
			this.set.addAll(existing);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Collection<E> getCollection() {
		return this.set;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String toString() {
		return "ManagedSet [set=" + this.set + ", session=" + this.session + ", managedInstance=" + this.managedInstance + ", snapshot="
			+ this.snapshot + "]";
	}
}
