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
package org.batoo.jpa.parser.metadata.type;

import javax.persistence.AccessType;

import org.batoo.jpa.parser.metadata.LocatableMatadata;
import org.batoo.jpa.parser.metadata.attribute.AttributesMetadata;

/**
 * The definition of the managed types.
 * 
 * @author hceylan
 * @since $version
 */
public interface ManagedTypeMetadata extends LocatableMatadata {

	/**
	 * Returns the access type of the entity.
	 * 
	 * @return the access type of the entity
	 * 
	 * @since $version
	 * @author hceylan
	 */
	AccessType getAccessType();

	/**
	 * Returns the attributes of the entity.
	 * 
	 * @return the attributes of the entity
	 * 
	 * @since $version
	 * @author hceylan
	 */
	AttributesMetadata getAttributes();

	/**
	 * Returns the name of the class of the entity.
	 * 
	 * @return the name of the class of the entity
	 * 
	 * @since $version
	 * @author hceylan
	 */
	String getClassName();

	/**
	 * Returns if the entity's metadata is complete.
	 * 
	 * @return true if the entity's metadata is complete
	 * 
	 * @since $version
	 * @author hceylan
	 */
	boolean isMetadataComplete();
}
