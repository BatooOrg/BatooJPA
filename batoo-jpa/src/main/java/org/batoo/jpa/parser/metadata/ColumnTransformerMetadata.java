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
package org.batoo.jpa.parser.metadata;

/**
 * Definition for ColumnTransformer
 * 
 * @author asimarslan
 * @since 2.0.0
 */
public interface ColumnTransformerMetadata extends LocatableMatadata {

	/**
	 * Return the custom read sql expression
	 * 
	 * @return the custom read sql expression
	 * 
	 * @since version
	 */
	String getRead();

	/**
	 * Return the custom write sql expression
	 * 
	 * @return the custom write sql expression
	 * 
	 * @since version
	 */
	String getWrite();
}
