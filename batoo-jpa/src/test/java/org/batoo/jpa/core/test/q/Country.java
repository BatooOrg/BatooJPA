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

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * 
 * @author hceylan
 * @since 2.0.0
 */
@Entity
public class Country {

	@Id
	private String code;

	@Column(name = "NAME", columnDefinition = "VARCHAR(100)")
	private String name;

	@Basic
	@Column(name = "lastActivity", nullable = true)
	private Date lastActivity;

	/**
	 * @since 2.0.0
	 */
	public Country() {
		super();
	}

	/**
	 * @param code
	 *            the id
	 * @param name
	 *            the name
	 * 
	 * @since 2.0.0
	 */
	public Country(String code, String name) {
		super();

		this.code = code;
		this.name = name;
	}

	/**
	 * Returns the code of the Country.
	 * 
	 * @return the code of the Country
	 * 
	 * @since 2.0.0
	 */
	public String getCode() {
		return this.code;
	}

	/**
	 * Returns the lastActivity of the Country.
	 * 
	 * @return the lastActivity of the Country
	 * 
	 * @since 2.0.1
	 */
	public java.util.Date getLastActivity() {
		return this.lastActivity;
	}

	/**
	 * Returns the name.
	 * 
	 * @return the name
	 * @since 2.0.0
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Sets the lastActivity of the Country.
	 * 
	 * @param lastActivity
	 *            the lastActivity to set for Country
	 * 
	 * @since 2.0.1
	 */
	public void setLastActivity(java.util.Date lastActivity) {
		this.lastActivity = lastActivity;
	}

	/**
	 * Sets the name.
	 * 
	 * @param name
	 *            the name to set
	 * @since 2.0.0
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String toString() {
		return "Country [name=" + this.name + "]";
	}
}
