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

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MapKeyColumn;

import com.google.common.collect.Maps;

/**
 * 
 * @author hceylan
 * @since $version
 */
@Entity
public class Foo2 {

	@Id
	@GeneratedValue
	private Integer key;

	@ElementCollection
	@MapKeyColumn(name = "IMAGE_NAME")
	@Column(name = "IMAGE_FILENAME")
	@CollectionTable(name = "IMAGE_MAPPING")
	private final Map<String, String> images = Maps.newHashMap();

	/**
	 * Returns the images of the Foo2.
	 * 
	 * @return the images of the Foo2
	 * 
	 * @since $version
	 * @author hceylan
	 */
	protected Map<String, String> getImages() {
		return this.images;
	}

	/**
	 * Returns the key of the Foo2.
	 * 
	 * @return the key of the Foo2
	 * 
	 * @since $version
	 * @author hceylan
	 */
	protected Integer getKey() {
		return this.key;
	}
}
