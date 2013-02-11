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
package org.batoo.jpa.community.test.t9;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@SuppressWarnings({ "javadoc" })
public class Customer extends BaseEntity {

	@Id
	private int id;

	private String name;

	/**
	 * Returns the id of the Customer.
	 * 
	 * @return the id of the Customer
	 * 
	 * @since $version
	 */
	public int getId() {
		return this.id;
	}

	/**
	 * Returns the name of the Customer.
	 * 
	 * @return the name of the Customer
	 * 
	 * @since $version
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Sets the id of the Customer.
	 * 
	 * @param id
	 *            the id to set for Customer
	 * 
	 * @since $version
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Sets the name of the Customer.
	 * 
	 * @param name
	 *            the name to set for Customer
	 * 
	 * @since $version
	 */
	public void setName(String name) {
		this.name = name;
	}
}
