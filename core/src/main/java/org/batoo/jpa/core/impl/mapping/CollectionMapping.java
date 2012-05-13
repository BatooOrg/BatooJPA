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

import java.sql.SQLException;
import java.util.Collection;

import org.batoo.jpa.core.impl.SessionImpl;
import org.batoo.jpa.core.impl.collections.ManagedCollection;
import org.batoo.jpa.core.impl.instance.ManagedInstance;
import org.batoo.jpa.core.impl.metamodel.PluralAttributeImpl;

/**
 * Indicator interface for collection type mappings
 * 
 * @author hceylan
 * @since $version
 */
public interface CollectionMapping<X, C, E> extends Mapping<X, C> {

	/**
	 * Returns the declaring attribute.
	 * 
	 * @return the declaring attribute
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Override
	PluralAttributeImpl<X, C, E> getDeclaringAttribute();

	/**
	 * Selects the children for the mapping owned by the entity with the managedId.
	 * 
	 * @param session
	 *            the session
	 * @param managedInstance
	 *            the owner managed instance
	 * @return collection of children
	 * @throws SQLException
	 * 
	 * @since $version
	 * @author hceylan
	 */
	Collection<E> performSelect(SessionImpl session, ManagedInstance<X> managedInstance) throws SQLException;

	/**
	 * Resets the association. Useful for refresh operation.
	 * 
	 * @param instance
	 *            the managed instance of which the association will be reset
	 * @since $version
	 * @author hceylan
	 */
	void reset(Object instance);

	/**
	 * Sets the collection for the mapping
	 * 
	 * @param instance
	 * @param newInstance
	 * 
	 * @since $version
	 * @author hceylan
	 */
	void setCollection(Object instance, ManagedCollection<?> collection);
}
