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

import java.sql.SQLException;

import javax.persistence.metamodel.Attribute;

import org.batoo.jpa.core.impl.instance.ManagedInstance;
import org.batoo.jpa.core.impl.jdbc.ConnectionImpl;
import org.batoo.jpa.core.impl.jdbc.ForeignKey;
import org.batoo.jpa.core.impl.jdbc.JoinTable;
import org.batoo.jpa.core.impl.manager.SessionImpl;
import org.batoo.jpa.core.impl.model.EntityTypeImpl;
import org.batoo.jpa.core.impl.model.ManagedTypeImpl;
import org.batoo.jpa.parser.MappingException;

/**
 * Interface for association type attributes.
 * 
 * @param <X>
 *            The represented type that contains the association
 * @param <T>
 *            The type of the represented association
 * @param <Y>
 *            the attribute type
 * 
 * @author hceylan
 * @since $version
 */
public interface AssociatedAttribute<X, T, Y> extends Attribute<X, Y> {

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
	 * Checks that the association references not a transient instance
	 * 
	 * @param managedInstance
	 *            the managed instance
	 * 
	 * @since $version
	 * @author hceylan
	 */
	void checkTransient(ManagedInstance<? extends X> managedInstance);

	/**
	 * Describes the attribute.
	 * 
	 * @return the description
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public abstract String describe();

	/**
	 * Flushes the associates.
	 * 
	 * @param session
	 *            the session
	 * @param connection
	 *            the connection to use
	 * @param managedInstance
	 *            the managed instance
	 * @throws SQLException
	 *             thrown if there is an underlying SQL Exception
	 * 
	 * @since $version
	 * @author hceylan
	 */
	void flush(SessionImpl session, ConnectionImpl connection, ManagedInstance<? extends X> managedInstance) throws SQLException;

	/**
	 * Returns the bindable entity type.
	 * 
	 * @return the bindable entity type
	 * 
	 * @since $version
	 * @author hceylan
	 */
	EntityTypeImpl<T> getAssociationType();

	/**
	 * Return the managed type representing the type in which the attribute was declared.
	 * 
	 * @return declaring type
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Override
	ManagedTypeImpl<X> getDeclaringType();

	/**
	 * Returns the foreign key of the attribute.
	 * 
	 * @return the foreign key of the attribute
	 * 
	 * @since $version
	 * @author hceylan
	 */
	ForeignKey getForeignKey();

	/**
	 * Returns the inverse attribute.
	 * 
	 * @return the inverse attribute or null
	 * 
	 * 
	 * @since $version
	 * @author hceylan
	 */
	AssociatedAttribute<T, X, ?> getInverse();

	/**
	 * Returns the join table of the attribute.
	 * 
	 * @return the join table of the attribute
	 * 
	 * @since $version
	 * @author hceylan
	 */
	JoinTable getJoinTable();

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
	 * @param instance
	 *            the source instance
	 * @param reference
	 *            the associate instance
	 * @return true if source contains reference to the associate, false otherwise
	 * 
	 * @since $version
	 * @author hceylan
	 */
	boolean references(Object instance, Object reference);

	/**
	 * Sets the attribute value of instance.
	 * 
	 * @param managedInstance
	 *            the instance of which the value to set
	 * @param value
	 *            the value to set
	 * 
	 * @since $version
	 * @author hceylan
	 */
	void set(ManagedInstance<? extends X> managedInstance, Object value);

	/**
	 * Sets the inverse attribute.
	 * 
	 * @param inverse
	 *            the inverse association
	 * 
	 * @since $version
	 * @author hceylan
	 */
	void setInverse(AssociatedAttribute<T, X, ?> inverse);
}
