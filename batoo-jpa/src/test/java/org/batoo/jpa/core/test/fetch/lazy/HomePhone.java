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
package org.batoo.jpa.core.test.fetch.lazy;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

/**
 * 
 * @author hceylan
 * @since 2.0.0
 */
@Entity
public class HomePhone extends Phone {

	@ManyToOne(fetch = FetchType.LAZY)
	private Person person;

	/**
	 * 
	 * @since 2.0.0
	 */
	public HomePhone() {
		super();
	}

	/**
	 * @param person
	 *            the person
	 * @param phone
	 *            the phone
	 * 
	 * @since 2.0.0
	 */
	public HomePhone(Person person, String phone) {
		super(phone);

		this.person = person;

		this.person.getPhones().add(this);
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
