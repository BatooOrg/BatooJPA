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
package org.batoo.jpa.core.test.mappedsuperclass;

import java.util.List;

import javax.persistence.AssociationOverride;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;

import com.google.common.collect.Lists;

/**
 * 
 * @author hceylan
 * @since $version
 */
@Entity
@AssociationOverride(joinTable = @JoinTable(name = "Bar_Quux"), name = "quuxes")
public class Foo extends Bar {

	private String fooValue;

	@OneToMany(cascade = CascadeType.ALL)
	private final List<Quux> fooQuuxes = Lists.newArrayList();

	/**
	 * Returns the fooQuuxes.
	 * 
	 * @return the fooQuuxes
	 * @since $version
	 */
	public List<Quux> getFooQuuxes() {
		return this.fooQuuxes;
	}

	/**
	 * Returns the fooValue.
	 * 
	 * @return the fooValue
	 * @since $version
	 */
	public String getFooValue() {
		return this.fooValue;
	}

	/**
	 * Sets the fooValue.
	 * 
	 * @param fooValue
	 *            the fooValue to set
	 * @since $version
	 */
	public void setFooValue(String fooValue) {
		this.fooValue = fooValue;
	}
}
