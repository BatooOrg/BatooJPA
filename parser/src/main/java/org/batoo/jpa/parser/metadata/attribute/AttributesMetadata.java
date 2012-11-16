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
package org.batoo.jpa.parser.metadata.attribute;

import java.util.List;

/**
 * 
 * 
 * @author hceylan
 * @since 2.0.0
 */
public interface AttributesMetadata {

	/**
	 * Returns the basic attributes.
	 * 
	 * @return the basic attributes
	 * 
	 * @since 2.0.0
	 */
	List<BasicAttributeMetadata> getBasics();

	/**
	 * Returns the element collection attributes.
	 * 
	 * @return the element collection attributes
	 * 
	 * @since 2.0.0
	 */
	List<ElementCollectionAttributeMetadata> getElementCollections();

	/**
	 * Returns the embedded id attributes.
	 * 
	 * @return the embedded id attributes
	 * 
	 * @since 2.0.0
	 */
	List<EmbeddedIdAttributeMetadata> getEmbeddedIds();

	/**
	 * Returns the embedded attributes.
	 * 
	 * @return the embedded attributes
	 * 
	 * @since 2.0.0
	 */
	List<EmbeddedAttributeMetadata> getEmbeddeds();

	/**
	 * Returns the id attributes.
	 * 
	 * @return the id attributes
	 * 
	 * @since 2.0.0
	 */
	List<IdAttributeMetadata> getIds();

	/**
	 * Returns the many-to-many attributes.
	 * 
	 * @return the many-to-many attributes
	 * 
	 * @since 2.0.0
	 */
	List<ManyToManyAttributeMetadata> getManyToManies();

	/**
	 * Returns the many-to-one attributes.
	 * 
	 * @return the many-to-one attributes
	 * 
	 * @since 2.0.0
	 */
	List<ManyToOneAttributeMetadata> getManyToOnes();

	/**
	 * Returns the one-to-many attributes.
	 * 
	 * @return the one-to-many attributes
	 * 
	 * @since 2.0.0
	 */
	List<OneToManyAttributeMetadata> getOneToManies();

	/**
	 * Returns the one-to-one attributes.
	 * 
	 * @return the one-to-one attributes
	 * 
	 * @since 2.0.0
	 */
	List<OneToOneAttributeMetadata> getOneToOnes();

	/**
	 * Returns the transient attributes.
	 * 
	 * @return the transient attributes
	 * 
	 * @since 2.0.0
	 */
	List<TransientAttributeMetadata> getTransients();

	/**
	 * Returns the version attributes.
	 * 
	 * @return the version attributes
	 * 
	 * @since 2.0.0
	 */
	List<VersionAttributeMetadata> getVersions();
}
