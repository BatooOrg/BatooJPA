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

package org.batoo.jpa.core.test.derivedIdentities.e2b;

import javax.persistence.Embeddable;
import javax.persistence.Embedded;

/**
 * 
 * @author asimarslan
 * @since 2.0.0
 */
@Embeddable
public class DependentId {

	private String name;

	@Embedded
	private EmployeeId empPK;

	/**
	 * 
	 * @since 2.0.0
	 */
	public DependentId() {
		super();
	}

	/**
	 * @param name
	 *            the name
	 * @param empPK
	 *            the employe id
	 * 
	 * @since 2.0.0
	 */
	public DependentId(String name, EmployeeId empPK) {
		super();

		this.name = name;
		this.empPK = empPK;
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
		if (!(obj instanceof DependentId)) {
			return false;
		}
		final DependentId other = (DependentId) obj;
		if (this.empPK == null) {
			if (other.empPK != null) {
				return false;
			}
		}
		else if (!this.empPK.equals(other.empPK)) {
			return false;
		}
		if (this.name == null) {
			if (other.name != null) {
				return false;
			}
		}
		else if (!this.name.equals(other.name)) {
			return false;
		}
		return true;
	}

	/**
	 * @return the empPK
	 * @since 2.0.0
	 */
	public EmployeeId getEmpPK() {
		return this.empPK;
	}

	/**
	 * @return the name
	 * @since 2.0.0
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((this.empPK == null) ? 0 : this.empPK.hashCode());
		result = (prime * result) + ((this.name == null) ? 0 : this.name.hashCode());
		return result;
	}

	/**
	 * @param empPK
	 *            the empPK to set
	 * @since 2.0.0
	 */
	public void setEmpPK(EmployeeId empPK) {
		this.empPK = empPK;
	}

	/**
	 * @param name
	 *            the name to set
	 * @since 2.0.0
	 */
	public void setName(String name) {
		this.name = name;
	}
}
