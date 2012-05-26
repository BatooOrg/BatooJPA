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
package org.batoo.jpa.core.impl.manager.jdbc;

import org.batoo.jpa.core.impl.manager.model.attribute.AttributeImpl;
import org.batoo.jpa.core.jdbc.adapter.JdbcAdaptor;
import org.batoo.jpa.parser.metadata.ColumnMetadata;

/**
 * Column to persist basic attributes of the entity.
 * 
 * @author hceylan
 * @since $version
 */
public class PhysicalColumn extends AbstractColumn {

	private final AttributeImpl<?, ?> attribute;
	private AbstractTable table;

	/**
	 * @param jdbcAdaptor
	 *            the JDBC adaptor
	 * @param attribute
	 *            the attribute
	 * @param sqlType
	 *            the SQL type
	 * @param metadata
	 *            the metadata
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public PhysicalColumn(JdbcAdaptor jdbcAdaptor, AttributeImpl<?, ?> attribute, int sqlType, ColumnMetadata metadata) {
		super(jdbcAdaptor, attribute.getName(), sqlType, metadata);

		this.attribute = attribute;
	}

	/**
	 * Sets the table of the column.
	 * 
	 * @param table
	 *            the owning table
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void setTable(AbstractTable table) {
		this.table = table;

		this.table.addColumn(this);
	}
}
