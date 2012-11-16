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
package org.batoo.jpa.core.test.mapsid;

import javax.persistence.Embeddable;

/**
 * 
 * 
 * @author hceylan
 * @since $version
 */
@Embeddable
public class PhonePk {

	private Integer id;
	private Integer personId;

	/**
	 * 
	 * @since $version
	 */
	public PhonePk() {
		super();
	}

	/**
	 * @param id
	 *            the id
	 * @param personId
	 *            the personId
	 * 
	 * @since $version
	 */
	public PhonePk(Integer id, Integer personId) {
		super();

		this.id = id;
		this.personId = personId;
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

		if (this.getClass() != obj.getClass()) {
			return false;
		}

		final PhonePk other = (PhonePk) obj;
		if (this.id == null) {
			return false;
		}
		else if (!this.id.equals(other.id)) {
			return false;
		}

		if (this.personId == null) {
			return false;
		}
		else if (!this.personId.equals(other.personId)) {
			return false;
		}

		return true;
	}

	/**
	 * Returns the id of the PhonePk.
	 * 
	 * @return the id of the PhonePk
	 * 
	 * @since $version
	 */
	protected Integer getId() {
		return this.id;
	}

	/**
	 * Returns the personId of the PhonePk.
	 * 
	 * @return the personId of the PhonePk
	 * 
	 * @since $version
	 */
	protected Integer getPersonId() {
		return this.personId;
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
		result = (prime * result) + ((this.personId == null) ? 0 : this.personId.hashCode());
		return result;
	}

	/**
	 * Sets the id of the PhonePk.
	 * 
	 * @param id
	 *            the id to set for PhonePk
	 * 
	 * @since $version
	 */
	protected void setId(Integer id) {
		this.id = id;
	}

	/**
	 * Sets the personId of the PhonePk.
	 * 
	 * @param personId
	 *            the personId to set for PhonePk
	 * 
	 * @since $version
	 */
	protected void setPersonId(Integer personId) {
		this.personId = personId;
	}
}
