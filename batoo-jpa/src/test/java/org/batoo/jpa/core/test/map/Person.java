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
package org.batoo.jpa.core.test.map;

import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;

import com.google.common.collect.Maps;

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

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	private final Map<Integer, Address> addresses1 = Maps.newHashMap();

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinTable(name = "Person_Address2")
	@MapKey(name = "city")
	private final Map<String, Address> addresses2 = Maps.newHashMap();

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER, mappedBy = "person")
	private final Map<PhoneId, Phone> phones = Maps.newHashMap();

	private String name;

	/**
	 * @since $version
	 */
	public Person() {
		super();
	}

	/**
	 * @param name
	 *            the name
	 * 
	 * @since $version
	 */
	public Person(String name) {
		super();

		this.name = name;
	}

	/**
	 * Returns the addresses1 of the Person.
	 * 
	 * @return the addresses1 of the Person
	 * 
	 * @since $version
	 */
	public Map<Integer, Address> getAddresses1() {
		return this.addresses1;
	}

	/**
	 * Returns the addresses2 of the Person.
	 * 
	 * @return the addresses2 of the Person
	 * 
	 * @since $version
	 */
	protected Map<String, Address> getAddresses2() {
		return this.addresses2;
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
	 * Returns the phones of the Person.
	 * 
	 * @return the phones of the Person
	 * 
	 * @since $version
	 */
	protected Map<PhoneId, Phone> getPhones() {
		return this.phones;
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
}
