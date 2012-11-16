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
package org.batoo.jpa.core.test.lock;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Version;

/**
 * 
 * @author hceylan
 * @since $version
 */
@Entity
public class Bar {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	private String value;

	@ManyToOne
	private Foo foo;

	@Version
	private Integer version;

	/**
	 * 
	 * @since $version
	 */
	public Bar() {
		super();
	}

	/**
	 * @param foo
	 *            the foo
	 * @param value
	 *            the value
	 * 
	 * @since $version
	 */
	public Bar(Foo foo, String value) {
		super();

		this.foo = foo;
		this.value = value;

		this.foo.getBars().add(this);
	}

	/**
	 * Returns the foo of the Bar.
	 * 
	 * @return the foo of the Bar
	 * 
	 * @since $version
	 */
	public Foo getFoo() {
		return this.foo;
	}

	/**
	 * Returns the id of the Foo1.
	 * 
	 * @return the id of the Foo1
	 * 
	 * @since $version
	 */
	public Integer getId() {
		return this.id;
	}

	/**
	 * Returns the value of the Foo1.
	 * 
	 * @return the value of the Foo1
	 * 
	 * @since $version
	 */
	public String getValue() {
		return this.value;
	}

	/**
	 * Returns the version of the Foo.
	 * 
	 * @return the version of the Foo
	 * 
	 * @since $version
	 */
	public int getVersion() {
		return this.version;
	}

	/**
	 * Sets the foo of the Bar.
	 * 
	 * @param foo
	 *            the foo to set for Bar
	 * 
	 * @since $version
	 */
	public void setFoo(Foo foo) {
		this.foo = foo;
	}

	/**
	 * Sets the value of the Foo1.
	 * 
	 * @param value
	 *            the value to set for Foo1
	 * 
	 * @since $version
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * Sets the version of the Foo.
	 * 
	 * @param version
	 *            the version to set for Foo
	 * 
	 * @since $version
	 */
	public void setVersion(int version) {
		this.version = version;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String toString() {
		return "Bar [id=" + this.id + ", value=" + this.value + ", foo=" + this.foo + ", version=" + this.version + "]";
	}
}
