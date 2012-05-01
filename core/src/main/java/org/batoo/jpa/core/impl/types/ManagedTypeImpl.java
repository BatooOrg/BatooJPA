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
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.ManagedType;
import javax.persistence.metamodel.MapAttribute;
import javax.persistence.metamodel.PluralAttribute;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;

import org.batoo.jpa.core.BLogger;
import org.batoo.jpa.core.BatooException;
import org.batoo.jpa.core.MappingException;
import org.batoo.jpa.core.impl.mapping.MetamodelImpl;
import org.batoo.jpa.core.impl.mapping.TypeFactory;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * Implementation of {@link ManagedType}
 * 
 * @author hceylan
 * @since $version
 */
public abstract class ManagedTypeImpl<X> extends TypeImpl<X> implements ManagedType<X> {

	private static final BLogger LOG = BLogger.getLogger(ManagedTypeImpl.class);

	protected final Map<String, Attribute<? super X, ?>> attributes = Maps.newHashMap();

	private Set<PluralAttribute<? super X, ?, ?>> plurals;

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
	public ManagedTypeImpl(MetamodelImpl metaModel, Class<X> javaType) throws MappingException {
		super(metaModel, javaType);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Attribute<? super X, ?> getAttribute(String name) {
		final Attribute<? super X, ?> attribute = this.attributes.get(name);

		if (attribute != null) {
			return attribute;
		}

		return this.throwNotFound();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Set<Attribute<? super X, ?>> getAttributes() {
		return Sets.newHashSet(this.attributes.values());
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public CollectionAttribute<? super X, ?> getCollection(String name) {
		final Attribute<? super X, ?> attribute = this.getAttribute(name);

		if (attribute instanceof CollectionAttribute) {
			return (CollectionAttribute<? super X, ?>) attribute;
		}

		return this.throwNotFound();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <E> CollectionAttribute<? super X, E> getCollection(String name, Class<E> elementType) {
		final CollectionAttribute<? super X, ?> attribute = this.getCollection(name);

		if (attribute.getElementType().getJavaType() == elementType) {
			return (CollectionAttribute<? super X, E>) attribute;
		}

		return this.throwNotFound();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public Attribute<X, ?> getDeclaredAttribute(String name) {
		final Attribute<? super X, ?> attribute = this.attributes.get(name);

		if ((attribute != null) && (attribute.getDeclaringType() == this)) {
			return (Attribute<X, ?>) attribute;
		}

		return this.throwNotFound();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public Set<Attribute<X, ?>> getDeclaredAttributes() {
		final Set<Attribute<X, ?>> attributes = Sets.newHashSet();
		for (final Attribute<? super X, ?> attribute : this.attributes.values()) {
			if (attribute.getDeclaringType() == this) {
				attributes.add((Attribute<X, ?>) attribute);
			}
		}

		return attributes;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public CollectionAttribute<X, ?> getDeclaredCollection(String name) {
		final Attribute<? super X, ?> attribute = this.getAttribute(name);

		if (attribute instanceof CollectionAttribute) {
			return (CollectionAttribute<X, ?>) attribute;
		}

		return this.throwNotFound();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <E> CollectionAttribute<X, E> getDeclaredCollection(String name, Class<E> elementType) {
		final CollectionAttribute<? super X, ?> attribute = this.getDeclaredCollection(name);

		if (attribute.getElementType().getJavaType() == elementType) {
			return (CollectionAttribute<X, E>) attribute;
		}

		return this.throwNotFound();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public ListAttribute<X, ?> getDeclaredList(String name) {
		final Attribute<? super X, ?> attribute = this.getAttribute(name);

		if (attribute instanceof ListAttribute) {
			return (ListAttribute<X, ?>) attribute;
		}

		return this.throwNotFound();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <E> ListAttribute<X, E> getDeclaredList(String name, Class<E> elementType) {
		final ListAttribute<X, ?> attribute = this.getDeclaredList(name);

		if (attribute.getElementType().getJavaType() == elementType) {
			return (ListAttribute<X, E>) attribute;
		}

		return this.throwNotFound();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public MapAttribute<X, ?, ?> getDeclaredMap(String name) {
		final Attribute<? super X, ?> attribute = this.getAttribute(name);

		if (attribute instanceof MapAttribute) {
			return (MapAttribute<X, ?, ?>) attribute;
		}

		return this.throwNotFound();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <K, V> MapAttribute<X, K, V> getDeclaredMap(String name, Class<K> keyType, Class<V> valueType) {
		final MapAttribute<X, ?, ?> attribute = this.getDeclaredMap(name);

		if ((attribute.getKeyJavaType() == keyType) && (attribute.getElementType().getJavaType() == valueType)) {
			return (MapAttribute<X, K, V>) attribute;
		}

		return this.throwNotFound();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public Set<PluralAttribute<X, ?, ?>> getDeclaredPluralAttributes() {
		final Set<PluralAttribute<X, ?, ?>> attributes = Sets.newHashSet();
		for (final Attribute<? super X, ?> attribute : this.attributes.values()) {
			if ((attribute instanceof PluralAttribute) && (attribute.getDeclaringType() == this)) {
				attributes.add((PluralAttribute<X, ?, ?>) attribute);
			}
		}

		return attributes;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public SetAttribute<X, ?> getDeclaredSet(String name) {
		final Attribute<? super X, ?> attribute = this.getAttribute(name);

		if (attribute instanceof SetAttribute) {
			return (SetAttribute<X, ?>) attribute;
		}

		return this.throwNotFound();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <E> SetAttribute<X, E> getDeclaredSet(String name, Class<E> elementType) {
		final SetAttribute<X, ?> attribute = this.getDeclaredSet(name);

		if (attribute.getElementType().getJavaType() == elementType) {
			return (SetAttribute<X, E>) attribute;
		}

		return this.throwNotFound();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public SingularAttribute<X, ?> getDeclaredSingularAttribute(String name) {
		final Attribute<X, ?> attribute = this.getDeclaredAttribute(name);

		if (attribute instanceof SingularAttribute) {
			return (SingularAttribute<X, ?>) attribute;
		}

		return this.throwNotFound();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <Y> SingularAttribute<X, Y> getDeclaredSingularAttribute(String name, Class<Y> type) {
		final SingularAttribute<X, ?> attribute = this.getDeclaredSingularAttribute(name);

		if (attribute.getJavaType() == type) {
			return (SingularAttribute<X, Y>) attribute;
		}

		return this.throwNotFound();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public Set<SingularAttribute<X, ?>> getDeclaredSingularAttributes() {
		final Set<SingularAttribute<X, ?>> attributes = Sets.newHashSet();
		for (final Attribute<? super X, ?> attribute : this.getDeclaredAttributes()) {
			if ((attribute instanceof SingularAttributeImpl) && (attribute.getDeclaringType() == this)) {
				attributes.add((SingularAttribute<X, ?>) attribute);
			}
		}

		return attributes;
	}

	protected Constructor<X> getDefaultConstructor(final Class<X> javaType) throws MappingException {
		final Constructor<X> constructor = AccessController.doPrivileged(new PrivilegedAction<Constructor<X>>() {
			@Override
			public Constructor<X> run() {
				try {
					return javaType.getConstructor();
				}
				catch (final NoSuchMethodException e) {
					return null;
				}
				catch (final SecurityException e) {
					return null;
				}
			}
		});

		if (constructor == null) {
			throw new MappingException("Class " + javaType + " must have a default constructor");
		}

		return constructor;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public ListAttribute<? super X, ?> getList(String name) {
		final Attribute<? super X, ?> attribute = this.getAttribute(name);

		if (attribute instanceof ListAttribute) {
			return (ListAttribute<? super X, ?>) attribute;
		}

		return this.throwNotFound();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <E> ListAttribute<? super X, E> getList(String name, Class<E> elementType) {
		final ListAttribute<? super X, ?> attribute = this.getList(name);

		if (attribute.getElementType().getJavaType() == elementType) {
			return (ListAttribute<? super X, E>) attribute;
		}

		return this.throwNotFound();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public MapAttribute<? super X, ?, ?> getMap(String name) {
		final Attribute<? super X, ?> attribute = this.getAttribute(name);

		if (attribute instanceof MapAttribute) {
			return (MapAttribute<? super X, ?, ?>) attribute;
		}

		return this.throwNotFound();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <K, V> MapAttribute<? super X, K, V> getMap(String name, Class<K> keyType, Class<V> valueType) {
		final MapAttribute<? super X, ?, ?> attribute = this.getMap(name);

		if ((attribute.getKeyJavaType() == keyType) && (attribute.getElementType().getJavaType() == valueType)) {
			return (MapAttribute<? super X, K, V>) attribute;
		}

		return this.throwNotFound();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public javax.persistence.metamodel.Type.PersistenceType getPersistenceType() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public Set<PluralAttribute<? super X, ?, ?>> getPluralAttributes() {
		if (this.plurals != null) {
			return this.plurals;
		}

		final Predicate<? super Attribute<? super X, ?>> predicate = new Predicate<Attribute<? super X, ?>>() {

			@Override
			public boolean apply(Attribute<? super X, ?> input) {
				return input instanceof PluralAttribute;
			}
		};

		final Collection<Attribute<? super X, ?>> set = Collections2.filter(this.attributes.values(), predicate);

		this.plurals = Sets.newHashSet();
		this.plurals.addAll((Collection<? extends PluralAttribute<X, ?, ?>>) set);

		return this.plurals;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public SetAttribute<? super X, ?> getSet(String name) {
		final Attribute<? super X, ?> attribute = this.getAttribute(name);

		if (attribute instanceof SetAttribute) {
			return (SetAttribute<? super X, ?>) attribute;
		}

		return this.throwNotFound();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <E> SetAttribute<? super X, E> getSet(String name, Class<E> elementType) {
		final SetAttribute<? super X, ?> attribute = this.getSet(name);

		if (attribute.getElementType().getJavaType() == elementType) {
			return (SetAttribute<? super X, E>) attribute;
		}

		return this.throwNotFound();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public SingularAttribute<? super X, ?> getSingularAttribute(String name) {
		final Attribute<? super X, ?> attribute = this.getAttribute(name);

		if (attribute instanceof SingularAttribute) {
			return (SingularAttribute<? super X, ?>) attribute;
		}

		return this.throwNotFound();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <Y> SingularAttribute<? super X, Y> getSingularAttribute(String name, Class<Y> type) {
		final SetAttribute<? super X, ?> attribute = this.getSet(name);

		if (attribute.getJavaType() == type) {
			return (SingularAttribute<? super X, Y>) attribute;
		}

		return this.throwNotFound();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public Set<SingularAttribute<? super X, ?>> getSingularAttributes() {
		final Predicate<? super Attribute<? super X, ?>> predicate = new Predicate<Attribute<? super X, ?>>() {

			@Override
			public boolean apply(Attribute<? super X, ?> input) {
				return input instanceof SingularAttribute;
			}
		};

		final Collection<Attribute<? super X, ?>> set = Collections2.filter(this.attributes.values(), predicate);

		final Set<SingularAttribute<? super X, ?>> plurals = Sets.newHashSet();
		plurals.addAll((Collection<? extends SingularAttribute<X, ?>>) set);

		return plurals;
	}

	/**
	 * Parses the meta data of the type.
	 * 
	 * @param parsed
	 *            the set of annotations parsed
	 * @throws BatooException
	 *             thrown in case of a mapping error
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void parse(Set<Class<? extends Annotation>> parsed) throws BatooException {
		LOG.info("Parsing type {0}", this.getJavaType());

		final Field[] fields = AccessController.doPrivileged(new PrivilegedAction<Field[]>() {

			@Override
			public Field[] run() {
				return ManagedTypeImpl.this.getJavaType().getDeclaredFields();
			}
		});

		for (final Field field : fields) {
			// skip if field is transient
			final int mod = field.getModifiers();
			if (Modifier.isTransient(mod)) {
				LOG.trace("Skipping transient property {0}.{1}", this.getJavaType().getSimpleName(), field.getName());

				continue;
			}

			if (Modifier.isStatic(mod)) {
				LOG.trace("Skipping static property {0}.{1}", this.getJavaType().getSimpleName(), field.getName());

				continue;
			}

			final Attribute<X, ?> attribute = TypeFactory.<X> forType(this, field, field.getType());

			LOG.debug("Found {0}.{1} {2}", this.getJavaType().getSimpleName(), field.getName(), attribute);

			this.attributes.put(field.getName(), attribute);
		}
	}

	protected void performClassChecks(final Class<X> type) throws MappingException {
		final String error = AccessController.doPrivileged(new PrivilegedAction<String>() {

			@Override
			public String run() {
				try {
					type.getConstructor();
				}
				catch (final SecurityException e) {
					// not likely
				}
				catch (final NoSuchMethodException e) {
					return "Type " + type + " does not have a default constructor";
				}

				if (type.isInterface() || type.isAnnotation() || type.isEnum()) {
					return "Type " + type + " is not a regular class";
				}

				if (Modifier.isFinal(type.getModifiers())) {
					return "Type " + type + " must not be final";
				}

				return null;
			}
		});

		if (error != null) {
			throw new MappingException(error);
		}
	}

	/**
	 * @return
	 * 
	 * @since $version
	 * @author hceylan
	 */
	protected <V> V throwNotFound() {
		throw new IllegalArgumentException("Attribute not found");
	}

}
