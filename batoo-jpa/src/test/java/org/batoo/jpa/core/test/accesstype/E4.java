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
package org.batoo.jpa.core.test.accesstype;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * 
 * 
 * @author hceylan
 * @since $version
 */
@Entity
public class E4 extends E {

	@ManyToOne
	private E1 e1;

	/**
	 * Returns the e1 of the E3.
	 * 
	 * @return the e1 of the E3
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public E1 getE1() {
		return this.e1;
	}

	/**
	 * Sets the e1 of the E3.
	 * 
	 * @param e1
	 *            the e1 to set for E3
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void setE1(E1 e1) {
		this.e1 = e1;
	}
}
