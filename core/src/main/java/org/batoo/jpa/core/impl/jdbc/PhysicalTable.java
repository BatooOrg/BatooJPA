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

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.UniqueConstraint;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.lang.StringUtils;
import org.batoo.jpa.core.MappingException;
import org.batoo.jpa.core.impl.instance.ManagedInstance;
import org.batoo.jpa.core.impl.mapping.TableTemplate;
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

/**
 * Template of a single table.
 * 
 * @author hceylan
 * @since $version
 */
public class PhysicalTable implements Table {

	private final EntityTypeImpl<?> owner;
	private final JDBCAdapter jdbcAdapter;

	private final String schema;
	private final String name;
	private final String physicalName;
	private final String physicalSchema;

	private final boolean primary;

	private final UniqueConstraint[] uniqueConstraints;
	private final PrimaryKeyJoinColumn[] primaryKeyJoinColumns;

	private PhysicalColumn identityColumn;
	private final List<PhysicalColumn> primaryKeys = Lists.newArrayList();
	private final List<PhysicalColumn> columns = Lists.newArrayList();
	private final List<ForeignKey> foreignKeys = Lists.newArrayList();

	private String insertSql;
	private String deleteSql;
	private String updateSql;

	private final List<PhysicalColumn> insertColumns = Lists.newArrayList();

	private int h;

	/**
	 * @param entityTypeImpl
	 *            the owner entity
	 * @param jdbcAdapter
	 *            the JDBC Adapter
	 * @throws MappingException
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public PhysicalTable(EntityTypeImpl<?> entity, JDBCAdapter jdbcAdapter) throws MappingException {
		super();

		this.owner = entity;
		this.jdbcAdapter = jdbcAdapter;

		this.primary = true;
		this.schema = null;
		this.physicalSchema = null;
		this.name = entity.getName();
		this.physicalName = this.jdbcAdapter.escape(entity.getName());
		this.uniqueConstraints = new UniqueConstraint[] {};
		this.primaryKeyJoinColumns = new PrimaryKeyJoinColumn[] {};
	}

	/**
	 * @param entity
	 *            the owner entity
	 * @param template
	 *            the template for the table
	 * @param jdbcAdapter
	 *            the JDBC Adapter
	 * @since $version
	 * @author hceylan
	 */
	public PhysicalTable(EntityTypeImpl<?> entity, TableTemplate template, JDBCAdapter jdbcAdapter) throws MappingException {
		super();

		this.owner = entity;
		this.jdbcAdapter = jdbcAdapter;

		this.primary = this.name == null;
		this.schema = template.getSchema();
		this.physicalSchema = this.jdbcAdapter.escape(this.schema);
		this.name = template.getName() != null ? template.getName() : entity.getName();
		this.physicalName = jdbcAdapter.escape(this.name);
		this.uniqueConstraints = template.getUniqueConstraints();
		this.primaryKeyJoinColumns = template.getPrimaryKeyJoinColumns();
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
		this.columns.add(column);

		if (column.isId()) {
			this.primaryKeys.add(column);
		}

		if (column.getIdType() == IdType.IDENTITY) {
			if (this.identityColumn != null) {
				throw new MappingException("Multiple identity columns: " + this.identityColumn.getName() + ", " + column.getName() + " on "
					+ this.owner.getJavaType().getCanonicalName());
			}

			this.identityColumn = column;
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

			JDBCAdapter.dropAndCreateSchemaIfNecessary(jdbcAdapter, datasource, schemas, ddlMode, this.schema);

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
		final PhysicalTable other = (PhysicalTable) obj;
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
			this.sortColumns();

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
			this.deleteSql = "DELETE FROM " + this.getQualifiedPhysicalName() //
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
			this.sortColumns();

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
					PhysicalTable.this.insertColumns.add(input);

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
			this.insertSql = "INSERT INTO " + this.getQualifiedPhysicalName() //
				+ "\n(" + columnNamesStr + ")"//
				+ "\nVALUES (" + parametersStr + ")";
		}

		return this.insertSql;
	}

	/**
	 * Returns the jdbcAdapter.
	 * 
	 * @return the jdbcAdapter
	 * @since $version
	 */
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
	 * Returns the physicalName.
	 * 
	 * @return the physicalName
	 * @since $version
	 */
	public String getPhysicalName() {
		return this.physicalName;
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
	 * Returns the primaryKeyJoinColumns.
	 * 
	 * @return the primaryKeyJoinColumns
	 * @since $version
	 */
	public PrimaryKeyJoinColumn[] getPrimaryKeyJoinColumns() {
		return this.primaryKeyJoinColumns;
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
	 * Returns the qualified physical name of the table.
	 * 
	 * @return the qualified physical name of the table
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public String getQualifiedPhysicalName() {
		if (StringUtils.isNotBlank(this.physicalSchema)) {
			return this.physicalSchema + "." + this.physicalName;
		}

		return this.physicalName;
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
			this.sortColumns();

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
			this.updateSql = "UPDATE " + this.getQualifiedPhysicalName() //
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
	 * Returns the primary.
	 * 
	 * @return the primary
	 * @since $version
	 */
	public boolean isPrimary() {
		return this.primary;
	}

	/**
	 * Performs inserts to the table for the managed instance.
	 * 
	 * @param connection
	 *            the connection to use
	 * @param managedInstance
	 *            the managed instance to perform insert for
	 * @throws SQLException
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void performInsert(Connection connection, final ManagedInstance<?> managedInstance) throws SQLException {
		final QueryRunner runner = new QueryRunner();

		// Do not inline, generation of the insert sql will initialize the insertColumns!
		final String insertSql = this.getInsertSql();

		final List<Object> params = Lists.transform(this.insertColumns, new Function<PhysicalColumn, Object>() {
			@Override
			public Object apply(PhysicalColumn input) {
				return input.getPhysicalValue(managedInstance.getSession(), managedInstance.getInstance());
			}
		});

		runner.update(connection, insertSql, params.toArray());

		if (this.identityColumn != null) {
			final String selectLastIdSql = this.jdbcAdapter.getSelectLastIdentitySql();
			final Number id = runner.query(connection, selectLastIdSql, new SingleValueHandler<Number>());

			this.identityColumn.getMapping().setValue(managedInstance.getInstance(), id);
		}
	}

	/**
	 * Sorts the columns of the table
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void sortColumns() {
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

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String toString() {
		final String name = this.schema != null ? this.schema + "." + this.name : this.name;

		final String columns = Joiner.on(", ").join(Lists.transform(this.columns, new Function<PhysicalColumn, String>() {

			@Override
			public String apply(PhysicalColumn input) {
				final StringBuffer out = new StringBuffer();
				out.append(input.isId() ? "ID [" : "COL [");

				out.append("name=");
				out.append(input.getName());
				out.append(", type=");
				out.append(input.getSqlType());

				out.append("]");
				return out.toString();
			}
		}));

		return "PhysicalTable [owner=" + this.owner.getName() + ", name=" + name + ", primary=" + this.primary + ", columns=[" + columns
			+ "]]";
	}

}
