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

package org.batoo.jpa.core.test.priotizer;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 * 
 * @author hceylan
 * @since 2.0.0
 */
@Entity
public class Child {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Integer id;

	@ManyToOne
	private Parent parent;

	/**
	 * 
	 * @since 2.0.0
	 */
	public Child() {
		super();
	}

	/**
	 * @param parent
	 *            the parent
	 * 
	 * @since 2.0.0
	 */
	public Child(Parent parent) {
		super();

		this.parent = parent;
	}

	/**
	 * Returns the id of the Child.
	 * 
	 * @return the id of the Child
	 * 
	 * @since 2.0.0
	 */
	public Integer getId() {
		return this.id;
	}

	/**
	 * Returns the parent of the Child.
	 * 
	 * @return the parent of the Child
	 * 
	 * @since 2.0.0
	 */
	public Parent getParent() {
		return this.parent;
	}

	/**
	 * Sets the parent of the Child.
	 * 
	 * @param parent
	 *            the parent to set for Child
	 * 
	 * @since 2.0.0
	 */
	public void setParent(Parent parent) {
		this.parent = parent;
	}
}
