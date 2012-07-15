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
package org.batoo.jpa.core.impl.manager;

/**
 * 
 * @author hceylan
 * @since $version
 */
public class CallbackAvailability {

	private boolean preRemove;
	private boolean postRemove;
	private boolean preWrite;
	private boolean postWrite;
	private boolean postLoad;

	/**
	 * Returns if there is postRemove callbacks.
	 * 
	 * @return true if there is PreRemove callbacks, false otherwise
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public boolean postRemove() {
		return this.preRemove;
	}

	/**
	 * Returns if there is PostWrite callbacks.
	 * 
	 * @return true if there is PostWrite callbacks, false otherwise
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public boolean postWrite() {
		return this.postWrite;
	}

	/**
	 * Returns if there is PreRemove callbacks.
	 * 
	 * @return true if there is PreRemove callbacks, false otherwise
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public boolean preRemove() {
		return this.preRemove;
	}

	/**
	 * Returns if there is PreWrite callbacks.
	 * 
	 * @return true if there is PreWrite callbacks, false otherwise
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public boolean preWrite() {
		return this.preWrite;
	}

	/**
	 * @param availability
	 *            the availability to update
	 * @param forUpdates
	 *            true if for updates or false for removals
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void updateAvailability(CallbackAvailability availability, Boolean forUpdates) {
		if ((forUpdates == null) || forUpdates) {
			this.preWrite |= availability.preWrite;
			this.postWrite |= availability.postWrite;
		}

		if ((forUpdates == null) || !forUpdates) {
			this.preRemove |= availability.preRemove;
			this.postRemove |= availability.postRemove;
		}

		if (forUpdates == null) {
			this.postLoad |= availability.postLoad;
		}
	}

	/**
	 * Updates the availability.
	 * 
	 * @param callbackManager
	 *            the callback manager
	 * @return the updated availability
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public CallbackAvailability updateAvailability(CallbackManager callbackManager) {
		this.preRemove |= callbackManager.preRemove() != null;
		this.postRemove |= callbackManager.postRemove() != null;

		this.preWrite |= (callbackManager.prePersist() != null) || (callbackManager.preUpdate() != null);
		this.postWrite |= (callbackManager.postPersist() != null) || (callbackManager.postUpdate() != null);

		this.postLoad |= callbackManager.postLoad() != null;

		return this;
	}
}
