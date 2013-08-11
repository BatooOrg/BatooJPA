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

package org.batoo.jpa.core.test.q;

import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;

/**
 * 
 * @author hceylan
 * @since 2.0.0
 */
@Embeddable
public class Address2 {

	private String city;

	@ManyToOne
	private Country country;

	/**
	 * @since 2.0.0
	 */
	public Address2() {
		super();
	}

	/**
	 * @param city
	 *            the city
	 * @param country
	 *            the country
	 * 
	 * @since 2.0.0
	 */
	public Address2(String city, Country country) {
		super();

		this.city = city;
		this.country = country;
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
	 * Returns the country.
	 * 
	 * @return the country
	 * @since 2.0.0
	 */
	public Country getCountry() {
		return this.country;
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
	 * Sets the country.
	 * 
	 * @param country
	 *            the country to set
	 * @since 2.0.0
	 */
	public void setCountry(Country country) {
		this.country = country;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String toString() {
		return "Address2 [country=" + this.country + ", city=" + this.city + "]";
	}
}
