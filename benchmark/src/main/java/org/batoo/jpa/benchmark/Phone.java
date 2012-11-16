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
package org.batoo.jpa.benchmark;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.TableGenerator;

/**
 * 
 * @author hceylan
 * @since 2.0.0
 */
@Entity
@TableGenerator(name = "phone_id", allocationSize = 1000)
public class Phone {

	@Id
	@GeneratedValue(generator = "phone_id", strategy = GenerationType.TABLE)
	private Integer id;

	@ManyToOne
	private Person person;

	private String phoneNo;

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		final Phone other = (Phone) obj;
		if (this.id == null) {
			return false;
		}
		else if (!this.id.equals(other.id)) {
			return false;
		}
		return true;
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
	 * Returns the person.
	 * 
	 * @return the person
	 * @since 2.0.0
	 */
	public Person getPerson() {
		return this.person;
	}

	/**
	 * Returns the phoneNo.
	 * 
	 * @return the phoneNo
	 * @since 2.0.0
	 */
	public String getPhoneNo() {
		return this.phoneNo;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((this.id == null) ? 0 : this.id.hashCode());
		return result;
	}

	/**
	 * Sets the id.
	 * 
	 * @param id
	 *            the id to set
	 * @since 2.0.0
	 */
	public void setId(Integer id) {
		this.id = id;
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

	/**
	 * Sets the phoneNo.
	 * 
	 * @param phoneNo
	 *            the phoneNo to set
	 * @since 2.0.0
	 */
	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}

}
