/*
 * Copyright (c) 2012 - Batoo Software ve Consultancy Ltd.
 * 
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */
package org.batoo.jpa.parser.impl.metadata.type;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.PersistenceException;

import org.apache.commons.lang.StringUtils;
import org.batoo.jpa.parser.impl.AbstractLocator;
import org.batoo.jpa.parser.impl.metadata.JavaLocator;
import org.batoo.jpa.parser.impl.metadata.attribute.AttributesMetadataImpl;
import org.batoo.jpa.parser.metadata.attribute.AttributesMetadata;
import org.batoo.jpa.parser.metadata.type.ManagedTypeMetadata;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * Implementation of {@link ManagedTypeMetadatImpl}.
 * 
 * @author hceylan
 * @since $version
 */
public abstract class ManagedTypeMetadatImpl implements ManagedTypeMetadata {

	private final JavaLocator locator;
	private final Class<?> clazz;
	private final AccessType accessType;
	private final AttributesMetadataImpl attributes;
	private final Set<Class<? extends Annotation>> parsed = Sets.newHashSet();

	/**
	 * @param clazz
	 *            the represented class
	 * @param metadata
	 *            the metadata
	 * @param parentAccessType
	 *            the parent access type
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public ManagedTypeMetadatImpl(Class<?> clazz, ManagedTypeMetadata metadata, AccessType parentAccessType) {
		super();

		this.clazz = clazz;
		this.locator = new JavaLocator(clazz);
		this.accessType = this.getAccessType(metadata, parentAccessType);

		// handle attributes
		this.attributes = new AttributesMetadataImpl(this, clazz, metadata != null ? metadata.getAttributes() : null);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public final AccessType getAccessType() {
		return this.accessType;
	}

	/**
	 * Returns the access type.
	 * <p>
	 * if metadata exists and it specifies the access type then it is returned.
	 * <p>
	 * then is class has {@link Access} annotation then it is returned.
	 * <p>
	 * finally default {@link AccessType#FIELD} is returned.
	 * 
	 * @param metadata
	 *            the metadata
	 * @param parentAccessType
	 *            the parent access type
	 * @return the access type
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private AccessType getAccessType(ManagedTypeMetadata metadata, AccessType parentAccessType) {
		if ((metadata != null) && (metadata.getAccessType() != null)) {
			return metadata.getAccessType();
		}

		final Access access = this.clazz.getAnnotation(Access.class);
		if ((access != null) && (access.value() != null)) {
			return access.value();
		}

		return this.inferAccessType(parentAccessType);
	}

	/**
	 * Returns the set of annotations parsed.
	 * 
	 * @return the set of annotations parsed
	 * 
	 * @since $version
	 * @author hceylan
	 */
	protected Set<Class<? extends Annotation>> getAnnotationsParsed() {
		return this.parsed;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public AttributesMetadata getAttributes() {
		return this.attributes;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public final String getClassName() {
		return this.clazz.getName();
	}

	/**
	 * Returns the clazz of the managed type.
	 * 
	 * @return the clazz of the managed type
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public Class<?> getClazz() {
		return this.clazz;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public final AbstractLocator getLocator() {
		return this.locator;
	}

	/**
	 * Infers and returns the access type based on all persistence annotations being on fields or methods and parent parent access type.
	 * 
	 * @param parentAccessType
	 *            the parent access type
	 * @return the inferred access type
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private AccessType inferAccessType(AccessType parentAccessType) {
		boolean methodsHasAnnotations = false;
		boolean fieldsHasAnnotations = false;

		final List<String> alternated = Lists.newArrayList();

		final Field[] fields = this.clazz.getDeclaredFields();
		final Method[] methods = this.clazz.getDeclaredMethods();

		// find the alternated ones with @Access
		for (final Method m : methods) {
			if ((m.getParameterTypes().length != 0) || (m.getReturnType() == null)) {
				continue;
			}

			final Access access = m.getAnnotation(Access.class);
			if (access != null) {
				final String name = m.getName();
				if ((m.getReturnType() == boolean.class) && name.startsWith("is")) {
					alternated.add(StringUtils.capitalize(name.substring(2)));
				}
				else if (name.startsWith("get")) {
					alternated.add(StringUtils.capitalize(name.substring(3)));
				}
			}
		}

		for (final Field f : fields) {
			final Access access = f.getAnnotation(Access.class);

			if (access != null) {
				alternated.add(StringUtils.capitalize(f.getName()));
			}
		}

		// check methods
		for (final Method m : methods) {
			for (final Annotation a : m.getAnnotations()) {
				// ignore @Access(PROPERTY)
				if (a instanceof Access) {
					if (((Access) a).value() != AccessType.PROPERTY) {
						continue;
					}
				}

				if ((m.getReturnType() == null) || (m.getParameterTypes().length > 0)) {
					continue;
				}

				String name = a.annotationType().getName();
				if (name.startsWith("javax.persistence") || name.startsWith("org.batoo.jpa.annotation")) {
					name = m.getName();

					if ((boolean.class == m.getReturnType()) || name.startsWith("is")) {
						name = name.substring(2);
					}
					else if (name.startsWith("get")) {
						name = name.substring(3);
					}

					if (alternated.contains(StringUtils.capitalize(name))) {
						continue;
					}

					methodsHasAnnotations = true;
					break;
				}
			}
		}

		// check fields
		for (final Field f : fields) {
			for (final Annotation a : f.getAnnotations()) {
				// ignore @Access(FIELD)
				if (a instanceof Access) {
					if (((Access) a).value() != AccessType.FIELD) {
						continue;
					}
				}

				final String name = a.annotationType().getName();
				if (name.startsWith("javax.persistence") || name.startsWith("org.batoo.jpa.annotation")) {
					if (alternated.contains(StringUtils.capitalize(f.getName()))) {
						continue;
					}

					fieldsHasAnnotations = true;
					break;
				}
			}
		}

		if (fieldsHasAnnotations && methodsHasAnnotations) {
			throw new PersistenceException("At least one field and one method has persistence annotations: " + this.clazz.getName());
		}

		if (methodsHasAnnotations) {
			return AccessType.PROPERTY;
		}

		if (fieldsHasAnnotations) {
			return AccessType.FIELD;
		}

		if (parentAccessType != null) {
			return parentAccessType;
		}

		return AccessType.FIELD;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean isMetadataComplete() {
		return false; // N/A
	}
}
