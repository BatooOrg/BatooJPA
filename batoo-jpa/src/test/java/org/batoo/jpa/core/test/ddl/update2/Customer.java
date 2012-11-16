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
package org.batoo.jpa.core.test.ddl.update2;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import com.google.common.collect.Lists;

/**
 * 
 * 
 * @author hceylan
 * @since $version
 */
@Entity
public class Customer {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	private String title;
	private String name;
	private String surname;

	@OneToMany
	private final List<Address> addresses = Lists.newArrayList();

	/**
	 * 
	 * @since $version
	 */
	public Customer() {
		super();
	}

	/**
	 * Returns the addresses of the Customer.
	 * 
	 * @return the addresses of the Customer
	 * 
	 * @since $version
	 */
	public List<Address> getAddresses() {
		return this.addresses;
	}

	/**
	 * Returns the id of the Customer.
	 * 
	 * @return the id of the Customer
	 * 
	 * @since $version
	 */
	public Integer getId() {
		return this.id;
	}

	/**
	 * Returns the name of the Customer.
	 * 
	 * @return the name of the Customer
	 * 
	 * @since $version
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Returns the surname of the Customer.
	 * 
	 * @return the surname of the Customer
	 * 
	 * @since $version
	 */
	public String getSurname() {
		return this.surname;
	}

	/**
	 * Returns the title of the Customer.
	 * 
	 * @return the title of the Customer
	 * 
	 * @since $version
	 */
	public String getTitle() {
		return this.title;
	}

	/**
	 * Sets the name of the Customer.
	 * 
	 * @param name
	 *            the name to set for Customer
	 * 
	 * @since $version
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Sets the surname of the Customer.
	 * 
	 * @param surname
	 *            the surname to set for Customer
	 * 
	 * @since $version
	 */
	public void setSurname(String surname) {
		this.surname = surname;
	}

	/**
	 * Sets the title of the Customer.
	 * 
	 * @param title
	 *            the title to set for Customer
	 * 
	 * @since $version
	 */
	public void setTitle(String title) {
		this.title = title;
	}

}
