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

import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.spi.PersistenceProvider;
import javax.persistence.spi.PersistenceUnitInfo;
import javax.persistence.spi.ProviderUtil;

import org.batoo.jpa.core.impl.manager.EntityManagerFactoryImpl;
import org.batoo.jpa.parser.PersistenceParser;

/**
 * Implementation of {@link PersistenceProvider}.
 * 
 * @author hceylan
 * @since $version
 */
public class BatooPersistenceProvider implements PersistenceProvider {

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public EntityManagerFactoryImpl createContainerEntityManagerFactory(PersistenceUnitInfo info, @SuppressWarnings("rawtypes") Map map) {
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public EntityManagerFactory createEntityManagerFactory(String emName, @SuppressWarnings("rawtypes") Map map) {
		// create the persistence parser
		final PersistenceParser parser = new PersistenceParser(emName);

		// finally, create the entity manager factory
		return new EntityManagerFactoryImpl(emName, parser);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public ProviderUtil getProviderUtil() {
		// TODO Auto-generated method stub
		return null;
	}
}
