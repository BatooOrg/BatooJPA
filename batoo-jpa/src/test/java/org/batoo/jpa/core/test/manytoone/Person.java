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
package org.batoo.jpa.core.test.manytoone;

import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.FetchType.LAZY;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
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
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Integer id;

	@Column(name="name")
	private String name;

	@Column(name = "address_id", insertable = false, updatable = false)
	private Integer addressId;

	@JoinColumn(name = "address_id")
	@ManyToOne(cascade = PERSIST, fetch = LAZY)
	private Address homeAddress; // (addressId)

	
	/**
	 * @since $version
	 * @author hceylan
	 */
	public Person() {
		super();
	}

	/**
	 * @param name
	 *            the name of the person
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public Person(String name) {
		super();

		this.name = name;
	}

	/**
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the addressId
	 */
	public Integer getAddressId() {
		return addressId;
	}

	/**
	 * @param addressId the addressId to set
	 */
	public void setAddressId(Integer addressId) {
		this.addressId = addressId;
	}

	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
	// many-to-one: Person.addressId ==> Address.id
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

	public Address getHomeAddress() {
		return homeAddress;
	}

	/**
	 * Set the homeAddress without adding this Account instance on the passed
	 * homeAddress If you want to preserve referential integrity we recommend to
	 * use instead the corresponding adder method provided by {@link Address}
	 */
	public void setHomeAddress(Address homeAddress) {
		this.homeAddress = homeAddress;

		// We set the foreign key property so it can be used by our JPA search
		// by Example facility.
		if (homeAddress != null) {
			setAddressId(homeAddress.getId());
		} else {
			setAddressId(null);
		}
	}


}
