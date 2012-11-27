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

import java.util.Collection;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

/**
 * 
 * 
 * @author hceylan
 * @since $version
 */
@Entity
public class Foo {

	@Id
	@GeneratedValue
	private Integer id;

	@OneToMany(mappedBy = "foo", fetch = FetchType.EAGER, orphanRemoval = true)
	private Collection<Bar> bars;

	@OneToMany(mappedBy = "secondaryFoo")
	private Collection<Bar> bars2;

	/**
	 * 
	 * @since $version
	 */
	public Foo() {
		super();
	}

	/**
	 * Returns the bars of the Foo.
	 * 
	 * @return the bars of the Foo
	 * 
	 * @since $version
	 */
	public Collection<Bar> getBars() {
		return this.bars;
	}

	/**
	 * Returns the bars2 of the Foo.
	 * 
	 * @return the bars2 of the Foo
	 * 
	 * @since $version
	 */
	public Collection<Bar> getBars2() {
		return this.bars2;
	}

	/**
	 * Returns the id of the Foo.
	 * 
	 * @return the id of the Foo
	 * 
	 * @since $version
	 */
	public Integer getId() {
		return this.id;
	}

	/**
	 * Sets the bars of the Foo.
	 * 
	 * @param bars
	 *            the bars to set for Foo
	 * 
	 * @since $version
	 */
	public void setBars(List<Bar> bars) {
		this.bars = bars;
	}

	/**
	 * Sets the bars2 of the Foo.
	 * 
	 * @param bars2
	 *            the bars2 to set for Foo
	 * 
	 * @since $version
	 */
	public void setBars2(List<Bar> bars2) {
		this.bars2 = bars2;
	}

	/**
	 * Sets the id of the Foo.
	 * 
	 * @param id
	 *            the id to set for Foo
	 * 
	 * @since $version
	 */
	public void setId(Integer id) {
		this.id = id;
	}
}
