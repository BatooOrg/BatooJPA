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
 * @since $version
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
	 * @since $version
	 * @author hceylan
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
