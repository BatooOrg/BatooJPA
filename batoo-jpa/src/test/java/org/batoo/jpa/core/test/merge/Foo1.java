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
package org.batoo.jpa.core.test.merge;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

/**
 * 
 * 
 * @author hceylan
 * @since 2.0.0
 */
@Entity
public class Foo1 {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Integer id;

	private String value;

	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "foo1")
	private Foo2 foo2;

	/**
	 * 
	 * @since 2.0.0
	 */
	public Foo1() {
		super();
	}

	/**
	 * @param id
	 *            the id
	 * @param value
	 *            the value
	 * 
	 * @since 2.0.0
	 */
	public Foo1(Integer id, String value) {
		super();

		this.id = id < 0 ? null : id;
		this.value = value;
	}

	/**
	 * Returns the foo2 of the Foo1.
	 * 
	 * @return the foo2 of the Foo1
	 * 
	 * @since 2.0.0
	 */
	public Foo2 getFoo2() {
		return this.foo2;
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
	 * Sets the foo2 of the Foo1.
	 * 
	 * @param foo2
	 *            the foo2 to set for Foo1
	 * 
	 * @since 2.0.0
	 */
	public void setFoo2(Foo2 foo2) {
		if (this.foo2 != null) {
			this.foo2.setFoo1(null);
		}

		this.foo2 = foo2;

		if (this.foo2 != null) {
			this.foo2.setFoo1(this);
		}
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
