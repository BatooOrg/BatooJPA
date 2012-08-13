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
package org.batoo.jpa.core.test.q;

import javax.persistence.Entity;
import javax.persistence.FetchType;
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
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Integer id;

	@ManyToOne
	private Person person;

	private String city;

	@ManyToOne(fetch = FetchType.LAZY)
	private Country country;

	private boolean primary;

	/**
	 * @since $version
	 * @author hceylan
	 */
	public Address() {
		super();
	}

	/**
	 * @param person
	 *            the person
	 * @param city
	 *            the city
	 * @param country
	 *            the country
	 * @param primary
	 *            if the address is primary
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public Address(Person person, String city, Country country, boolean primary) {
		super();

		this.person = person;
		this.city = city;
		this.country = country;
		this.primary = primary;

		this.person.getAddresses().add(this);
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
	 * Returns the country.
	 * 
	 * @return the country
	 * @since $version
	 */
	public Country getCountry() {
		return this.country;
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
	 * Returns the person.
	 * 
	 * @return the person
	 * @since $version
	 */
	public Person getPerson() {
		return this.person;
	}

	/**
	 * Returns the primary of the Address.
	 * 
	 * @return the primary of the Address
	 * 
	 * @since $version
	 * @author hceylan
	 */
	protected boolean isPrimary() {
		return this.primary;
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
	 * Sets the country.
	 * 
	 * @param country
	 *            the country to set
	 * @since $version
	 */
	public void setCountry(Country country) {
		this.country = country;
	}

	/**
	 * Sets the person.
	 * 
	 * @param person
	 *            the person to set
	 * @since $version
	 */
	public void setPerson(Person person) {
		this.person = person;
	}

	/**
	 * Sets the primary of the Address.
	 * 
	 * @param primary
	 *            the primary to set for Address
	 * 
	 * @since $version
	 * @author hceylan
	 */
	protected void setPrimary(boolean primary) {
		this.primary = primary;
	}
}
