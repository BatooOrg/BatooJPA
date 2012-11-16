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

import java.sql.Connection;

import org.apache.commons.lang.StringUtils;
import org.batoo.jpa.core.impl.model.mapping.BasicMapping;
import org.batoo.jpa.core.jdbc.adapter.JdbcAdaptor;
import org.batoo.jpa.parser.metadata.ColumnMetadata;

/**
 * BasicColumn to persist basic attributes of the entity.
 * 
 * @author hceylan
 * @since 2.0.0
 */
public class BasicColumn extends AbstractColumn {

	private AbstractTable table;
	private final int sqlType;
	private final String name;
	private final String columnDefinition;
	private final int length;
	private final int precision;
	private final int scale;
	private final String tableName;
	private final boolean nullable;
	private final boolean insertable;
	private final boolean unique;
	private final boolean updatable;
	private final BasicMapping<?, ?> mapping;
	private final JdbcAdaptor jdbcAdaptor;

	/**
	 * @param jdbcAdaptor
	 *            the jdbc adaptor
	 * @param mapping
	 *            the mapping
	 * @param sqlType
	 *            the SQL type
	 * @param metadata
	 *            the metadata
	 * 
	 * @since 2.0.0
	 */
	public BasicColumn(JdbcAdaptor jdbcAdaptor, BasicMapping<?, ?> mapping, int sqlType, ColumnMetadata metadata) {
		super(mapping.getAttribute().getJavaType(), mapping.getIdType(), mapping.getAttribute().getTemporalType(), mapping.getAttribute().getEnumType(),
			mapping.getAttribute().isLob(), mapping.getAttribute().getLocator());

		this.jdbcAdaptor = jdbcAdaptor;
		this.mapping = mapping;
		this.sqlType = sqlType;

		this.name = this.jdbcAdaptor.escape((metadata != null) && StringUtils.isNotBlank(metadata.getName()) ? metadata.getName()
			: this.mapping.getAttribute().getName());

		this.tableName = metadata != null ? metadata.getTable() : "";
		this.columnDefinition = metadata != null ? metadata.getColumnDefinition() : "";
		this.length = metadata != null ? metadata.getLength() : 255;
		this.precision = metadata != null ? metadata.getPrecision() : 0;
		this.scale = metadata != null ? metadata.getScale() : 0;
		this.insertable = metadata != null ? metadata.isInsertable() : true;
		this.nullable = metadata != null ? metadata.isNullable() : true;
		this.unique = metadata != null ? metadata.isUnique() : false;
		this.updatable = metadata != null ? metadata.isUpdatable() : true;
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
	 * Returns the mapping of the BasicColumn.
	 * 
	 * @return the mapping of the BasicColumn
	 * 
	 * @since 2.0.0
	 */
	@Override
	public BasicMapping<?, ?> getMapping() {
		return this.mapping;
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
		return this.tableName;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Object getValue(Connection connection, Object instance) {
		return this.convertValue(connection, this.mapping.get(instance));
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
	 * Sets the table of the column.
	 * 
	 * @param table
	 *            the owning table
	 * 
	 * @since 2.0.0
	 */
	@Override
	public void setTable(AbstractTable table) {
		this.table = table;

		this.table.addColumn(this);
	}

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
	@Override
	public void setValue(Object instance, Object value) {
		this.mapping.set(instance, this.convertValueForSet(value));
	}
}
