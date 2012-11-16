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
package org.batoo.jpa.core.test.q;

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
public class Person2 {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Integer id;

	@Embedded
	private Contact contact;

	private String name;

	/**
	 * @since 2.0.0
	 */
	public Person2() {
		super();
	}

	/**
	 * @param name
	 *            the name
	 * @param city
	 *            the city
	 * @param country
	 *            the country
	 * @param phone
	 *            the phone
	 * 
	 * @since 2.0.0
	 */
	public Person2(String name, String city, Country country, String phone) {
		super();

		this.name = name;
		this.contact = new Contact(city, country, phone);
	}

	/**
	 * Returns the contact of the Person2.
	 * 
	 * @return the contact of the Person2
	 * 
	 * @since 2.0.0
	 */
	protected Contact getContact() {
		return this.contact;
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
	 * Sets the contact of the Person2.
	 * 
	 * @param contact
	 *            the contact to set for Person2
	 * 
	 * @since 2.0.0
	 */
	protected void setContact(Contact contact) {
		this.contact = contact;
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
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String toString() {
		return "[name=" + this.name + ", contact=" + this.contact + "]";
	}
}
