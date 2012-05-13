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
package org.batoo.jpa.core.impl.metamodel;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.Set;

import javax.persistence.Embeddable;
import javax.persistence.metamodel.EmbeddableType;

import org.batoo.jpa.core.BatooException;
import org.batoo.jpa.core.MappingException;
import org.batoo.jpa.core.impl.mapping.MetamodelImpl;
import org.batoo.jpa.core.impl.reflect.ReflectHelper;

import sun.reflect.ConstructorAccessor;

/**
 * Implementation of {@link EmbeddableType}.
 * 
 * @author hceylan
 * @since $version
 */
@SuppressWarnings("restriction")
public class EmbeddableTypeImpl<X> extends ManagedTypeImpl<X> implements EmbeddableType<X> {

	protected final ConstructorAccessor constructor0;
	protected final Constructor<X> constructor1;

	/**
	 * @param metaModel
	 *            the meta model
	 * @param javaType
	 *            the java type this type corresponds to
	 * @throws MappingException
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public EmbeddableTypeImpl(MetamodelImpl metaModel, Class<X> clazz) throws MappingException {
		super(metaModel, clazz);

		this.constructor0 = ReflectHelper.createConstructor(this.javaType);
		if (this.constructor0 == null) {
			Constructor<X> constructor = null;

			try {
				constructor = this.javaType.getConstructor();
			}
			catch (final Exception e) {} // not possible

			this.constructor1 = constructor;
		}
		else {
			this.constructor1 = null;
		}

		metaModel.addEmbeddable(this);
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
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean isEmbeddable() {
		return true;
	}

	/**
	 * Creates a new instance.
	 * 
	 * @return a new instance
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@SuppressWarnings("unchecked")
	public X newInstance() {
		try {
			if (this.constructor0 != null) {
				return (X) this.constructor0.newInstance(new Object[] {});
			}

			return this.constructor1.newInstance();
		}
		catch (final Exception e) {
			// Not possible
		}

		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void parse(Set<Class<? extends Annotation>> parsed) throws BatooException {
		super.parse(parsed);

		final Class<X> type = this.getJavaType();

		final Embeddable embeddable = type.getAnnotation(Embeddable.class);
		if (embeddable == null) {
			throw new MappingException("Type is not an embeddable " + type);
		}
		parsed.add(Embeddable.class);

		this.performClassChecks(type);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String toString() {
		return "EmbeddableTypeImpl [name=" + this.name + ", attributes=" + this.getAttributes() + "]";
	}
}
