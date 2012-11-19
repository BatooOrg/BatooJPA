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
package org.batoo.jpa.jdbc;

import java.sql.Connection;

import javax.persistence.EnumType;
import javax.persistence.TemporalType;

import org.batoo.jpa.jdbc.mapping.Mapping;
import org.batoo.jpa.parser.metadata.ColumnMetadata;

/**
 * Columns for map key type attributes.
 * 
 * @author hceylan
 * @since 2.0.0
 */
public class MapKeyColumn extends AbstractColumn {

	private final String columnDefinition;
	private final String name;
	private final AbstractTable table;
	private final boolean insertable;
	private final boolean nullable;
	private final boolean updatable;
	private final int sqlType;
	private final int precision;
	private final int scale;
	private final int length;

	/**
	 * @param table
	 *            the join table
	 * @param metadata
	 *            the column definition
	 * @param name
	 *            the name of the column
	 * @param temporalType
	 *            the temporal type of the column
	 * @param enumType
	 *            the enum type of the column
	 * @param javaType
	 *            the java type
	 * 
	 * @since 2.0.0
	 */
	public MapKeyColumn(AbstractTable table, ColumnMetadata metadata, String name, TemporalType temporalType, EnumType enumType, Class<?> javaType) {
		super(metadata != null ? metadata.getLocator() : null, false);

		this.sqlType = TypeFactory.getSqlType(javaType, temporalType, enumType, false);
		this.table = table;
		this.name = name;
		this.columnDefinition = metadata != null ? metadata.getColumnDefinition() : null;
		this.insertable = metadata != null ? metadata.isInsertable() : true;
		this.nullable = metadata != null ? metadata.isNullable() : true;
		this.updatable = metadata != null ? metadata.isUpdatable() : true;
		this.length = metadata != null ? metadata.getLength() : 255;
		this.scale = metadata != null ? metadata.getScale() : 0;
		this.precision = metadata != null ? metadata.getPrecision() : 0;
		this.table.addColumn(this);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getColumnDefinition() {
		return this.columnDefinition;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public int getLength() {
		return this.length;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Mapping<?, ?, ?> getMapping() {
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getName() {
		return this.name;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public int getPrecision() {
		return this.precision;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public int getScale() {
		return this.scale;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public int getSqlType() {
		return this.sqlType;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public AbstractTable getTable() {
		return this.table;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getTableName() {
		return this.table.getName();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Object getValue(Connection connection, Object instance) {
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean isInsertable() {
		return this.insertable;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean isNullable() {
		return this.nullable;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean isUnique() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean isUpdatable() {
		return this.updatable;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void setTable(AbstractTable table) {
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void setValue(Object instance, Object value) {
	}
}
