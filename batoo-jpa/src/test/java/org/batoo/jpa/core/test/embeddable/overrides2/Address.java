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
package org.batoo.jpa.core.test.embeddable.overrides2;

import javax.persistence.Embeddable;
import javax.persistence.Embedded;

/**
 * 
 * @author hceylan
 * @since $version
 */
@Embeddable
public class Address {

	@Embedded
	private Zipcode zipcode;

	private String city;

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
	 * Returns the zipcode.
	 * 
	 * @return the zipcode
	 * @since $version
	 */
	public Zipcode getZipcode() {
		return this.zipcode;
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
	 * Sets the zipcode.
	 * 
	 * @param zipcode
	 *            the zipcode to set
	 * @since $version
	 */
	public void setZipcode(Zipcode zipcode) {
		this.zipcode = zipcode;
	}

}
