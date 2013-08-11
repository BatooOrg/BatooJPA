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

package org.batoo.jpa.core.test.validation;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

/**
 * 
 * @author hceylan
 * @since 2.0.0
 */
@Entity
public class Bar {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@NotNull
	private String value;

	@NotNull
	@ManyToOne
	private Foo foo;

	/**
	 * Returns the foo of the Bar.
	 * 
	 * @return the foo of the Bar
	 * 
	 * @since 2.0.0
	 */
	public Foo getFoo() {
		return this.foo;
	}

	/**
	 * Returns the id of the Bar.
	 * 
	 * @return the id of the Bar
	 * 
	 * @since 2.0.0
	 */
	public Integer getId() {
		return this.id;
	}

	/**
	 * Returns the value of the Bar.
	 * 
	 * @return the value of the Bar
	 * 
	 * @since 2.0.0
	 */
	public String getValue() {
		return this.value;
	}

	/**
	 * Sets the foo of the Bar.
	 * 
	 * @param foo
	 *            the foo to set for Bar
	 * 
	 * @since 2.0.0
	 */
	public void setFoo(Foo foo) {
		this.foo = foo;
	}

	/**
	 * Sets the value of the Bar.
	 * 
	 * @param value
	 *            the value to set for Bar
	 * 
	 * @since 2.0.0
	 */
	public void setValue(String value) {
		this.value = value;
	}

}
