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

package org.batoo.jpa.community.test.i142;

import java.util.Date;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;

import org.joda.time.DateTime;

/**
 * 
 * 
 * @author hceylan
 * @since 2.0.1
 */
@Entity
public class MyEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Transient
	private DateTime someDate;

	/**
	 * 
	 * @since 2.0.1
	 */
	public MyEntity() {
		super();

		this.someDate = new DateTime();
	}

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
		if (!(obj instanceof MyEntity)) {
			return false;
		}
		final MyEntity other = (MyEntity) obj;
		if (this.id == null) {
			if (other.id != null) {
				return false;
			}
		}
		else if (!this.id.equals(other.id)) {
			return false;
		}
		if (this.someDate == null) {
			if (other.someDate != null) {
				return false;
			}
		}
		else if (!this.someDate.equals(other.someDate)) {
			return false;
		}
		return true;
	}

	/**
	 * Returns the date
	 * 
	 * @return the date
	 * 
	 * @since 2.0.1
	 */
	@Access(AccessType.PROPERTY)
	public Date getDate() {
		return this.someDate.toDate();
	}

	/**
	 * Returns the id of the MyEntity.
	 * 
	 * @return the id of the MyEntity
	 * 
	 * @since 2.0.1
	 */
	public Integer getId() {
		return this.id;
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
		result = (prime * result) + ((this.someDate == null) ? 0 : this.someDate.hashCode());
		return result;
	}

	/**
	 * Sets the date
	 * 
	 * @param date
	 *            the date to set
	 * 
	 * @since 2.0.1
	 */
	public void setDate(Date date) {
		this.someDate = new DateTime(date);
	}

	/**
	 * Sets the id of the MyEntity.
	 * 
	 * @param id
	 *            the id to set for MyEntity
	 * 
	 * @since 2.0.1
	 */
	public void setId(Integer id) {
		this.id = id;
	}
}
