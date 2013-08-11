/*
 * Copyright (c) 2012-2013, Batu Alp Ceylan
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

package org.batoo.jpa.core.test.priotizer;

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
 * @since 2.0.0
 */
@Entity
public class Parent {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Integer id;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "parent")
	private final List<Child> children1 = Lists.newArrayList();

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private final List<Child> children2 = Lists.newArrayList();

	/**
	 * Returns the children1 of the Parent.
	 * 
	 * @return the children1 of the Parent
	 * 
	 * @since 2.0.0
	 */
	public List<Child> getChildren1() {
		return this.children1;
	}

	/**
	 * Returns the children2 of the Parent.
	 * 
	 * @return the children2 of the Parent
	 * 
	 * @since 2.0.0
	 */
	public List<Child> getChildren2() {
		return this.children2;
	}

	/**
	 * Returns the id of the Parent.
	 * 
	 * @return the id of the Parent
	 * 
	 * @since 2.0.0
	 */
	public Integer getId() {
		return this.id;
	}
}
