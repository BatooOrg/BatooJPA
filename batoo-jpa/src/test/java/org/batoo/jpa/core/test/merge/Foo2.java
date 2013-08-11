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

package org.batoo.jpa.core.test.merge;

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
public class Foo2 {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Integer id;

	private String value;

	@OneToOne
	private Foo1 foo1;

	/**
	 * 
	 * @since 2.0.0
	 */
	public Foo2() {
		super();
	}

	/**
	 * @param id
	 *            the id
	 * @param value
	 *            the value
	 * @param foo1
	 *            the foo1
	 * 
	 * @since 2.0.0
	 */
	public Foo2(Integer id, String value, Foo1 foo1) {
		super();

		this.id = id < 0 ? null : id;
		this.value = value;
		this.foo1 = foo1;

		if (foo1 != null) {
			foo1.setFoo2(this);
		}
	}

	/**
	 * Returns the foo1 of the Foo2.
	 * 
	 * @return the foo1 of the Foo2
	 * 
	 * @since 2.0.0
	 */
	public Foo1 getFoo1() {
		return this.foo1;
	}

	/**
	 * Returns the id of the Foo1.
	 * 
	 * @return the id of the Foo1
	 * 
	 * @since 2.0.0
	 */
	public Integer getId() {
		return this.id;
	}

	/**
	 * Returns the value of the Foo1.
	 * 
	 * @return the value of the Foo1
	 * 
	 * @since 2.0.0
	 */
	public String getValue() {
		return this.value;
	}

	/**
	 * Sets the foo1 of the Foo2.
	 * 
	 * @param foo1
	 *            the foo1 to set for Foo2
	 * 
	 * @since 2.0.0
	 */
	public void setFoo1(Foo1 foo1) {
		this.foo1 = foo1;
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
