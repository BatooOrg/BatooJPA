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

import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

/**
 * 
 * 
 * @author hceylan
 * @since 2.0.0
 */
@Entity
public class E1 extends E {

	private String v1;
	private int v2;
	private List<E2> e2;

	private String transiet;

	/**
	 * Returns the e2 of the E1.
	 * 
	 * @return the e2 of the E1
	 * 
	 * @since 2.0.0
	 */
	@OneToMany
	public List<E2> getE2() {
		return this.e2;
	}

	/**
	 * Returns the transiet of the E1.
	 * 
	 * @return the transiet of the E1
	 * 
	 * @since 2.0.0
	 */
	@Transient
	public String getTransiet() {
		return this.transiet;
	}

	/**
	 * Returns the v1 of the E1.
	 * 
	 * @return the v1 of the E1
	 * 
	 * @since 2.0.0
	 */
	@Basic
	public String getV1() {
		return this.v1;
	}

	/**
	 * Returns the v2 of the E1.
	 * 
	 * @return the v2 of the E1
	 * 
	 * @since 2.0.0
	 */
	public int getV2() {
		return this.v2;
	}

	/**
	 * Sets the e2 of the E1.
	 * 
	 * @param e2
	 *            the e2 to set for E1
	 * 
	 * @since 2.0.0
	 */
	public void setE2(List<E2> e2) {
		this.e2 = e2;
	}

	/**
	 * Sets the transiet of the E1.
	 * 
	 * @param transiet
	 *            the transiet to set for E1
	 * 
	 * @since 2.0.0
	 */
	public void setTransiet(String transiet) {
		this.transiet = transiet;
	}

	/**
	 * Sets the v1 of the E1.
	 * 
	 * @param v1
	 *            the v1 to set for E1
	 * 
	 * @since 2.0.0
	 */
	public void setV1(String v1) {
		this.v1 = v1;
	}

	/**
	 * Sets the v2 of the E1.
	 * 
	 * @param v2
	 *            the v2 to set for E1
	 * 
	 * @since 2.0.0
	 */
	public void setV2(int v2) {
		this.v2 = v2;
	}
}
