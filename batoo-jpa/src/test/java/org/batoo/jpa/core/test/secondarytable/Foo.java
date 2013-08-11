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

package org.batoo.jpa.core.test.secondarytable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SecondaryTable;

/**
 * 
 * @author hceylan
 * @since 2.0.0
 */
@Entity
@SecondaryTable(name = "FooExtra")
public class Foo {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer key;

	private String value1;

	@Column(table = "FooExtra")
	private String value2;

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
	 * Returns the value1.
	 * 
	 * @return the value1
	 * @since 2.0.0
	 */
	public String getValue1() {
		return this.value1;
	}

	/**
	 * Returns the value2.
	 * 
	 * @return the value2
	 * @since 2.0.0
	 */
	public String getValue2() {
		return this.value2;
	}

	/**
	 * Sets the value1.
	 * 
	 * @param value1
	 *            the value1 to set
	 * @since 2.0.0
	 */
	public void setValue1(String value1) {
		this.value1 = value1;
	}

	/**
	 * Sets the value2.
	 * 
	 * @param value2
	 *            the value2 to set
	 * @since 2.0.0
	 */
	public void setValue2(String value2) {
		this.value2 = value2;
	}
}
