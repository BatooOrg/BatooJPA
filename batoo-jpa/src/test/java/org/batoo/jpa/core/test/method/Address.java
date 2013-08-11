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

package org.batoo.jpa.core.test.method;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * 
 * @author hceylan
 * @since 2.0.0
 */
@Entity
@Access(AccessType.PROPERTY)
public class Address {

	private Integer id;
	private String city;

	/**
	 * @since 2.0.0
	 */
	public Address() {
		super();
	}

	/**
	 * @param person
	 *            the person
	 * @param city
	 *            the city
	 * 
	 * @since 2.0.0
	 */
	public Address(Person person, String city) {
		super();

		this.city = city;

		person.getAddresses().add(this);
	}

	/**
	 * Returns the city.
	 * 
	 * @return the city
	 * @since 2.0.0
	 */
	public String getCity() {
		return this.city;
	}

	/**
	 * Returns the id.
	 * 
	 * @return the id
	 * @since 2.0.0
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	public Integer getId() {
		return this.id;
	}

	/**
	 * Sets the city.
	 * 
	 * @param city
	 *            the city to set
	 * @since 2.0.0
	 */
	public void setCity(String city) {
		this.city = city;
	}

	/**
	 * Sets the id of the Address.
	 * 
	 * @param id
	 *            the id to set for Address
	 * 
	 * @since 2.0.0
	 */
	public void setId(Integer id) {
		this.id = id;
	}
}
