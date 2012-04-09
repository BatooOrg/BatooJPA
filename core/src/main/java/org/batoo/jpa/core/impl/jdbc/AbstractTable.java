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

import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.UniqueConstraint;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.lang.StringUtils;
import org.batoo.jpa.core.MappingException;
import org.batoo.jpa.core.impl.types.EntityTypeImpl;
import org.batoo.jpa.core.jdbc.DDLMode;
import org.batoo.jpa.core.jdbc.IdType;
import org.batoo.jpa.core.jdbc.Table;
import org.batoo.jpa.core.jdbc.adapter.JDBCAdapter;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Abstract base class for physical tables.
 * 
 * @author hceylan
 * @since $version
 */
public abstract class AbstractTable implements Table {

	protected final EntityTypeImpl<?> owner;
	protected final JDBCAdapter jdbcAdapter;

	private final String name;
	private final String schema;
	private final UniqueConstraint[] uniqueConstraints;

	protected final List<PhysicalColumn> primaryKeys = Lists.newArrayList();
	protected final List<PhysicalColumn> columns = Lists.newArrayList();
	private final List<ForeignKey> foreignKeys = Lists.newArrayList();

	private final Map<String, PhysicalColumn> columnNames = Maps.newHashMap();

	private int h;
	private String insertSql;
	private String deleteSql;
	private String updateSql;
	protected final List<PhysicalColumn> insertColumns = Lists.newArrayList();

	/**
	 * @param owner
	 *            the owner entity
	 * @param schema
	 *            the name of the schema
	 * @param name
	 *            the name of the table
	 * @param uniqueConstraints
	 *            the unique constraints
	 * @param jdbcAdapter
	 *            the JDBC adapter
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public AbstractTable(EntityTypeImpl<?> owner, String schema, String name, UniqueConstraint[] uniqueConstraints, JDBCAdapter jdbcAdapter) {
		super();

		this.owner = owner;
		this.jdbcAdapter = jdbcAdapter;

		this.schema = this.jdbcAdapter.escape(schema);
		this.name = this.jdbcAdapter.escape(name);
		this.uniqueConstraints = uniqueConstraints;
	}

	/**
	 * Adds a new mapping to the table
	 * 
	 * @param column
	 *            the mapping
	 * @throws MappingException
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void addColumn(PhysicalColumn column) throws MappingException {
		if (this.columnNames.keySet().contains(column.getPhysicalName())) {
			final PhysicalColumn other = this.columnNames.get(column.getPhysicalName());
			throw new MappingException("Duplicate column on entity " + this.owner.getJavaType().getCanonicalName() + ", "
				+ other.getMapping().getPathAsString() + " - " + column.getMapping().getPathAsString());
		}

		this.columns.add(column);
		this.columnNames.put(column.getPhysicalName(), column);

		if (column.isId()) {
			this.primaryKeys.add(column);
		}
	}

	/**
	 * @param foreignKey
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void addForeignKey(ForeignKey foreignKey) {
		this.foreignKeys.add(foreignKey);
	}

	/**
	 * Returns the sql to create the table.
	 * 
	 * @param jdbcAdapter
	 *            the jdbc adapter to use
	 * @return the sql to create the table
	 * 
	 * @since $version
	 * @author hceylan
	 * @param tableDefinition
	 */
	private String createCreateTableStatement(JDBCAdapter jdbcAdapter) {
		final List<String> ddlColumns = Lists.newArrayList();

		for (final PhysicalColumn column : this.columns) {
			ddlColumns.add(jdbcAdapter.createColumnDDL(column));
		}

		return jdbcAdapter.createCreateTableStatement(this, ddlColumns);
	}

	/**
	 * Performs the DDL operation on the table.
	 * 
	 * @param jdbcAdapter
	 *            the JDBC adapter to use
	 * @param datasource
	 *            the datasource to use
	 * @param ddlMode
	 *            the DDL mode
	 * @param schemas
	 *            the schema cache
	 * @throws SQLException
	 * 
	 * @since $version
	 * @author hceylan
	 * @param firstPass
	 */
	public void ddl(JDBCAdapter jdbcAdapter, DataSourceImpl datasource, DDLMode ddlMode, Set<String> schemas, boolean firstPass)
		throws SQLException {
		if (firstPass) { // Create table
			final QueryRunner runner = new QueryRunner(datasource);
			// Sort columns to look nice
			this.sortColumns();

			this.jdbcAdapter.dropAndCreateSchemaIfNecessary(jdbcAdapter, datasource, schemas, ddlMode, this.schema);

			runner.update(this.createCreateTableStatement(jdbcAdapter));
		}

		if (!firstPass) { // create foreign key
			for (final ForeignKey foreignKey : this.foreignKeys) {
				synchronized (this.jdbcAdapter) {
					this.jdbcAdapter.createForeignKey(datasource, foreignKey);
				}
			}
		}
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
		final AbstractTable other = (AbstractTable) obj;
		if (this.name == null) {
			if (other.name != null) {
				return false;
			}
		}
		else if (!this.name.equals(other.name)) {
			return false;
		}
		if (this.schema == null) {
			if (other.schema != null) {
				return false;
			}
		}
		else if (!this.schema.equals(other.schema)) {
			return false;
		}
		return true;
	}

	/**
	 * Returns the columns.
	 * 
	 * @return the columns
	 * @since $version
	 */
	public List<PhysicalColumn> getColumns() {
		return this.columns;
	}

	/**
	 * Returns the delete statement for the table.
	 * 
	 * @return the delete statement
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public synchronized String getDeleteSql() {
		if (this.deleteSql == null) {
			// Prepare predicates in the form of "COL = ?"
			final List<String> predicates = Lists.transform(this.primaryKeys, new Function<PhysicalColumn, String>() {

				@Override
				public String apply(PhysicalColumn input) {
					return input.getPhysicalName() + " = ?";
				}
			});

			// Join predicates COL = ? [ AND COL = ?]*
			final String whereStr = Joiner.on(" AND ").join(predicates);

			// DELETE FROM SCHEMA.TABLE
			// WHERE COL = ? [ AND COL = ?]*
			this.deleteSql = "DELETE FROM " + this.getQualifiedName() //
				+ "\nWHERE " //
				+ whereStr;
		}

		return this.deleteSql;
	}

	/**
	 * Returns the foreignKeys.
	 * 
	 * @return the foreignKeys
	 * @since $version
	 */
	public List<ForeignKey> getForeignKeys() {
		return this.foreignKeys;
	}

	/**
	 * Returns the insert statement for the table.
	 * 
	 * @return the insert statement
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public synchronized String getInsertSql() {
		if (this.insertSql == null) {
			// Filter out the identity columns
			final Collection<PhysicalColumn> filteredColumns = Collections2.filter(this.columns, new Predicate<PhysicalColumn>() {

				@Override
				public boolean apply(PhysicalColumn input) {
					return input.getIdType() != IdType.IDENTITY;
				}
			});

			// prepare the names tuple in the form of "COLNAME [, COLNAME]*"
			final Collection<String> columnNames = Collections2.transform(filteredColumns, new Function<PhysicalColumn, String>() {

				@Override
				public String apply(PhysicalColumn input) {
					AbstractTable.this.insertColumns.add(input);

					return input.getPhysicalName();
				}
			});

			// prepare the parameters in the form of "? [, ?]*"
			final Collection<String> parameters = Collections2.transform(filteredColumns, new Function<PhysicalColumn, String>() {

				@Override
				public String apply(PhysicalColumn input) {
					return "?";
				}
			});

			final String columnNamesStr = Joiner.on(", ").join(columnNames);
			final String parametersStr = Joiner.on(", ").join(parameters);

			// INSERT INTO SCHEMA.TABLE
			// (COL [, COL]*)
			// VALUES (PARAM [, PARAM]*)
			this.insertSql = "INSERT INTO " + this.getQualifiedName() //
				+ "\n(" + columnNamesStr + ")"//
				+ "\nVALUES (" + parametersStr + ")";
		}

		return this.insertSql;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public JDBCAdapter getJdbcAdapter() {
		return this.jdbcAdapter;
	}

	/**
	 * Returns the name.
	 * 
	 * @return the name
	 * @since $version
	 */
	@Override
	public String getName() {
		return this.name;
	}

	/**
	 * Returns the owner.
	 * 
	 * @return the owner
	 * @since $version
	 */
	public EntityTypeImpl<?> getOwner() {
		return this.owner;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Collection<String> getPrimaryKeyColumns() {
		return Collections2.transform(this.primaryKeys, new Function<PhysicalColumn, String>() {

			@Override
			public String apply(PhysicalColumn input) {
				return input.getPhysicalName();
			}
		});
	}

	/**
	 * Returns the primaryKeys.
	 * 
	 * @return the primaryKeys
	 * @since $version
	 */
	public List<PhysicalColumn> getPrimaryKeys() {
		return this.primaryKeys;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getQualifiedName() {
		if (StringUtils.isNotBlank(this.schema)) {
			return this.schema + "." + this.name;
		}

		return this.name;
	}

	/**
	 * Returns the schema.
	 * 
	 * @return the schema
	 * @since $version
	 */
	@Override
	public String getSchema() {
		return this.schema;
	}

	/**
	 * Returns the uniqueConstraints.
	 * 
	 * @return the uniqueConstraints
	 * @since $version
	 */
	public final UniqueConstraint[] getUniqueConstraints() {
		return this.uniqueConstraints;
	}

	/**
	 * Returns the update statement for the table.
	 * 
	 * @return the update statement
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public synchronized String getUpdateSql() {
		if (this.updateSql == null) {
			// Filter out the identity columns
			final Collection<PhysicalColumn> filteredColumns = Collections2.filter(this.columns, new Predicate<PhysicalColumn>() {

				@Override
				public boolean apply(PhysicalColumn input) {
					return !input.isId();
				}
			});

			// prepare the names tuple in the form of "COLNAME [, COLNAME]*"
			final Collection<String> columnNames = Collections2.transform(filteredColumns, new Function<PhysicalColumn, String>() {

				@Override
				public String apply(PhysicalColumn input) {
					return input.getPhysicalName();
				}
			});

			// prepare the parameters in the form of "? [, ?]*"
			final Collection<String> parameters = Collections2.transform(filteredColumns, new Function<PhysicalColumn, String>() {

				@Override
				public String apply(PhysicalColumn input) {
					return "?";
				}
			});

			final String columnNamesStr = Joiner.on(", ").join(columnNames);
			final String parametersStr = Joiner.on(", ").join(parameters);

			// UPDATE SCHEMA.TABLE
			// (COL [, COL]*)
			// VALUES (PARAM [, PARAM]*)
			this.updateSql = "UPDATE " + this.getQualifiedName() //
				+ "\n(" + columnNamesStr + ")"//
				+ "\nVALUES (" + parametersStr + ")";
		}

		return this.updateSql;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public int hashCode() {
		if (this.h != 0) {
			return this.h;
		}

		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((this.name == null) ? 0 : this.name.hashCode());
		this.h = (prime * result) + ((this.schema == null) ? 0 : this.schema.hashCode());

		return this.h;
	}

	/**
	 * Sorts the columns of the table
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private void sortColumns() {
		final Comparator<PhysicalColumn> c = new Comparator<PhysicalColumn>() {

			@Override
			public int compare(PhysicalColumn o1, PhysicalColumn o2) {
				if (o1.isId() && !o2.isId()) {
					return -1;
				}

				if (o2.isId() && !o1.isId()) {
					return 1;
				}

				return o1.getName().compareToIgnoreCase(o2.getName());
			}
		};

		Collections.sort(this.primaryKeys, c);
		Collections.sort(this.columns, c);
	}

}
