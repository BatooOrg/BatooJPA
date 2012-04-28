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
package org.batoo.jpa.core.impl.types;

import java.lang.annotation.Annotation;
import java.util.Set;

import javax.persistence.MappedSuperclass;
import javax.persistence.metamodel.MappedSuperclassType;

import org.batoo.jpa.core.BatooException;
import org.batoo.jpa.core.MappingException;
import org.batoo.jpa.core.impl.mapping.MetamodelImpl;

/**
 * Implementation of {@link MappedSuperclass}.
 * 
 * @author hceylan
 * @since $version
 */
public class MappedSuperclassTypeImpl<X> extends IdentifiableTypeImpl<X> implements MappedSuperclassType<X> {

	/**
	 * @param metaModel
	 *            the meta model
	 * @param javaType
	 *            the java type this type corresponds to
	 * @throws MappingException
	 *             thrown in case of a mapping error
	 * 
	 * @since $version
	 * @author hceylan
	 * @param metaModel
	 */
	public MappedSuperclassTypeImpl(MetamodelImpl metaModel, Class<X> javaType) throws MappingException {
		super(metaModel, javaType);

		metaModel.addMappedSuperclass(this);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public PersistenceType getPersistenceType() {
		return PersistenceType.MAPPED_SUPERCLASS;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void parse(Set<Class<? extends Annotation>> parsed) throws BatooException {
		super.parse(parsed);

		final Class<X> type = this.getJavaType();
		final MappedSuperclass mappedSuperclass = type.getAnnotation(MappedSuperclass.class);
		if (mappedSuperclass == null) {
			throw new MappingException("Type is not a mapped super class " + type);
		}

		parsed.add(MappedSuperclass.class);
	}
}
