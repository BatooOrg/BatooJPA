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
package org.batoo.jpa.core.test.embeddedid;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

/**
 * 
 * @author hceylan
 * @since 2.0.0
 */
@Entity
public class Foo {

	@EmbeddedId
	private FooPk id;

	private String value;

	/**
	 * Returns the id.
	 * 
	 * @return the id
	 * @since 2.0.0
	 */
	public FooPk getId() {
		return this.id;
	}

	/**
	 * Returns the value.
	 * 
	 * @return the value
	 * @since 2.0.0
	 */
	public String getValue() {
		return this.value;
	}

	/**
	 * Sets the id.
	 * 
	 * @param id
	 *            the id to set
	 * @since 2.0.0
	 */
	public void setId(FooPk id) {
		this.id = id;
	}

	/**
	 * Sets the value.
	 * 
	 * @param value
	 *            the value to set
	 * @since 2.0.0
	 */
	public void setValue(String value) {
		this.value = value;
	}

}
