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
package org.batoo.jpa.core.impl.model.mapping;

import org.batoo.jpa.core.impl.instance.ManagedInstance;
import org.batoo.jpa.core.impl.model.attribute.SingularAttributeImpl;
import org.batoo.jpa.core.impl.model.type.EntityTypeImpl;

/**
 * The base class for {@link EmbeddedMapping} and {@link BasicMapping}.
 * 
 * @param <Z>
 *            the source type
 * @param <X>
 *            the destination type
 * 
 * @author hceylan
 * @since $version
 */
public interface SingularMapping<Z, X> {

	/**
	 * Fills the sequence / table generated value.
	 * <p>
	 * The operation returns false if at least one entity needs to obtain identity from the database.
	 * 
	 * @param type
	 *            the entity type
	 * @param managedInstance
	 *            the managed instance
	 * @param instance
	 *            the instance to fill ids.
	 * @return false if all OK, true if if at least one entity needs to obtain identity from the database
	 * 
	 * @since $version
	 * @author hceylan
	 */
	boolean fillValue(EntityTypeImpl<?> type, ManagedInstance<?> managedInstance, Object instance);

	/**
	 * Returns the mapping value of instance.
	 * 
	 * @param instance
	 *            the instance of which the value to be returned
	 * @return the mapping value of instance
	 * 
	 * @since $version
	 * @author hceylan
	 */
	X get(Object instance);

	/**
	 * Returns the attribute of the mapping.
	 * 
	 * @return the attribute of the mapping
	 * 
	 * @since $version
	 * @author hceylan
	 */
	SingularAttributeImpl<? super Z, X> getAttribute();

	/**
	 * Sets the mapping value of instance.
	 * 
	 * @param instance
	 *            the instance of which the value to set
	 * @param value
	 *            the value to set
	 * 
	 * @since $version
	 * @author hceylan
	 */
	void set(Object instance, Object value);
}
