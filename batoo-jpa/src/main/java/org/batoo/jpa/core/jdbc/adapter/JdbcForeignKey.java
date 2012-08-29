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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import org.batoo.jpa.core.impl.jdbc.ForeignKey;
import org.batoo.jpa.core.impl.jdbc.JoinColumn;

import com.google.common.collect.Maps;

/**
 * The metadata for a foreign of an existing table.
 * 
 * @author hceylan
 * @since $version
 */
public class JdbcForeignKey {

	private static final String FK_NAME = "FK_NAME";
	private static final String PKTABLE_NAME = "PKTABLE_NAME";

	private static final String FKCOLUMN_NAME = "FKCOLUMN_NAME";
	private static final String PKCOLUMN_NAME = "PKCOLUMN_NAME";

	private final String name;
	private final String refTable;

	private final Map<String, String> columns = Maps.newHashMap();

	/**
	 * @param metadata
	 *            the foreign key metadata
	 * @throws SQLException
	 *             thrown in case of an SQL error.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public JdbcForeignKey(ResultSet metadata) throws SQLException {
		super();

		this.name = metadata.getString(JdbcForeignKey.FK_NAME);
		this.refTable = metadata.getString(JdbcForeignKey.PKTABLE_NAME);
	}

	/**
	 * Adds a column to the foreign key.
	 * 
	 * @param metadata
	 *            the column metadata
	 * @throws SQLException
	 *             thrown in case of an SQL error.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void addColumn(ResultSet metadata) throws SQLException {
		final String columnName = metadata.getString(JdbcForeignKey.FKCOLUMN_NAME);
		final String referencedColumnName = metadata.getString(JdbcForeignKey.PKCOLUMN_NAME);

		this.columns.put(columnName.toUpperCase(), referencedColumnName);
	}

	/**
	 * Returns the name of the JdbcForeignKey. foreign key
	 * 
	 * @return the name of the foreign key
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Returns the refTable of the foreign key.
	 * 
	 * @return the refTable of the foreign key
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public String getRefTable() {
		return this.refTable;
	}

	/**
	 * Checks if the metadata matches the foreign key definition.
	 * 
	 * @param foreignKey
	 *            the foreign key definition
	 * @return true if it matches, false otherwise
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public boolean matches(ForeignKey foreignKey) {
		// referenced table name must match
		if (!this.refTable.equalsIgnoreCase(foreignKey.getReferencedTableName())) {
			return false;
		}

		// number of columns must match
		if (this.columns.size() != foreignKey.getJoinColumns().size()) {
			return false;
		}

		// each column must match
		for (final JoinColumn joinColumn : foreignKey.getJoinColumns()) {
			final String referencedColumnName = this.columns.get(joinColumn.getName().toUpperCase());

			if ((referencedColumnName == null) || !referencedColumnName.equalsIgnoreCase(joinColumn.getReferencedColumnName())) {
				return false;
			}
		}

		// keys match!
		return true;
	}
}
