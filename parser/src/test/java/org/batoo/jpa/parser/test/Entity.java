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
package org.batoo.jpa.parser.test;

import java.sql.Timestamp;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * The default test entity
 * 
 * @author hceylan
 * @since $version
 */
@javax.persistence.Entity
public class Entity {

	@Id
	@GeneratedValue
	private Integer id;

	private String attribute;

	private Timestamp version;

	/**
	 * Returns the attribute of the Entity.
	 * 
	 * @return the attribute of the Entity
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public String getAttribute() {
		return this.attribute;
	}

	/**
	 * Returns the id of the Entity.
	 * 
	 * @return the id of the Entity
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public Integer getId() {
		return this.id;
	}

	/**
	 * Returns the version of the Entity.
	 * 
	 * @return the version of the Entity
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public Timestamp getVersion() {
		return this.version;
	}

	/**
	 * Sets the attribute of the Entity.
	 * 
	 * @param attribute
	 *            the attribute to set for Entity
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}
}
