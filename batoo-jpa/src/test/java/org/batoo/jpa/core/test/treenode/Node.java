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
package org.batoo.jpa.core.test.treenode;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.google.common.collect.Lists;

/**
 * 
 * @author kozzi11
 * @since 2.0.1
 */
@Entity
@SuppressWarnings("javadoc")
public class Node {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	Long id;

	@Column
	Integer myValue;

	@ManyToOne
	private Node parent;

	@OneToMany(mappedBy = "parent", cascade = { CascadeType.ALL })
	private List<Node> children = Lists.newArrayList();

	/**
	 * @since 2.0.1
	 */
	public Node() {
		super();
	}

	/**
	 * @param myValue
	 * 
	 * @since 2.0.1
	 */
	public Node(Integer myValue) {
		super();
		this.myValue = myValue;
	}

	/**
	 * @param myValue
	 * @param parent
	 * 
	 * @since 2.0.1
	 */
	public Node(Integer myValue, Node parent) {
		super();
		this.myValue = myValue;
		this.setParent(parent);
	}

	/**
	 * Returns the children
	 * 
	 * @return the children
	 * @since 2.0.1
	 */
	public List<Node> getChildren() {
		return this.children;
	}

	/**
	 * Returns the id.
	 * 
	 * @return the id
	 * @since 2.0.1
	 */
	public Long getId() {
		return this.id;
	}

	/**
	 * Returns the myValue
	 * 
	 * @return the myValue
	 * @since 2.0.1
	 */
	public Integer getMyValue() {
		return this.myValue;
	}

	/**
	 * Returns the parent
	 * 
	 * @return the parent
	 * @since 2.0.1
	 */
	public Node getParent() {
		return this.parent;
	}

	/**
	 * Sets the children
	 * 
	 * @param children
	 * 
	 * @since 2.0.1
	 */
	public void setChildren(List<Node> children) {
		this.children = children;
	}

	/**
	 * Sets the myValue.
	 * 
	 * @param myValue
	 * 
	 * @since 2.0.1
	 */
	public void setMyValue(Integer myValue) {
		this.myValue = myValue;
	}

	/**
	 * Sets the parent.
	 * 
	 * @param parent
	 *            the parent to set
	 * @since 2.0.1
	 */
	public void setParent(Node parent) {
		this.parent = parent;
	}
}
