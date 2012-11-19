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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.CharArrayWriter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.util.Calendar;
import java.util.Date;

import javax.persistence.EnumType;
import javax.persistence.PersistenceException;
import javax.persistence.TemporalType;
import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialClob;

import org.apache.commons.io.IOUtils;
import org.batoo.common.reflect.ReflectHelper;
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

	private final Class<?> numberType;
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
		this.numberType = null;
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

		this.numberType = Number.class.isAssignableFrom(javaType) ? javaType : null;

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

		if (this.numberType != null) {
			return ReflectHelper.convertNumber((Number) value, this.numberType);
		}

		if (this.enumType != null) {
			final Enum<?> enumValue = (Enum<?>) value;
			if (this.enumType == EnumType.ORDINAL) {
				return enumValue.ordinal();
			}

			return enumValue.name();
		}

		if (this.lob) {
			try {
				if (this.javaType == String.class) {
					return new SerialClob(((String) value).toCharArray());
				}
				else if (this.javaType == char[].class) {
					return new SerialClob((char[]) value);
				}
				else if (this.javaType == byte[].class) {
					return new SerialBlob((byte[]) value);
				}
				else {
					final ByteArrayOutputStream os = new ByteArrayOutputStream();
					final ObjectOutputStream oos = new ObjectOutputStream(os);
					try {
						oos.writeObject(value);
					}
					finally {
						oos.close();
					}

					return new SerialBlob(os.toByteArray());
				}
			}
			catch (final Exception e) {
				throw new PersistenceException("Cannot set parameter", e);
			}
		}
		else {
			return value;
		}
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
	protected Object convertValueForSet(Object value) {
		if (value == null) {
			return null;
		}

		if (this.enumType != null) {
			if (this.enumType == EnumType.ORDINAL) {
				value = this.values[((Number) value).shortValue()];
			}
			else {
				try {
					value = this.method.invoke(null, value);
				}
				catch (final Exception e) {}
			}
		}

		if (this.lob) {
			value = this.readLob(value);
		}

		return value;
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

	private Object readLob(Object value) {
		try {
			if (value instanceof Clob) {
				final Clob clob = (Clob) value;

				if (this.javaType == String.class) {
					final StringWriter w = new StringWriter();
					IOUtils.copy(clob.getAsciiStream(), w);
					value = w.toString();
				}
				else {
					final CharArrayWriter w = new CharArrayWriter((int) clob.length());
					IOUtils.copy(clob.getCharacterStream(), w);
					value = w.toCharArray();
				}
			}
			else if (value instanceof byte[]) {
				if (this.javaType == String.class) {
					final StringWriter w = new StringWriter();
					IOUtils.copy(new ByteArrayInputStream((byte[]) value), w);
					value = w.toString();
				}
				else if (this.javaType == char[].class) {
					final byte[] byteArray = (byte[]) value;

					final char[] charArray = new char[byteArray.length];

					for (int i = 0; i < charArray.length; i++) {
						charArray[i] = (char) byteArray[i];
					}

					value = charArray;
				}
				else if (this.javaType != byte[].class) {
					final ObjectInputStream is = new ObjectInputStream(new ByteArrayInputStream((byte[]) value));
					try {
						return is.readObject();
					}
					finally {
						is.close();
					}
				}
			}
			else if (value instanceof String) {
				return value;
			}
			else {
				final Blob blob = (Blob) value;

				if (this.javaType == byte[].class) {
					final ByteArrayOutputStream os = new ByteArrayOutputStream();

					IOUtils.copy(blob.getBinaryStream(), os);

					value = os.toByteArray();
				}
				else {

					final ObjectInputStream is = new ObjectInputStream(blob.getBinaryStream());
					try {
						value = is.readObject();
					}
					finally {
						is.close();
					}
				}
			}
			return value;
		}
		catch (final Exception e) {
			throw new PersistenceException("Cannot read sql data", e);
		}
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
