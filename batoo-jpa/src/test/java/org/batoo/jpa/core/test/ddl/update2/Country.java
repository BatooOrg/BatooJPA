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
package org.batoo.jpa.core.test.ddl.update2;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * 
 * 
 * @author hceylan
 * @since $version
 */
@Entity
public class Country {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	private String name;

	/**
	 * 
	 * @since $version
	 */
	public Country() {
		super();
	}

	/**
	 * Returns the id of the Country.
	 * 
	 * @return the id of the Country
	 * 
	 * @since $version
	 */
	public Integer getId() {
		return this.id;
	}

	/**
	 * Returns the name of the Country.
	 * 
	 * @return the name of the Country
	 * 
	 * @since $version
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Sets the name of the Country.
	 * 
	 * @param name
	 *            the name to set for Country
	 * 
	 * @since $version
	 */
	public void setName(String name) {
		this.name = name;
	}

}
