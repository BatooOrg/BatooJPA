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
package org.batoo.jpa.community.test.t4;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * 
 * @author hceylan
 * @since $version
 */
@Entity
public class E2 {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private String value;

	/**
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public E2() {
		super();
	}

	/**
	 * @param value
	 *            the value
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public E2(String value) {
		super();

		this.value = value;
	}

	/**
	 * Returns the id of the E.
	 * 
	 * @return the id of the E
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public Long getId() {
		return this.id;
	}

	/**
	 * Returns the value of the E2.
	 * 
	 * @return the value of the E2
	 * 
	 * @since $version
	 * @author hceylan
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
	 * @since $version
	 * @author hceylan
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * Sets the value of the E2.
	 * 
	 * @param value
	 *            the value to set for E2
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void setValue(String value) {
		this.value = value;
	}
}
