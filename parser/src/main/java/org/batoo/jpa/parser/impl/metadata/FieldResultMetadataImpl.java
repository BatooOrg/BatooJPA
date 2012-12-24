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
package org.batoo.jpa.parser.impl.metadata;

import javax.persistence.FieldResult;

import org.batoo.jpa.parser.AbstractLocator;
import org.batoo.jpa.parser.metadata.FieldResultMetadata;

/**
 * 
 * @author asimarslan
 * @since $version
 */
public class FieldResultMetadataImpl implements FieldResultMetadata {

	private final AbstractLocator locator;
	private final String name;
	private final String column;

	/**
	 * 
	 * @param locator
	 * @param field
	 * @since $version
	 */
	public FieldResultMetadataImpl(AbstractLocator locator, FieldResult annotation) {
		super();

		this.locator = locator;

		this.column = annotation.column();
		this.name = annotation.name();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getColumn() {
		return this.column;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public AbstractLocator getLocator() {
		return this.locator;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getName() {
		return this.name;
	}

}
