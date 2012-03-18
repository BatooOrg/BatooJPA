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
package org.batoo.jpa.core;

import java.util.Collections;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.spi.PersistenceUnitInfo;
import javax.persistence.spi.ProviderUtil;

import org.batoo.jpa.core.impl.Configuration;
import org.batoo.jpa.core.impl.PersistenceUnitInfoImpl;

import com.google.common.collect.Maps;

/**
 * @author hceylan
 * 
 * @since 0.1
 */
public class PersistenceProvider implements javax.persistence.spi.PersistenceProvider {

	private static final BLogger LOG = BLogger.getLogger(PersistenceProvider.class);

	/**
	 * 
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public PersistenceProvider() {
		super();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public EntityManagerFactory createContainerEntityManagerFactory(PersistenceUnitInfo info, @SuppressWarnings("rawtypes") Map map) {
		map = this.sanitize(map);

		@SuppressWarnings("unchecked")
		final Configuration configuration = new Configuration(info, map);

		try {
			return configuration.buildEntityManagerFactory();
		}
		catch (final MappingException e) {
			throw new IllegalArgumentException("Cannot create entity manager factory", e);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public EntityManagerFactory createEntityManagerFactory(String emName, @SuppressWarnings("rawtypes") Map map) {
		map = this.sanitize(map);

		try {
			final PersistenceUnitInfoImpl info = new PersistenceUnitInfoImpl(emName, map);
			return this.createContainerEntityManagerFactory(info, info.getProperties());
		}
		catch (final BatooException e) {
			PersistenceProvider.LOG.error(e, "Unable to build EntityManagerFactory");
		}

		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public ProviderUtil getProviderUtil() {
		return null;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Map sanitize(Map map) {
		if (map != null) {
			return Collections.unmodifiableMap(map);
		}

		return Maps.newHashMap();
	}

}
