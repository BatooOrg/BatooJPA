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
package org.batoo.jpa.core.test.remove;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
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
public class Parent {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Integer id;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "parent")
	private final List<Child1> children1 = Lists.newArrayList();

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "parent")
	private final List<Child2> children2 = Lists.newArrayList();

	@OneToMany(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
	private final List<Child3> children3 = Lists.newArrayList();

	@OneToMany(cascade = CascadeType.PERSIST, orphanRemoval = true)
	private final List<Child4> children4 = Lists.newArrayList();

	private String value;

	/**
	 * @since $version
	 */
	public Parent() {
		super();
	}

	/**
	 * @param value
	 *            the value
	 * 
	 * @since $version
	 */
	public Parent(String value) {
		super();

		this.value = value;
	}

	/**
	 * Returns the children1 of the Parent.
	 * 
	 * @return the children1 of the Parent
	 * 
	 * @since $version
	 */
	public List<Child1> getChildren1() {
		return this.children1;
	}

	/**
	 * Returns the children2 of the Parent.
	 * 
	 * @return the children2 of the Parent
	 * 
	 * @since $version
	 */
	public List<Child2> getChildren2() {
		return this.children2;
	}

	/**
	 * Returns the children3 of the Parent.
	 * 
	 * @return the children3 of the Parent
	 * 
	 * @since $version
	 */
	public List<Child3> getChildren3() {
		return this.children3;
	}

	/**
	 * Returns the children4 of the Parent.
	 * 
	 * @return the children4 of the Parent
	 * 
	 * @since $version
	 */
	public List<Child4> getChildren4() {
		return this.children4;
	}

	/**
	 * Returns the id of the Parent.
	 * 
	 * @return the id of the Parent
	 * 
	 * @since $version
	 */
	public Integer getId() {
		return this.id;
	}

	/**
	 * Returns the value of the Parent.
	 * 
	 * @return the value of the Parent
	 * 
	 * @since $version
	 */
	public String getValue() {
		return this.value;
	}

	/**
	 * Sets the value of the Parent.
	 * 
	 * @param value
	 *            the value to set for Parent
	 * 
	 * @since $version
	 */
	public void setValue(String value) {
		this.value = value;
	}

}
