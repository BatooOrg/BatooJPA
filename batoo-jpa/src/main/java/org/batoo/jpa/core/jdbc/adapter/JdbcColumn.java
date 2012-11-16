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

/**
 * The metadata for a column of an existing table.
 * 
 * @author hceylan
 * @since 2.0.0
 */
public class JdbcColumn {

	private static final String COLUMN_NAME = "COLUMN_NAME";
	private static final String COLUMN_SIZE = "COLUMN_SIZE";
	private static final String IS_NULLABLE = "IS_NULLABLE";
	private static final String DECIMAL_DIGITS = "DECIMAL_DIGITS";
	private static final String DATA_TYPE = "DATA_TYPE";

	private final String name;
	private final int size;
	private final int decimalDigits;
	private final boolean nullable;
	private final int type;

	/**
	 * @param metadata
	 *            the column metadata
	 * @throws SQLException
	 *             thrown in case of an SQL error.
	 * 
	 * @since 2.0.0
	 */
	public JdbcColumn(ResultSet metadata) throws SQLException {
		super();

		this.name = metadata.getString(JdbcColumn.COLUMN_NAME);
		this.size = metadata.getInt(JdbcColumn.COLUMN_SIZE);
		this.decimalDigits = metadata.getInt(JdbcColumn.DECIMAL_DIGITS);
		this.type = metadata.getInt(JdbcColumn.DATA_TYPE);
		this.nullable = !metadata.getString(JdbcColumn.IS_NULLABLE).equals("NO");
	}

	/**
	 * Returns the decimalDigits of the column.
	 * 
	 * @return the decimalDigits of the column
	 * 
	 * @since 2.0.0
	 */
	public int getDecimalDigits() {
		return this.decimalDigits;
	}

	/**
	 * Returns the name of the column.
	 * 
	 * @return the name of the column
	 * 
	 * @since 2.0.0
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Returns the size of the column.
	 * 
	 * @return the size of the column
	 * 
	 * @since 2.0.0
	 */
	public int getSize() {
		return this.size;
	}

	/**
	 * Returns the type of the column.
	 * 
	 * @return the type of the column
	 * 
	 * @since 2.0.0
	 */
	public int getType() {
		return this.type;
	}

	/**
	 * Returns if the column is nullable.
	 * 
	 * @return true if the column is nullable, false otherwise
	 * 
	 * @since 2.0.0
	 */
	public boolean isNullable() {
		return this.nullable;
	}
}
