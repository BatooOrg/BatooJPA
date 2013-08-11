/*
 * Copyright (c) 2012-2013, Batu Alp Ceylan
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
package org.batoo.jpa.jdbc;

import java.sql.Connection;

import org.batoo.jpa.jdbc.mapping.Mapping;
import org.batoo.jpa.parser.AbstractLocator;

/**
 * 
 *
 * @author hceylan
 * @since 2.0.1
 */
public interface Column {

	/**
	 * Converts the value corresponding to enum, temporal type, number or [cb]lob.
	 * 
	 * @param connection
	 *            the connection
	 * @param value
	 *            the raw value
	 * @return the converted value
	 * 
	 * @since 2.0.0
	 */
	Object convertValue(Connection connection, final Object value);

	/**
	 * Returns the static definition of the column.
	 * 
	 * @return the static definition of the column
	 * 
	 * @since 2.0.0
	 */
	String getColumnDefinition();

	/**
	 * Returns the idType of the column.
	 * 
	 * @return the idType of the column
	 * 
	 * @since 2.0.0
	 */
	IdType getIdType();

	/**
	 * Returns the length of the column.
	 * 
	 * @return the length of the column
	 * 
	 * @since 2.0.0
	 */
	int getLength();

	/**
	 * Returns the locator of the column.
	 * 
	 * @return the locator of the column
	 * 
	 * @since 2.0.0
	 */
	AbstractLocator getLocator();

	/**
	 * Returns the mapping of the BasicColumn.
	 * 
	 * @return the mapping of the BasicColumn
	 * 
	 * @since 2.0.0
	 */
	Mapping<?, ?, ?> getMapping();

	/**
	 * Returns the name of the column.
	 * 
	 * @return the name of the column
	 * 
	 * @since 2.0.0
	 */
	String getName();

	/**
	 * Returns the precision of the column.
	 * 
	 * @return the precision of the column
	 * 
	 * @since 2.0.0
	 */
	int getPrecision();

	/**
	 * Returns the scale of the column.
	 * 
	 * @return the scale of the column
	 * 
	 * @since 2.0.0
	 */
	int getScale();

	/**
	 * Returns the SQL Type of the column.
	 * 
	 * @return the SQL Type of the column
	 * 
	 * @since 2.0.0
	 */
	int getSqlType();

	/**
	 * Returns the table of the column
	 * 
	 * @return the table
	 * 
	 * @since 2.0.0
	 */
	AbstractTable getTable();

	/**
	 * Returns the table name of the column.
	 * 
	 * @return the table name of the column
	 * 
	 * @since 2.0.0
	 */
	String getTableName();

	/**
	 * Returns the value for the column
	 * 
	 * @param connection
	 *            the connection
	 * @param instance
	 *            the instance
	 * 
	 * @return the value
	 * 
	 * @since 2.0.0
	 */
	Object getValue(Connection connection, Object instance);

	/**
	 * Returns the insertable of the column.
	 * 
	 * @return the insertable of the column
	 * 
	 * @since 2.0.0
	 */
	boolean isInsertable();

	/**
	 * Returns if the column is lob type.
	 * 
	 * @return true if the column is lob type, false otherwise
	 * 
	 * @since 2.0.0
	 */
	boolean isLob();

	/**
	 * Returns the nullable of the column.
	 * 
	 * @return the nullable of the column
	 * 
	 * @since 2.0.0
	 */
	boolean isNullable();

	/**
	 * Returns if the column is a primary key column.
	 * 
	 * @return true if the column is a primary key column false otherwise
	 * 
	 * @since 2.0.0
	 */
	boolean isPrimaryKey();

	/**
	 * Returns the unique of the column.
	 * 
	 * @return the unique of the column
	 * 
	 * @since 2.0.0
	 */
	boolean isUnique();

	/**
	 * Returns the updatable of the column.
	 * 
	 * @return the updatable of the column
	 * 
	 * @since 2.0.0
	 */
	boolean isUpdatable();

	/**
	 * 
	 * Sets the column as id column.
	 * 
	 * @since 2.0.0
	 */
	void setId();

	/**
	 * Sets the table of the column.
	 * 
	 * @param table
	 *            the owning table
	 * 
	 * @since 2.0.0
	 */
	void setTable(AbstractTable table);

	/**
	 * Sets the value for the instance
	 * 
	 * @param instance
	 *            the instance of which to set value
	 * @param value
	 *            the value to set
	 * 
	 * @since 2.0.0
	 */
	void setValue(Object instance, Object value);

}
