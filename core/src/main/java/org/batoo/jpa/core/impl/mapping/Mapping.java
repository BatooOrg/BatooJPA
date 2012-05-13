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

import java.util.Deque;

import javax.persistence.metamodel.Attribute.PersistentAttributeType;

import org.batoo.jpa.core.impl.metamodel.AttributeImpl;
import org.batoo.jpa.core.impl.metamodel.EntityTypeImpl;
import org.batoo.jpa.core.impl.metamodel.TypeImpl;

/**
 * Interface for mappings
 * 
 * @author hceylan
 * @since $version
 */
public interface Mapping<X, T> {

	public enum AssociationType {
		/**
		 * <ul>
		 * <li>{@link PersistentAttributeType#BASIC}
		 * </ul>
		 */
		BASIC,

		/**
		 * <ul>
		 * <li>{@link PersistentAttributeType#ONE_TO_ONE}
		 * <li>{@link PersistentAttributeType#EMBEDDED}
		 * </ul>
		 */
		ONE,

		/**
		 * <ul>
		 * <li>{@link PersistentAttributeType#ONE_TO_MANY},
		 * <li>{@link PersistentAttributeType#MANY_TO_ONE},
		 * <li>{@link PersistentAttributeType#MANY_TO_MANY},
		 * <li>{@link PersistentAttributeType#ELEMENT_COLLECTION}
		 * </ul>
		 */
		MANY,
	}

	/**
	 * Returns the associationType.
	 * 
	 * @return the associationType
	 * @since $version
	 */
	AssociationType getAssociationType();

	/**
	 * Returns the declaring attribute.
	 * 
	 * @return the declaring attribute
	 * 
	 * @since $version
	 * @author hceylan
	 */
	AttributeImpl<X, T> getDeclaringAttribute();

	/**
	 * Returns the java type of the attribute
	 * 
	 * @return
	 * 
	 * @since $version
	 * @author hceylan
	 */
	Class<T> getJavaType();

	/**
	 * Returns the owner type.
	 * 
	 * @return the owner
	 * @since $version
	 */
	EntityTypeImpl<?> getOwner();

	/**
	 * Returns the path.
	 * 
	 * @return the path
	 * @since $version
	 */
	Deque<AttributeImpl<?, ?>> getPath();

	/**
	 * Returns the path as string.
	 * 
	 * @return the path as string
	 * 
	 * @since $version
	 * @author hceylan
	 */
	String getPathAsString();

	/**
	 * Returns the peer type.
	 * 
	 * @return the owner
	 * @since $version
	 */
	TypeImpl<T> getType();

	/**
	 * Returns the value for the mapping.
	 * 
	 * @param instance
	 *            the instance
	 * @return the value for the mapping
	 * 
	 * @since $version
	 * @author hceylan
	 */
	T getValue(Object instance);

	/**
	 * Sets the value for the mapping.
	 * 
	 * @param instance
	 *            the instance
	 * @param the
	 *            value to set
	 * 
	 * @since $version
	 * @author hceylan
	 */
	void setValue(Object instance, Object value);

}
