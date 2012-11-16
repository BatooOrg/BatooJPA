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
package org.batoo.jpa.core.test.manytomany;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

import com.google.common.collect.Lists;

/**
 * 
 * @author hceylan
 * @since $version
 */
@Entity
public class PhoneNumber {

	@Id
	@GeneratedValue
	private Integer id;

	private String number;

	@ManyToMany(mappedBy = "phoneNumbers")
	private final List<Customer> customers = Lists.newArrayList();

	/**
	 * @since $version
	 */
	public PhoneNumber() {
		super();
	}

	/**
	 * @param number
	 *            the number
	 * 
	 * @since $version
	 */
	public PhoneNumber(String number) {
		super();

		this.number = number;
	}

	/**
	 * Returns the customers.
	 * 
	 * @return the customers
	 * @since $version
	 */
	public List<Customer> getCustomers() {
		return this.customers;
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
	 * Returns the number.
	 * 
	 * @return the number
	 * @since $version
	 */
	public String getNumber() {
		return this.number;
	}

	/**
	 * Sets the number.
	 * 
	 * @param number
	 *            the number to set
	 * @since $version
	 */
	public void setNumber(String number) {
		this.number = number;
	}

}
