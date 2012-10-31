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
package org.batoo.jpa.community.test.t3;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * 
 * 
 * @author hceylan
 * @since $version
 */
@Entity
public class E2 extends E {

	@ManyToOne
	private E3 e3;

	/**
	 * @param e3
	 *            the e3
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public E2(E3 e3) {
		super();

		this.e3 = e3;
	}

	/**
	 * Returns the e3 of the E2.
	 * 
	 * @return the e3 of the E2
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public E3 getE3() {
		return this.e3;
	}

	/**
	 * Sets the e3 of the E2.
	 * 
	 * @param e3
	 *            the e3 to set for E2
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void setE3(E3 e3) {
		this.e3 = e3;
	}
}
