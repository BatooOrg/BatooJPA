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

import java.util.HashSet;
import java.util.Set;

import org.batoo.jpa.core.impl.instance.ManagedInstance;
import org.batoo.jpa.core.impl.mapping.CollectionMapping;

import com.google.common.collect.Sets;

/**
 * 
 * @author hceylan
 * @since $version
 */
public class ManagedSet<E> extends AbstractManagedCollection<E> implements Set<E> {

	private final HashSet<E> set = Sets.newHashSet();

	/**
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
	public ManagedSet(ManagedInstance<?> managedInstance, CollectionMapping<?, Set<E>, E> mapping, boolean lazy) {
		super(managedInstance, mapping, lazy);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public HashSet<E> getCollection() {
		return this.set;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String toString() {
		return "ManagedSet [set=" + this.set + ", managedInstance=" + this.managedInstance + ", snapshot=" + this.snapshot + "]";
	}
}
