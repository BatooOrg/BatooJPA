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
