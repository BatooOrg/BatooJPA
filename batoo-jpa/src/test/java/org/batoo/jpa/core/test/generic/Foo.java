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
package org.batoo.jpa.core.test.generic;

import javax.persistence.Entity;

/**
 * 
 * @author hceylan
 * @since 2.0.0
 */
@Entity
public class Foo extends AbstractPersistable<Long> {

	private String fooValue;

	/**
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public Foo() {
		super();
	}

	/**
	 * @param fooValue
	 *            the foo value
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public Foo(String fooValue) {
		super();
		this.fooValue = fooValue;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (!(obj instanceof Foo)) {
			return false;
		}
		final Foo other = (Foo) obj;
		if (this.fooValue == null) {
			if (other.fooValue != null) {
				return false;
			}
		}
		else if (!this.fooValue.equals(other.fooValue)) {
			return false;
		}
		return true;
	}

	/**
	 * Returns the fooValue of the Foo.
	 * 
	 * @return the fooValue of the Foo
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public String getFooValue() {
		return this.fooValue;
	}

	/**
	 * Sets the fooValue of the Foo.
	 * 
	 * @param fooValue
	 *            the fooValue to set for Foo
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void setFooValue(String fooValue) {
		this.fooValue = fooValue;
	}
}
