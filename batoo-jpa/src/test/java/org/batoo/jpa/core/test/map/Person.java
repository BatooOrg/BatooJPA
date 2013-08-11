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
 * @since 2.0.0
 */
@Entity
public class Person {

	// public enum KeyEnum {
	// key1,
	// key2,
	// key3,
	// key4;
	// }

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Integer id;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	private final Map<Integer, Address> addresses1 = Maps.newHashMap();

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinTable(name = "Person_Address2")
	@MapKey(name = "city")
	private final Map<String, Address> addresses2 = Maps.newHashMap();

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY, mappedBy = "person")
	private final Map<PhoneId, Phone> phones = Maps.newHashMap();

	// @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	// @JoinTable(name = "Person_Address3")
	// @MapKey(name = "city")
	// @MapKeyEnumerated(EnumType.STRING)
	// private final Map<KeyEnum, Address> addresses3 = Maps.newHashMap();

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
	 * Returns the addresses1 of the Person.
	 * 
	 * @return the addresses1 of the Person
	 * 
	 * @since 2.0.0
	 */
	public Map<Integer, Address> getAddresses1() {
		return this.addresses1;
	}

	/**
	 * Returns the addresses2 of the Person.
	 * 
	 * @return the addresses2 of the Person
	 * 
	 * @since 2.0.0
	 */
	protected Map<String, Address> getAddresses2() {
		return this.addresses2;
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
	protected Map<PhoneId, Phone> getPhones() {
		return this.phones;
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
}
