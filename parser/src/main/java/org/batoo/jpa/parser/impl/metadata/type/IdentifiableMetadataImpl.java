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
package org.batoo.jpa.parser.impl.metadata.type;

import java.util.Collections;
import java.util.List;

import javax.persistence.EntityListeners;
import javax.persistence.ExcludeDefaultListeners;
import javax.persistence.ExcludeSuperclassListeners;
import javax.persistence.IdClass;

import org.apache.commons.lang.StringUtils;
import org.batoo.jpa.parser.impl.metadata.EntityListenerMetadataImpl;
import org.batoo.jpa.parser.metadata.CallbackMetadata;
import org.batoo.jpa.parser.metadata.EntityListenerMetadata;
import org.batoo.jpa.parser.metadata.type.IdentifiableTypeMetadata;

import com.google.common.collect.Lists;

/**
 * Implementation {@link IdentifiableTypeMetadata}.
 * 
 * @author hceylan
 * @since $version
 */
public abstract class IdentifiableMetadataImpl extends ManagedTypeMetadatImpl implements IdentifiableTypeMetadata {

	private final String idClass;
	private final List<EntityListenerMetadata> listeners = Lists.newArrayList();
	private final EntityListenerMetadata selfListener;
	private final boolean excludeSuperclassListeners;
	private final boolean excludeDefaultListeners;

	/**
	 * @param clazz
	 *            the represented class
	 * @param metadata
	 *            the metadata
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public IdentifiableMetadataImpl(Class<?> clazz, IdentifiableTypeMetadata metadata) {
		super(clazz, metadata);

		this.idClass = this.handleIdClass(metadata);
		this.excludeDefaultListeners = this.handleDefaultListeners(metadata);
		this.excludeSuperclassListeners = this.handleSuperclassListeners(metadata);

		this.handleEntityListeners(metadata);
		this.selfListener = this.handleCallbacks(metadata);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean excludeDefaultListeners() {
		return this.excludeDefaultListeners;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean excludeSuperclassListeners() {
		return this.excludeSuperclassListeners;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public List<CallbackMetadata> getCallbacks() {
		if (this.selfListener != null) {
			return this.selfListener.getCallbacks();
		}
		else {
			return Collections.emptyList();
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public final String getIdClass() {
		return this.idClass;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public List<EntityListenerMetadata> getListeners() {
		return this.listeners;
	}

	/**
	 * Handles the callbacks.
	 * 
	 * @param metadata
	 *            the metadata
	 * 
	 * @since $version
	 * @author hceylan
	 * @return
	 */
	private EntityListenerMetadata handleCallbacks(IdentifiableTypeMetadata metadata) {
		if ((metadata != null) && (metadata.getCallbacks().size() > 0)) {
			return new EntityListenerMetadataImpl(metadata.getLocator(), metadata, this.getClassName());
		}
		else {
			return new EntityListenerMetadataImpl(this.getLocator(), this.getClazz(), this.getAnnotationsParsed());
		}
	}

	/**
	 * Handles the default listeners exclusion.
	 * 
	 * @param metadata
	 *            the metadata
	 * @return if default listeners excluded
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private boolean handleDefaultListeners(IdentifiableTypeMetadata metadata) {
		if ((metadata != null) && metadata.excludeDefaultListeners()) {
			return true;
		}

		final ExcludeDefaultListeners annotation = this.getClazz().getAnnotation(ExcludeDefaultListeners.class);
		if (annotation != null) {
			this.getAnnotationsParsed().add(ExcludeDefaultListeners.class);

			return true;
		}

		return false;
	}

	/**
	 * Handles the entity listeners.
	 * 
	 * @param metadata
	 *            the metadata
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private void handleEntityListeners(IdentifiableTypeMetadata metadata) {
		if ((metadata != null) && (metadata.getListeners().size() > 0)) {
			for (final EntityListenerMetadata listener : metadata.getListeners()) {
				new EntityListenerMetadataImpl(metadata.getLocator(), listener);
			}

			this.listeners.addAll(metadata.getListeners());
		}
		else {
			final EntityListeners entityListeners = this.getClazz().getAnnotation(EntityListeners.class);
			if (entityListeners != null) {
				this.getAnnotationsParsed().add(EntityListeners.class);

				for (final Class<?> clazz : entityListeners.value()) {
					this.listeners.add(new EntityListenerMetadataImpl(this.getLocator(), clazz));
				}
			}
		}
	}

	/**
	 * Handles the id class definition of the entity.
	 * 
	 * @param metadata
	 *            the metadata
	 * @param parsed
	 *            the set of annotations parsed
	 * @return the id class attribute
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private String handleIdClass(IdentifiableTypeMetadata metadata) {
		if ((metadata != null) && StringUtils.isNotBlank(metadata.getIdClass())) {
			return metadata.getIdClass();
		}

		final IdClass idClass = this.getClazz().getAnnotation(IdClass.class);
		if (idClass != null) {
			this.getAnnotationsParsed().add(IdClass.class);

			return idClass.value().getName();
		}

		return null;
	}

	/**
	 * Handles the super class listeners exclusion.
	 * 
	 * @param metadata
	 *            the metadata
	 * @return if default listeners excluded
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private boolean handleSuperclassListeners(IdentifiableTypeMetadata metadata) {
		if ((metadata != null) && metadata.excludeSuperclassListeners()) {
			return true;
		}

		final ExcludeSuperclassListeners annotation = this.getClazz().getAnnotation(ExcludeSuperclassListeners.class);
		if (annotation != null) {
			this.getAnnotationsParsed().add(ExcludeSuperclassListeners.class);

			return true;
		}

		return false;
	}
}
