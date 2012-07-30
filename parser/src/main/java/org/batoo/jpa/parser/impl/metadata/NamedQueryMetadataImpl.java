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
package org.batoo.jpa.parser.impl.metadata;

import java.util.Map;

import javax.persistence.LockModeType;
import javax.persistence.NamedQuery;
import javax.persistence.QueryHint;

import org.batoo.jpa.parser.impl.AbstractLocator;
import org.batoo.jpa.parser.metadata.NamedQueryMetadata;

import com.google.common.collect.Maps;

/**
 * Implementation of {@link NamedQueryMetadata}.
 * 
 * @author hceylan
 * @since $version
 */
public class NamedQueryMetadataImpl implements NamedQueryMetadata {

	private final AbstractLocator locator;
	private final String query;
	private final String name;
	private final Map<String, Object> hints = Maps.newHashMap();
	private final LockModeType lockMode;

	/**
	 * @param locator
	 *            the locator
	 * @param annotation
	 *            the annotation
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public NamedQueryMetadataImpl(AbstractLocator locator, NamedQuery annotation) {
		super();

		this.locator = locator;
		this.name = annotation.name();
		this.query = annotation.query();
		this.lockMode = annotation.lockMode();

		for (final QueryHint hint : annotation.hints()) {
			this.hints.put(hint.name(), hint.value());
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Map<String, Object> getHints() {
		return this.hints;
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
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public LockModeType getLockMode() {
		return this.lockMode;
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
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getQuery() {
		return this.query;
	}
}
