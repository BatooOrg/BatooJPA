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
import javax.persistence.PersistenceException;
import javax.persistence.spi.PersistenceProvider;
import javax.persistence.spi.PersistenceUnitInfo;
import javax.persistence.spi.ProviderUtil;

import org.batoo.jpa.common.BatooException;
import org.batoo.jpa.common.log.BLogger;
import org.batoo.jpa.common.log.BLoggerFactory;
import org.batoo.jpa.core.impl.manager.EntityManagerFactoryImpl;
import org.batoo.jpa.core.impl.manager.PersistenceUtilImpl;
import org.batoo.jpa.parser.MappingException;
import org.batoo.jpa.parser.PersistenceParser;

/**
 * Implementation of {@link PersistenceProvider}.
 * 
 * @author hceylan
 * @since $version
 */
public class BatooPersistenceProvider implements PersistenceProvider {

	private static final BLogger LOG = BLoggerFactory.getLogger(BatooPersistenceProvider.class);

	private static final String PROVIDER_NAME = "org.batoo.jpa.core.BatooPersistenceProvider";

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
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public EntityManagerFactoryImpl createContainerEntityManagerFactory(PersistenceUnitInfo info, Map map) {
		try {
			final PersistenceParser parser = new org.batoo.jpa.parser.PersistenceParserImpl(info, map);

			return parser.getJtaDatasource() != null ? //
				new org.batoo.jpa.core.impl.manager.JtaEntityManagerFactoryImpl(info.getPersistenceUnitName(), parser) : new EntityManagerFactoryImpl(
					info.getPersistenceUnitName(), parser);
		}
		catch (final PersistenceException e) {
			throw e;
		}
		catch (final MappingException e) {
			throw e;
		}
		catch (final BatooException e) {
			throw e;
		}
		catch (final Exception e) {
			throw new PersistenceException("Unable to build the entity manager factory for persistence unit: " + info.getPersistenceUnitName(), e);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public EntityManagerFactory createEntityManagerFactory(String emName, Map map) {
		String provider;

		try {
			// create the persistence parser
			final PersistenceParser parser = new org.batoo.jpa.parser.PersistenceParserImpl(emName, map);

			provider = parser.getProvider();
			if ((provider == null) || BatooPersistenceProvider.PROVIDER_NAME.equals(provider)) {
				// finally, create the entity manager factory
				return new EntityManagerFactoryImpl(emName, parser);
			}
		}
		catch (final Exception e) {
			if ((e instanceof PersistenceException) || (e instanceof MappingException) || (e instanceof BatooException)) {
				throw (RuntimeException) e;
			}

			throw new PersistenceException("Unable to build entity manager factory for persistence unit " + emName, e);
		}

		try {
			// try to delegate
			final Class<?> providerClass = Class.forName(provider);
			final PersistenceProvider persistenceProvider = (PersistenceProvider) providerClass.newInstance();

			return persistenceProvider.createEntityManagerFactory(emName, map);
		}
		catch (final ClassNotFoundException e) {
			throw new PersistenceException("Persistence provider " + provider + " is not found for persistence unit " + emName);
		}
		catch (final Exception e) {
			if (e instanceof RuntimeException) {
				throw (RuntimeException) e;
			}

			throw new RuntimeException(e);
		}
	}

	/**
	 * Creates a persistence unit without persistence.xml file. Suitable for non-standard platforms such as Android.
	 * 
	 * @param emName
	 *            the name of the persistence unit
	 * @param map
	 *            the properties
	 * @param classes
	 *            the list of classes
	 * @return the entity manager factory
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public EntityManagerFactory createEntityManagerFactory(String emName, Map<String, String> map, String[] classes) {
		try {
			// create the persistence parser
			final PersistenceParser parser = new org.batoo.jpa.parser.AndroidPersistenceParserImpl(map, classes);

			// finally, create the entity manager factory
			return new EntityManagerFactoryImpl(emName, parser);
		}
		catch (final Exception e) {
			if ((e instanceof PersistenceException) || (e instanceof MappingException) || (e instanceof BatooException)) {
				throw (RuntimeException) e;
			}

			BatooPersistenceProvider.LOG.info(e, "Unable to build persistence unit: " + emName);

			return null;
		}
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
