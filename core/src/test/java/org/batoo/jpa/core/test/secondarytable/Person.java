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
package org.batoo.jpa.core.test.secondarytable;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SecondaryTable;

import com.google.common.collect.Lists;

/**
 * 
 * @author hceylan
 * @since $version
 */
@Entity
@SecondaryTable(name = "PersonExtra")
public class Person {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Integer id;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER, mappedBy = "person")
	private final List<Address> addresses = Lists.newArrayList();

	private String name;

	@Column(table = "PersonExtra")
	private String surname;

	/**
	 * @since $version
	 * @author hceylan
	 */
	public Person() {
		super();
	}

	/**
	 * @param name
	 *            the name
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public Person(String name) {
		super();

		this.name = name;
	}

	/**
	 * Returns the addresses.
	 * 
	 * @return the addresses
	 * @since $version
	 */
	public List<Address> getAddresses() {
		return this.addresses;
	}

	/**
	 * Returns the id.
	 * 
	 * @return the id
	 * @since $version
	 */
	public Integer getId() {
		return this.id;
	}

	/**
	 * Returns the name.
	 * 
	 * @return the name
	 * @since $version
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Returns the surname.
	 * 
	 * @return the surname
	 * @since $version
	 */
	public String getSurname() {
		return this.surname;
	}

	/**
	 * Sets the name.
	 * 
	 * @param name
	 *            the name to set
	 * @since $version
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Sets the surname.
	 * 
	 * @param surname
	 *            the surname to set
	 * @since $version
	 */
	public void setSurname(String surname) {
		this.surname = surname;
	}
}
