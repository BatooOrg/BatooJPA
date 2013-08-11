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

package org.batoo.jpa.core.test.inheritence.complex;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

/**
 * 
 * 
 * @author hceylan
 * @since 2.0.0
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(discriminatorType = DiscriminatorType.STRING, length = 3, name = "PERSON_TYPE")
public abstract class Person extends BaseEntity {

	@Column(length = 25, nullable = false)
	private String firstName;

	@Column(length = 25, nullable = false)
	private String lastName;

	/**
	 * 
	 * @since 2.0.0
	 */
	public Person() {
		super();
	}

	/**
	 * @param firstName
	 *            the first name
	 * @param lastName
	 *            the last name
	 * 
	 * @since 2.0.0
	 */
	public Person(String firstName, String lastName) {
		super();

		this.firstName = firstName;
		this.lastName = lastName;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getDisplayText() {
		return this.getFirstName() + " " + this.getLastName();
	}

	/**
	 * Returns the firstName of the Person.
	 * 
	 * @return the firstName of the Person
	 * 
	 * @since 2.0.0
	 */
	public String getFirstName() {
		return this.firstName;
	}

	/**
	 * Returns the lastName of the Person.
	 * 
	 * @return the lastName of the Person
	 * 
	 * @since 2.0.0
	 */
	public String getLastName() {
		return this.lastName;
	}

	/**
	 * Sets the firstName of the Person.
	 * 
	 * @param firstName
	 *            the firstName to set for Person
	 * 
	 * @since 2.0.0
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/**
	 * Sets the lastName of the Person.
	 * 
	 * @param lastName
	 *            the lastName to set for Person
	 * 
	 * @since 2.0.0
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
}
