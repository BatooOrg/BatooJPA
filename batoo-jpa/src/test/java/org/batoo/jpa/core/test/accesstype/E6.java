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

package org.batoo.jpa.core.test.accesstype;

import javax.persistence.Entity;

/**
 * 
 * 
 * @author hceylan
 * @since 2.0.0
 */
@Entity
public class E6 extends E {

	private String transiet;

	/**
	 * Returns the transiet of the E6.
	 * 
	 * @return the transiet of the E6
	 * 
	 * @since 2.0.0
	 */
	public String getTransiet() {
		return this.transiet;
	}

	/**
	 * Sets the transiet of the E6.
	 * 
	 * @param transiet
	 *            the transiet to set for E6
	 * 
	 * @since 2.0.0
	 */
	public void setTransiet(String transiet) {
		this.transiet = transiet;
	}
}
