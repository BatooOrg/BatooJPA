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

import java.util.Map;
import java.util.Set;

import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.IdentifiableType;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.Type;

import org.batoo.jpa.core.impl.model.MetamodelImpl;
import org.batoo.jpa.core.impl.model.attribute.AttributeImpl;
import org.batoo.jpa.core.impl.model.attribute.IdAttributeImpl;
import org.batoo.jpa.core.impl.model.attribute.VersionAttributeImpl;
import org.batoo.jpa.parser.MappingException;
import org.batoo.jpa.parser.metadata.attribute.AttributesMetadata;
import org.batoo.jpa.parser.metadata.attribute.IdAttributeMetadata;
import org.batoo.jpa.parser.metadata.attribute.VersionAttributeMetadata;
import org.batoo.jpa.parser.metadata.type.ManagedTypeMetadata;

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
	private final Map<String, IdAttributeImpl<? super X, ?>> idAttributes = Maps.newHashMap();
	private final IdentifiableTypeImpl<? super X> supertype;
	private VersionAttributeImpl<X, ?> declaredVersionAttribute;
	private VersionAttributeImpl<? super X, ?> versionAttribute;

	private IdAttributeImpl<? super X, ?>[] idAttributes0;

	/**
	 * @param metamodel
	 *            the metamodel
	 * @param supertype
	 *            the super type
	 * @param javaType
	 *            the java type of the managed type
	 * @param metadata
	 *            the metadata
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public IdentifiableTypeImpl(MetamodelImpl metamodel, IdentifiableTypeImpl<? super X> supertype, Class<X> javaType,
		ManagedTypeMetadata metadata) {
		super(metamodel, javaType, metadata);
		this.supertype = supertype;

		this.addIdAttributes(metadata);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings({ "unchecked" })
	protected void addAttribute(AttributeImpl<? super X, ?> attribute) {
		if (attribute instanceof IdAttributeImpl) {
			if (attribute.getDeclaringType() == this) {
				this.declaredIdAttributes.put(attribute.getName(), (IdAttributeImpl<X, ?>) attribute);
			}

			this.idAttributes.put(attribute.getName(), (IdAttributeImpl<? super X, ?>) attribute);
		}

		if ((attribute instanceof VersionAttributeImpl)) {
			if (this.versionAttribute != null) {
				throw new MappingException("Multiple version attributes not supported.", this.versionAttribute.getLocator(),
					attribute.getLocator());
			}

			if (attribute.getDeclaringType() == this) {
				this.declaredVersionAttribute = (VersionAttributeImpl<X, ?>) attribute;
			}

			this.versionAttribute = (VersionAttributeImpl<? super X, ?>) attribute;
		}

		super.addAttribute(attribute);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	protected void addAttributes(ManagedTypeMetadata entityMetadata) {
		if (this.supertype != null) {
			for (final Attribute<?, ?> attribute : this.supertype.getAttributes()) {
				this.addAttribute((AttributeImpl<? super X, ?>) attribute);
			}

			if (this.supertype.versionAttribute != null) {
				this.addAttribute(this.supertype.versionAttribute);
			}
		}

		super.addAttributes(entityMetadata);
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
	private void addIdAttributes(ManagedTypeMetadata metadata) {
		final AttributesMetadata attributes = metadata.getAttributes();

		// add id attributes
		for (final IdAttributeMetadata attribute : attributes.getIds()) {
			this.addAttribute(new IdAttributeImpl(this, attribute));
		}

		// add version attributes
		for (final VersionAttributeMetadata attribute : attributes.getVersions()) {
			this.declaredVersionAttribute = new VersionAttributeImpl(this, attribute);
			this.addAttribute(this.declaredVersionAttribute);
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
	@SuppressWarnings("unchecked")
	public <Y> SingularAttribute<X, Y> getDeclaredVersion(Class<Y> type) {
		return (SingularAttribute<X, Y>) this.versionAttribute;
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
	public IdentifiableTypeImpl<? super X> getSupertype() {
		return this.supertype;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <Y> VersionAttributeImpl<? super X, Y> getVersion(Class<Y> type) {
		if (this.versionAttribute == null) {
			return null;
		}

		if (this.versionAttribute.getJavaType() != type) {
			throw new IllegalArgumentException("Version does not match specified type : " + type.getName());
		}

		return (VersionAttributeImpl<? super X, Y>) this.versionAttribute;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean hasSingleIdAttribute() {
		return this.idAttributes.size() == 1;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean hasVersionAttribute() {
		return this.versionAttribute != null;
	}
}
