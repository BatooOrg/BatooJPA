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
package org.batoo.jpa.parser.impl.metadata.type;

import javax.persistence.Access;
import javax.persistence.AccessType;

import org.batoo.jpa.parser.impl.AbstractLocator;
import org.batoo.jpa.parser.impl.metadata.JavaLocator;
import org.batoo.jpa.parser.impl.metadata.attribute.AttributesMetadataImpl;
import org.batoo.jpa.parser.metadata.attribute.AttributesMetadata;
import org.batoo.jpa.parser.metadata.type.EntityMetadata;
import org.batoo.jpa.parser.metadata.type.ManagedTypeMetadata;

/**
 * Implementation of {@link ManagedTypeMetadatImpl}.
 * 
 * @author hceylan
 * @since $version
 */
public class ManagedTypeMetadatImpl implements ManagedTypeMetadata {

	private final JavaLocator locator;
	private final Class<?> clazz;
	private final AccessType accessType;
	private final AttributesMetadataImpl attributes;

	/**
	 * @param clazz
	 *            the represented class
	 * @param metadata
	 *            the metadata
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public ManagedTypeMetadatImpl(Class<?> clazz, EntityMetadata metadata) {
		super();

		this.clazz = clazz;
		this.locator = new JavaLocator(clazz);
		this.accessType = this.getAccessType(metadata);

		// handle attributes
		this.attributes = new AttributesMetadataImpl(this, clazz, metadata != null ? metadata.getAttributes() : null);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public final AccessType getAccessType() {
		return this.accessType;
	}

	/**
	 * Returns the access type.
	 * <p>
	 * if metadata exists and it specifies the access type then it is returned.
	 * <p>
	 * then is class has {@link Access} annotation then it is returned.
	 * <p>
	 * finally default {@link AccessType#FIELD} is returned.
	 * 
	 * @param metadata
	 * @return
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private AccessType getAccessType(EntityMetadata metadata) {
		if ((metadata != null) && (metadata.getAccessType() != null)) {
			return metadata.getAccessType();
		}

		final Access access = this.clazz.getAnnotation(Access.class);
		if ((access != null) && (access.value() != null)) {
			return access.value();
		}

		return AccessType.FIELD;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public AttributesMetadata getAttributes() {
		return this.attributes;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public final String getClassName() {
		return this.clazz.getName();
	}

	/**
	 * Returns the clazz of the ManagedTypeMetadatImpl.
	 * 
	 * @return the clazz of the ManagedTypeMetadatImpl
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public Class<?> getClazz() {
		return this.clazz;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public final AbstractLocator getLocator() {
		return this.locator;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean isMetadataComplete() {
		return false; // N/A
	}
}
