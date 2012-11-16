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

import org.batoo.jpa.parser.metadata.GeneratedValueMetadata;
import org.batoo.jpa.parser.metadata.SequenceGeneratorMetadata;
import org.batoo.jpa.parser.metadata.TableGeneratorMetadata;

/**
 * The definition of the id attributes.
 * 
 * @author hceylan
 * @since 2.0.0
 */
public interface IdAttributeMetadata extends PhysicalAttributeMetadata {

	/**
	 * Returns the value generation definition of the id attribute.
	 * 
	 * @return the value generation definition of the id attribute
	 * 
	 * @since 2.0.0
	 */
	GeneratedValueMetadata getGeneratedValue();

	/**
	 * Returns the sequence generator definition of the id attribute.
	 * 
	 * @return the sequence generator definition of the id attribute
	 * 
	 * @since 2.0.0
	 */
	SequenceGeneratorMetadata getSequenceGenerator();

	/**
	 * Returns the table generator definition of the id attribute.
	 * 
	 * @return the table generator definition of the id attribute
	 * 
	 * @since 2.0.0
	 */
	TableGeneratorMetadata getTableGenerator();

}
