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
package org.batoo.jpa.core.test.derivedIdentities.e2b;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * 
 * 
 * @author hceylan
 * @since $version
 */
@Entity
public class Venue {

	@Id
	private String venueCode;

	private String name;

	/**
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public Venue() {
		super();
	}

	/**
	 * @param venueCode
	 *            the code
	 * @param name
	 *            the name
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public Venue(String venueCode, String name) {
		super();

		this.venueCode = venueCode;
		this.name = name;
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
		if (!(obj instanceof Venue)) {
			return false;
		}
		final Venue other = (Venue) obj;
		if (this.venueCode == null) {
			if (other.venueCode != null) {
				return false;
			}
		}
		else if (!this.venueCode.equals(other.venueCode)) {
			return false;
		}
		return true;
	}

	/**
	 * Returns the name of the Venue.
	 * 
	 * @return the name of the Venue
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Returns the venueCode of the Venue.
	 * 
	 * @return the venueCode of the Venue
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public String getVenueCode() {
		return this.venueCode;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((this.venueCode == null) ? 0 : this.venueCode.hashCode());
		return result;
	}

	/**
	 * Sets the name of the Venue.
	 * 
	 * @param name
	 *            the name to set for Venue
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Sets the venueCode of the Venue.
	 * 
	 * @param venueCode
	 *            the venueCode to set for Venue
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void setVenueCode(String venueCode) {
		this.venueCode = venueCode;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String toString() {
		return "Venue [venueCode=" + this.venueCode + ", name=" + this.name + "]";
	}
}
