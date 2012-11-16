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

import org.batoo.jpa.annotations.Index;
import org.batoo.jpa.parser.impl.AbstractLocator;
import org.batoo.jpa.parser.metadata.IndexMetadata;

/**
 * Implementation of {@link IndexMetadata}.
 * 
 * @author hceylan
 * @since 2.0.0
 */
public class IndexMetadataImpl implements IndexMetadata {

	private final AbstractLocator locator;
	private final String[] columnNames;
	private final String name;
	private final String table;

	/**
	 * @param locator
	 *            the java locator
	 * @param annotation
	 *            the annotation
	 * 
	 * @since 2.0.0
	 * @author hceylan
	 */
	public IndexMetadataImpl(AbstractLocator locator, Index annotation) {
		super();

		this.locator = locator;
		this.name = annotation.name();
		this.columnNames = annotation.columns();
		this.table = annotation.table();
	}

	/**
	 * @param locator
	 *            the java locator
	 * @param annotation
	 *            the annotation
	 * @param name
	 *            field column name
	 * 
	 * @since 2.0.0
	 * @author hceylan
	 */
	public IndexMetadataImpl(AbstractLocator locator, Index annotation, String name) {
		super();

		this.locator = locator;
		this.name = annotation.name();
		this.columnNames = new String[] { name };
		this.table = annotation.table();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String[] getColumnNames() {
		return this.columnNames;
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

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getTable() {
		return this.table;
	}
}
