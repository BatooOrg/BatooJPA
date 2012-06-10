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
package org.batoo.jpa.core.impl.model;

import java.util.Map;
import java.util.Set;

import javax.persistence.metamodel.IdentifiableType;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.Type;

import org.batoo.jpa.core.impl.metamodel.MetamodelImpl;
import org.batoo.jpa.core.impl.model.attribute.IdAttributeImpl;
import org.batoo.jpa.parser.metadata.attribute.IdAttributeMetadata;
import org.batoo.jpa.parser.metadata.type.EntityMetadata;

import com.google.common.collect.Maps;

/**
 * Implementation of {@link IdentifiableType}.
 * 
 * @param <X>
 *            The represented entity or mapped superclass type.
 * 
 * @author hceylan
 * @since $version
 */
public abstract class IdentifiableTypeImpl<X> extends ManagedTypeImpl<X> implements IdentifiableType<X> {

	private final Map<String, IdAttributeImpl<X, ?>> declaredIdAttributes = Maps.newHashMap();
	private IdAttributeImpl<? super X, ?>[] idAttributes;;

	/**
	 * @param metamodel
	 *            the metamodel
	 * @param parent
	 *            the parent type
	 * @param javaType
	 *            the java type of the managed type
	 * @param metadata
	 *            the metadata
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public IdentifiableTypeImpl(MetamodelImpl metamodel, IdentifiableTypeImpl<? super X> parent, Class<X> javaType, EntityMetadata metadata) {
		super(metamodel, parent, javaType, metadata);

		this.addIdAttributes(metadata);
	}

	/**
	 * Creates and adds the attributes of the managed type from the metadata.
	 * 
	 * @param metadata
	 *            the metadata
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void addIdAttributes(EntityMetadata metadata) {
		for (final IdAttributeMetadata id : metadata.getAttributes().getIds()) {
			final IdAttributeImpl attribute = new IdAttributeImpl(this, id);

			this.declaredIdAttributes.put(id.getName(), attribute);
			this.addDeclaredAttribute(attribute);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <Y> SingularAttribute<X, Y> getDeclaredId(Class<Y> type) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <Y> SingularAttribute<X, Y> getDeclaredVersion(Class<Y> type) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <Y> SingularAttribute<? super X, Y> getId(Class<Y> type) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Returns an array of id attributes.
	 * 
	 * @return an array of id attributes
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@SuppressWarnings("unchecked")
	public IdAttributeImpl<? super X, ?>[] getIdAttributes() {
		if (this.idAttributes != null) {
			return this.idAttributes;
		}

		// populate the id attributes with the inheritance
		synchronized (this) {
			if (this.idAttributes != null) {
				return this.idAttributes;
			}

			final Map<String, IdAttributeImpl<? super X, ?>> idAttributes = Maps.newHashMap();
			for (final IdAttributeImpl<X, ?> attribute : this.declaredIdAttributes.values()) {
				idAttributes.put(attribute.getName(), attribute);
			}

			// XXX implement inheritance
			// addAll getParent().getIdAttributes();

			final IdAttributeImpl<X, ?>[] idAttributes0 = new IdAttributeImpl[idAttributes.size()];
			idAttributes.values().toArray(idAttributes0);

			this.idAttributes = idAttributes0;
		}

		return this.idAttributes;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Set<SingularAttribute<? super X, ?>> getIdClassAttributes() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Type<?> getIdType() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public IdentifiableType<? super X> getSupertype() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <Y> SingularAttribute<? super X, Y> getVersion(Class<Y> type) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean hasSingleIdAttribute() {
		return this.idAttributes.length == 1;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean hasVersionAttribute() {
		// TODO Auto-generated method stub
		return false;
	}
}
