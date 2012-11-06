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
package org.batoo.jpa.core.test.accesstype;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

/**
 * 
 * 
 * @author hceylan
 * @since $version
 */
@Entity
public class E7 {

	private Long id;
	private String transiet;

	/**
	 * Returns the id of the E7.
	 * 
	 * @return the id of the E7
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Id
	public Long getId() {
		return this.id;
	}

	/**
	 * Returns the transiet of the E6.
	 * 
	 * @return the transiet of the E6
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Transient
	public String getTransiet() {
		return this.transiet;
	}

	/**
	 * Sets the id of the E7.
	 * 
	 * @param id
	 *            the id to set for E7
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * Sets the transiet of the E6.
	 * 
	 * @param transiet
	 *            the transiet to set for E6
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void setTransiet(String transiet) {
		this.transiet = transiet;
	}
}
