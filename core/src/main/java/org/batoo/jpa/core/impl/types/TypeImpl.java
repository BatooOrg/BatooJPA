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

import javax.persistence.metamodel.Type;

import org.apache.commons.lang.NotImplementedException;
import org.batoo.jpa.core.impl.mapping.MetamodelImpl;

/**
 * Implementation of {@link Type}
 * 
 * @author hceylan
 * @since $version
 */
public abstract class TypeImpl<X> implements Type<X> {

	protected final Class<X> javaType;
	protected final MetamodelImpl metaModel;
	protected final String name;
	private int h;

	/**
	 * @param metaModel
	 *            the meta model
	 * @param javaType
	 *            the java type this type corresponds to
	 * 
	 * @since $version
	 * @author hceylan
	 * @param jdbcAdapter
	 */
	public TypeImpl(MetamodelImpl metaModel, Class<X> javaType) {
		super();

		this.metaModel = metaModel;
		this.javaType = javaType;

		this.name = javaType != null ? javaType.getSimpleName() : "null";
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Class<X> getJavaType() {
		return this.javaType;
	}

	/**
	 * Returns the metaModel.
	 * 
	 * @return the metaModel
	 * @since $version
	 */
	public MetamodelImpl getMetaModel() {
		return this.metaModel;
	}

	/**
	 * @return
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public int hashCode() {
		if (this.h != 0) {
			return this.h;
		}

		return this.h = super.hashCode();
	}

	/**
	 * Returns if the type is embeddable type.
	 * 
	 * @return true if the type is embeddable type
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public boolean isEmbeddable() {
		return false;
	}

	protected void throwNotImplemented() {
		throw new NotImplementedException();
	}
}
