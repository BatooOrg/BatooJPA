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

package org.batoo.jpa.core.test.inheritence.joined;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
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
	@GeneratedValue
	private Integer key;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private final Set<Foo> foos = Sets.newHashSet();

	/**
	 * Returns the foos.
	 * 
	 * @return the foos
	 * @since 2.0.0
	 */
	public Set<Foo> getFoos() {
		return this.foos;
	}

	/**
	 * Returns the key.
	 * 
	 * @return the key
	 * @since 2.0.0
	 */
	public Integer getKey() {
		return this.key;
	}

}
