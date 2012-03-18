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

import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.EmbeddableType;

import org.batoo.jpa.core.impl.mapping.MetamodelImpl;

/**
 * Implementation of {@link EmbeddableType}.
 * 
 * @author hceylan
 * @since $version
 */
public class EmbeddableTypeImpl<X> extends ManagedTypeImpl<X> implements EmbeddableType<X> {

	/**
	 * @param metaModel
	 *            the meta model
	 * @param javaType
	 *            the java type this type corresponds to
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public EmbeddableTypeImpl(MetamodelImpl metaModel, Class<X> clazz) {
		super(metaModel, clazz);

		metaModel.addEmbeddable(this);
	}

	/**
	 * Returns if this type directly or transitively embeds the <code>type</code>.
	 * 
	 * @param type
	 *            the embeddablable type
	 * @return true if embeds
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public boolean embeds(EmbeddableTypeImpl<?> type) {
		for (final Attribute<? super X, ?> attribute : this.setAttributes) {
			final boolean embeds = ((AttributeImpl<?, ?>) attribute).embeds(type);
			if (embeds) {
				return true;
			}
		}

		return false;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public PersistenceType getPersistenceType() {
		return PersistenceType.EMBEDDABLE;
	}

}
