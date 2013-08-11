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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.CharArrayWriter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.sql.Blob;
import java.sql.Clob;
import java.util.Calendar;
import java.util.Date;

import javax.persistence.EnumType;
import javax.persistence.PersistenceException;
import javax.persistence.TemporalType;
import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialClob;

import org.apache.commons.io.IOUtils;
import org.batoo.common.reflect.ReflectHelper;

/**
 * Converts Basic Java values to Jdbc friendly values<br/>
 * 
 * handling Temporal types, Enums and Date types
 * 
 * @author asimarslan
 * @since 2.0.1
 */
public class ValueConverter {

	/**
	 * convert jdbc data to entity basic values
	 * 
	 * @param value
	 *            jdbc raw value
	 * @param javaType
	 *            type of value
	 * @param temporalType
	 *            temporal type
	 * @param enumType
	 *            enum type
	 * @param values
	 *            EnumType values array for faster operation, leave it to be null but it will be slow
	 * @param method
	 *            Method for enum valueOf for faster operation, leave it to be null but it will be slow
	 * @param lob
	 *            is Lob
	 * @return java value
	 * @since 2.0.1
	 */
	@SuppressWarnings("unchecked")
	public static Object fromJdbc(Object value, Class<?> javaType, TemporalType temporalType, EnumType enumType, Enum<?>[] values, Method method, boolean lob) {
		if (value == null) {
			return null;
		}

		if (temporalType != null) {
			if (javaType == Calendar.class) {
				final Calendar calendarValue = Calendar.getInstance();
				switch (temporalType) {
					case DATE:
						if (value instanceof java.sql.Date) {
							calendarValue.setTime((java.sql.Date) value);
							return calendarValue;
						}
					case TIME:
						if (value instanceof java.sql.Time) {
							calendarValue.setTime((java.sql.Time) value);
							return calendarValue;
						}
					case TIMESTAMP:
						if (value instanceof java.sql.Timestamp) {
							calendarValue.setTime((java.sql.Timestamp) value);
							return calendarValue;
						}
				}
			}
		}

		if (enumType != null) {
			try {
				final Class<Enum<?>> enumJavaType = (Class<Enum<?>>) javaType;
				if (enumType == EnumType.ORDINAL && values == null) {
					values = (Enum<?>[]) enumJavaType.getMethod("values").invoke(null);
				}
				else if (enumType == EnumType.STRING && method == null) {
					method = enumJavaType.getMethod("valueOf", String.class);
				}

				if (enumType == EnumType.ORDINAL) {
					value = values[((Number) value).shortValue()];
				}
				else {
					value = method.invoke(null, value);
				}
			}
			catch (final Exception e) {}
		}

		if (lob) {
			value = readLob(value, javaType);
		}

		return value;
	}

	private static Object readLob(Object value, Class<?> javaType) {
		try {
			if (value instanceof Clob) {
				final Clob clob = (Clob) value;

				if (javaType == String.class) {
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
				if (javaType == String.class) {
					final StringWriter w = new StringWriter();
					IOUtils.copy(new ByteArrayInputStream((byte[]) value), w);
					value = w.toString();
				}
				else if (javaType == char[].class) {
					final byte[] byteArray = (byte[]) value;

					final char[] charArray = new char[byteArray.length];

					for (int i = 0; i < charArray.length; i++) {
						charArray[i] = (char) byteArray[i];
					}

					value = charArray;
				}
				else if (javaType != byte[].class) {
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

				if (javaType == byte[].class) {
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
	 * convert java objects to jdbc friendly
	 * 
	 * @param value
	 *            jdbc raw value
	 * @param javaType
	 *            type of value
	 * @param temporalType
	 *            temporal type
	 * @param enumType
	 *            enum type
	 * @param lob
	 *            is Lob
	 * @return jdbc friendly value
	 * @since 2.0.1
	 */
	public static Object toJdbc(Object value, Class<?> javaType, TemporalType temporalType, EnumType enumType, boolean lob) {
		if (value == null) {
			return null;
		}

		if (temporalType != null) {
			switch (temporalType) {
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

		if (Number.class.isAssignableFrom(javaType)) {
			return ReflectHelper.convertNumber((Number) value, javaType);
		}

		if (enumType != null) {
			final Enum<?> enumValue = (Enum<?>) value;
			if (enumType == EnumType.ORDINAL) {
				return enumValue.ordinal();
			}

			return enumValue.name();
		}

		if (lob) {
			try {
				if (javaType == String.class) {
					return new SerialClob(((String) value).toCharArray());
				}
				else if (javaType == char[].class) {
					return new SerialClob((char[]) value);
				}
				else if (javaType == byte[].class) {
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
}
