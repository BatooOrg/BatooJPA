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

import java.util.List;
import java.util.Set;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.batoo.jpa.core.test.enums.Foo.FooType;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * 
 * @author hceylan
 * @since $version
 */
@Entity
public class Foo {

	@SuppressWarnings("javadoc")
	public enum FooTypes {
		TYPE1,
		TYPE2,
		TYPE3
	}

	@Id
	@GeneratedValue
	private Integer key;

	@ElementCollection(fetch = FetchType.EAGER)
	private final List<String> codes = Lists.newArrayList();

	@ElementCollection
	private final Set<String> codes2 = Sets.newHashSet();

	@ElementCollection
	private final Set<FooType> codes3 = Sets.newHashSet();

	@ElementCollection
	@Enumerated(EnumType.STRING)
	private final Set<FooType> codes4 = Sets.newHashSet();

	private String value;

	/**
	 * Returns the codes.
	 * 
	 * @return the codes
	 * @since $version
	 */
	public List<String> getCodes() {
		return this.codes;
	}

	/**
	 * Returns the codes2 of the Foo.
	 * 
	 * @return the codes2 of the Foo
	 * 
	 * @since $version
	 * @author hceylan
	 */
	protected Set<String> getCodes2() {
		return this.codes2;
	}

	/**
	 * Returns the codes3 of the Foo.
	 * 
	 * @return the codes3 of the Foo
	 * 
	 * @since $version
	 * @author hceylan
	 */
	protected Set<FooType> getCodes3() {
		return this.codes3;
	}

	/**
	 * Returns the codes4 of the Foo.
	 * 
	 * @return the codes4 of the Foo
	 * 
	 * @since $version
	 * @author hceylan
	 */
	protected Set<FooType> getCodes4() {
		return this.codes4;
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
	 * Returns the value.
	 * 
	 * @return the value
	 * @since $version
	 */
	public String getValue() {
		return this.value;
	}

	/**
	 * Sets the value.
	 * 
	 * @param value
	 *            the value to set
	 * @since $version
	 */
	public void setValue(String value) {
		this.value = value;
	}

}
