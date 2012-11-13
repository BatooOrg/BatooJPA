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
package org.batoo.jpa.parser;

import java.util.Map;

import javax.persistence.SharedCacheMode;
import javax.sql.DataSource;

import org.batoo.jpa.parser.impl.metadata.MetadataImpl;
import org.batoo.jpa.parser.persistence.Persistence.PersistenceUnit;
import org.batoo.jpa.parser.persistence.PersistenceUnitCachingType;

import com.google.common.collect.Maps;

/**
 * The main entry point to parse the persistence units for Android platforms.
 * 
 * @author hceylan
 * @since $version
 */
public class AndroidPersistenceParserImpl implements PersistenceParser {

	private final ClassLoader classloader;
	private final MetadataImpl metadata;
	private final PersistenceUnit persistenceUnit;
	private final Map<String, Object> properties = Maps.newHashMap();

	/**
	 * @param properties
	 *            the list of properties
	 * @param classes
	 *            the array of classes
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public AndroidPersistenceParserImpl(Map<String, String> properties, String[] classes) {
		super();

		this.classloader = Thread.currentThread().getContextClassLoader();

		// initialize the persistence unit
		this.persistenceUnit = new PersistenceUnit();
		for (final String clazz : classes) {
			this.persistenceUnit.getClazzs().add(clazz);
		}

		this.properties.putAll(properties);

		this.metadata = new MetadataImpl(this.persistenceUnit.getClazzs());
		this.metadata.parse(this.classloader);
	}

	/**
	 * Returns the classloader of the PersistenceParser.
	 * 
	 * @return the classloader of the PersistenceParser
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Override
	public ClassLoader getClassloader() {
		return this.classloader;
	}

	/**
	 * Returns the JTA datasource JNDI name.
	 * 
	 * @return the JTA datasource JNDI name
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Override
	public DataSource getJtaDataSource() {
		return null;
	}

	/**
	 * Returns the metadata of the parser.
	 * 
	 * @return the metadata of the parser
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Override
	public MetadataImpl getMetadata() {
		return this.metadata;
	}

	/**
	 * Returns the non-JTA datasource JNDI name.
	 * 
	 * @return the non-JTA datasource JNDI name
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Override
	public DataSource getNonJtaDataSource() {
		return null;
	}

	/**
	 * Returns the properties of the persistence unit.
	 * 
	 * @return the properties of the persistence unit
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Override
	public Map<String, Object> getProperties() {
		return this.properties;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getProvider() {
		return null;
	}

	/**
	 * Returns the specification of how the provider must use a second-level cache for the persistence unit.
	 * 
	 * @return the second-level cache mode that must be used by the provider for the persistence unit
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Override
	public SharedCacheMode getSharedCacheMode() {
		final PersistenceUnitCachingType cacheMode = this.persistenceUnit.getSharedCacheMode();

		return cacheMode == null ? SharedCacheMode.NONE : SharedCacheMode.valueOf(cacheMode.name());
	}

	/**
	 * Returns if the persistence unit has validators
	 * 
	 * @return true if the persistence unit has validators, false otherwise
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Override
	public boolean hasValidators() {
		return false;
	}
}
