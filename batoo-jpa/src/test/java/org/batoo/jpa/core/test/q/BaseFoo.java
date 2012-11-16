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

import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.ManyToOne;

/**
 * 
 * @author hceylan
 * @since 2.0.0
 */
@Entity
@Inheritance
@DiscriminatorColumn(name = "FOO_TYPE")
@DiscriminatorValue("BASE_FOO")
public class BaseFoo {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Integer id;

	@ManyToOne
	private Bar bar;

	/**
	 * 
	 * @since 2.0.0
	 */
	public BaseFoo() {
		super();
	}

	/**
	 * @param bar
	 *            the bar
	 * 
	 * @since 2.0.0
	 */
	public BaseFoo(Bar bar) {
		super();

		this.bar = bar;

		this.bar.getFoos().add(this);
	}

	/**
	 * Returns the bar of the BaseFoo.
	 * 
	 * @return the bar of the BaseFoo
	 * 
	 * @since 2.0.0
	 */
	public Bar getBar() {
		return this.bar;
	}

	/**
	 * Returns the id of the BasepublicFoo.
	 * 
	 * @return the id of the BaseFoo
	 * 
	 * @since 2.0.0
	 */
	public Integer getId() {
		return this.id;
	}

	/**
	 * Sets the bar of the BaseFoo.
	 * 
	 * @param bar
	 *            the bar to set for BaseFoo
	 * 
	 * @since 2.0.0
	 */
	public void setBar(Bar bar) {
		this.bar = bar;
	}
}
