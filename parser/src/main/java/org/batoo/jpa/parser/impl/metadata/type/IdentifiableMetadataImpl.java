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

import javax.persistence.IdClass;

import org.batoo.jpa.parser.metadata.type.IdentifiableTypeMetadata;

/**
 * Implementation {@link IdentifiableTypeMetadata}.
 * 
 * @author hceylan
 * @since $version
 */
public abstract class IdentifiableMetadataImpl extends ManagedTypeMetadatImpl implements IdentifiableTypeMetadata {

	private final String idClass;

	/**
	 * @param clazz
	 *            the represented class
	 * @param metadata
	 *            the metadata
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public IdentifiableMetadataImpl(Class<?> clazz, IdentifiableTypeMetadata metadata) {
		super(clazz, metadata);

		this.idClass = this.handleIdClass(metadata);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public final String getIdClass() {
		return this.idClass;
	}

	/**
	 * Handles the id class definition of the entity.
	 * 
	 * @param metadata
	 *            the metadata
	 * @param parsed
	 *            the set of annotations parsed
	 * @return the id class attribute
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private String handleIdClass(IdentifiableTypeMetadata metadata) {
		final IdClass idClass = this.getClazz().getAnnotation(IdClass.class);
		if (idClass != null) {
			this.getAnnotationsParsed().add(IdClass.class);

			return idClass.value().getName();
		}

		return null;
	}
}
