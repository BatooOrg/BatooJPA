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
package org.batoo.jpa.core.test.elementcollection;

import javax.persistence.Embeddable;

/**
 * 
 * @author hceylan
 * @since $version
 */
@Embeddable
public class Bar3 {

	private int intValue;
	private String strValue;

	/**
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public Bar3() {
		super();
	}

	/**
	 * @param intValue
	 *            int value
	 * @param strValue
	 *            str value
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public Bar3(int intValue, String strValue) {
		super();

		this.intValue = intValue;
		this.strValue = strValue;
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
		final Bar3 other = (Bar3) obj;
		if (this.intValue != other.intValue) {
			return false;
		}
		if (this.strValue == null) {
			if (other.strValue != null) {
				return false;
			}
		}
		else if (!this.strValue.equals(other.strValue)) {
			return false;
		}
		return true;
	}

	/**
	 * Returns the intValue of the Bar3.
	 * 
	 * @return the intValue of the Bar3
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public int getIntValue() {
		return this.intValue;
	}

	/**
	 * Returns the strValue of the Bar3.
	 * 
	 * @return the strValue of the Bar3
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public String getStrValue() {
		return this.strValue;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + this.intValue;
		result = (prime * result) + ((this.strValue == null) ? 0 : this.strValue.hashCode());
		return result;
	}

	/**
	 * Sets the intValue of the Bar3.
	 * 
	 * @param intValue
	 *            the intValue to set for Bar3
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void setIntValue(int intValue) {
		this.intValue = intValue;
	}

	/**
	 * Sets the strValue of the Bar3.
	 * 
	 * @param strValue
	 *            the strValue to set for Bar3
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void setStrValue(String strValue) {
		this.strValue = strValue;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String toString() {
		return "Bar3 [intValue=" + this.intValue + ", strValue=" + this.strValue + "]";
	}
}
