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
package org.batoo.jpa.core.impl.metamodel;

import org.apache.commons.lang.StringUtils;
import org.batoo.jpa.parser.metadata.TableGeneratorMetadata;

/**
 * Table based generator.
 * 
 * @author hceylan
 * @since $version
 */
public class TableGenerator extends AbstractGenerator {

	private static final String DEFAULT_PK_COLUMN_NAME = "name";
	private static final String DEFAULT_PK_COLUMN_VALUE = "default";
	private static final String DEFAULT_TABLE_NAME = "batoo_id";
	private static final String DEFAULT_VALUE_COLUMN_NAME = "next_id";

	private final String pkColumnName;
	private final String pkColumnValue;
	private final String table;
	private final String valueColumnName;

	/**
	 * @param metadata
	 *            the metadata
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public TableGenerator(TableGeneratorMetadata metadata) {
		super(metadata);

		this.table = StringUtils.isBlank(metadata.getTable()) ? //
			TableGenerator.DEFAULT_TABLE_NAME : metadata.getTable();

		this.pkColumnName = StringUtils.isBlank(metadata.getPkColumnName()) ? //
			TableGenerator.DEFAULT_PK_COLUMN_NAME : metadata.getPkColumnName();

		this.pkColumnValue = StringUtils.isBlank(metadata.getPkColumnValue()) ? //
			TableGenerator.DEFAULT_PK_COLUMN_VALUE : metadata.getPkColumnValue();

		this.valueColumnName = StringUtils.isBlank(metadata.getValueColumnName()) ? //
			TableGenerator.DEFAULT_VALUE_COLUMN_NAME : metadata.getValueColumnName();
	}

	/**
	 * Returns the pkColumnName of the table generator.
	 * 
	 * @return the pkColumnName of the table generator
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public String getPkColumnName() {
		return this.pkColumnName;
	}

	/**
	 * Returns the pkColumnValue of the table generator.
	 * 
	 * @return the pkColumnValue of the table generator
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public String getPkColumnValue() {
		return this.pkColumnValue;
	}

	/**
	 * Returns the table of the table generator.
	 * 
	 * @return the table of the table generator
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public String getTable() {
		return this.table;
	}

	/**
	 * Returns the valueColumnName of the table generator.
	 * 
	 * @return the valueColumnName of the table generator
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public String getValueColumnName() {
		return this.valueColumnName;
	}

}
