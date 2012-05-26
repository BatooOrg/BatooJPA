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
package org.batoo.jpa.core.impl.model;

import org.batoo.jpa.parser.metadata.TableGeneratorMetadata;

/**
 * Table based generator.
 * 
 * @author hceylan
 * @since $version
 */
public class TableGenerator extends AbstractGenerator {

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

		this.pkColumnName = metadata.getPkColumnName();
		this.pkColumnValue = metadata.getPkColumnValue();
		this.table = metadata.getTable();
		this.valueColumnName = metadata.getValueColumnName();
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
