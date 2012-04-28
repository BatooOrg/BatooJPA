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

import javax.persistence.CascadeType;
import javax.persistence.FetchType;

import org.batoo.jpa.core.impl.types.EntityTypeImpl;

/**
 * Interfae for Associations
 * 
 * @author hceylan
 * @since $version
 */
public interface Association<X, T> extends Mapping<X, T> {

	/**
	 * Returns if the association detaches the associate.
	 * <p>
	 * This corresponds to the {@link CascadeType#ALL} and {@link CascadeType#DETACH}
	 * 
	 * @return true if detaches
	 * 
	 * @since $version
	 * @author hceylan
	 */
	boolean cascadeDetach();

	/**
	 * Returns if the association merges the associate.
	 * <p>
	 * This corresponds to the {@link CascadeType#ALL} and {@link CascadeType#MERGE}
	 * 
	 * @return true if merges
	 * 
	 * @since $version
	 * @author hceylan
	 */
	boolean cascadeMerge();

	/**
	 * Returns if the association persists the associate.
	 * <p>
	 * This corresponds to the {@link CascadeType#ALL} and {@link CascadeType#PERSIST}
	 * 
	 * @return true if persists
	 * 
	 * @since $version
	 * @author hceylan
	 */
	boolean cascadePersist();

	/**
	 * Returns if the association refreshes the associate.
	 * <p>
	 * This corresponds to the {@link CascadeType#ALL} and {@link CascadeType#REFRESH}
	 * 
	 * @return true if refreshes
	 * 
	 * @since $version
	 * @author hceylan
	 */
	boolean cascadeRefresh();

	/**
	 * Returns if the association removes the associate.
	 * <p>
	 * This corresponds to the {@link CascadeType#ALL} and {@link CascadeType#REMOVE}
	 * 
	 * @return true if refreshes
	 * 
	 * @since $version
	 * @author hceylan
	 */
	boolean cascadeRemove();

	/**
	 * Returns the opposite mapping if this is a bidirectional mapping or null.
	 * 
	 * @return the opposite mapping if this is a bidirectional mapping or null
	 * 
	 * @since $version
	 * @author hceylan
	 */
	Association<T, X> getOpposite();

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public EntityTypeImpl<T> getType();

	/**
	 * Returns if the association is annotated with {@link FetchType#EAGER}
	 * 
	 * @return
	 * 
	 * @since $version
	 * @author hceylan
	 */
	boolean isEager();

	/**
	 * Returns if the association is owner.
	 * 
	 * @return true if the association is owner
	 * 
	 * @since $version
	 * @author hceylan
	 */
	boolean isOwner();
}
