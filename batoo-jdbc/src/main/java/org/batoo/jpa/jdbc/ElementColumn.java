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

import javax.persistence.EnumType;
import javax.persistence.TemporalType;

import org.batoo.jpa.jdbc.adapter.JdbcAdaptor;
import org.batoo.jpa.jdbc.mapping.Mapping;
import org.batoo.jpa.parser.metadata.ColumnMetadata;

/**
 * Columns for list type attributes.
 * 
 * @author hceylan
 * @since 2.0.0
 */
public class ElementColumn extends AbstractColumn {

	private final Mapping<?, ?, ?> mapping;
	private final CollectionTable table;
	private final int sqlType;
	private final String columnDefinition;
	private final String name;
	private final boolean insertable;
	private final boolean nullable;
	private final boolean updatable;
	private final int length;
	private final int precision;
	private final int scale;
	private final boolean unique;

	/**
	 * 
	 * @param jdbcAdaptor
	 *            the jdbc adaptor
	 * @param mapping
	 *            the mapping
	 * @param table
	 *            the table
	 * @param name
	 *            the name of the column
	 * @param javaType
	 *            the java type of the column
	 * @param enumType
	 *            the enum tpe of the column
	 * @param temporalType
	 *            the temporal type of the column
	 * @param lob
	 *            the lob type of the column
	 * @param metadata
	 *            the column metadata
	 * 
	 * @since 2.0.0
	 */
	public ElementColumn(JdbcAdaptor jdbcAdaptor, Mapping<?, ?, ?> mapping, CollectionTable table, String name, Class<?> javaType, EnumType enumType,
		TemporalType temporalType, boolean lob, ColumnMetadata metadata) {
		super(javaType, null, temporalType, enumType, lob, metadata != null ? metadata.getLocator() : null);

		this.mapping = mapping;

		this.table = table;
		this.sqlType = TypeFactory.getSqlType(javaType, temporalType, enumType, lob);
		this.name = jdbcAdaptor.escape(name);

		this.columnDefinition = metadata != null ? metadata.getColumnDefinition() : null;
		this.length = metadata != null ? metadata.getLength() : 255;
		this.precision = metadata != null ? metadata.getPrecision() : 0;
		this.scale = metadata != null ? metadata.getScale() : 0;
		this.insertable = metadata != null ? metadata.isInsertable() : true;
		this.nullable = metadata != null ? metadata.isNullable() : true;
		this.unique = metadata != null ? metadata.isUnique() : false;
		this.updatable = metadata != null ? metadata.isUpdatable() : true;

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
	public Object getValue(Connection connection, Object value) {
		return this.convertValue(connection, value);
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
		return this.unique;
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
		// noop
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void setValue(Object instance, Object value) {
		this.mapping.set(instance, this.convertValueForSet(value));
	}
}
