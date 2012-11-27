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
package org.batoo.jpa.community.test.i106;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 * 
 * 
 * @author hceylan
 * @since $version
 */
@Entity
public class Bar {

	@Id
	@GeneratedValue
	private Integer id;

	@ManyToOne
	private Foo foo;

	@ManyToOne
	private Foo secondaryFoo;

	/**
	 * 
	 * @since $version
	 */
	public Bar() {
		super();
	}

	/**
	 * Returns the secondaryFoo of the Bar.
	 * 
	 * @return the secondaryFoo of the Bar
	 * 
	 * @since $version
	 */
	public Foo getSecondaryFoo() {
		return this.secondaryFoo;
	}

	/**
	 * Sets the secondaryFoo of the Bar.
	 * 
	 * @param secondaryFoo
	 *            the secondaryFoo to set for Bar
	 * 
	 * @since $version
	 */
	public void setSecondaryFoo(Foo secondaryFoo) {
		this.secondaryFoo = secondaryFoo;
	}
}
