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

import org.batoo.jpa.core.impl.manager.SessionImpl;
import org.batoo.jpa.core.impl.model.mapping.PluralAssociationMapping;

/**
 * The set implementation of managed collection.
 * 
 * @param <X>
 *            The type the represented collection belongs to
 * @param <E>
 *            The element type of the represented collection
 * @author hceylan
 * @since $version
 */
public class ManagedSet<X, E> extends HashSet<E> {

	private final PluralAssociationMapping<?, E, ?> mapping;
	private final SessionImpl session;
	private final Object id;

	/**
	 * @param mapping
	 *            the mapping
	 * @param session
	 *            the session
	 * @param id
	 *            the id of the root entity
	 * @param value
	 *            the initial values
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public ManagedSet(PluralAssociationMapping<?, E, ?> mapping, SessionImpl session, Object id, Collection<? extends E> value) {
		super(value);
		this.session = session;

		this.id = id;
		this.mapping = mapping;
	}
}
