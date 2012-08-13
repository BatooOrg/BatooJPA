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
package org.batoo.jpa.core.test.map;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * 
 * @author hceylan
 * @since $version
 */
@Entity
public class Address {

	@Id
	private Integer id;

	private String city;

	/**
	 * @since $version
	 * @author hceylan
	 */
	public Address() {
		super();
	}

	/**
	 * @param id
	 *            the id of the address
	 * @param city
	 *            the city
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public Address(Integer id, String city) {
		super();

		this.id = id;
		this.city = city;
	}

	/**
	 * Returns the city.
	 * 
	 * @return the city
	 * @since $version
	 */
	public String getCity() {
		return this.city;
	}

	/**
	 * Returns the id.
	 * 
	 * @return the id
	 * @since $version
	 */
	public Integer getId() {
		return this.id;
	}

	/**
	 * Sets the city.
	 * 
	 * @param city
	 *            the city to set
	 * @since $version
	 */
	public void setCity(String city) {
		this.city = city;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String toString() {
		return "Address [id=" + this.id + ", city=" + this.city + "]";
	}
}
