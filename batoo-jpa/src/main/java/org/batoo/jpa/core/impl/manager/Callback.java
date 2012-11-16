/*
 * Copyright (c) 2012 - Batoo Software ve Consultancy Ltd.
 * 
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */
package org.batoo.jpa.core.impl.manager;

import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;

import javax.persistence.PersistenceException;

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
	 * Fires the callback.
	 * 
	 * @param instance
	 *            the instance
	 * 
	 * @since $version
	 */
	public void fire(Object instance) {
		try {
			if (this.callbackType == CallbackType.CALLBACK) {
				this.method.invoke(instance);
			}
			else {
				this.method.invoke(this.instance, instance);
			}
		}
		catch (final Exception e) {
			throw new PersistenceException("Error while invoking callback or listener", e);
		}
	}

	/**
	 * Returns the callback type of the Callback.
	 * 
	 * @return the callback type of the Callback
	 * 
	 * @since $version
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
	 */
	public EntityListenerType getListenerType() {
		return this.listenerType;
	}
}
