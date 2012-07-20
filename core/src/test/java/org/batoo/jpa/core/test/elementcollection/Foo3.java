/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
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
