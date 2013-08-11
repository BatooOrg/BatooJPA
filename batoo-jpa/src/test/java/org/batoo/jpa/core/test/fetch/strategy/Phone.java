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

package org.batoo.jpa.core.test.fetch.strategy;

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
public class Phone {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Integer id;

	@ManyToOne
	private Person person;

	private String number;

	/**
	 * @since 2.0.0
	 */
	public Phone() {
		super();
	}

	/**
	 * @param person
	 *            the person
	 * @param number
	 *            the number
	 * 
	 * @since 2.0.0
	 */
	public Phone(Person person, String number) {
		super();

		this.person = person;
		this.number = number;

		this.person.getPhones().add(this);
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
	 * Returns the number of the Phone.
	 * 
	 * @return the number of the Phone
	 * 
	 * @since 2.0.0
	 */
	public String getNumber() {
		return this.number;
	}

	/**
	 * Returns the person.
	 * 
	 * @return the person
	 * @since 2.0.0
	 */
	public Person getPerson() {
		return this.person;
	}

	/**
	 * Sets the number of the Phone.
	 * 
	 * @param number
	 *            the number to set for Phone
	 * 
	 * @since 2.0.0
	 */
	public void setNumber(String number) {
		this.number = number;
	}

	/**
	 * Sets the person.
	 * 
	 * @param person
	 *            the person to set
	 * @since 2.0.0
	 */
	public void setPerson(Person person) {
		this.person = person;
	}
}
