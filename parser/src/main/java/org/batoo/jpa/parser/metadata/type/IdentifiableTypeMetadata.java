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

import java.util.List;

import org.batoo.jpa.parser.metadata.CallbackMetadata;
import org.batoo.jpa.parser.metadata.EntityListenerMetadata;

/**
 * The definition for identifiable types.
 * 
 * @author hceylan
 * @since $version
 */
public interface IdentifiableTypeMetadata extends ManagedTypeMetadata {

	/**
	 * Returns if the default listeners are excluded.
	 * 
	 * @return true the default listeners are excluded, false otherwise
	 * 
	 * @since $version
	 * @author hceylan
	 */
	boolean excludeDefaultListeners();

	/**
	 * Returns if the super class listeners are excluded.
	 * 
	 * @return true the super class listeners are excluded, false otherwise
	 * 
	 * @since $version
	 * @author hceylan
	 */
	boolean excludeSuperclassListeners();

	/**
	 * Returns the callbacks of the identifiable type.
	 * 
	 * @return the callbacks of the identifiable type
	 * 
	 * @since $version
	 * @author hceylan
	 */
	List<CallbackMetadata> getCallbacks();

	/**
	 * Returns the id class of the identifiable type.
	 * 
	 * @return the id class of the identifiable type
	 * 
	 * @since $version
	 * @author hceylan
	 */
	String getIdClass();

	/**
	 * Returns the entity listeners of the identifiable type.
	 * 
	 * @return the entity listeners of the identifiable type
	 * 
	 * @since $version
	 * @author hceylan
	 */
	List<EntityListenerMetadata> getListeners();
}
