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
package org.batoo.jpa.core.test.orderby;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

import com.google.common.collect.Lists;

/**
 * 
 * @author hceylan
 * @since 2.0.0
 */
@Entity
public class Person {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Integer id;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "person")
	@OrderBy
	private final List<Address> addresses = Lists.newArrayList();

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "person")
	@OrderBy("city desc, street")
	private final List<Address2> addresses2 = Lists.newArrayList();

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
	public List<Address> getAddresses() {
		return this.addresses;
	}

	/**
	 * Returns the addresses2 of the Person.
	 * 
	 * @return the addresses2 of the Person
	 * 
	 * @since 2.0.0
	 */
	protected List<Address2> getAddresses2() {
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
