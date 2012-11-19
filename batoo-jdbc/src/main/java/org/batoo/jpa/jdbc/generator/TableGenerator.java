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
package org.batoo.jpa.jdbc.generator;

import org.apache.commons.lang.StringUtils;
import org.batoo.jpa.jdbc.AbstractGenerator;
import org.batoo.jpa.parser.metadata.TableGeneratorMetadata;

import com.google.common.base.Joiner;

/**
 * Table based generator.
 * 
 * @author hceylan
 * @since 2.0.0
 */
public class TableGenerator extends AbstractGenerator {

	private static final String DEFAULT_PK_COLUMN_NAME = "NAME";
	private static final String DEFAULT_TABLE_NAME = "BATOO_ID";
	private static final String DEFAULT_VALUE_COLUMN_NAME = "NEXT_ID";

	private final String pkColumnName;
	private final String pkColumnValue;
	private final String table;
	private final String valueColumnName;

	/**
	 * @param metadata
	 *            the metadata
	 * 
	 * @since 2.0.0
	 */
	public TableGenerator(TableGeneratorMetadata metadata) {
		super(metadata);

		this.table = (metadata != null) && StringUtils.isNotBlank(metadata.getTable()) ? metadata.getTable() : TableGenerator.DEFAULT_TABLE_NAME;

		this.pkColumnName = (metadata != null) && StringUtils.isNotBlank(metadata.getPkColumnName()) ? metadata.getPkColumnName()
			: TableGenerator.DEFAULT_PK_COLUMN_NAME;

		this.valueColumnName = (metadata != null) && StringUtils.isNotBlank(metadata.getValueColumnName()) ? metadata.getValueColumnName()
			: TableGenerator.DEFAULT_VALUE_COLUMN_NAME;

		if (metadata != null) {
			if (StringUtils.isNotBlank(metadata.getPkColumnValue())) {
				this.pkColumnValue = metadata.getPkColumnValue();
			}
			else if (StringUtils.isNotBlank(metadata.getName())) {
				this.pkColumnValue = metadata.getName();
			}
			else {
				this.pkColumnValue = TableGenerator.DEFAULT_TABLE_NAME;
			}
		}
		else {
			this.pkColumnValue = TableGenerator.DEFAULT_TABLE_NAME;
		}
	}

	/**
	 * Returns the pkColumnName of the table generator.
	 * 
	 * @return the pkColumnName of the table generator
	 * 
	 * @since 2.0.0
	 */
	public String getPkColumnName() {
		return this.pkColumnName;
	}

	/**
	 * Returns the pkColumnValue of the table generator.
	 * 
	 * @return the pkColumnValue of the table generator
	 * 
	 * @since 2.0.0
	 */
	public String getPkColumnValue() {
		return this.pkColumnValue;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getQName() {
		return Joiner.on(".").skipNulls().join(this.getSchema(), this.table);
	}

	/**
	 * Returns the table of the table generator.
	 * 
	 * @return the table of the table generator
	 * 
	 * @since 2.0.0
	 */
	public String getTable() {
		return this.table;
	}

	/**
	 * Returns the valueColumnName of the table generator.
	 * 
	 * @return the valueColumnName of the table generator
	 * 
	 * @since 2.0.0
	 */
	public String getValueColumnName() {
		return this.valueColumnName;
	}
}
