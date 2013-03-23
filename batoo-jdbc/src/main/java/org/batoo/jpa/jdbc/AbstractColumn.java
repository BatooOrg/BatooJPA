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

import java.lang.reflect.Method;
import java.sql.Connection;

import javax.persistence.EnumType;
import javax.persistence.TemporalType;

import org.batoo.jpa.jdbc.mapping.Mapping;
import org.batoo.jpa.parser.AbstractLocator;
import org.batoo.jpa.parser.MappingException;

/**
 * Abstract base implementation for columns
 * 
 * @author hceylan
 * @since 2.0.0
 */
public abstract class AbstractColumn implements Column {

	private final Class<?> javaType;
	private IdType idType;
	private final TemporalType temporalType;
	private final EnumType enumType;
	private final boolean lob;
	private final AbstractLocator locator;

	private final Enum<?>[] values;
	private final Method method;

	/**
	 * @param locator
	 *            the locator
	 * @param id
	 *            if the column is id column
	 * 
	 * @since 2.0.0
	 */
	public AbstractColumn(AbstractLocator locator, boolean id) {
		super();

		this.javaType = null;
		this.idType = id ? IdType.MANUAL : null;
		this.locator = locator;
		this.temporalType = null;
		this.enumType = null;
		this.lob = false;
		this.values = null;
		this.method = null;
	}

	/**
	 * @param javaType
	 *            the java type
	 * @param idType
	 *            the id type
	 * @param temporalType
	 *            the temporal type
	 * @param enumType
	 *            the enum type
	 * @param lob
	 *            if the column is lob
	 * @param locator
	 *            the locator
	 * 
	 * @since 2.0.0
	 */
	@SuppressWarnings("unchecked")
	public AbstractColumn(Class<?> javaType, IdType idType, TemporalType temporalType, EnumType enumType, boolean lob, AbstractLocator locator) {
		super();

		this.javaType = javaType;

		this.idType = idType;
		this.temporalType = temporalType;
		this.enumType = enumType;
		this.lob = lob;
		this.locator = locator;

		if (this.enumType != null) {
			Class<Enum<?>> enumJavaType;
			enumJavaType = (Class<Enum<?>>) javaType;
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
				throw new MappingException("Unable to map enum type", this.locator);
			}
		}
		else {
			this.values = null;
			this.method = null;
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Object convertValue(Connection connection, final Object value) {
		return ValueConverter.toJdbc(value, this.javaType, this.temporalType, this.enumType, this.lob);
	}

	/**
	 * Converts the value corresponding to enum, temporal type, number or [cb]lob.
	 * 
	 * @param value
	 *            the raw value
	 * @return the converted value
	 * 
	 * @since 2.0.0
	 */
	public Object convertValueForSet(Object value) {
		return ValueConverter.fromJdbc(value, this.javaType, this.temporalType, this.enumType, this.values, this.method, this.lob);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public abstract String getColumnDefinition();

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public IdType getIdType() {
		return this.idType;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public abstract int getLength();

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public final AbstractLocator getLocator() {
		return this.locator;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public abstract Mapping<?, ?, ?> getMapping();

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public abstract String getName();

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public abstract int getPrecision();

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public abstract int getScale();

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public abstract int getSqlType();

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public abstract AbstractTable getTable();

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public abstract String getTableName();

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public abstract Object getValue(Connection connection, Object instance);

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public abstract boolean isInsertable();

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean isLob() {
		return this.lob;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public abstract boolean isNullable();

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean isPrimaryKey() {
		return this.idType != null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public abstract boolean isUnique();

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public abstract boolean isUpdatable();

	/**
	 * Returns if the column is a version column.
	 * 
	 * @return <code>true</code> if the column is a version column, <code>false</code> otherwise
	 * 
	 * @since 2.0.1
	 */
	public boolean isVersion() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void setId() {
		this.idType = IdType.MANUAL;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public abstract void setTable(AbstractTable table);

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
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
