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
package org.batoo.jpa.core.test.manytoonetomany;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;

/**
 * 
 * @author hceylan
 * @since $version
 */
@Entity
public class Phone {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Integer id;

	@ManyToOne
	@JoinTable
	private Person person;

	private String phone;

	/**
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public Phone() {
		super();
	}

	/**
	 * @param person
	 *            the person
	 * @param phone
	 *            the phone
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public Phone(Person person, String phone) {
		super();

		this.person = person;
		this.phone = phone;

		person.getPhones().add(this);
	}

	/**
	 * Returns the id of the Phone.
	 * 
	 * @return the id of the Phone
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public Integer getId() {
		return this.id;
	}

	/**
	 * Returns the person of the Phone.
	 * 
	 * @return the person of the Phone
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public Person getPerson() {
		return this.person;
	}

	/**
	 * Returns the phone of the Phone.
	 * 
	 * @return the phone of the Phone
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public String getPhone() {
		return this.phone;
	}

	/**
	 * Sets the person of the Phone.
	 * 
	 * @param person
	 *            the person to set for Phone
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void setPerson(Person person) {
		this.person = person;
	}

	/**
	 * Sets the phone of the Phone.
	 * 
	 * @param phone
	 *            the phone to set for Phone
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void setPhone(String phone) {
		this.phone = phone;
	}

}
