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
package org.batoo.jpa.parser.metadata;

import java.util.List;

/**
 * The definition for entity listeners.
 * 
 * @author hceylan
 * @since $version
 */
public interface EntityListenerMetadata extends LocatableMatadata {

	/**
	 * 
	 * Entity listener callback types.
	 * 
	 * @author hceylan
	 * @since $version
	 */
	public enum EntityListenerType {
		/**
		 * Callback type that is called prior to persist operations.
		 */
		PRE_PERSIST,

		/**
		 * Callback type that is called prior to update operations.
		 */
		PRE_UPDATE,

		/**
		 * Callback type that is called prior to remove operations.
		 */
		PRE_REMOVE,

		/**
		 * Callback type that is called after load operations.
		 */
		POST_LOAD,

		/**
		 * Callback type that is called after persist operations.
		 */
		POST_PERSIST,

		/**
		 * Callback type that is called after update operations.
		 */
		POST_UPDATE,

		/**
		 * Callback type that is called after remove operations.
		 */
		POST_REMOVE
	}

	/**
	 * Returns the list of callbacks.
	 * 
	 * @return the list of callbacks
	 * 
	 * @since $version
	 * @author hceylan
	 */
	List<CallbackMetadata> getCallbacks();

	/**
	 * Returns the entity listener class.
	 * 
	 * @return the entity listener class
	 * 
	 * @since $version
	 * @author hceylan
	 */
	String getClassName();
}
