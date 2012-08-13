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

import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.Date;

import javax.persistence.EnumType;
import javax.persistence.TemporalType;

import org.apache.commons.lang.StringUtils;
import org.batoo.jpa.core.impl.model.mapping.BasicMapping;
import org.batoo.jpa.core.jdbc.adapter.JdbcAdaptor;
import org.batoo.jpa.parser.MappingException;
import org.batoo.jpa.parser.impl.AbstractLocator;
import org.batoo.jpa.parser.metadata.ColumnMetadata;

/**
 * BasicColumn to persist basic attributes of the entity.
 * 
 * @author hceylan
 * @since $version
 */
public class BasicColumn extends AbstractColumn {

	private AbstractTable table;
	private final int sqlType;
	private final String name;
	private final String columnDefinition;
	private final int length;
	private final AbstractLocator locator;
	private final int precision;
	private final int scale;
	private final String tableName;
	private final boolean nullable;
	private final boolean insertable;
	private final boolean unique;
	private final boolean updatable;
	private final String mappingName;
	private final BasicMapping<?, ?> mapping;
	private final JdbcAdaptor jdbcAdaptor;
	private final EnumType enumType;
	private final Enum<?>[] values;
	private final Method method;
	private final TemporalType temporalType;

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
	 * @since $version
	 * @author hceylan
	 */
	@SuppressWarnings("unchecked")
	public BasicColumn(JdbcAdaptor jdbcAdaptor, BasicMapping<?, ?> mapping, int sqlType, ColumnMetadata metadata) {
		super();

		this.jdbcAdaptor = jdbcAdaptor;
		this.mapping = mapping;
		this.locator = metadata != null ? metadata.getLocator() : null;
		this.sqlType = sqlType;

		this.mappingName = (metadata != null) && StringUtils.isNotBlank(metadata.getName()) ? metadata.getName() : this.mapping.getAttribute().getName();
		this.name = this.jdbcAdaptor.escape(this.mappingName);

		this.tableName = metadata != null ? metadata.getTable() : "";
		this.columnDefinition = metadata != null ? metadata.getColumnDefinition() : "";
		this.length = metadata != null ? metadata.getLength() : 255;
		this.precision = metadata != null ? metadata.getPrecision() : 0;
		this.scale = metadata != null ? metadata.getScale() : 0;
		this.insertable = metadata != null ? metadata.isInsertable() : true;
		this.nullable = metadata != null ? metadata.isNullable() : true;
		this.unique = metadata != null ? metadata.isUnique() : false;
		this.updatable = metadata != null ? metadata.isUpdatable() : true;

		this.temporalType = mapping.getAttribute().getTemporalType();

		this.enumType = mapping.getAttribute().getEnumType();
		if (this.enumType != null) {
			Class<Enum<?>> enumJavaType;
			enumJavaType = (Class<Enum<?>>) mapping.getAttribute().getJavaType();
			try {
				if (this.enumType == EnumType.ORDINAL) {
					this.values = (Enum<?>[]) enumJavaType.getMethod("values").invoke(null);
					this.method = null;
				}
				else {
					this.values = null;
					this.method = enumJavaType.getMethod("valueOf", String.class);
				}
			}
			catch (final Exception e) {
				throw new MappingException("Unable to map enum type", this.mapping.getAttribute().getLocator());
			}
		}
		else {
			this.values = null;
			this.method = null;
		}
	}

	/**
	 * Converts the value corresponding to enum or temporal type
	 * 
	 * @param value
	 *            the raw value
	 * @return the converted value
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public Object convertValue(final Object value) {
		if (value == null) {
			return null;
		}

		if (this.temporalType != null) {
			switch (this.temporalType) {
				case DATE:
					if (value instanceof java.sql.Date) {
						return value;
					}

					if (value instanceof Date) {
						return new java.sql.Date(((Date) value).getTime());
					}

					return new java.sql.Date(((Calendar) value).getTimeInMillis());
				case TIME:
					if (value instanceof java.sql.Time) {
						return value;
					}

					if (value instanceof Date) {
						return new java.sql.Time(((Date) value).getTime());
					}

					return new java.sql.Time(((Calendar) value).getTimeInMillis());
				case TIMESTAMP:
					if (value instanceof java.sql.Timestamp) {
						return value;
					}

					if (value instanceof Date) {
						return new java.sql.Timestamp(((Date) value).getTime());
					}

					return new java.sql.Timestamp(((Calendar) value).getTimeInMillis());
			}
		}

		if (this.enumType == null) {
			return value;
		}

		final Enum<?> enumValue = (Enum<?>) value;
		if (this.enumType == EnumType.ORDINAL) {
			return enumValue.ordinal();
		}

		return enumValue.name();
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
	public AbstractLocator getLocator() {
		return this.locator;
	}

	/**
	 * Returns the mapping of the BasicColumn.
	 * 
	 * @return the mapping of the BasicColumn
	 * 
	 * @since $version
	 * @author hceylan
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
	public String getMappingName() {
		return this.mappingName;
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
	public Object getValue(Object instance) {
		final Object value = this.mapping.get(instance);

		return this.convertValue(value);
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
	 * @since $version
	 * @author hceylan
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
	 * @since $version
	 * @author hceylan
	 */
	@Override
	public void setValue(Object instance, Object value) {
		if ((value != null) && (this.enumType != null)) {
			if (this.enumType == EnumType.ORDINAL) {
				value = this.values[(Integer) value];
			}
			else {
				try {
					value = this.method.invoke(null, value);
				}
				catch (final Exception e) {}
			}
		}

		this.mapping.set(instance, value);
	}
}
