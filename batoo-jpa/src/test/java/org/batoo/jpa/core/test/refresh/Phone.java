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
package org.batoo.jpa.core.test.refresh;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
	private Person person;

	private String phoneNo;

	/**
	 * @since $version
	 */
	public Phone() {
		super();
	}

	/**
	 * @param person
	 *            the person
	 * @param phoneNo
	 *            the phone number
	 * 
	 * @since $version
	 */
	public Phone(Person person, String phoneNo) {
		super();

		this.person = person;
		this.phoneNo = phoneNo;

		this.person.getPhones().add(this);
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
	 * Returns the person.
	 * 
	 * @return the person
	 * @since $version
	 */
	public Person getPerson() {
		return this.person;
	}

	/**
	 * Returns the phoneNo.
	 * 
	 * @return the phoneNo
	 * @since $version
	 */
	public String getPhoneNo() {
		return this.phoneNo;
	}

	/**
	 * Sets the person.
	 * 
	 * @param person
	 *            the person to set
	 * @since $version
	 */
	public void setPerson(Person person) {
		this.person = person;
	}

	/**
	 * Sets the phoneNo.
	 * 
	 * @param phoneNo
	 *            the phoneNo to set
	 * @since $version
	 */
	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}
}
