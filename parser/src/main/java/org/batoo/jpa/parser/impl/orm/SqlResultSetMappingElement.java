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

import org.batoo.jpa.parser.metadata.ColumnResultMetadata;
import org.batoo.jpa.parser.metadata.EntityResultMetadata;
import org.batoo.jpa.parser.metadata.SqlResultSetMappingMetadata;

import com.google.common.collect.Lists;

/**
 * Element for <code>sql-result-set-mapping</code>
 * 
 * @author asimarslan
 * @since 2.0.1
 */
public class SqlResultSetMappingElement extends ParentElement implements SqlResultSetMappingMetadata {

	private String name;

	private final List<ColumnResultMetadata> columns = Lists.newArrayList();
	private final List<EntityResultMetadata> entities = Lists.newArrayList();

	/**
	 * 
	 * @param parent
	 * @param attributes
	 * @param expectedChildElements
	 * @since 2.0.1
	 */
	public SqlResultSetMappingElement(ParentElement parent, Map<String, String> attributes, String... expectedChildElements) {
		super(parent, attributes, ParentElement.join(expectedChildElements, //
			ElementConstants.ELEMENT_COLUMN_RESULT,//
			ElementConstants.ELEMENT_ENTITY_RESULT));
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	protected void generate() {
		this.name = this.getAttribute(ElementConstants.ATTR_NAME, ElementConstants.EMPTY);
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
	public String getName() {
		return this.name;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	protected void handleChild(Element child) {
		if (child instanceof ColumnResultElement) {
			this.columns.add((ColumnResultMetadata) child);
		}
		if (child instanceof EntityResultElement) {
			this.entities.add((EntityResultMetadata) child);
		}
	}

}
