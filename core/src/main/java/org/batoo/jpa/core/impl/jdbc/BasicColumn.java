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

import org.batoo.jpa.core.impl.model.attribute.AttributeImpl;
import org.batoo.jpa.core.jdbc.adapter.JdbcAdaptor;
import org.batoo.jpa.parser.metadata.ColumnMetadata;

/**
 * BasicColumn to persist basic attributes of the entity.
 * 
 * @author hceylan
 * @since $version
 */
public class BasicColumn extends AbstractColumn {

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
	public BasicColumn(JdbcAdaptor jdbcAdaptor, AttributeImpl<?, ?> attribute, int sqlType, ColumnMetadata metadata) {
		super(jdbcAdaptor, //
			attribute.getName(), //
			sqlType, //
			metadata != null ? metadata.getLength() : 255, //
			metadata != null ? metadata.getPrecision() : 0, //
			metadata != null ? metadata.getScale() : 0, //
			metadata);

		this.attribute = attribute;
	}

	/**
	 * Returns the table of the column
	 * 
	 * @return the table
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public AbstractTable getTable() {
		return this.table;
	}

	/**
	 * Returns the value for the column
	 * 
	 * @param instance
	 *            the instance
	 * @return the value
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public Object getValue(Object instance) {
		return this.attribute.get(instance);

		// XXX implement with embeddables
		// for (final AttributeImpl<?, ?> attribute : this.path) {
		//
		// if (value == null) {
		// break;
		// }
		// }
		//
		// return (Y) value;
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

	/**
	 * Sets the value for the instance
	 * 
	 * @param instance
	 *            the instance of which to set value
	 * @param value
	 *            the value to set
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void setValue(Object instance, Object value) {
		this.attribute.set(instance, value);
		// XXX implement with embeddables
		// for (int i = 0; i < this.path.length; i++) {
		// final AttributeImpl<?, ?> attribute = this.path[i];
		//
		// if (i == (this.path.length - 1)) {
		// attribute.set(instance, value);
		// }
		// else {
		// instance = attribute.get(instance);
		// }
		// }
	}
}
