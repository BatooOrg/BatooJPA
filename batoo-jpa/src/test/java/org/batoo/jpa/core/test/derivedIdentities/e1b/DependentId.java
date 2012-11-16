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
package org.batoo.jpa.core.test.derivedIdentities.e1b;

import javax.persistence.Embeddable;

/**
 * 
 * @author asimarslan
 * @since $version
 */
@Embeddable
public class DependentId {

	private String name;
	private Long empPk;

	/**
	 * 
	 * @since $version
	 */
	public DependentId() {
		super();
	}

	/**
	 * 
	 * @param name
	 *            the name
	 * @param empPk
	 *            the employee
	 * @since $version
	 */
	public DependentId(String name, Long empPk) {
		super();

		this.name = name;
		this.empPk = empPk;
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
		if (this.empPk == null) {
			if (other.empPk != null) {
				return false;
			}
		}
		else if (!this.empPk.equals(other.empPk)) {
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
	 * Returns the empPk of the DependentId.
	 * 
	 * @return the empPk of the DependentId
	 * 
	 * @since $version
	 */
	public Long getEmpPk() {
		return this.empPk;
	}

	/**
	 * 
	 * @return the name
	 * 
	 * @since $version
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
		result = (prime * result) + ((this.empPk == null) ? 0 : this.empPk.hashCode());
		result = (prime * result) + ((this.name == null) ? 0 : this.name.hashCode());
		return result;
	}

	/**
	 * Sets the empPk of the DependentId.
	 * 
	 * @param empPk
	 *            the empPk to set for DependentId
	 * 
	 * @since $version
	 */
	public void setEmpPk(Long empPk) {
		this.empPk = empPk;
	}

	/**
	 * 
	 * @param name
	 *            the name to set
	 * 
	 * @since $version
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String toString() {
		return "DependentId [name=" + this.name + ", empPk=" + this.empPk + "]";
	}
}
