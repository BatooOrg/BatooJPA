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
@DiscriminatorValue("FOO_TYPE_2")
public class FooType2 extends BaseFoo {

	private int valueType2;

	/**
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public FooType2() {
		super();
	}

	/**
	 * @param bar
	 *            the bar
	 * @param valueType2
	 *            the value type 2
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public FooType2(Bar bar, int valueType2) {
		super(bar);

		this.valueType2 = valueType2;
	}

	/**
	 * Returns the valueType2 of the FooType2.
	 * 
	 * @return the valueType2 of the FooType2
	 * 
	 * @since $version
	 * @author hceylan
	 */
	protected int getValueType2() {
		return this.valueType2;
	}

	/**
	 * Sets the valueType2 of the FooType2.
	 * 
	 * @param valueType2
	 *            the valueType2 to set for FooType2
	 * 
	 * @since $version
	 * @author hceylan
	 */
	protected void setValueType2(int valueType2) {
		this.valueType2 = valueType2;
	}
}
