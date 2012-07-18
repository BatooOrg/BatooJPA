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

import java.lang.reflect.Method;

import javax.persistence.EnumType;
import javax.persistence.TemporalType;

import org.batoo.jpa.core.impl.instance.ManagedInstance;
import org.batoo.jpa.core.impl.model.mapping.Mapping;
import org.batoo.jpa.core.jdbc.adapter.JdbcAdaptor;
import org.batoo.jpa.parser.MappingException;
import org.batoo.jpa.parser.impl.AbstractLocator;
import org.batoo.jpa.parser.metadata.ColumnMetadata;

/**
 * Columns for list type attributes.
 * 
 * @author hceylan
 * @since $version
 */
public class ElementColumn extends AbstractColumn {

	private final CollectionTable table;
	private final int sqlType;
	private final String columnDefinition;
	private final AbstractLocator locator;
	private final String name;
	private final boolean insertable;
	private final boolean nullable;
	private final boolean updatable;
	private final int length;
	private final int precision;
	private final int scale;
	private final boolean unique;
	private final EnumType enumType;
	private final Enum<?>[] values;
	private final Method method;

	/**
	 * 
	 * @param jdbcAdaptor
	 *            the jdbc adaptor
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
	 * @since $version
	 * @author hceylan
	 */
	public ElementColumn(JdbcAdaptor jdbcAdaptor, CollectionTable table, String name, Class<?> javaType, EnumType enumType, TemporalType temporalType,
		boolean lob, ColumnMetadata metadata) {
		super();

		this.locator = metadata != null ? metadata.getLocator() : null;

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

		if (javaType.isEnum()) {
			this.enumType = enumType != null ? enumType : EnumType.ORDINAL;

			try {
				if (this.enumType == EnumType.ORDINAL) {
					this.values = (Enum<?>[]) javaType.getMethod("values").invoke(null);
					this.method = null;
				}
				else {
					this.values = null;
					this.method = javaType.getMethod("valueOf", String.class);
				}
			}
			catch (final Exception e) {
				throw new MappingException("Unable to map enum type", this.locator);
			}

		}
		else {
			this.enumType = null;
			this.values = null;
			this.method = null;
		}

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
	public AbstractLocator getLocator() {
		return this.locator;
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
	public String getMappingName() {
		return this.name;
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
	public Object getValue(Object instance) {
		if (instance == null) {
			return null;
		}

		if (this.enumType == null) {
			return instance;
		}

		final Enum<?> enumValue = (Enum<?>) instance;
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
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("rawtypes")
	public void setValue(ManagedInstance instance, Object value) {
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
	}
}
