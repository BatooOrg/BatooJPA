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

import org.batoo.jpa.parser.metadata.TableGeneratorMetadata;
import org.batoo.jpa.parser.metadata.UniqueConstraintMetadata;

import com.google.common.collect.Lists;

/**
 * Element for <code>sequence-generator</code> elements.
 * 
 * @author hceylan
 * @since 2.0.0
 */
public class TableGeneratorElement extends GeneratorElement implements TableGeneratorMetadata {

	private String table;
	private String pkColumnName;
	private String pkColumnValue;
	private String valueColumnName;
	private final List<UniqueConstraintMetadata> uniqueConstraints = Lists.newArrayList();

	/**
	 * @param parent
	 *            the metamodel
	 * @param attributes
	 *            the attributes
	 * 
	 * @since 2.0.0
	 */
	public TableGeneratorElement(ParentElement parent, Map<String, String> attributes) {
		super(parent, attributes, ElementConstants.ELEMENT_UNIQUE_CONSTRAINT);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	protected void generate() {
		super.generate();

		this.table = this.getAttribute(ElementConstants.ATTR_TABLE, ElementConstants.EMPTY);
		this.pkColumnName = this.getAttribute(ElementConstants.ATTR_PK_COLUMN_NAME, ElementConstants.EMPTY);
		this.pkColumnValue = this.getAttribute(ElementConstants.ATTR_PK_COLUMN_VALUE, ElementConstants.EMPTY);
		this.valueColumnName = this.getAttribute(ElementConstants.ATTR_VALUE_COLUMN_NAME, ElementConstants.EMPTY);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getPkColumnName() {
		return this.pkColumnName;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getPkColumnValue() {
		return this.pkColumnValue;
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
	public List<UniqueConstraintMetadata> getUniqueConstraints() {
		return this.uniqueConstraints;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getValueColumnName() {
		return this.valueColumnName;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	protected void handleChild(Element child) {
		if (child instanceof UniqueConstraintsElement) {
			this.uniqueConstraints.add((UniqueConstraintsElement) child);
		}
	}
}
