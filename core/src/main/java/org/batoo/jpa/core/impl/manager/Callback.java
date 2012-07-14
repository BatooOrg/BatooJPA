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

import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;

import org.batoo.jpa.parser.MappingException;
import org.batoo.jpa.parser.impl.AbstractLocator;
import org.batoo.jpa.parser.metadata.EntityListenerMetadata.EntityListenerType;

/**
 * 
 * 
 * @author hceylan
 * @since $version
 */
public class Callback {

	/**
	 * Type of the callback
	 * 
	 * @author hceylan
	 * @since $version
	 */
	public static enum CallbackType {
		/**
		 * the listener type
		 */
		LISTENER,

		/**
		 * The callback type
		 */
		CALLBACK
	}

	private final EntityListenerType listenerType;
	private final CallbackType callbackType;
	private Method method;
	private Object instance;

	/**
	 * @param locator
	 *            the locator
	 * @param clazz
	 *            the class
	 * @param name
	 *            the name of the callback method
	 * @param listenerType
	 *            the listener type
	 * @param callbackType
	 *            the callback type
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public Callback(final AbstractLocator locator, final Class<?> clazz, final String name, EntityListenerType listenerType, final CallbackType callbackType) {
		super();

		this.callbackType = callbackType;
		this.listenerType = listenerType;

		this.method = AccessController.doPrivileged(new PrivilegedAction<Method>() {

			@Override
			public Method run() {
				try {
					if (callbackType == CallbackType.CALLBACK) {
						return clazz.getMethod(name);
					}
					else {
						return clazz.getMethod(name, Object.class);
					}
				}
				catch (final Exception e) {
					throw new MappingException("Unable to map callback " + clazz.getName() + "." + name, locator);
				}
			}
		});

		try {
			this.instance = callbackType == CallbackType.CALLBACK ? null : clazz.newInstance();
		}
		catch (final Exception e) {
			throw new MappingException("Unable to map callback " + clazz.getName() + "." + name, locator);
		}
	}

	/**
	 * Returns the callback type of the Callback.
	 * 
	 * @return the callback type of the Callback
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public CallbackType getCallbackType() {
		return this.callbackType;
	}

	/**
	 * Returns the listener type of the Callback.
	 * 
	 * @return the listener type of the Callback
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public EntityListenerType getListenerType() {
		return this.listenerType;
	}
}
