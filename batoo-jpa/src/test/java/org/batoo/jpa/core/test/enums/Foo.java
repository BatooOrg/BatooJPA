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
package org.batoo.jpa.core.test.enums;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * 
 * @author hceylan
 * @since 2.0.0
 */
@Entity
public class Foo {

	@SuppressWarnings("javadoc")
	public static enum FooType {
		TYPE1,
		TYPE2,
		TYPE3,
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@Enumerated(EnumType.STRING)
	private FooType footype;

	private FooType footype2;

	/**
	 * Returns the footype of the Foo.
	 * 
	 * @return the footype of the Foo
	 * 
	 * @since 2.0.0
	 */
	protected FooType getFootype() {
		return this.footype;
	}

	/**
	 * Returns the footype2 of the Foo.
	 * 
	 * @return the footype2 of the Foo
	 * 
	 * @since 2.0.0
	 */
	protected FooType getFootype2() {
		return this.footype2;
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
	 * Sets the footype of the Foo.
	 * 
	 * @param footype
	 *            the footype to set for Foo
	 * 
	 * @since 2.0.0
	 */
	protected void setFootype(FooType footype) {
		this.footype = footype;
	}

	/**
	 * Sets the footype2 of the Foo.
	 * 
	 * @param footype2
	 *            the footype2 to set for Foo
	 * 
	 * @since 2.0.0
	 */
	protected void setFootype2(FooType footype2) {
		this.footype2 = footype2;
	}

}
