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

/**
 * 
 * @author hceylan
 * @since 2.0.0
 */
public class SimpleCity implements Comparable<SimpleCity> {

	private String city;
	private String country;

	/**
	 * @param city
	 *            the city
	 * @param country
	 *            the country
	 * 
	 * @since 2.0.0
	 */
	public SimpleCity(String city, String country) {
		super();

		this.city = city;
		this.country = country;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public int compareTo(SimpleCity o) {
		if (this.country.compareTo(o.country) != 0) {
			return this.country.compareTo(o.country);
		}

		if (this.city.compareTo(o.city) != 0) {
			return this.city.compareTo(o.city);
		}

		return 0;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		final SimpleCity other = (SimpleCity) obj;
		if (this.city == null) {
			if (other.city != null) {
				return false;
			}
		}
		else if (!this.city.equals(other.city)) {
			return false;
		}
		if (this.country == null) {
			if (other.country != null) {
				return false;
			}
		}
		else if (!this.country.equals(other.country)) {
			return false;
		}
		return true;
	}

	/**
	 * Returns the city of the SimpleCity.
	 * 
	 * @return the city of the SimpleCity
	 * 
	 * @since 2.0.0
	 */
	protected String getCity() {
		return this.city;
	}

	/**
	 * Returns the country of the SimpleCity.
	 * 
	 * @return the country of the SimpleCity
	 * 
	 * @since 2.0.0
	 */
	protected String getCountry() {
		return this.country;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((this.city == null) ? 0 : this.city.hashCode());
		result = (prime * result) + ((this.country == null) ? 0 : this.country.hashCode());
		return result;
	}

	/**
	 * Sets the city of the SimpleCity.
	 * 
	 * @param city
	 *            the city to set for SimpleCity
	 * 
	 * @since 2.0.0
	 */
	protected void setCity(String city) {
		this.city = city;
	}

	/**
	 * Sets the country of the SimpleCity.
	 * 
	 * @param country
	 *            the country to set for SimpleCity
	 * 
	 * @since 2.0.0
	 */
	protected void setCountry(String country) {
		this.country = country;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String toString() {
		return "SimpleCity [city=" + this.city + ", country=" + this.country + "]";
	}

}
