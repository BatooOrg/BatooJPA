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

import javax.persistence.criteria.JoinType;

import org.batoo.jpa.core.impl.model.EntityTypeImpl;
import org.batoo.jpa.core.impl.model.attribute.AttributeImpl;
import org.batoo.jpa.core.impl.model.attribute.IdAttributeImpl;
import org.batoo.jpa.core.jdbc.adapter.JdbcAdaptor;
import org.batoo.jpa.parser.MappingException;
import org.batoo.jpa.parser.metadata.JoinColumnMetadata;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

/**
 * Foreign key definition.
 * 
 * @author hceylan
 * @since $version
 */
public class ForeignKey {

	private final List<JoinColumn> joinColumns = Lists.newArrayList();

	private String tableName;
	private AbstractTable table;

	/**
	 * @param metadata
	 *            the metadata for join column
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public ForeignKey(List<JoinColumnMetadata> metadata) {
		super();

		for (final JoinColumnMetadata columnMetadata : metadata) {
			this.joinColumns.add(new JoinColumn(columnMetadata));
		}
	}

	/**
	 * Creates the join for destination foreign keys.
	 * 
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
	public String createDestinationJoin(JoinType joinType, String parentAlias, String alias) {
		return this.createJoin(joinType, parentAlias, alias, this.joinColumns.get(0).getReferencedTable().getName(), false);
	}

	private String createJoin(JoinType joinType, String parentAlias, String alias, final String tableName, final boolean source) {
		final List<String> parts = Lists.newArrayList();

		for (final JoinColumn joinColumn : this.joinColumns) {
			final String leftColumnName = source ? joinColumn.getReferencedColumnName() : joinColumn.getName();
			final String rightColumnName = source ? joinColumn.getName() : joinColumn.getReferencedColumnName();

			final String part = parentAlias + "." + leftColumnName + " = " + alias + "." + rightColumnName;
			parts.add(part);
		}

		final String join = Joiner.on(" AND ").join(parts);

		// append the join part
		switch (joinType) {
			case INNER:
				return "\tINNER JOIN " + tableName + " AS " + alias + " ON " + join;
			case LEFT:
				return "\tLEFT JOIN " + tableName + " AS " + alias + " ON " + join;
			default:
				return "\tRIGHT JOIN " + tableName + " AS " + alias + " ON " + join;
		}
	}

	/**
	 * Creates the join for source foreign keys.
	 * 
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
	public String createSourceJoin(JoinType joinType, String parentAlias, String alias) {
		return this.createJoin(joinType, parentAlias, alias, this.joinColumns.get(0).getTable().getName(), true);
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
	public AbstractTable getReferencedTable() {
		return this.joinColumns.get(0).getReferencedTable();
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
	 * Links the foreign key
	 * 
	 * @param jdbcAdaptor
	 *            the JDBC Adaptor
	 * @param attribute
	 *            the owner attribute
	 * @param targetEntity
	 *            the target entity
	 * 
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void link(JdbcAdaptor jdbcAdaptor, AttributeImpl<?, ?> attribute, EntityTypeImpl<?> targetEntity) {
		final IdAttributeImpl<?, ?>[] idAttributes = targetEntity.getIdAttributes();

		// single primary key
		if (idAttributes.length == 1) {
			final IdAttributeImpl<?, ?> idAttribute = idAttributes[0];

			// no definition for the join column
			if (this.joinColumns.size() == 0) {
				// create the join column
				this.joinColumns.add(new JoinColumn(jdbcAdaptor, attribute, idAttribute));
			}
			// existing definition for the join column
			else {
				final JoinColumn joinColumn = this.joinColumns.get(0);
				joinColumn.setColumnProperties(jdbcAdaptor, attribute, idAttribute);
			}
		}
		// composite primary key
		else {
			for (final IdAttributeImpl<?, ?> idAttribute : idAttributes) {
				// no definition for the join columns
				if (this.joinColumns.size() == 0) {
					this.joinColumns.add(new JoinColumn(jdbcAdaptor, attribute, idAttribute));
				}
				// existing definition for the join column
				else {
					// locate the corresponding join column
					JoinColumn joinColumn = null;
					for (final JoinColumn column : this.joinColumns) {
						if (idAttribute.getColumn().getName().equals(column.getReferencedColumnName())) {
							joinColumn = column;
							break;
						}
					}

					if (joinColumn == null) {
						throw new MappingException("Join column cannot be located in a composite key target entity");
					}

					joinColumn.setColumnProperties(jdbcAdaptor, attribute, idAttribute);
				}
			}
		}

		if (attribute != null) {
			final AbstractTable table = ((EntityTypeImpl<?>) attribute.getDeclaringType()).getTable(this.tableName);
			if (table == null) {
				throw new MappingException("Table " + this.tableName + " could not be found");
			}

			this.setTable(table);
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
