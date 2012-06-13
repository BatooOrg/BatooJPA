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

import java.lang.reflect.Member;

import javax.persistence.metamodel.Attribute;

import org.batoo.jpa.common.reflect.AbstractAccessor;
import org.batoo.jpa.common.reflect.ReflectHelper;
import org.batoo.jpa.core.impl.model.MetamodelImpl;
import org.batoo.jpa.core.impl.model.type.ManagedTypeImpl;
import org.batoo.jpa.parser.impl.AbstractLocator;
import org.batoo.jpa.parser.impl.metadata.attribute.AttributeMetadataImpl;
import org.batoo.jpa.parser.metadata.attribute.AttributeMetadata;

/**
 * Implementation of {@link Attribute}.
 * 
 * @param <X>
 *            The represented type that contains the attribute
 * @param <Y>
 *            The type of the represented attribute
 * 
 * @author hceylan
 * @since $version
 */
public abstract class AttributeImpl<X, Y> implements Attribute<X, Y> {

	private final AttributeMetadata metadata;
	private final ManagedTypeImpl<X> declaringType;
	private final String name;
	private final Member javaMember;
	private final Class<Y> javaType;
	private final MetamodelImpl metamodel;
	private final AbstractAccessor accessor;

	/**
	 * @param declaringType
	 *            the declaring type
	 * @param metadata
	 *            the metadata
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@SuppressWarnings("unchecked")
	public AttributeImpl(ManagedTypeImpl<X> declaringType, AttributeMetadata metadata) {
		super();

		this.metadata = metadata;

		this.declaringType = declaringType;
		this.name = metadata.getName();
		this.javaMember = ((AttributeMetadataImpl) metadata).getMember();
		this.javaType = (Class<Y>) ReflectHelper.getMemberType(this.javaMember);
		this.metamodel = declaringType.getMetamodel();
		this.accessor = ReflectHelper.getAccessor(this.javaMember);
	}

	/**
	 * Returns the attribute value of instance.
	 * 
	 * @param instance
	 *            the instance of which the value to be returned
	 * @return the attribute value of instance
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@SuppressWarnings("unchecked")
	public Y get(Object instance) {
		if (instance == null) {
			return null;
		}

		return (Y) this.accessor.get(instance);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public ManagedTypeImpl<X> getDeclaringType() {
		return this.declaringType;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Member getJavaMember() {
		return this.javaMember;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Class<Y> getJavaType() {
		return this.javaType;
	}

	/**
	 * Returns the locator of the AttributeImpl.
	 * 
	 * @return the locator of the AttributeImpl
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public AbstractLocator getLocator() {
		return this.metadata.getLocator();
	}

	/**
	 * Returns the metadata of the attribute.
	 * 
	 * @return the metadata of the attribute
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public AttributeMetadata getMetadata() {
		return this.metadata;
	}

	/**
	 * Returns the metamodel.
	 * 
	 * @return the metamodel
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public MetamodelImpl getMetamodel() {
		return this.metamodel;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getName() {
		return this.name;
	}

	/**
	 * Sets the attribute value of instance.
	 * 
	 * @param instance
	 *            the instance
	 * @param value
	 *            the value to set
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void set(Object instance, Object value) {
		this.accessor.set(instance, value);
	}
}
