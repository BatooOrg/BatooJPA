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
package org.batoo.jpa.core.test.method;

import java.util.List;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

import com.google.common.collect.Lists;

/**
 * 
 * @author hceylan
 * @since 2.0.0
 */
@Entity
@Access(AccessType.PROPERTY)
public class Person {

	private Integer id;

	private List<Address> addresses = Lists.newArrayList();
	private List<Phone> phones = Lists.newArrayList();

	private String name;

	/**
	 * @since 2.0.0
	 */
	public Person() {
		super();
	}

	/**
	 * @param name
	 *            the name
	 * 
	 * @since 2.0.0
	 */
	public Person(String name) {
		super();

		this.name = name;
	}

	/**
	 * Returns the addresses.
	 * 
	 * @return the addresses
	 * @since 2.0.0
	 */
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	public List<Address> getAddresses() {
		return this.addresses;
	}

	/**
	 * Returns the id.
	 * 
	 * @return the id
	 * @since 2.0.0
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	public Integer getId() {
		return this.id;
	}

	/**
	 * Returns the name.
	 * 
	 * @return the name
	 * @since 2.0.0
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Returns the phones of the Person.
	 * 
	 * @return the phones of the Person
	 * 
	 * @since 2.0.0
	 */
	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = "person")
	public List<Phone> getPhones() {
		return this.phones;
	}

	/**
	 * Sets the addresses of the Person.
	 * 
	 * @param addresses
	 *            the addresses to set for Person
	 * 
	 * @since 2.0.0
	 */
	public void setAddresses(List<Address> addresses) {
		this.addresses = addresses;
	}

	/**
	 * Sets the id of the Person.
	 * 
	 * @param id
	 *            the id to set for Person
	 * 
	 * @since 2.0.0
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * Sets the name.
	 * 
	 * @param name
	 *            the name to set
	 * @since 2.0.0
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Sets the phones of the Person.
	 * 
	 * @param phones
	 *            the phones to set for Person
	 * 
	 * @since 2.0.0
	 */
	public void setPhones(List<Phone> phones) {
		this.phones = phones;
	}
}
