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
package org.batoo.jpa.core.test.mappedsuperclass;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 * 
 * @author hceylan
 * @since $version
 */
@Entity
public class Quux {

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	private Integer key;

	private Integer quuxValue;

	@ManyToOne
	private Foo foo;

	/**
	 * @since $version
	 */
	public Quux() {
		super();
	}

	/**
	 * Returns the foo.
	 * 
	 * @return the foo
	 * @since $version
	 */
	public Foo getFoo() {
		return this.foo;
	}

	/**
	 * Returns the key.
	 * 
	 * @return the key
	 * @since $version
	 */
	public Integer getKey() {
		return this.key;
	}

	/**
	 * Returns the quuxValue.
	 * 
	 * @return the quuxValue
	 * @since $version
	 */
	public Integer getQuuxValue() {
		return this.quuxValue;
	}

	/**
	 * Sets the foo.
	 * 
	 * @param foo
	 *            the foo to set
	 * @since $version
	 */
	public void setFoo(Foo foo) {
		this.foo = foo;
	}

	/**
	 * Sets the quuxValue.
	 * 
	 * @param quuxValue
	 *            the quuxValue to set
	 * @since $version
	 */
	public void setQuuxValue(Integer quuxValue) {
		this.quuxValue = quuxValue;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String toString() {
		return "Quux [key=" + this.key + ", foo=" + this.foo + "]";
	}

}
