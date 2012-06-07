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
import java.util.Map;

import javax.persistence.criteria.JoinType;

import org.batoo.jpa.core.impl.model.attribute.AttributeImpl;
import org.batoo.jpa.core.jdbc.adapter.JdbcAdaptor;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

/**
 * Foreign key definition.
 * 
 * @author hceylan
 * @since $version
 */
public class ForeignKey {

	private final AttributeImpl<?, ?> attribute;
	private final List<JoinColumn> joinColumns;

	private String tableName;
	private AbstractTable table;
	private EntityTable referencedTable;

	/**
	 * @param attribute
	 *            the attribute
	 * @param joinColumns
	 *            the list of join columns
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public ForeignKey(AttributeImpl<?, ?> attribute, List<JoinColumn> joinColumns) {
		super();

		this.attribute = attribute;
		this.joinColumns = joinColumns;
	}

	/**
	 * @param joinType
	 *            the type of the join
	 * @param parentAlias
	 *            the alias of the parent table
	 * @param alias
	 *            the alias of the table
	 * @return the join SQL fragment
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public String createJoin(JoinType joinType, String parentAlias, String alias) {
		final List<String> parts = Lists.newArrayList();

		for (final JoinColumn joinColumn : this.joinColumns) {
			final String part = parentAlias + "." + joinColumn.getReferencedColumn().getName() + " = " + alias + "." + joinColumn.getName();
			parts.add(part);
		}

		final String join = Joiner.on(" AND ").join(parts);

		// append the join part
		switch (joinType) {
			case INNER:
				return "INNER JOIN " + this.table.getName() + " AS " + alias + " ON " + join;
			case LEFT:
				return "LEFT JOIN " + this.table.getName() + " AS " + alias + " ON " + join;
			default:
				return "RIGHT JOIN " + this.table.getName() + " AS " + alias + " ON " + join;
		}
	}

	/**
	 * Returns the list of join columns of the foreign key.
	 * 
	 * @return the list of join columns of the foreign key.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public List<JoinColumn> getJoinColumns() {
		return this.joinColumns;
	}

	/**
	 * Returns the referenced table of the foreign key.
	 * 
	 * @return the referenced table of the foreign key
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public EntityTable getReferencedTable() {
		return this.referencedTable;
	}

	/**
	 * Returns the table of the ForeignKey.
	 * 
	 * @return the table of the ForeignKey
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public AbstractTable getTable() {
		return this.table;
	}

	/**
	 * Returns the name of the table name of the foreign key.
	 * 
	 * @return the name of the table name of the foreign key
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public String getTableName() {
		return this.tableName;
	}

	/**
	 * Links the foreign key
	 * 
	 * @param jdbcAdaptor
	 *            the JDBC Adaptor
	 * @param referencedTable
	 *            the referenced table
	 * 
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void link(JdbcAdaptor jdbcAdaptor, EntityTable referencedTable) {
		this.referencedTable = referencedTable;

		final Map<String, PkColumn> pkColumns = referencedTable.getPkColumns();

		// single primary key
		if (pkColumns.size() == 1) {
			final PkColumn pkColumn = pkColumns.values().iterator().next();

			// no definition for the join column
			if (this.joinColumns.size() == 0) {
				// create the join column
				this.joinColumns.add(new JoinColumn(jdbcAdaptor, this.attribute, pkColumn));
				this.tableName = "";
			}
		}
	}

	/**
	 * Sets the table of the foreign key.
	 * 
	 * @param table
	 *            the table
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void setTable(AbstractTable table) {
		this.table = table;

		for (final JoinColumn joinColumn : this.joinColumns) {
			joinColumn.setTable(table);
		}

		this.table.addForeignKey(this);
	}
}
