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

import java.util.ArrayList;
import java.util.Collection;

import org.batoo.jpa.core.impl.instance.ManagedInstance;
import org.batoo.jpa.core.impl.model.attribute.PluralAttributeImpl;

/**
 * @param <X>
 *            The type the represented collection belongs to
 * @param <E>
 *            The element type of the represented collection
 * @author hceylan
 * @since $version
 */
public class ManagedList<X, E> extends ArrayList<E> {

	private final PluralAttributeImpl<X, ?, E> attribute;
	private final ManagedInstance<? extends X> managedInstance;

	/**
	 * @param attribute
	 *            the attribute
	 * @param managedInstance
	 *            the managed instance
	 * @param values
	 *            the initial values
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public ManagedList(PluralAttributeImpl<X, ?, E> attribute, ManagedInstance<? extends X> managedInstance, Collection<? extends E> values) {
		super(values);

		this.attribute = attribute;
		this.managedInstance = managedInstance;
	}
}
