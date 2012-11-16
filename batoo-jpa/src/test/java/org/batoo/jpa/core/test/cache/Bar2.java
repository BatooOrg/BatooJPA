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
package org.batoo.jpa.core.test.cache;

import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 * 
 * 
 * @author hceylan
 * @since 2.0.0
 */
@Entity
@Cacheable
public class Bar2 {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	private Integer value;

	@ManyToOne
	private Foo foo;

	/**
	 * 
	 * @since 2.0.0
	 */
	public Bar2() {
		super();
	}

	/**
	 * @param foo
	 *            the foo
	 * @param value
	 *            the value
	 * 
	 * @since 2.0.0
	 */
	public Bar2(Foo foo, Integer value) {
		super();
		this.foo = foo;
		this.value = value;

		this.foo.getBars2().add(this);
	}

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
	public Integer getValue() {
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
	public void setValue(Integer value) {
		this.value = value;
	}
}
