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
package org.batoo.jpa.core.test.ddl.update1;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 * 
 * @author hceylan
 * @since 2.0.0
 */
@Entity
public class Address {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@ManyToOne
	private Customer customer;

	private String city;
	private String address;
	private String postalCode;

	@Column(nullable = false)
	private String countryName;

	/**
	 * 
	 * @since 2.0.0
	 */
	public Address() {
		super();
	}

	/**
	 * Returns the address of the Address.
	 * 
	 * @return the address of the Address
	 * 
	 * @since 2.0.0
	 */
	public String getAddress() {
		return this.address;
	}

	/**
	 * Returns the city of the Address.
	 * 
	 * @return the city of the Address
	 * 
	 * @since 2.0.0
	 */
	public String getCity() {
		return this.city;
	}

	/**
	 * Returns the countryName of the Address.
	 * 
	 * @return the countryName of the Address
	 * 
	 * @since 2.0.0
	 */
	public String getCountryName() {
		return this.countryName;
	}

	/**
	 * Returns the customer of the Address.
	 * 
	 * @return the customer of the Address
	 * 
	 * @since 2.0.0
	 */
	public Customer getCustomer() {
		return this.customer;
	}

	/**
	 * Returns the id of the Address.
	 * 
	 * @return the id of the Address
	 * 
	 * @since 2.0.0
	 */
	public Integer getId() {
		return this.id;
	}

	/**
	 * Returns the postalCode of the Address.
	 * 
	 * @return the postalCode of the Address
	 * 
	 * @since 2.0.0
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
	 * @since 2.0.0
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
	 * @since 2.0.0
	 */
	public void setCity(String city) {
		this.city = city;
	}

	/**
	 * Sets the countryName of the Address.
	 * 
	 * @param countryName
	 *            the countryName to set for Address
	 * 
	 * @since 2.0.0
	 */
	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}

	/**
	 * Sets the customer of the Address.
	 * 
	 * @param customer
	 *            the customer to set for Address
	 * 
	 * @since 2.0.0
	 */
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	/**
	 * Sets the postalCode of the Address.
	 * 
	 * @param postalCode
	 *            the postalCode to set for Address
	 * 
	 * @since 2.0.0
	 */
	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}
}
