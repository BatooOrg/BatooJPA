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
package org.batoo.jpa.community.test.i113;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * 
 * @author hceylan
 * @since 2.0.1
 */
@Entity
public class Foo {

	@Id
	private Integer id;

	@Basic
	@Column(name = "lastActivity", nullable = true)
	private java.util.Date lastActivity;

	/**
	 * 
	 * @return the id
	 * 
	 * @since 2.0.1
	 */
	public Integer getId() {
		return this.id;
	}

	/**
	 * 
	 * @return the lastActivity
	 * 
	 * @since 2.0.1
	 */
	public java.util.Date getLastActivity() {
		return this.lastActivity;
	}

	/**
	 * 
	 * @param id
	 *            the id to set
	 * 
	 * @since 2.0.1
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * 
	 * @param lastActivity
	 *            the lastActivity to set
	 * 
	 * @since 2.0.1
	 */
	public void setLastActivity(java.util.Date lastActivity) {
		this.lastActivity = lastActivity;
	}

}
