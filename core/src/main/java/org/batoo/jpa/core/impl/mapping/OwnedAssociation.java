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
package org.batoo.jpa.core.impl.mapping;

import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

/**
 * Interface for owned associations.
 * 
 * @author hceylan
 * @since $version
 */
public interface OwnedAssociation<X, T> extends Association<X, T> {

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	OwnerAssociation<T, X> getOpposite();

	/**
	 * Links the opposite side if any exists.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	void link();

	/**
	 * Returns if the association removes the orphans.
	 * <p>
	 * This corresponds to the {@link OneToOne#orphanRemoval()} {@link OneToMany#orphanRemoval()}
	 * <p>
	 * This must return false if the association is the owner.
	 * 
	 * @return true if removes.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	boolean orphanRemoval();
}
