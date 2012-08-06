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
package org.batoo.jpa.core.test.elementcollection;

import java.util.Map;
import java.util.Set;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * 
 * @author hceylan
 * @since $version
 */
@Entity
public class Foo3 {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Integer key;

	@ElementCollection
	private final Set<Bar3> images = Sets.newHashSet();

	@ElementCollection
	private final Map<Integer, Bar3> images2 = Maps.newHashMap();

	/**
	 * Returns the images of the Foo3.
	 * 
	 * @return the images of the Foo3
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public Set<Bar3> getImages() {
		return this.images;
	}

	/**
	 * Returns the images2 of the Foo3.
	 * 
	 * @return the images2 of the Foo3
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public Map<Integer, Bar3> getImages2() {
		return this.images2;
	}

	/**
	 * Returns the key of the Foo3.
	 * 
	 * @return the key of the Foo3
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public Integer getKey() {
		return this.key;
	}
}
