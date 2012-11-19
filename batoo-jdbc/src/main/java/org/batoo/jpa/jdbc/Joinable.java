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

/**
 * The data representing a single join.
 * 
 * @author hceylan
 * @since 2.0.0
 */
public class Joinable {

	private final Object key;
	private final Object value;
	private final int index;

	/**
	 * @param key
	 *            the key
	 * @param value
	 *            the value
	 * @param index
	 *            the index
	 * 
	 * @since 2.0.0
	 */
	public Joinable(Object key, Object value, int index) {
		super();

		this.key = key;
		this.value = value;
		this.index = index;
	}

	/**
	 * Returns the index of the Joinable.
	 * 
	 * @return the index of the Joinable
	 * 
	 * @since 2.0.0
	 */
	public int getIndex() {
		return this.index;
	}

	/**
	 * Returns the key of the Joinable.
	 * 
	 * @return the key of the Joinable
	 * 
	 * @since 2.0.0
	 */
	public Object getKey() {
		return this.key;
	}

	/**
	 * Returns the value of the Joinable.
	 * 
	 * @return the value of the Joinable
	 * 
	 * @since 2.0.0
	 */
	public Object getValue() {
		return this.value;
	}
}
