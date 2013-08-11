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

package org.batoo.jpa.core.test.ormxml;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * 
 * 
 * @author hceylan
 * @since 2.0.0
 */
@Entity
public class E4 {

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "userGen")
	private Long id;

	private String value;

	/**
	 * 
	 * @since 2.0.0
	 */
	public E4() {
		super();
	}

	/**
	 * 
	 * @param value
	 *            value
	 * @since 2.0.1
	 */
	public E4(String value) {
		super();
		this.value = value;
	}

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
	 * Returns the value of the E1.
	 * 
	 * @return the value of the E1
	 * 
	 * @since 2.0.0
	 */
	public String getValue() {
		return this.value;
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

	/**
	 * Sets the value of the E1.
	 * 
	 * @param value
	 *            the value to set for E1
	 * 
	 * @since 2.0.0
	 */
	public void setValue(String value) {
		this.value = value;
	}
}
