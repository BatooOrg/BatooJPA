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

import java.util.Arrays;

import javax.persistence.TableGenerator;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang.StringUtils;
import org.batoo.jpa.core.jdbc.adapter.JDBCAdapter;

/**
 * Physical Table generator for id generation using tables.
 * 
 * @author hceylan
 * @since $version
 */
public class PhysicalTableGenerator {

	public static final String DEFAULT_NAME = "default";

	private static final String DEFAULT_PK_COLUMN_NAME = "name";
	private static final String DEFAULT_PK_COLUMN_VALUE = "default";
	private static final String DEFAULT_TABLE_NAME = "batoo_id";
	private static final String DEFAULT_VALUE_COLUMN_NAME = "next_id";

	private final String name;
	private final String schema;
	private final String table;
	private final int initialValue;
	private final int allocationSize;

	private final String pkColumnName;
	private final String valueColumnName;
	private final String pkColumnValue;
	private final UniqueConstraint[] uniqueConstraints;

	/**
	 * @param generator
	 * 
	 * @since $version
	 * @author hceylan
	 * @param jdbcAdapter
	 */
	public PhysicalTableGenerator(TableGenerator generator, JDBCAdapter jdbcAdapter) {
		super();

		this.name = generator != null ? generator.name() : DEFAULT_NAME;
		this.schema = generator != null ? jdbcAdapter.escape(generator.schema()) : "";
		this.table = generator != null ? jdbcAdapter.escape(generator.table()) : DEFAULT_TABLE_NAME;
		this.initialValue = generator != null ? generator.initialValue() : 0;
		this.allocationSize = generator != null ? generator.allocationSize() : 50;
		this.pkColumnName = (generator != null) && StringUtils.isNotBlank(generator.pkColumnName()) ? //
			jdbcAdapter.escape(generator.pkColumnName()) : DEFAULT_PK_COLUMN_NAME;
		this.valueColumnName = (generator != null) && StringUtils.isNotBlank(generator.valueColumnName()) ? //
			jdbcAdapter.escape(generator.valueColumnName()) : DEFAULT_VALUE_COLUMN_NAME;
		this.pkColumnValue = (generator != null) && StringUtils.isNotBlank(generator.pkColumnValue()) ? //
			jdbcAdapter.escape(generator.pkColumnValue()) : DEFAULT_PK_COLUMN_VALUE;
		this.uniqueConstraints = generator != null ? generator.uniqueConstraints() : new UniqueConstraint[] {};
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		final PhysicalTableGenerator other = (PhysicalTableGenerator) obj;
		if (this.name == null) {
			if (other.name != null) {
				return false;
			}
		}
		else if (!this.name.equals(other.name)) {
			return false;
		}
		return true;
	}

	/**
	 * Returns the allocationSize.
	 * 
	 * @return the allocationSize
	 * @since $version
	 */
	public int getAllocationSize() {
		return this.allocationSize;
	}

	/**
	 * Returns the initialValue.
	 * 
	 * @return the initialValue
	 * @since $version
	 */
	public int getInitialValue() {
		return this.initialValue;
	}

	/**
	 * Returns the name.
	 * 
	 * @return the name
	 * @since $version
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Returns the pkColumnName.
	 * 
	 * @return the pkColumnName
	 * @since $version
	 */
	public String getPkColumnName() {
		return this.pkColumnName;
	}

	/**
	 * Returns the pkColumnValue.
	 * 
	 * @return the pkColumnValue
	 * @since $version
	 */
	public String getPkColumnValue() {
		return this.pkColumnValue;
	}

	/**
	 * Returns the qualified physical name of the table.
	 * 
	 * @return the qualified physical name of the table
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public String getQualifiedPhysicalName() {
		if (StringUtils.isNotBlank(this.schema)) {
			return this.schema + "." + this.table;
		}

		return this.table;
	}

	/**
	 * Returns the schema.
	 * 
	 * @return the schema
	 * @since $version
	 */
	public String getSchema() {
		return this.schema;
	}

	/**
	 * Returns the table.
	 * 
	 * @return the table
	 * @since $version
	 */
	public String getTable() {
		return this.table;
	}

	/**
	 * Returns the uniqueConstraints.
	 * 
	 * @return the uniqueConstraints
	 * @since $version
	 */
	public UniqueConstraint[] getUniqueConstraints() {
		return this.uniqueConstraints;
	}

	/**
	 * Returns the valueColumnName.
	 * 
	 * @return the valueColumnName
	 * @since $version
	 */
	public String getValueColumnName() {
		return this.valueColumnName;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((this.name == null) ? 0 : this.name.hashCode());
		return result;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String toString() {
		return "PhysicalTableGenerator [name=" + this.name + ", table=" + this.table + ", initialValue=" + this.initialValue
			+ ", allocationSize=" + this.allocationSize + ", pkColumnName=" + this.pkColumnName + ", valueColumnName="
			+ this.valueColumnName + ", pkColumnValue=" + this.pkColumnValue + ", uniqueConstraints="
			+ Arrays.toString(this.uniqueConstraints) + "]";
	}
}
