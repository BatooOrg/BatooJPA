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

package org.batoo.jpa.core.test.q;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import com.google.common.collect.Sets;

/**
 * 
 * @author hceylan
 * @since 2.0.0
 */
@Entity
public class Bar {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Integer id;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "bar")
	private final Set<BaseFoo> foos = Sets.newHashSet();

	/**
	 * 
	 * @since 2.0.0
	 */
	public Bar() {
		super();
	}

	/**
	 * Returns the foos of the Bar.
	 * 
	 * @return the foos of the Bar
	 * 
	 * @since 2.0.0
	 */
	public Set<BaseFoo> getFoos() {
		return this.foos;
	}

	/**
	 * Returns the id of the Bar.
	 * 
	 * @return the id of the Bar
	 * 
	 * @since 2.0.0
	 */
	public Integer getId() {
		return this.id;
	}
}
