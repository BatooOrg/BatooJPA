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
package org.batoo.jpa.core.test.columnTransformer;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.batoo.jpa.annotations.ColumnTransformer;

/**
 * 
 * @author hceylan
 * @since $version
 */
@Entity
public class FooRead {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@Column(name = "colname")
	@ColumnTransformer(read = "LTRIM(colname)")
	private String value;

	/**
	 * Returns the id of the Foo1.
	 * 
	 * @return the id of the Foo1
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public Integer getId() {
		return this.id;
	}

	/**
	 * Returns the value of the Foo1.
	 * 
	 * @return the value of the Foo1
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public String getValue() {
		return this.value;
	}

	/**
	 * Sets the value of the Foo1.
	 * 
	 * @param value
	 *            the value to set for Foo1
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void setValue(String value) {
		this.value = value;
	}
}
