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

package org.batoo.jpa.core.test.orderby;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 * 
 * @author hceylan
 * @since 2.0.0
 */
@Entity
public class Address2 {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Integer id;

	@ManyToOne
	private Person person;

	private String street;

	private String city;

	/**
	 * @since 2.0.0
	 */
	public Address2() {
		super();
	}

	/**
	 * @param person
	 *            the person
	 * @param city
	 *            the city
	 * @param street
	 *            the street
	 * 
	 * @since 2.0.0
	 */
	public Address2(Person person, String city, String street) {
		super();

		this.city = city;
		this.street = street;

		this.setPerson(person);
	}

	/**
	 * Returns the city.
	 * 
	 * @return the city
	 * @since 2.0.0
	 */
	public String getCity() {
		return this.city;
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
	 * Returns the person of the Address.
	 * 
	 * @return the person of the Address
	 * 
	 * @since 2.0.0
	 */
	protected Person getPerson() {
		return this.person;
	}

	/**
	 * Returns the street of the Address2.
	 * 
	 * @return the street of the Address2
	 * 
	 * @since 2.0.0
	 */
	protected String getStreet() {
		return this.street;
	}

	/**
	 * Sets the city.
	 * 
	 * @param city
	 *            the city to set
	 * @since 2.0.0
	 */
	public void setCity(String city) {
		this.city = city;
	}

	/**
	 * Sets the person of the Address.
	 * 
	 * @param person
	 *            the person to set for Address
	 * 
	 * @since 2.0.0
	 */
	protected void setPerson(Person person) {
		if (this.person != null) {
			this.person.getAddresses().remove(this);
		}

		this.person = person;

		if (this.person != null) {
			this.person.getAddresses2().add(this);
		}
	}

	/**
	 * Sets the street of the Address2.
	 * 
	 * @param street
	 *            the street to set for Address2
	 * 
	 * @since 2.0.0
	 */
	protected void setStreet(String street) {
		this.street = street;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String toString() {
		return "Address2 [city=" + this.city + ", street=" + this.street + "]";
	}
}
