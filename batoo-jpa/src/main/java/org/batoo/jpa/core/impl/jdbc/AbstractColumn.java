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
package org.batoo.jpa.core.impl.jdbc;

import org.batoo.jpa.core.impl.model.mapping.Mapping;
import org.batoo.jpa.parser.impl.AbstractLocator;

/**
 * Abstract base implementation for columns
 * 
 * @author hceylan
 * @since $version
 */
public abstract class AbstractColumn {

	/**
	 * Returns the static definition of the column.
	 * 
	 * @return the static definition of the column
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public abstract String getColumnDefinition();

	/**
	 * Returns the length of the column.
	 * 
	 * @return the length of the column
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public abstract int getLength();

	/**
	 * Returns the locator of the column.
	 * 
	 * @return the locator of the column
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public abstract AbstractLocator getLocator();

	/**
	 * Returns the mapping of the BasicColumn.
	 * 
	 * @return the mapping of the BasicColumn
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public abstract Mapping<?, ?, ?> getMapping();

	/**
	 * Returns the mapping name of the column.
	 * 
	 * @return the mapping name of the column
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public abstract String getMappingName();

	/**
	 * Returns the name of the column.
	 * 
	 * @return the name of the column
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public abstract String getName();

	/**
	 * Returns the precision of the column.
	 * 
	 * @return the precision of the column
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public abstract int getPrecision();

	/**
	 * Returns the scale of the column.
	 * 
	 * @return the scale of the column
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public abstract int getScale();

	/**
	 * Returns the SQL Type of the column.
	 * 
	 * @return the SQL Type of the column
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public abstract int getSqlType();

	/**
	 * Returns the table of the column
	 * 
	 * @return the table
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public abstract AbstractTable getTable();

	/**
	 * Returns the table name of the column.
	 * 
	 * @return the table name of the column
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public abstract String getTableName();

	/**
	 * Returns the value for the column
	 * 
	 * @param instance
	 *            the instance
	 * 
	 * @return the value
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public abstract Object getValue(Object instance);

	/**
	 * Returns the insertable of the column.
	 * 
	 * @return the insertable of the column
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public abstract boolean isInsertable();

	/**
	 * Returns the nullable of the column.
	 * 
	 * @return the nullable of the column
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public abstract boolean isNullable();

	/**
	 * Returns if the column is a primary key column.
	 * 
	 * @return true if the column is a primary key column false otherwise
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public boolean isPrimaryKey(){
		return false;
	}

	/**
	 * Returns the unique of the column.
	 * 
	 * @return the unique of the column
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public abstract boolean isUnique();

	/**
	 * Returns the updatable of the column.
	 * 
	 * @return the updatable of the column
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public abstract boolean isUpdatable();

	/**
	 * Sets the table of the column.
	 * 
	 * @param table
	 *            the owning table
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public abstract void setTable(AbstractTable table);

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
	public abstract void setValue(Object instance, Object value);

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String toString() {
		final String tableName = this.getTable() != null ? this.getTable().getName() : "N/A";
		final String mapping = this.getMapping() != null ? " " + this.getMapping().toString() + " " : "";

		return this.getClass().getSimpleName() + mapping + " [name=" + this.getName() + ", type=" + this.getSqlType() + ", length=" + this.getLength()
			+ ", precision=" + this.getPrecision() + ", scale=" + this.getScale() + ", table=" + tableName + "]";
	}
}
