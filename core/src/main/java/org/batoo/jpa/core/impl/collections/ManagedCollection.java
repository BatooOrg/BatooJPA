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

/**
 * Marker interface for managed collections.
 * 
 * @param <E>
 *            the element type of the collection
 * 
 * @author hceylan
 * @since $version
 */
public abstract class ManagedCollection<E> {

	/**
	 * Adds the child to the managed list without initialize checks.
	 * 
	 * @param child
	 *            the child to add
	 * @return if the child is added
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public abstract boolean addChild(Object child);

	/**
	 * Returns the delegate collection.
	 * 
	 * @return the delegate collection
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public abstract Collection<E> getDelegate();
}
