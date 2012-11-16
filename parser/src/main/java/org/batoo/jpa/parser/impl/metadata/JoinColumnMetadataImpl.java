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

import javax.persistence.JoinColumn;

import org.batoo.jpa.parser.impl.AbstractLocator;
import org.batoo.jpa.parser.metadata.JoinColumnMetadata;

/**
 * Implementation of {@link JoinColumnMetadata}.
 * 
 * @author hceylan
 * @since 2.0.0
 */
public class JoinColumnMetadataImpl implements JoinColumnMetadata {

	private final AbstractLocator locator;
	private final String columnDefinition;
	private final boolean insertable;
	private final String name;
	private final boolean nullable;
	private final String referencedColumnName;
	private final String table;
	private final boolean unique;
	private final boolean updatable;

	/**
	 * @param locator
	 *            the locator
	 * @param annotation
	 *            the annotation
	 * 
	 * @since 2.0.0
	 * @author hceylan
	 */
	public JoinColumnMetadataImpl(AbstractLocator locator, JoinColumn annotation) {
		super();

		this.locator = locator;
		this.columnDefinition = annotation.columnDefinition();
		this.insertable = annotation.insertable();
		this.name = annotation.name();
		this.nullable = annotation.nullable();
		this.referencedColumnName = annotation.referencedColumnName();
		this.table = annotation.table();
		this.unique = annotation.unique();
		this.updatable = annotation.updatable();
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

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getTable() {
		return this.table;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean isInsertable() {
		return this.insertable;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean isNullable() {
		return this.nullable;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean isUnique() {
		return this.unique;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean isUpdatable() {
		return this.updatable;
	}
}
