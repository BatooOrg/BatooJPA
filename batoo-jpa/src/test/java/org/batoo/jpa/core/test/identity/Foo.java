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

package org.batoo.jpa.core.test.identity;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

/**
 * 
 * @author hceylan
 * @since 2.0.0
 */
@Entity
public class Foo {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer key;

	private String value;

	@OneToOne(cascade = CascadeType.ALL)
	private Foo other;

	/**
	 * Returns the key.
	 * 
	 * @return the key
	 * @since 2.0.0
	 */
	public Integer getKey() {
		return this.key;
	}

	/**
	 * Returns the other of the Foo1.
	 * 
	 * @return the other of the Foo1
	 * 
	 * @since 2.0.0
	 */
	public Foo getOther() {
		return this.other;
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
	 * Sets the other of the Foo1.
	 * 
	 * @param other
	 *            the other to set for Foo1
	 * 
	 * @since 2.0.0
	 */
	public void setOther(Foo other) {
		this.other = other;
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
