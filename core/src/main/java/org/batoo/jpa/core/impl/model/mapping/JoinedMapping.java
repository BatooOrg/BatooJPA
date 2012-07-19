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

import java.sql.SQLException;

import javax.persistence.criteria.JoinType;

import org.batoo.jpa.core.impl.instance.ManagedInstance;
import org.batoo.jpa.core.impl.jdbc.ConnectionImpl;
import org.batoo.jpa.core.impl.jdbc.JoinableTable;
import org.batoo.jpa.core.impl.model.attribute.AttributeImpl;
import org.batoo.jpa.core.impl.model.type.TypeImpl;

/**
 * Mapping that have a join or collection table
 * 
 * @param <Z>
 *            the source type
 * @param <X>
 *            the destination type
 * @param <Y>
 *            the attribute type
 * 
 * @author hceylan
 * @since $version
 */
public interface JoinedMapping<Z, X, Y> {

	/**
	 * Type of the mapping
	 * 
	 * @author hceylan
	 * @since $version
	 */
	public enum MappingType {
		/**
		 * Singular association
		 */
		SINGULAR_ASSOCIATION,

		/**
		 * Plural association
		 */
		PLURAL_ASSOCIATION,

		/**
		 * Element collection
		 */
		ELEMENT_COLLECTION,

		/**
		 * Embeddable mapping
		 */
		EMBEDDABLE
	}

	/**
	 * Extracts key from the value.
	 * 
	 * @param value
	 *            the value
	 * @return the key of the value
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public Object extractKey(Y value);

	/**
	 * Flushes the associates.
	 * 
	 * @param connection
	 *            the connection to use
	 * @param managedInstance
	 *            the managed instance
	 * @param removals
	 *            true if the removals should be flushed and false for the additions
	 * @param force
	 *            true to force, effective only for insertions and for new entities.
	 * @throws SQLException
	 *             thrown if there is an underlying SQL Exception
	 * 
	 * @since $version
	 * @author hceylan
	 */
	void flush(ConnectionImpl connection, ManagedInstance<?> managedInstance, boolean removals, boolean force) throws SQLException;

	/**
	 * Returns the attribute of the mapping
	 * 
	 * @return the attribute of the mapping
	 * 
	 * @since $version
	 * @author hceylan
	 */
	AttributeImpl<? super Z, X> getAttribute();

	/**
	 * Returns the type of the mapping.
	 * 
	 * @return the type of the mapping
	 * 
	 * @since $version
	 * @author hceylan
	 */
	MappingType getMappingType();

	/**
	 * Returns the table for the relation.
	 * <p>
	 * This is, for associations the JoinTable, for element collections the CollectionTable.
	 * 
	 * @return the table
	 * 
	 * @since $version
	 * @author hceylan
	 */
	JoinableTable getTable();

	/**
	 * Returns the bindable entity type.
	 * 
	 * @return the bindable entity type
	 * 
	 * @since $version
	 * @author hceylan
	 */
	TypeImpl<Y> getType();

	/**
	 * Initializes the mapping.
	 * 
	 * @param instance
	 *            the instance
	 * 
	 * @since $version
	 * @author hceylan
	 */
	void initialize(ManagedInstance<?> instance);

	/**
	 * Returns if the mapping is an association.
	 * 
	 * @return true if the mapping is an association, false otherwise
	 * 
	 * @since $version
	 * @author hceylan
	 */
	boolean isAssociation();

	/**
	 * Returns if the association should be eagerly fetched.
	 * 
	 * @return true if the association should be eagerly fetched
	 * 
	 * @since $version
	 * @author hceylan
	 */
	boolean isEager();

	/**
	 * Returns if the mapping is a joined mapping.
	 * 
	 * @return true if the mapping is a joined mapping, false otherwise
	 * 
	 * @since $version
	 * @author hceylan
	 */
	boolean isJoined();

	/**
	 * Returns if the mapping is a map collection.
	 * 
	 * @return true if the mapping is a map collection, false otherwise
	 * 
	 * @since $version
	 * @author hceylan
	 */
	boolean isMap();

	/**
	 * Returns the join SQL for the mapping.
	 * 
	 * @return the join SQL for the mapping
	 * 
	 * @param parentAlias
	 *            the parent table alias
	 * @param alias
	 *            the primary table alias
	 * @param joinType
	 *            the join type
	 * 
	 * @since $version
	 * @author hceylan
	 */
	String join(String parentAlias, String alias, JoinType joinType);
}
