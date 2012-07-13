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
package org.batoo.jpa.core.impl.model.attribute;

import java.sql.Timestamp;

/**
 * Possible types for the version attributes.
 * 
 * @author hceylan
 * @since $version
 */
@SuppressWarnings("javadoc")
public enum VersionType {

	SHORT(Short.TYPE),

	SHORT_OBJECT(Short.class),

	INT(Integer.TYPE),

	INT_OBJECT(Integer.class),

	LONG(Long.TYPE),

	LONG_OBJECT(Long.class),

	TIMESTAMP(Timestamp.class);

	/**
	 * Returns the type of the version attribute.
	 * 
	 * @param clazz
	 *            the class of the version attribute
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public static VersionType versionType(Class<?> clazz) {
		for (final VersionType versionType : VersionType.values()) {
			if (versionType.clazz == clazz) {
				return versionType;
			}
		}

		throw new IllegalArgumentException("Illegal version attribute type: " + clazz);
	}

	private Class<?> clazz;

	VersionType(Class<?> clazz) {
		this.clazz = clazz;
	}
}
