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

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.FetchType;

import org.batoo.jpa.annotations.FetchStrategyType;
import org.batoo.jpa.parser.metadata.AssociationMetadata;

/**
 * The common definition of association attributes.
 * 
 * @author hceylan
 * @since 2.0.0
 */
public interface AssociationAttributeMetadata extends AttributeMetadata, AssociationMetadata {

	/**
	 * Returns the cascades of the association attribute.
	 * 
	 * @return the cascades of the association attribute
	 * 
	 * @since 2.0.0
	 * @author hceylan
	 */
	Set<CascadeType> getCascades();

	/**
	 * Returns the fetching strategy for eager joins.
	 * 
	 * @return the fetching strategy for eager joins
	 * 
	 * @author asimarslan
	 * @since 2.0.0
	 */
	FetchStrategyType getFetchStrategy();

	/**
	 * Returns the fetch type of the association attribute.
	 * 
	 * @return the fetch type of the association attribute
	 * 
	 * @since 2.0.0
	 * @author hceylan
	 */
	FetchType getFetchType();

	/**
	 * Returns the max fetch depth for eager joins.
	 * 
	 * @return the max fetch depth for eager joins
	 * 
	 * @author asimarslan
	 * @since 2.0.0
	 */
	int getMaxFetchDepth();

	/**
	 * Returns the name of the target entity of the association attribute.
	 * 
	 * @return the name of the target entity of the association attribute
	 * 
	 * @since 2.0.0
	 * @author hceylan
	 */
	String getTargetEntity();
}
