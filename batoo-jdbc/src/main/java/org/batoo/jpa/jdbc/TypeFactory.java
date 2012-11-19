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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Types;
import java.util.Calendar;
import java.util.Date;

import javax.persistence.EnumType;
import javax.persistence.TemporalType;

/**
 * 
 * @author hceylan
 * @since 2.0.0
 */
public class TypeFactory {

	/**
	 * Returns the SQL type of the java type
	 * 
	 * @param javaType
	 *            the java type
	 * @param temporal
	 *            temporal type of the type
	 * @param enumType
	 *            enum type of the type
	 * @param isLob
	 *            if is a lob type
	 * @return the corresponding SQL Type
	 * 
	 * @since 2.0.0
	 */
	public static int getSqlType(Class<?> javaType, TemporalType temporal, EnumType enumType, boolean isLob) {
		if (isLob) {
			if (Character.class.isAssignableFrom(javaType) //
				|| String.class.isAssignableFrom(javaType) //
				|| (javaType.isArray() && char.class.isAssignableFrom(javaType.getComponentType()))) {
				return Types.CLOB;
			}

			return Types.BLOB;
		}

		if (javaType.isArray()) {
			if (Character.class.isAssignableFrom(javaType.getComponentType()) || char.class.isAssignableFrom(javaType.getComponentType())) {
				return Types.CLOB;
			}

			return Types.BLOB;
		}

		if (Boolean.class.isAssignableFrom(javaType) || boolean.class.isAssignableFrom(javaType)) {
			return Types.BOOLEAN;
		}

		if (String.class.isAssignableFrom(javaType)) {
			return Types.VARCHAR;
		}

		if ((Calendar.class.isAssignableFrom(javaType)) || (Date.class.isAssignableFrom(javaType))) {
			if (temporal == null) {
				return Types.TIMESTAMP;
			}

			switch (temporal) {
				case DATE:
					return Types.DATE;
				case TIME:
					return Types.TIME;
				case TIMESTAMP:
					return Types.TIMESTAMP;
			}
		}

		if (Enum.class.isAssignableFrom(javaType)) {
			if ((enumType == null) || (enumType == EnumType.ORDINAL)) {
				return Types.SMALLINT;
			}

			return Types.VARCHAR;
		}

		if (Integer.class.isAssignableFrom(javaType) || int.class.isAssignableFrom(javaType)) {
			return Types.INTEGER;
		}

		if (Byte.class.isAssignableFrom(javaType) || byte.class.isAssignableFrom(javaType)) {
			return Types.TINYINT;
		}

		if (Character.class.isAssignableFrom(javaType) || char.class.isAssignableFrom(javaType)) {
			return Types.CHAR;
		}

		if (Short.class.isAssignableFrom(javaType) || short.class.isAssignableFrom(javaType)) {
			return Types.SMALLINT;
		}

		if (Long.class.isAssignableFrom(javaType) || long.class.isAssignableFrom(javaType)) {
			return Types.BIGINT;
		}

		if (Float.class.isAssignableFrom(javaType) || float.class.isAssignableFrom(javaType)) {
			return Types.FLOAT;
		}

		if (Double.class.isAssignableFrom(javaType) || double.class.isAssignableFrom(javaType)) {
			return Types.DOUBLE;
		}

		if (java.sql.Date.class.isAssignableFrom(javaType)) {
			return Types.DATE;
		}

		if (java.sql.Time.class.isAssignableFrom(javaType)) {
			return Types.TIME;
		}

		if (java.sql.Timestamp.class.isAssignableFrom(javaType)) {
			return Types.TIMESTAMP;
		}

		if (BigDecimal.class.isAssignableFrom(javaType) || BigInteger.class.isAssignableFrom(javaType)) {
			return Types.DECIMAL;
		}

		throw new IllegalArgumentException("Cannot determine sql type: " + javaType);
	}
}
