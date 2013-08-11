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

package org.batoo.jpa.core.test.embeddable.overrides;

import javax.persistence.Embeddable;

/**
 * 
 * @author hceylan
 * @since 2.0.0
 */
@Embeddable
public class Address {

	private String street;
	private String city;

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
	 * Returns the street.
	 * 
	 * @return the street
	 * @since 2.0.0
	 */
	public String getStreet() {
		return this.street;
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
	 * Sets the street.
	 * 
	 * @param street
	 *            the street to set
	 * @since 2.0.0
	 */
	public void setStreet(String street) {
		this.street = street;
	}
}
