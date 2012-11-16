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
package org.batoo.jpa.parser.impl.orm;

import java.util.Map;

/**
 * Element for <code>persistence-unit-metadata</code> elements.
 * 
 * @author hceylan
 * @since 2.0.0
 */
public class PersistenceUnitMetadataElement extends ParentElement {

	private boolean xmlMappingMetadataComplete;
	private PersistenceUnitDefaults persistenceUnitDefaults;

	/**
	 * @param parent
	 *            the parent element factory
	 * @param attributes
	 *            the attributes
	 * 
	 * @since 2.0.0
	 */
	public PersistenceUnitMetadataElement(ParentElement parent, Map<String, String> attributes) {
		super(parent, attributes, //
			ElementConstants.ELEMENT_XML_MAPPING_METADATA_COMPLETE, //
			ElementConstants.ELEMENT_PERSISTENCE_UNIT_DEFAULTS);
	}

	/**
	 * Returns the persistence unit defaults.
	 * 
	 * @return the persistence unit defaults
	 * 
	 * @since 2.0.0
	 */
	public PersistenceUnitDefaults getPersistenceUnitDefaults() {
		return this.persistenceUnitDefaults;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	protected void handleChild(Element child) {
		if (child instanceof XmlMappingMetadataCompleteElement) {
			this.xmlMappingMetadataComplete = true;
		}

		if (child instanceof PersistenceUnitDefaults) {
			this.persistenceUnitDefaults = (PersistenceUnitDefaults) child;
		}
	}

	/**
	 * Returns the xmlMappingMetadataComplete.
	 * 
	 * @return the xmlMappingMetadataComplete
	 * 
	 * @since 2.0.0
	 */
	public boolean isXmlMappingMetadataComplete() {
		return this.xmlMappingMetadataComplete;
	}
}
