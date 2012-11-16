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
package org.batoo.jpa.core.test.manualid;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * 
 * @author hceylan
 * @since 2.0.0
 */
@Entity
public class Foo {

	@Id
	private Integer key;

	private String value;

	/**
	 * Returns the key.
	 * 
	 * @return the key
	 * @since 2.0.0
	 */
	public Integer getKey() {
		return this.key;
	}

	/**
	 * Returns the value.
	 * 
	 * @return the value
	 * @since 2.0.0
	 */
	public String getValue() {
		return this.value;
	}

	/**
	 * Sets the key.
	 * 
	 * @param key
	 *            the key to set
	 * @since 2.0.0
	 */
	public void setKey(Integer key) {
		this.key = key;
	}

	/**
	 * Sets the value.
	 * 
	 * @param value
	 *            the value to set
	 * @since 2.0.0
	 */
	public void setValue(String value) {
		this.value = value;
	}

}
