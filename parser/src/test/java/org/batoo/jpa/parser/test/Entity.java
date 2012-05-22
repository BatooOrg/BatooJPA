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
package org.batoo.jpa.parser.test;

import java.sql.Timestamp;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * The default test entity
 * 
 * @author hceylan
 * @since $version
 */
@javax.persistence.Entity
public class Entity {

	@Id
	@GeneratedValue
	private Integer id;

	private String attribute;

	private Timestamp version;

	/**
	 * Returns the attribute of the Entity.
	 * 
	 * @return the attribute of the Entity
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public String getAttribute() {
		return this.attribute;
	}

	/**
	 * Returns the id of the Entity.
	 * 
	 * @return the id of the Entity
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public Integer getId() {
		return this.id;
	}

	/**
	 * Returns the version of the Entity.
	 * 
	 * @return the version of the Entity
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public Timestamp getVersion() {
		return this.version;
	}

	/**
	 * Sets the attribute of the Entity.
	 * 
	 * @param attribute
	 *            the attribute to set for Entity
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}
}
