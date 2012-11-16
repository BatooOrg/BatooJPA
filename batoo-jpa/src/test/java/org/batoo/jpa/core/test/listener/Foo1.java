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
import javax.persistence.PostPersist;
import javax.persistence.PrePersist;
import javax.persistence.Transient;

/**
 * 
 * @author hceylan
 * @since 2.0.0
 */
@Entity
public class Foo1 extends Foo implements FooType {

	@Transient
	private String value = "";

	/**
	 * Returns the value of the Foo1.
	 * 
	 * @return the value of the Foo1
	 * 
	 * @since 2.0.0
	 */
	@Override
	public String getValue() {
		return this.value;
	}

	/**
	 * 
	 * @since 2.0.0
	 */
	@PostPersist
	public void postPersist() {
		this.setValue(this.getValue() + "postPersist");
	}

	/**
	 * 
	 * @since 2.0.0
	 */
	@PrePersist
	public void prePersist() {
		this.setValue(this.getValue() + "prePersist");
	}

	/**
	 * Sets the value of the Foo1.
	 * 
	 * @param value
	 *            the value to set for Foo1
	 * 
	 * @since 2.0.0
	 */
	public void setValue(String value) {
		this.value = value;
	}
}
