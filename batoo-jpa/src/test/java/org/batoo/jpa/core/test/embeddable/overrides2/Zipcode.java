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

/**
 * 
 * @author hceylan
 * @since 2.0.0
 */
@Embeddable
public class Zipcode {

	private String zip;

	private String plusfour;

	/**
	 * Returns the plusfour.
	 * 
	 * @return the plusfour
	 * @since 2.0.0
	 */
	public String getPlusfour() {
		return this.plusfour;
	}

	/**
	 * Returns the zip.
	 * 
	 * @return the zip
	 * @since 2.0.0
	 */
	public String getZip() {
		return this.zip;
	}

	/**
	 * Sets the plusfour.
	 * 
	 * @param plusfour
	 *            the plusfour to set
	 * @since 2.0.0
	 */
	public void setPlusfour(String plusfour) {
		this.plusfour = plusfour;
	}

	/**
	 * Sets the zip.
	 * 
	 * @param zip
	 *            the zip to set
	 * @since 2.0.0
	 */
	public void setZip(String zip) {
		this.zip = zip;
	}
}
