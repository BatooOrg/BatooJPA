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
package org.batoo.jpa.core.impl.model.attribute;

import javax.persistence.metamodel.Attribute;

import org.batoo.jpa.core.impl.instance.ManagedInstance;
import org.batoo.jpa.parser.MappingException;

/**
 * Interface for association type attributes.
 * 
 * @param <X>
 *            The represented type that contains the association
 * @param <T>
 *            The type of the represented association
 * 
 * @author hceylan
 * @since $version
 */
public interface AssociatedAttribute<X, T> extends Attribute<X, T> {

	/**
	 * Returns if the type cascades detach operations.
	 * 
	 * @return true if the type cascades detach operations, false otherwise.
	 * @since $version
	 */
	boolean cascadesDetach();

	/**
	 * Returns if the type cascades merge operations.
	 * 
	 * @return true if the type cascades merge operations, false otherwise.
	 * @since $version
	 */
	boolean cascadesMerge();

	/**
	 * Returns if the type cascades persist operations.
	 * 
	 * @return true if the type cascades persist operations, false otherwise.
	 * @since $version
	 */
	boolean cascadesPersist();

	/**
	 * Returns if the type cascades refresh operations.
	 * 
	 * @return true if the type cascades refresh operations, false otherwise.
	 * @since $version
	 */
	boolean cascadesRefresh();

	/**
	 * Returns if the type cascades remove operations.
	 * 
	 * @return true if the type cascades remove operations, false otherwise.
	 * @since $version
	 */
	boolean cascadesRemove();

	/**
	 * Returns if the association is the owner side.
	 * 
	 * @return true if the association is the owner side
	 * 
	 * @since $version
	 * @author hceylan
	 */
	boolean isOwner();

	/**
	 * Links the attribute to its associate entity type and inverse attribute if bi-directional.
	 * 
	 * @throws MappingException
	 *             thrown in case of a linkage error
	 * 
	 * @since $version
	 * @author hceylan
	 */
	void link() throws MappingException;

	/**
	 * @param source
	 *            the source instance
	 * @param associate
	 *            the associate instance
	 * @return true if source contains reference to the associate, false otherwise
	 * 
	 * @since $version
	 * @author hceylan
	 */
	boolean references(ManagedInstance<?> source, ManagedInstance<?> associate);

	/**
	 * Sets the inverse attribute.
	 * 
	 * @param association
	 *            the inverse association
	 * 
	 * @since $version
	 * @author hceylan
	 */
	void setInverse(AssociatedAttribute<T, X> association);
}
