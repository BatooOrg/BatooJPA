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
package org.batoo.jpa.core.test.lob;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;

import com.google.common.collect.Sets;

/**
 * 
 * @author hceylan
 * @since $version
 */
@Entity
public class Foo {

	@Id
	@GeneratedValue
	private Integer key;

	@Lob
	private final HashSet<String> values = Sets.newHashSet();

	private byte[] blob;

	/**
	 * Returns the blob.
	 * 
	 * @return the blob
	 * @since $version
	 */
	public byte[] getBlob() {
		return this.blob;
	}

	/**
	 * Returns the key.
	 * 
	 * @return the key
	 * @since $version
	 */
	public Integer getKey() {
		return this.key;
	}

	/**
	 * Returns the values.
	 * 
	 * @return the values
	 * @since $version
	 */
	public Set<String> getValues() {
		return this.values;
	}

	/**
	 * Sets the blob.
	 * 
	 * @param blob
	 *            the blob to set
	 * @since $version
	 */
	public void setBlob(byte[] blob) {
		this.blob = blob;
	}

}
