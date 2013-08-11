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

package org.batoo.jpa.core.test.embeddedid;

import javax.persistence.Embeddable;

/**
 * 
 * @author hceylan
 * @since 2.0.0
 */
@Embeddable
public class FooPk {

	private String strKey;
	private Integer intKey;

	/**
	 * 
	 * @since 2.0.1
	 */
	public FooPk() {
		super();
	}

	/**
	 * 
	 * @param strKey
	 *            strKey
	 * @param intKey
	 *            intKey
	 * @since 2.0.1
	 */
	public FooPk(String strKey, Integer intKey) {
		super();
		this.strKey = strKey;
		this.intKey = intKey;
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
		final FooPk other = (FooPk) obj;
		if (this.intKey == null) {
			if (other.intKey != null) {
				return false;
			}
		}
		else if (!this.intKey.equals(other.intKey)) {
			return false;
		}
		if (this.strKey == null) {
			if (other.strKey != null) {
				return false;
			}
		}
		else if (!this.strKey.equals(other.strKey)) {
			return false;
		}
		return true;
	}

	/**
	 * Returns the intKey.
	 * 
	 * @return the intKey
	 * @since 2.0.0
	 */
	public Integer getIntKey() {
		return this.intKey;
	}

	/**
	 * Returns the strKey.
	 * 
	 * @return the strKey
	 * @since 2.0.0
	 */
	public String getStrKey() {
		return this.strKey;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((this.intKey == null) ? 0 : this.intKey.hashCode());
		result = (prime * result) + ((this.strKey == null) ? 0 : this.strKey.hashCode());
		return result;
	}

	/**
	 * Sets the intKey.
	 * 
	 * @param intKey
	 *            the intKey to set
	 * @since 2.0.0
	 */
	public void setIntKey(Integer intKey) {
		this.intKey = intKey;
	}

	/**
	 * Sets the strKey.
	 * 
	 * @param strKey
	 *            the strKey to set
	 * @since 2.0.0
	 */
	public void setStrKey(String strKey) {
		this.strKey = strKey;
	}
}
