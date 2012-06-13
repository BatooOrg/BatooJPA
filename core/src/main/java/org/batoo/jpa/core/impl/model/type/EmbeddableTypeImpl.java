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
package org.batoo.jpa.core.impl.model.type;

import javax.persistence.metamodel.EmbeddableType;

import org.batoo.jpa.common.reflect.ReflectHelper;
import org.batoo.jpa.core.impl.model.MetamodelImpl;
import org.batoo.jpa.parser.MappingException;
import org.batoo.jpa.parser.metadata.type.EmbeddableMetadata;

import sun.reflect.ConstructorAccessor;

/**
 * Implementation of {@link EmbeddableType}.
 * 
 * @param <X>
 *            The represented embeddable type
 * 
 * @author hceylan
 * @since $version
 */
@SuppressWarnings("restriction")
public class EmbeddableTypeImpl<X> extends ManagedTypeImpl<X> implements EmbeddableType<X> {
	private static final Object[] EMPTY_PARAMS = new Object[] {};

	private ConstructorAccessor constructor;

	/**
	 * @param metamodel
	 *            the metamodel
	 * @param javaType
	 *            the java type
	 * @param metadata
	 *            the metadata
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public EmbeddableTypeImpl(MetamodelImpl metamodel, Class<X> javaType, EmbeddableMetadata metadata) {
		super(metamodel, javaType, metadata);

		this.addAttributes(metadata);

		try {
			this.constructor = ReflectHelper.createConstructor(javaType.getConstructor());
		}
		catch (final Exception e) {
			throw new MappingException("Embeddable type does not have a default constructor", this.getLocator());
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public PersistenceType getPersistenceType() {
		return PersistenceType.EMBEDDABLE;
	}

	/**
	 * Returns a new instance of the type
	 * 
	 * @return a new instance of the type
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@SuppressWarnings("unchecked")
	public X newInstance() {
		try {
			return (X) this.constructor.newInstance(EmbeddableTypeImpl.EMPTY_PARAMS);
		}
		catch (final Exception e) {} // not possible at this stage

		return null;
	}

}
