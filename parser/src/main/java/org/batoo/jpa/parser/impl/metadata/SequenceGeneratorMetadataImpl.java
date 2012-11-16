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

import javax.persistence.SequenceGenerator;

import org.batoo.jpa.parser.impl.AbstractLocator;
import org.batoo.jpa.parser.metadata.SequenceGeneratorMetadata;

/**
 * Implementation of {@link SequenceGeneratorMetadata}.
 * 
 * @author hceylan
 * @since 2.0.0
 */
public class SequenceGeneratorMetadataImpl implements SequenceGeneratorMetadata {

	private final AbstractLocator locator;
	private final String catalog;
	private final String schema;
	private final String name;
	private final String sequenceName;
	private final int initialValue;
	private final int allocationSize;

	/**
	 * @param locator
	 *            the java locator
	 * @param annotation
	 *            the annotation
	 * 
	 * @since 2.0.0
	 * @author hceylan
	 */
	public SequenceGeneratorMetadataImpl(AbstractLocator locator, SequenceGenerator annotation) {
		super();

		this.locator = locator;
		this.catalog = annotation.catalog();
		this.schema = annotation.schema();
		this.name = annotation.name();
		this.sequenceName = annotation.sequenceName();
		this.initialValue = annotation.initialValue();
		this.allocationSize = annotation.allocationSize();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public int getAllocationSize() {
		return this.allocationSize;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getCatalog() {
		return this.catalog;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public int getInitialValue() {
		return this.initialValue;
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
	public String getSchema() {
		return this.schema;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getSequenceName() {
		return this.sequenceName;
	}
}
