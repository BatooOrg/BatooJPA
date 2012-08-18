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
package org.batoo.jpa.core.test.cache;

import java.util.List;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import com.google.common.collect.Lists;

/**
 * 
 * @author hceylan
 * @since $version
 */
@Entity
@Cacheable
public class Foo {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	private String value;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "foo")
	private final List<Bar> bars = Lists.newArrayList();

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "foo")
	private final List<Bar2> bars2 = Lists.newArrayList();

	/**
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public Foo() {
		super();
	}

	/**
	 * @param value
	 *            the value
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public Foo(String value) {
		super();
		this.value = value;
	}

	/**
	 * Returns the bars of the Foo.
	 * 
	 * @return the bars of the Foo
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public List<Bar> getBars() {
		return this.bars;
	}

	/**
	 * Returns the bars2 of the Foo.
	 * 
	 * @return the bars2 of the Foo
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public List<Bar2> getBars2() {
		return this.bars2;
	}

	/**
	 * Returns the id of the Foo1.
	 * 
	 * @return the id of the Foo1
	 * 
	 * @since $version
	 * @author hceylan
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
	 * @author hceylan
	 */
	public String getValue() {
		return this.value;
	}

	/**
	 * Sets the value of the Foo1.
	 * 
	 * @param value
	 *            the value to set for Foo1
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void setValue(String value) {
		this.value = value;
	}
}
