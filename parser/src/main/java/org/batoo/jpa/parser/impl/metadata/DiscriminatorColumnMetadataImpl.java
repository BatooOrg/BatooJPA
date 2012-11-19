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

import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;

import org.batoo.jpa.parser.AbstractLocator;
import org.batoo.jpa.parser.metadata.DiscriminatorColumnMetadata;

/**
 * Implementation of {@link DiscriminatorColumnMetadata}.
 * 
 * @author hceylan
 * @since 2.0.0
 */
public class DiscriminatorColumnMetadataImpl implements DiscriminatorColumnMetadata {

	private final DiscriminatorType discriminatorType;
	private final String columnDefinition;
	private final int length;
	private final String name;
	private final AbstractLocator locator;

	/**
	 * @param locator
	 *            the locator
	 * @param discriminatorColumn
	 *            the discriminator column annotation
	 * 
	 * @since 2.0.0
	 */
	public DiscriminatorColumnMetadataImpl(AbstractLocator locator, DiscriminatorColumn discriminatorColumn) {
		super();

		this.locator = locator;
		this.discriminatorType = discriminatorColumn.discriminatorType();
		this.columnDefinition = discriminatorColumn.columnDefinition();
		this.length = discriminatorColumn.length();
		this.name = discriminatorColumn.name();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getColumnDefinition() {
		return this.columnDefinition;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public DiscriminatorType getDiscriminatorType() {
		return this.discriminatorType;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public int getLength() {
		return this.length;
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
