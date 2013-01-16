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
package org.batoo.jpa.core.test.embeddedid;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.TableGenerator;

/**
 * 
 * @author asimarslan
 * @since $version
 */
@Entity
@TableGenerator(name = "bar_id", allocationSize = 100)
public class Bar {

	@Id
	@GeneratedValue(generator = "bar_id", strategy = GenerationType.TABLE)
	private long id;

	@ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
	private Foo foo;

	/**
	 * 
	 * @since $version
	 */
	public Bar() {
		super();
	}

	/**
	 * 
	 * @param id
	 *            id
	 * @param foo
	 *            foo
	 * @since $version
	 */
	public Bar(long id, Foo foo) {
		super();
		this.id = id;
		this.foo = foo;
	}

	/**
	 * 
	 * @return Foo
	 * @since $version
	 */
	public Foo getFoo() {
		return this.foo;
	}

	/**
	 * 
	 * @return id
	 * @since $version
	 */
	public long getId() {
		return this.id;
	}

	/**
	 * 
	 * @param foo
	 *            foo
	 * @since $version
	 */
	public void setFoo(Foo foo) {
		this.foo = foo;
	}

	/**
	 * 
	 * @param id
	 *            id
	 * @since $version
	 */
	public void setId(long id) {
		this.id = id;
	}

}
