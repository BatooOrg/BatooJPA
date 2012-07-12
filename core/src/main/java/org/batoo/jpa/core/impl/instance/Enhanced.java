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
package org.batoo.jpa.core.impl.instance;

import javax.persistence.PersistenceException;

import org.batoo.jpa.core.impl.manager.SessionImpl;

/**
 * A Sample class for the enhanced type. Only exists as a reference
 * 
 * @author hceylan
 * @since $version
 */
@SuppressWarnings("javadoc")
public class Enhanced implements EnhancedInstance {

	private static final long serialVersionUID = 1L;

	private boolean __enhanced_$$__initialized;
	private transient final Object __enhanced_$$__id;
	private transient final Class<?> __enhanced_$$__type;
	private transient final SessionImpl __enhanced_$$__session;
	private transient ManagedInstance<?> __enhanced__$$__managedInstance;

	public Enhanced() {
		super();

		this.__enhanced_$$__id = null;
		this.__enhanced_$$__type = null;
		this.__enhanced_$$__session = null;
	}

	public Enhanced(Class<?> type, SessionImpl session, Object id, boolean initialized) {
		super();

		this.__enhanced_$$__type = type;
		this.__enhanced_$$__session = session;
		this.__enhanced_$$__id = id;
		this.__enhanced_$$__initialized = initialized;
	}

	@Override
	public Object __enhanced__$$__getId() {
		return this.__enhanced_$$__id;
	}

	@Override
	public ManagedInstance<?> __enhanced__$$__getManagedInstance() {
		return this.__enhanced__$$__managedInstance;
	}

	@Override
	public boolean __enhanced__$$__isInitialized() {
		return this.__enhanced_$$__initialized;
	}

	@Override
	public void __enhanced__$$__setInitialized() {
		this.__enhanced_$$__initialized = true;
	}

	@Override
	public void __enhanced__$$__setManagedInstance(ManagedInstance<?> instance) {
		this.__enhanced__$$__managedInstance = instance;
	}

	@SuppressWarnings("unused")
	private void __enhanced_$$__check() {
		if (!this.__enhanced_$$__initialized) {
			if (this.__enhanced_$$__session == null) {
				throw new PersistenceException("No session to initialize the instance");
			}

			this.__enhanced_$$__session.getEntityManager().find(this.__enhanced_$$__type, this.__enhanced_$$__id);

			this.__enhanced_$$__initialized = true;
		}

		if (this.__enhanced_$$__session != null) {
			this.__enhanced__$$__managedInstance.changed();
		}

		return;
	}

	public Object get__enhanced__$$__id() {
		return this.__enhanced_$$__id;
	}

}
