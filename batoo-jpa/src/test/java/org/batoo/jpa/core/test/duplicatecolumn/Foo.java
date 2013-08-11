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

package org.batoo.jpa.core.test.duplicatecolumn;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * 
 * @author hceylan
 * @since 2.0.0
 */
@Entity
public class Foo {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	private String value;

	@Embedded
	private Address homeAddress;

	@Embedded
	private Address workAddress;

	/**
	 * Returns the homeAddress.
	 * 
	 * @return the homeAddress
	 * @since 2.0.0
	 */
	public Address getHomeAddress() {
		return this.homeAddress;
	}

	/**
	 * Returns the id.
	 * 
	 * @return the id
	 * @since 2.0.0
	 */
	public Integer getId() {
		return this.id;
	}

	/**
	 * Returns the value.
	 * 
	 * @return the value
	 * @since 2.0.0
	 */
	public String getValue() {
		return this.value;
	}

	/**
	 * Returns the workAddress.
	 * 
	 * @return the workAddress
	 * @since 2.0.0
	 */
	public Address getWorkAddress() {
		return this.workAddress;
	}

	/**
	 * Sets the homeAddress.
	 * 
	 * @param homeAddress
	 *            the homeAddress to set
	 * @since 2.0.0
	 */
	public void setHomeAddress(Address homeAddress) {
		this.homeAddress = homeAddress;
	}

	/**
	 * Sets the value.
	 * 
	 * @param value
	 *            the value to set
	 * @since 2.0.0
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * Sets the workAddress.
	 * 
	 * @param workAddress
	 *            the workAddress to set
	 * @since 2.0.0
	 */
	public void setWorkAddress(Address workAddress) {
		this.workAddress = workAddress;
	}
}
