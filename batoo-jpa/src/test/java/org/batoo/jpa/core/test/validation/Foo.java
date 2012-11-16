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
package org.batoo.jpa.core.test.validation;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

import com.google.common.collect.Lists;

/**
 * 
 * @author hceylan
 * @since $version
 */
@Entity
public class Foo {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@NotNull
	private String value;

	@NotNull(groups = { Update.class })
	@Null(groups = { Remove.class })
	private Integer value2;

	@NotEmpty
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "foo")
	private final List<Bar> bars = Lists.newArrayList();

	/**
	 * Returns the bars of the Foo.
	 * 
	 * @return the bars of the Foo
	 * 
	 * @since $version
	 */
	public List<Bar> getBars() {
		return this.bars;
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
	 * Returns the value2 of the Foo.
	 * 
	 * @return the value2 of the Foo
	 * 
	 * @since $version
	 */
	public Integer getValue2() {
		return this.value2;
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
	 * Sets the value2 of the Foo.
	 * 
	 * @param value2
	 *            the value2 to set for Foo
	 * 
	 * @since $version
	 */
	public void setValue2(Integer value2) {
		this.value2 = value2;
	}
}
