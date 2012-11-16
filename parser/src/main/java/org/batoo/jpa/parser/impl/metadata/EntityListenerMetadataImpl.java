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
package org.batoo.jpa.parser.impl.metadata;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

import javax.persistence.PostLoad;
import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;

import org.batoo.jpa.parser.impl.AbstractLocator;
import org.batoo.jpa.parser.metadata.CallbackMetadata;
import org.batoo.jpa.parser.metadata.EntityListenerMetadata;
import org.batoo.jpa.parser.metadata.type.IdentifiableTypeMetadata;

import com.google.common.collect.Lists;

/**
 * Implementation of {@link EntityListenerMetadata}.
 * 
 * @author hceylan
 * @since 2.0.0
 */
public class EntityListenerMetadataImpl implements EntityListenerMetadata {

	private final AbstractLocator locator;
	private final boolean self;
	private final String className;
	private final List<CallbackMetadata> callbacks = Lists.newArrayList();

	/**
	 * @param locator
	 *            the locator
	 * @param clazz
	 *            the name of the class
	 * @since 2.0.0
	 */
	public EntityListenerMetadataImpl(AbstractLocator locator, Class<?> clazz) {
		super();

		this.locator = locator;
		this.className = clazz.getName();
		this.self = false;

		this.handleCallbacks(clazz, null);
	}

	/**
	 * @param locator
	 *            the locator
	 * @param clazz
	 *            the class
	 * @param annotationsParsed
	 *            the set of annotations parsed
	 * 
	 * @since 2.0.0
	 */
	public EntityListenerMetadataImpl(AbstractLocator locator, Class<?> clazz, Set<Class<? extends Annotation>> annotationsParsed) {
		super();

		this.locator = locator;
		this.className = clazz.getName();
		this.handleCallbacks(clazz, annotationsParsed);
		this.self = true;
	}

	/**
	 * @param locator
	 *            the locator
	 * @param metadata
	 *            the metadata
	 * 
	 * @since 2.0.0
	 */
	public EntityListenerMetadataImpl(AbstractLocator locator, EntityListenerMetadata metadata) {
		super();

		this.locator = locator;
		this.className = metadata.getClassName();
		this.callbacks.addAll(metadata.getCallbacks());
		this.self = false;
	}

	/**
	 * @param locator
	 *            the locator
	 * @param metadata
	 *            the metadata
	 * @param className
	 *            the name of the class
	 * 
	 * @since 2.0.0
	 */
	public EntityListenerMetadataImpl(AbstractLocator locator, IdentifiableTypeMetadata metadata, String className) {
		super();

		this.locator = locator;
		this.className = className;
		this.callbacks.addAll(metadata.getCallbacks());
		this.self = true;
	}

	private CallbackMetadata createCallback(final Method method, final EntityListenerType type) {
		return new CallbackMetadata() {

			@Override
			public AbstractLocator getLocator() {
				return new JavaLocator(method);
			}

			@Override
			public String getName() {
				return method.getName();
			}

			@Override
			public EntityListenerType getType() {
				return type;
			}
		};
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public List<CallbackMetadata> getCallbacks() {
		return this.callbacks;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getClassName() {
		return this.className;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public AbstractLocator getLocator() {
		return this.locator;
	}

	/**
	 * Handles the callbacks.
	 * 
	 * @param clazz
	 * 
	 * @since 2.0.0
	 * @param annotationsParsed
	 */
	private void handleCallbacks(Class<?> clazz, Set<Class<? extends Annotation>> annotationsParsed) {
		for (final Method method : clazz.getMethods()) {
			if (method.getDeclaringClass() != clazz) {
				continue;
			}

			if (method.getAnnotation(PrePersist.class) != null) {
				if (annotationsParsed != null) {
					annotationsParsed.add(PrePersist.class);
				}

				this.callbacks.add(this.createCallback(method, EntityListenerType.PRE_PERSIST));
			}

			if (method.getAnnotation(PreUpdate.class) != null) {
				if (annotationsParsed != null) {
					annotationsParsed.add(PreUpdate.class);
				}

				this.callbacks.add(this.createCallback(method, EntityListenerType.PRE_UPDATE));
			}

			if (method.getAnnotation(PreRemove.class) != null) {
				if (annotationsParsed != null) {
					annotationsParsed.add(PreRemove.class);
				}

				this.callbacks.add(this.createCallback(method, EntityListenerType.PRE_REMOVE));
			}

			if (method.getAnnotation(PostLoad.class) != null) {
				if (annotationsParsed != null) {
					annotationsParsed.add(PostLoad.class);
				}

				this.callbacks.add(this.createCallback(method, EntityListenerType.POST_LOAD));
			}

			if (method.getAnnotation(PostPersist.class) != null) {
				if (annotationsParsed != null) {
					annotationsParsed.add(PostPersist.class);
				}

				this.callbacks.add(this.createCallback(method, EntityListenerType.POST_PERSIST));
			}

			if (method.getAnnotation(PostRemove.class) != null) {
				if (annotationsParsed != null) {
					annotationsParsed.add(PostRemove.class);
				}

				this.callbacks.add(this.createCallback(method, EntityListenerType.POST_REMOVE));
			}

			if (method.getAnnotation(PostUpdate.class) != null) {
				if (annotationsParsed != null) {
					annotationsParsed.add(PostUpdate.class);
				}

				this.callbacks.add(this.createCallback(method, EntityListenerType.POST_UPDATE));
			}
		}
	}

	/**
	 * Returns if the listeners is the entity itself.
	 * 
	 * @return true if the listeners is the entity itself, false otherwise
	 * 
	 * @since 2.0.0
	 */
	public boolean self() {
		return this.self;
	}
}
