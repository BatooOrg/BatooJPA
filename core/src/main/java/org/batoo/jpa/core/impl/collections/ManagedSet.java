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
import java.util.HashSet;

import org.batoo.jpa.core.impl.instance.ManagedInstance;
import org.batoo.jpa.core.impl.model.mapping.PluralAssociationMapping;

/**
 * @param <X>
 *            The type the represented collection belongs to
 * @param <E>
 *            The element type of the represented collection
 * @author hceylan
 * @since $version
 */
public class ManagedSet<X, E> extends HashSet<E> {

	private final ManagedInstance<? extends X> managedInstance;
	private final PluralAssociationMapping<?, E, ?> mapping;

	/**
	 * @param managedInstance
	 *            the managed instance
	 * @param mapping
	 *            the mapping
	 * @param value
	 *            the initial values
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public ManagedSet(ManagedInstance<? extends X> managedInstance, PluralAssociationMapping<?, E, ?> mapping, Collection<? extends E> value) {
		super(value);

		this.managedInstance = managedInstance;
		this.mapping = mapping;
	}
}
