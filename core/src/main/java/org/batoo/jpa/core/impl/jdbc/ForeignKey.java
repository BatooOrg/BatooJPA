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

import java.util.List;

import org.apache.derby.impl.sql.compile.ResultColumnList.ColumnMapping;

/**
 * Foreign key definition.
 * 
 * @author hceylan
 * @since $version
 */
public class ForeignKey {

	private final EntityTable table;
	private final EntityTable referencedTable;
	private final List<ColumnMapping> columnMappings;

	/**
	 * @param table
	 *            the table that owns the foreign key, join or entity table
	 * @param referencedTable
	 *            the referenced entity table
	 * @param columnMappings
	 *            the column mappings
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public ForeignKey(EntityTable table, EntityTable referencedTable, List<ColumnMapping> columnMappings) {
		super();

		this.table = table;
		this.referencedTable = referencedTable;
		this.columnMappings = columnMappings;

		this.table.addForeignKey(this);
	}

	/**
	 * Returns the columnMappings.
	 * 
	 * @return the columnMappings
	 * @since $version
	 */
	public List<ColumnMapping> getColumnMappings() {
		return this.columnMappings;
	}

	/**
	 * Returns the referencedTable.
	 * 
	 * @return the referencedTable
	 * @since $version
	 */
	public EntityTable getReferencedTable() {
		return this.referencedTable;
	}

	/**
	 * Returns the table.
	 * 
	 * @return the table
	 * @since $version
	 */
	public EntityTable getTable() {
		return this.table;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String toString() {
		return "ForeignKey [table=" + this.table.getEntity().getName() + ":" + this.table.getQName() //
			+ ", referencedTable=" + this.referencedTable.getEntity().getName() + ":" + this.referencedTable.getQName() + //
			", columnMappings=" + this.columnMappings + "]";
	}

}
