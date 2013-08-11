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

package org.batoo.jpa.core.test.q;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * 
 * 
 * @author hceylan
 * @since 2.0.1
 */
@Entity
public class ReservedWordEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Integer id;

	private int order;

	/**
	 * Returns the id of the ReservedWordEntity.
	 * 
	 * @return the id of the ReservedWordEntity
	 * 
	 * @since 2.0.1
	 */
	public Integer getId() {
		return this.id;
	}

	/**
	 * Returns the order of the ReservedWordEntity.
	 * 
	 * @return the order of the ReservedWordEntity
	 * 
	 * @since 2.0.1
	 */
	public int getOrder() {
		return this.order;
	}

	/**
	 * Sets the id of the ReservedWordEntity.
	 * 
	 * @param id
	 *            the id to set for ReservedWordEntity
	 * 
	 * @since 2.0.1
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * Sets the order of the ReservedWordEntity.
	 * 
	 * @param order
	 *            the order to set for ReservedWordEntity
	 * 
	 * @since 2.0.1
	 */
	public void setOrder(int order) {
		this.order = order;
	}
}
