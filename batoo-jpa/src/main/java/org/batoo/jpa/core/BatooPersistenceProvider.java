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
package org.batoo.jpa.core;

import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.spi.PersistenceProvider;
import javax.persistence.spi.PersistenceUnitInfo;
import javax.persistence.spi.ProviderUtil;

import org.batoo.jpa.core.impl.manager.EntityManagerFactoryImpl;
import org.batoo.jpa.core.impl.manager.PersistenceUtilImpl;
import org.batoo.jpa.parser.PersistenceParser;

/**
 * Implementation of {@link PersistenceProvider}.
 * 
 * @author hceylan
 * @since $version
 */
public class BatooPersistenceProvider implements PersistenceProvider {

	private final ProviderUtil providerUtil;

	/**
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public BatooPersistenceProvider() {
		super();

		this.providerUtil = new PersistenceUtilImpl();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("rawtypes")
	public EntityManagerFactoryImpl createContainerEntityManagerFactory(PersistenceUnitInfo info, Map map) {
		final PersistenceParser parser = new PersistenceParser(info, null);

		return new EntityManagerFactoryImpl(info.getPersistenceUnitName(), parser);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("rawtypes")
	public EntityManagerFactory createEntityManagerFactory(String emName, Map map) {
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
		return this.providerUtil;
	}
}
