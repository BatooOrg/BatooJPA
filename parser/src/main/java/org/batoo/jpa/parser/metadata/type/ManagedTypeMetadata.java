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
package org.batoo.jpa.parser.metadata.type;

import javax.persistence.AccessType;

import org.batoo.jpa.parser.metadata.LocatableMatadata;
import org.batoo.jpa.parser.metadata.attribute.AttributesMetadata;

/**
 * The definition of the managed types.
 * 
 * @author hceylan
 * @since 2.0.0
 */
public interface ManagedTypeMetadata extends LocatableMatadata {

	/**
	 * Returns the access type of the entity.
	 * 
	 * @return the access type of the entity
	 * 
	 * @since 2.0.0
	 */
	AccessType getAccessType();

	/**
	 * Returns the attributes of the entity.
	 * 
	 * @return the attributes of the entity
	 * 
	 * @since 2.0.0
	 */
	AttributesMetadata getAttributes();

	/**
	 * Returns the name of the class of the entity.
	 * 
	 * @return the name of the class of the entity
	 * 
	 * @since 2.0.0
	 */
	String getClassName();

	/**
	 * Returns if the entity's metadata is complete.
	 * 
	 * @return true if the entity's metadata is complete
	 * 
	 * @since 2.0.0
	 */
	boolean isMetadataComplete();
}
