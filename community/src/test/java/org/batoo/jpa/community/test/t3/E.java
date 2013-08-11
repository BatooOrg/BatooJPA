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

package org.batoo.jpa.community.test.t3;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * 
 * 
 * @author hceylan
 * @since 2.0.0
 */
@MappedSuperclass
public class E {

	@Id
	private Long id;

	/**
	 * Returns the id of the E.
	 * 
	 * @return the id of the E
	 * 
	 * @since 2.0.0
	 */
	public Long getId() {
		return this.id;
	}

	/**
	 * Sets the id of the E.
	 * 
	 * @param id
	 *            the id to set for E
	 * 
	 * @since 2.0.0
	 */
	public void setId(Long id) {
		this.id = id;
	}
}
