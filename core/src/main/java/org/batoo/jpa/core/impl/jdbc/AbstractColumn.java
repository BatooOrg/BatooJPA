/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.batoo.jpa.core.impl.jdbc;

import org.apache.commons.lang.StringUtils;
import org.batoo.jpa.parser.impl.AbstractLocator;
import org.batoo.jpa.parser.metadata.ColumnMetadata;

/**
 * Abstract base implementation for Primary Key Column, Basic Column and Join Columns.
 * 
 * @author hceylan
 * @since $version
 */
public class AbstractColumn {

	private final int sqlType;
	private final String name;
	private final String columnDefinition;
	private final int length;
	private final AbstractLocator locator;
	private final int precision;
	private final int scale;
	private final String tableName;
	private final boolean nullable;
	private final boolean insertable;
	private final boolean unique;
	private final boolean updatable;

	/**
	 * @param defaultName
	 *            the default name for the column
	 * @param sqlType
	 *            the SQL type
	 * @param metadata
	 *            the metadata
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public AbstractColumn(String defaultName, int sqlType, ColumnMetadata metadata) {
		super();

		this.sqlType = sqlType;

		// if metadata exists, then create the column based on the metadata
		if (metadata != null) {
			this.locator = metadata.getLocator();
			this.tableName = metadata.getTable();
			this.name = StringUtils.isNotBlank(metadata.getName()) ? metadata.getName() : defaultName;
			this.columnDefinition = metadata.getColumnDefinition();
			this.length = metadata.getLength();
			this.precision = metadata.getPrecision();
			this.scale = metadata.getScale();
			this.insertable = metadata.isInsertable();
			this.nullable = metadata.isNullable();
			this.unique = metadata.isUnique();
			this.updatable = metadata.isUpdatable();
		}
		// othewrwise use the defaults
		else {
			this.locator = null;
			this.tableName = null;
			this.name = defaultName;
			this.columnDefinition = null;
			this.length = 255;
			this.precision = 0;
			this.scale = 0;
			this.insertable = true;
			this.nullable = true;
			this.unique = false;
			this.updatable = true;
		}
	}

	/**
	 * Returns the static definition of the column.
	 * 
	 * @return the static definition of the column
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public String getColumnDefinition() {
		return this.columnDefinition;
	}

	/**
	 * Returns the length of the column.
	 * 
	 * @return the length of the column
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public int getLength() {
		return this.length;
	}

	/**
	 * Returns the locator for the column.
	 * 
	 * @return the locator for the column
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public AbstractLocator getLocator() {
		return this.locator;
	}

	/**
	 * Returns the name of the column.
	 * 
	 * @return the name of the column
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Returns the precision of the column.
	 * 
	 * @return the precision of the column
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public int getPrecision() {
		return this.precision;
	}

	/**
	 * Returns the scale of the column.
	 * 
	 * @return the scale of the column
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public int getScale() {
		return this.scale;
	}

	/**
	 * Returns the SQL Type of the column.
	 * 
	 * @return the SQL Type of the column
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public int getSqlType() {
		return this.sqlType;
	}

	/**
	 * Returns the table name of the column.
	 * 
	 * @return the table name of the column
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public String getTableName() {
		return this.tableName;
	}

	/**
	 * Returns the insertable of the column.
	 * 
	 * @return the insertable of the column
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public boolean isInsertable() {
		return this.insertable;
	}

	/**
	 * Returns the nullable of the column.
	 * 
	 * @return the nullable of the column
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public boolean isNullable() {
		return this.nullable;
	}

	/**
	 * Returns the unique of the column.
	 * 
	 * @return the unique of the column
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public boolean isUnique() {
		return this.unique;
	}

	/**
	 * Returns the updatable of the column.
	 * 
	 * @return the updatable of the column
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public boolean isUpdatable() {
		return this.updatable;
	}
}
