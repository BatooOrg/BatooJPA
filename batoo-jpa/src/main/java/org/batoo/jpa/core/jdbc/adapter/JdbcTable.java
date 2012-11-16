/*
 * Copyright (c) 2012 - Batoo Software ve Consultancy Ltd.
 * 
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */
package org.batoo.jpa.core.jdbc.adapter;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.apache.commons.dbutils.DbUtils;
import org.batoo.common.log.BLogger;
import org.batoo.common.log.BLoggerFactory;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * The metadata for an existing table in the database.
 * 
 * @author hceylan
 * @since $version
 */
public class JdbcTable {

	// Table constants
	private static final String TABLE_NAME = "TABLE_NAME";
	private static final String TABLE_SCHEM = "TABLE_SCHEM";
	private static final String TABLE_CAT = "TABLE_CAT";

	// Primary Constants
	private static final String COLUMN_NAME = "COLUMN_NAME";

	// Foreign Key Constants
	private static final String FK_NAME = "FK_NAME";

	// Index constants
	private static final String INDEX_NAME = "INDEX_NAME";

	private static final BLogger LOG = BLoggerFactory.getLogger(JdbcTable.class);

	private final String catalog;
	private final String schema;
	private final String name;
	private final String pkName;

	private final Map<String, JdbcColumn> columns = Maps.newHashMap();
	private final Map<String, JdbcForeignKey> foreignKeys = Maps.newHashMap();
	private final Map<String, JdbcIndex> indexes = Maps.newHashMap();
	private final Set<String> pkColumns = Sets.newHashSet();

	/**
	 * @param dbMetadata
	 *            the JDBC database metadata
	 * @param metadata
	 *            the table metadata obtained from the JDBC database metadata
	 * @throws SQLException
	 *             thrown in case of an SQL error.
	 * 
	 * @since $version
	 */
	public JdbcTable(DatabaseMetaData dbMetadata, ResultSet metadata) throws SQLException {
		super();

		this.catalog = metadata.getString(JdbcTable.TABLE_CAT);
		this.schema = metadata.getString(JdbcTable.TABLE_SCHEM);
		this.name = metadata.getString(JdbcTable.TABLE_NAME);
		this.pkName = this.readPrimaryKeyColumn(dbMetadata);

		this.readColumns(dbMetadata);
		this.readIndexes(dbMetadata);
		this.readForeignKeys(dbMetadata, metadata);
	}

	/**
	 * Returns the column metadata for the column with the name.
	 * 
	 * @param name
	 *            the name of the column
	 * @return the column metadta or null
	 * 
	 * @since $version
	 */
	public JdbcColumn getColumn(String name) {
		return this.columns.get(name.toUpperCase());
	}

	/**
	 * Returns the foreign key with then name.
	 * 
	 * @param name
	 *            the name of the foreign key
	 * @return the foreign key or null if there is no existing foreign key with the name
	 * 
	 * @since $version
	 */
	public JdbcForeignKey getForeignKey(String name) {
		return this.foreignKeys.get(name.toUpperCase());
	}

	/**
	 * Returns the foreign keys of the table.
	 * 
	 * @return the foreign keys of the table
	 * 
	 * @since $version
	 */
	public Collection<JdbcForeignKey> getForeignKeys() {
		return this.foreignKeys.values();
	}

	/**
	 * Returns the index with then name.
	 * 
	 * @param name
	 *            the name of the index
	 * @return the index or null if there is no existing index with the name
	 * 
	 * @since $version
	 */
	public JdbcIndex getIndex(String name) {
		return this.indexes.get(name.toUpperCase());
	}

	/**
	 * Returns the name of the table.
	 * 
	 * @return the name of the table
	 * 
	 * @since $version
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Returns the primary key name of the table.
	 * 
	 * @return the primary key name of the table
	 * 
	 * @since $version
	 */
	public String getPkName() {
		return this.pkName;
	}

	/**
	 * Returns the schema of the table.
	 * 
	 * @return the schema of the table
	 * 
	 * @since $version
	 */
	public String getSchema() {
		return this.schema;
	}

	/**
	 * Logs the list of columns that do now exist in the persistence unit yet not nullable.
	 * 
	 * @param columns
	 *            the set of columns
	 * 
	 * @since $version
	 */
	public void logNotNullExtraColumns(Collection<String> columns) {
		final Set<String> nonNullColumns = Sets.newHashSet();

		final Set<String> columns2 = Sets.newHashSet();
		for (final String columnName : columns) {
			columns2.add(columnName.toUpperCase());
		}

		for (final JdbcColumn column : this.columns.values()) {
			if (!column.isNullable() && !columns2.contains(column.getName().toUpperCase())) {
				nonNullColumns.add(column.getName());
			}
		}

		if (!nonNullColumns.isEmpty()) {
			JdbcTable.LOG.warn("Table {0} has non null columns that are not referenced by the persistence unit {1}", this.name, nonNullColumns);
		}
	}

	private void readColumns(DatabaseMetaData dbMetadata) throws SQLException {
		ResultSet rs = null;
		try {
			rs = dbMetadata.getColumns(this.catalog, this.schema, this.name, "%");
			while (rs.next()) {
				final JdbcColumn jdbcColumn = new JdbcColumn(rs);
				this.columns.put(jdbcColumn.getName().toUpperCase(), jdbcColumn);
			}
		}
		finally {
			DbUtils.closeQuietly(rs);
		}
	}

	private void readForeignKeys(DatabaseMetaData dbMetadata, ResultSet metadata) throws SQLException {
		ResultSet rs = null;
		try {
			rs = dbMetadata.getImportedKeys(this.catalog, this.schema, this.name);
			while (rs.next()) {
				final String name = rs.getString(JdbcTable.FK_NAME);
				JdbcForeignKey foreignKey = this.getForeignKey(name);
				if (foreignKey == null) {
					foreignKey = new JdbcForeignKey(rs);
					this.foreignKeys.put(rs.getString(JdbcTable.FK_NAME).toUpperCase(), foreignKey);
				}

				foreignKey.addColumn(rs);
			}
		}
		finally {
			DbUtils.closeQuietly(rs);
		}
	}

	private void readIndexes(DatabaseMetaData dbMetadata) throws SQLException {
		ResultSet rs = null;
		try {
			rs = dbMetadata.getIndexInfo(this.catalog, this.schema, this.name, false, true);
			while (rs.next()) {
				final String name = rs.getString(JdbcTable.INDEX_NAME);

				if (name == null) {
					continue;
				}

				JdbcIndex index = this.getIndex(name);
				if (index == null) {
					index = new JdbcIndex(name);
					this.indexes.put(name.toUpperCase(), index);
				}

				index.addColumn(rs.getString(JdbcTable.COLUMN_NAME));
			}
		}
		finally {
			DbUtils.closeQuietly(rs);
		}
	}

	private String readPrimaryKeyColumn(DatabaseMetaData dbMetadata) throws SQLException {
		String pkName = null;

		ResultSet rs = null;
		try {
			rs = dbMetadata.getPrimaryKeys(this.catalog, this.schema, this.name);
			while (rs.next()) {
				pkName = rs.getString("PK_NAME");
				this.pkColumns.add(rs.getString(JdbcTable.COLUMN_NAME).toUpperCase());
			}
		}
		finally {
			DbUtils.closeQuietly(rs);
		}

		return pkName;
	}

	/**
	 * Returns if primary key drop is required.
	 * 
	 * @param pkColumnNames
	 *            the set of required primary keys
	 * @return true if primary key drop is required, false otherwise
	 * 
	 * @since $version
	 */
	public boolean requiresPkDrop(Set<String> pkColumnNames) {
		if (this.pkColumns.isEmpty()) {
			return false;
		}

		final Set<String> pkColumnNames2 = Sets.newHashSet();
		for (final String columnName : pkColumnNames) {
			pkColumnNames2.add(columnName.toUpperCase());
		}

		return !pkColumnNames2.equals(this.pkColumns);
	}
}
