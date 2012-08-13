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
package org.batoo.jpa.core.test.q;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * 
 * 
 * @author hceylan
 * @since $version
 */
@Entity
@DiscriminatorValue("FOO_TYPE_1")
public class FooType1 extends BaseFoo {

	private String valueType1;

	/**
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public FooType1() {
		super();
	}

	/**
	 * @param bar
	 *            the bar
	 * @param valueType1
	 *            the value type 1
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public FooType1(Bar bar, String valueType1) {
		super(bar);

		this.valueType1 = valueType1;
	}

	/**
	 * Returns the valueType1 of the FooType1.
	 * 
	 * @return the valueType1 of the FooType1
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public String getValueType1() {
		return this.valueType1;
	}

	/**
	 * Sets the valueType1 of the FooType1.
	 * 
	 * @param valueType1
	 *            the valueType1 to set for FooType1
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void setValueType1(String valueType1) {
		this.valueType1 = valueType1;
	}
}
