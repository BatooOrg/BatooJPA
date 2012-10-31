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

import java.io.ByteArrayInputStream;
import java.io.CharArrayReader;
import java.io.CharArrayWriter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.NClob;
import java.util.Calendar;
import java.util.Date;

import javax.persistence.EnumType;
import javax.persistence.PersistenceException;
import javax.persistence.TemporalType;

import org.apache.commons.io.IOUtils;
import org.batoo.jpa.common.reflect.ReflectHelper;
import org.batoo.jpa.core.impl.model.mapping.Mapping;
import org.batoo.jpa.parser.MappingException;
import org.batoo.jpa.parser.impl.AbstractLocator;

/**
 * Abstract base implementation for columns
 * 
 * @author hceylan
 * @since $version
 */
public abstract class AbstractColumn {

	private final Class<?> javaType;
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
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public AbstractColumn(AbstractLocator locator) {
		super();

		this.javaType = null;
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
	 * @param temporalType
	 *            the temporal type
	 * @param enumType
	 *            the enum type
	 * @param lob
	 *            if the column is lob
	 * @param locator
	 *            the locator
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@SuppressWarnings("unchecked")
	public AbstractColumn(Class<?> javaType, TemporalType temporalType, EnumType enumType, boolean lob, AbstractLocator locator) {
		super();

		this.javaType = javaType;

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
	 * Converts the value corresponding to enum, temporal type, number or [cb]lob.
	 * 
	 * @param connection
	 *            the connection
	 * @param value
	 *            the raw value
	 * @return the converted value
	 * 
	 * @since $version
	 * @author hceylan
	 */
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
					final NClob clob = connection.createNClob();
					clob.setString(1, (String) value);

					return clob;
				}
				else if (this.javaType == char[].class) {
					final NClob clob = connection.createNClob();
					final OutputStream os = clob.setAsciiStream(1);

					try {
						IOUtils.copy(new CharArrayReader((char[]) value), os);
					}
					finally {
						os.close();
					}

					return clob;
				}
				else if (this.javaType == byte[].class) {
					final Blob blob = connection.createBlob();
					final OutputStream os = blob.setBinaryStream(1);

					try {
						IOUtils.copy(new ByteArrayInputStream((byte[]) value), os);
					}
					finally {
						os.close();
					}

					return blob;
				}
				else {
					final Blob blob = connection.createBlob();
					final ObjectOutputStream os = new ObjectOutputStream(blob.setBinaryStream(1));
					try {
						os.writeObject(value);
					}
					finally {
						os.close();
					}

					return blob;
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
	 * @since $version
	 * @author hceylan
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
	public final AbstractLocator getLocator() {
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
	 * @param connection
	 *            the connection
	 * @param instance
	 *            the instance
	 * 
	 * @return the value
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public abstract Object getValue(Connection connection, Object instance);

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
	 * Returns if the column is lob type.
	 * 
	 * @return true if the column is lob type, false otherwise
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public boolean isLob() {
		return this.lob;
	}

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
	public boolean isPrimaryKey() {
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
			else {
				final Blob blob = (Blob) value;

				final ObjectInputStream is = new ObjectInputStream(blob.getBinaryStream());
				try {
					value = is.readObject();
				}
				finally {
					is.close();
				}
			}
			return value;
		}
		catch (final Exception e) {
			throw new PersistenceException("Cannot read sql data", e);
		}
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
