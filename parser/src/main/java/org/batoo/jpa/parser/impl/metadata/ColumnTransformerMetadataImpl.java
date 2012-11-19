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

import org.batoo.jpa.annotations.ColumnTransformer;
import org.batoo.jpa.parser.AbstractLocator;
import org.batoo.jpa.parser.metadata.ColumnTransformerMetadata;

/**
 * 
 * @author asimarslan
 * @since 2.0.0
 */
public class ColumnTransformerMetadataImpl implements ColumnTransformerMetadata {

	private final String read;
	private final String write;
	private final AbstractLocator locator;

	/**
	 * @param locator
	 *            the locator
	 * @param columnTransformer
	 *            the column transformer
	 * 
	 * @since 2.0.0
	 */
	public ColumnTransformerMetadataImpl(AbstractLocator locator, ColumnTransformer columnTransformer) {
		super();

		this.locator = locator;
		this.read = columnTransformer.read();
		this.write = columnTransformer.write();
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
	public String getRead() {
		return this.read;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getWrite() {
		return this.write;
	}
}
