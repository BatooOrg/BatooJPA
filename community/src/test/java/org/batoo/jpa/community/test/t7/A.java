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

package org.batoo.jpa.community.test.t7;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

/**
 * 
 * @author hceylan
 * @since 2.0.0
 */
@Entity
public class A implements I<String> {

	private long key;

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Transient
	@Override
	public String getId() {
		return "nice!";
	}

	/**
	 * @return the key
	 * 
	 * @since 2.0.0
	 */
	@Id
	public long getKey() {
		return this.key;
	}

	/**
	 * @param key
	 *            the key
	 * 
	 * @since 2.0.0
	 */
	public void setKey(long key) {
		this.key = key;
	}
}
