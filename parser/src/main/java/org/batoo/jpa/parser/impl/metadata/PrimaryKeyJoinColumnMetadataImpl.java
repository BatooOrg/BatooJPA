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

import javax.persistence.PrimaryKeyJoinColumn;

import org.batoo.jpa.parser.impl.AbstractLocator;
import org.batoo.jpa.parser.metadata.PrimaryKeyJoinColumnMetadata;
import org.batoo.jpa.parser.metadata.UniqueConstraintMetadata;

/**
 * Implementation of {@link UniqueConstraintMetadata}.
 * 
 * @author hceylan
 * @since 2.0.0
 */
public class PrimaryKeyJoinColumnMetadataImpl implements PrimaryKeyJoinColumnMetadata {

	private final AbstractLocator locator;
	private final String name;
	private final String columnDefinition;
	private final String referencedColumnName;

	/**
	 * @param locator
	 *            the locator
	 * @param annotation
	 *            the annotation
	 * 
	 * @since 2.0.0
	 */
	public PrimaryKeyJoinColumnMetadataImpl(AbstractLocator locator, PrimaryKeyJoinColumn annotation) {
		super();

		this.locator = locator;
		this.name = annotation.name();
		this.columnDefinition = annotation.columnDefinition();
		this.referencedColumnName = annotation.referencedColumnName();

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
	public String getReferencedColumnName() {
		return this.referencedColumnName;
	}

}
