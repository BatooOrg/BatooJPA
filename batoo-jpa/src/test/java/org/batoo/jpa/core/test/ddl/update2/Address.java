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
package org.batoo.jpa.core.test.ddl.update2;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 * 
 * @author hceylan
 * @since $version
 */
@Entity
public class Address {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@ManyToOne
	private Country country;

	private String city;
	private String address;
	private String postalCode;

	/**
	 * 
	 * @since $version
	 */
	public Address() {
		super();
	}

	/**
	 * Returns the address of the Address.
	 * 
	 * @return the address of the Address
	 * 
	 * @since $version
	 */
	public String getAddress() {
		return this.address;
	}

	/**
	 * Returns the city of the Address.
	 * 
	 * @return the city of the Address
	 * 
	 * @since $version
	 */
	public String getCity() {
		return this.city;
	}

	/**
	 * Returns the country of the Address.
	 * 
	 * @return the country of the Address
	 * 
	 * @since $version
	 */
	public Country getCountry() {
		return this.country;
	}

	/**
	 * Returns the id of the Address.
	 * 
	 * @return the id of the Address
	 * 
	 * @since $version
	 */
	public Integer getId() {
		return this.id;
	}

	/**
	 * Returns the postalCode of the Address.
	 * 
	 * @return the postalCode of the Address
	 * 
	 * @since $version
	 */
	public String getPostalCode() {
		return this.postalCode;
	}

	/**
	 * Sets the address of the Address.
	 * 
	 * @param address
	 *            the address to set for Address
	 * 
	 * @since $version
	 */
	public void setAddress(String address) {
		this.address = address;
	}

	/**
	 * Sets the city of the Address.
	 * 
	 * @param city
	 *            the city to set for Address
	 * 
	 * @since $version
	 */
	public void setCity(String city) {
		this.city = city;
	}

	/**
	 * Sets the country of the Address.
	 * 
	 * @param country
	 *            the country to set for Address
	 * 
	 * @since $version
	 */
	public void setCountry(Country country) {
		this.country = country;
	}

	/**
	 * Sets the postalCode of the Address.
	 * 
	 * @param postalCode
	 *            the postalCode to set for Address
	 * 
	 * @since $version
	 */
	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}
}
