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

import java.util.List;

import javax.persistence.ColumnResult;
import javax.persistence.EntityResult;
import javax.persistence.SqlResultSetMapping;

import org.batoo.jpa.parser.AbstractLocator;
import org.batoo.jpa.parser.metadata.ColumnResultMetadata;
import org.batoo.jpa.parser.metadata.EntityResultMetadata;
import org.batoo.jpa.parser.metadata.SqlResultSetMappingMetadata;

import com.google.common.collect.Lists;

/**
 * Implemention of {@link SqlResultSetMappingMetadata}
 * 
 * @author asimarslan
 * @since $version
 */
public class SqlResultSetMappingMetadataImpl implements SqlResultSetMappingMetadata {

	private final AbstractLocator locator;
	private final String name;

	private final List<ColumnResultMetadata> columns = Lists.newArrayList();
	private final List<EntityResultMetadata> entities = Lists.newArrayList();

	/**
	 * 
	 * @param locator
	 * @param annotation
	 * @since $version
	 */
	public SqlResultSetMappingMetadataImpl(AbstractLocator locator, SqlResultSetMapping annotation) {
		super();

		this.locator = locator;
		this.name = annotation.name();

		for (final ColumnResult columnResult : annotation.columns()) {
			this.columns.add(new ColumnResultMetadataImpl(locator, columnResult));
		}

		for (final EntityResult entityResult : annotation.entities()) {
			this.entities.add(new EntityResultMetadataImpl(locator, entityResult));
		}

	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public List<ColumnResultMetadata> getColumns() {
		return this.columns;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public List<EntityResultMetadata> getEntities() {
		return this.entities;
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
