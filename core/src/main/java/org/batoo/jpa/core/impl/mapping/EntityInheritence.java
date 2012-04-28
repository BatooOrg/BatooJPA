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
package org.batoo.jpa.core.impl.mapping;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

/**
 * Class representing the inheritence strategy for the root class.
 * 
 * @author hceylan
 * @since $version
 */
public class EntityInheritence {

	private final String name;
	private final int length;
	private final DiscriminatorType type;
	private final InheritanceType inheritenceType;

	/**
	 * @param discriminatorColumn
	 *            the discriminator column mapping
	 * @param inheritance
	 *            the inheritence mapping
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public EntityInheritence(DiscriminatorColumn discriminatorColumn, Inheritance inheritance) {
		super();

		this.name = discriminatorColumn != null ? discriminatorColumn.name() : "DTYPE";
		this.length = discriminatorColumn != null ? discriminatorColumn.length() : 31;
		this.type = discriminatorColumn != null ? discriminatorColumn.discriminatorType() : DiscriminatorType.STRING;
		this.inheritenceType = inheritance != null ? inheritance.strategy() : InheritanceType.SINGLE_TABLE;
	}

	/**
	 * Returns the inheritenceType.
	 * 
	 * @return the inheritenceType
	 * @since $version
	 */
	public InheritanceType getInheritenceType() {
		return this.inheritenceType;
	}

	/**
	 * Returns the length.
	 * 
	 * @return the length
	 * @since $version
	 */
	public int getLength() {
		return this.length;
	}

	/**
	 * Returns the name.
	 * 
	 * @return the name
	 * @since $version
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Returns the type.
	 * 
	 * @return the type
	 * @since $version
	 */
	public DiscriminatorType getType() {
		return this.type;
	}

}
