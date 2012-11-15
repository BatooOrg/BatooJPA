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
package org.batoo.jpa.core.test.readonlycolumn;

import static javax.persistence.CascadeType.PERSIST;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * 
 * @author hceylan
 * @since $version
 */
@Entity
public class Person {

	@Id
	private Integer id;

	@Column(name = "name")
	private String name;

	@Column(name = "address_id")
	private Integer addressId;

	@JoinColumn(name = "address_id", insertable = false, updatable = false)
	@ManyToOne(cascade = PERSIST, fetch = FetchType.LAZY)
	private Address homeAddress; // (addressId)

	/**
	 * @since $version
	 * @author hceylan
	 */
	public Person() {
		super();
	}

	/**
	 * @param id
	 *            the id
	 * @param name
	 *            the name of the person
	 * @param address
	 *            the address
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public Person(Integer id, String name, Address address) {
		super();

		this.id = id;
		this.name = name;

		this.setHomeAddress(address);
	}

	/**
	 * @return the addressId
	 * @since $version
	 */
	public Integer getAddressId() {
		return this.addressId;
	}

	/**
	 * @return the home address
	 * 
	 */
	public Address getHomeAddress() {
		return this.homeAddress;
	}

	/**
	 * @return the id
	 */
	public Integer getId() {
		return this.id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @param addressId
	 *            the addressId to set
	 * @since $version
	 */
	public void setAddressId(Integer addressId) {
		this.addressId = addressId;
	}

	/**
	 * Set the homeAddress without adding this Account instance on the passed homeAddress If you want to preserve referential integrity we
	 * recommend to use instead the corresponding adder method provided by {@link Address}
	 * 
	 * @param homeAddress
	 *            the home address to set
	 */
	public void setHomeAddress(Address homeAddress) {
		this.homeAddress = homeAddress;

		this.addressId = homeAddress.getId();
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
}
