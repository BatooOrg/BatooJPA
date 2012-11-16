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
package org.batoo.jpa.core.test.listener;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.PostPersist;
import javax.persistence.Transient;

/**
 * 
 * @author hceylan
 * @since $version
 */
@Entity
@EntityListeners(value = FooListener.class)
public class Foo3 extends Foo implements FooType {

	@Transient
	private String value = "";

	private String fooValue;

	/**
	 * Returns the fooValue of the Foo3.
	 * 
	 * @return the fooValue of the Foo3
	 * 
	 * @since $version
	 */
	protected String getFooValue() {
		return this.fooValue;
	}

	/**
	 * Returns the value of the Foo3.
	 * 
	 * @return the value of the Foo3
	 * 
	 * @since $version
	 */
	@Override
	public String getValue() {
		return this.value;
	}

	/**
	 * 
	 * @since $version
	 */
	@PostPersist
	public void postPersist() {
		this.setValue(this.getValue() + "postPersist");
	}

	/**
	 * Sets the fooValue of the Foo3.
	 * 
	 * @param fooValue
	 *            the fooValue to set for Foo3
	 * 
	 * @since $version
	 */
	protected void setFooValue(String fooValue) {
		this.fooValue = fooValue;
	}

	/**
	 * Sets the value of the Foo3.
	 * 
	 * @param value
	 *            the value to set for Foo3
	 * 
	 * @since $version
	 */
	@Override
	public void setValue(String value) {
		this.value = value;
	}
}
