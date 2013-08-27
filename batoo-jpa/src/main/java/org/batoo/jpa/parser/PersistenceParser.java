/*
 * Copyright (c) 2012-2013, Batu Alp Ceylan
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

/**
 * 
 * 
 * @author hceylan
 * @since 2.0.0
 */
public interface PersistenceParser {

	/**
	 * Returns the classloader of the PersistenceParser.
	 * 
	 * @return the classloader of the PersistenceParser
	 * 
	 * @since 2.0.0
	 */
	ClassLoader getClassloader();

	/**
	 * Returns the JTA datasource JNDI name.
	 * 
	 * @return the JTA datasource JNDI name
	 * 
	 * @since 2.0.0
	 */
	DataSource getJtaDataSource();

	/**
	 * Returns the metadata of the parser.
	 * 
	 * @return the metadata of the parser
	 * 
	 * @since 2.0.0
	 */
	MetadataImpl getMetadata();

	/**
	 * Returns the non-JTA datasource JNDI name.
	 * 
	 * @return the non-JTA datasource JNDI name
	 * 
	 * @since 2.0.0
	 */
	DataSource getNonJtaDataSource();

	/**
	 * Returns the properties of the persistence unit.
	 * 
	 * @return the properties of the persistence unit
	 * 
	 * @since 2.0.0
	 */
	Map<String, Object> getProperties();

	/**
	 * Returns the persistence provider.
	 * 
	 * @return the persistence provider
	 * 
	 * @since 2.0.0
	 */
	String getProvider();

	/**
	 * Returns the specification of how the provider must use a second-level cache for the persistence unit.
	 * 
	 * @return the second-level cache mode that must be used by the provider for the persistence unit
	 * 
	 * @since 2.0.0
	 */
	SharedCacheMode getSharedCacheMode();

	/**
	 * Returns if the persistence unit has validators
	 * 
	 * @return true if the persistence unit has validators, false otherwise
	 * 
	 * @since 2.0.0
	 */
	boolean hasValidators();
}
