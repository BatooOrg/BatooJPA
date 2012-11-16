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
package org.batoo.jpa.parser.impl.orm;

import java.util.List;
import java.util.Map;

import org.batoo.jpa.parser.metadata.PrimaryKeyJoinColumnMetadata;
import org.batoo.jpa.parser.metadata.TableMetadata;
import org.batoo.jpa.parser.metadata.UniqueConstraintMetadata;

import com.google.common.collect.Lists;

/**
 * Base Element for <code>table</code>, <code>join-table</code> and <code>secondary-table</code> elements.
 * 
 * @author hceylan
 * @since 2.0.0
 */
public class SecondaryTableElement extends ParentElement implements TableMetadata {

	private String catalog;
	private String schema;
	private String name;
	private final List<UniqueConstraintMetadata> uniqueConstraints = Lists.newArrayList();
	private final List<PrimaryKeyJoinColumnMetadata> primaryKeyJoinColumns = Lists.newArrayList();

	/**
	 * @param parent
	 *            the parent element factory
	 * @param attributes
	 *            the attributes
	 * 
	 * @since 2.0.0
	 * @author hceylan
	 */
	public SecondaryTableElement(ParentElement parent, Map<String, String> attributes) {
		super(parent, attributes, //
			ElementConstants.ELEMENT_UNIQUE_CONSTRAINT, //
			ElementConstants.ELEMENT_PRIMARY_KEY_JOIN_COLUMN);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	protected void generate() {
		this.catalog = this.getAttribute(ElementConstants.ATTR_CATALOG, ElementConstants.EMPTY);
		this.schema = this.getAttribute(ElementConstants.ATTR_SCHEMA, ElementConstants.EMPTY);
		this.name = this.getAttribute(ElementConstants.ATTR_NAME, ElementConstants.EMPTY);
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
	public List<UniqueConstraintMetadata> getUniqueConstraints() {
		return this.uniqueConstraints;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	protected void handleChild(Element child) {
		if (child instanceof UniqueConstraintsElement) {
			this.uniqueConstraints.add((UniqueConstraintMetadata) child);
		}

		if (child instanceof PrimaryKeyJoinColumnElement) {
			this.primaryKeyJoinColumns.add((PrimaryKeyJoinColumnMetadata) child);
		}
	}
}
