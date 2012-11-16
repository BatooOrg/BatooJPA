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
package org.batoo.jpa.core.impl.model.attribute;

import java.sql.Timestamp;

/**
 * Possible types for the version attributes.
 * 
 * @author hceylan
 * @since 2.0.0
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
	 * @since 2.0.0
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
