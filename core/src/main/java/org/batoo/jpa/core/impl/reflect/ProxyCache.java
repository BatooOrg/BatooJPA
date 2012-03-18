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
package org.batoo.jpa.core.impl.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Map;

import org.batoo.jpa.core.BLogger;
import org.batoo.jpa.core.util.Pair2;

import com.google.common.base.Function;

/**
 * A Cache to cache proxy clasess to improve performance.
 * <p>
 * All accesses should go through {@link ReflectHelper#getProxyInstance(ClassLoader, Class, InvocationHandler)}.
 * 
 * @author hceylan
 * @since $version
 */
public final class ProxyCache {

	private static final BLogger LOG = BLogger.getLogger(ProxyCache.class);

	public static final ProxyCache INSTANCE = new ProxyCache();

	private Map<Pair2<ClassLoader, Class<?>>, Constructor<?>> cache;

	// Private, accesses should go through INSTANCE
	private ProxyCache() {
		super();

		this.cache = LoadingConcurrentHashMap.newInstance(new Function<Pair2<ClassLoader, Class<?>>, Constructor<?>>() {

			public Constructor<?> apply(Pair2<ClassLoader, Class<?>> input) {
				return ProxyCache.this.loadProxyClass(input);
			}
		});
	}

	/**
	 * Returns an instance of proxy instance.
	 * 
	 * @param loader
	 *            the class loader
	 * @param _interface
	 *            the interface
	 * @param h
	 *            the invocation handler
	 * @return the proxy instance
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@SuppressWarnings("unchecked")
	/* package */<T> T getProxyInstance(ClassLoader loader, Class<T> _interface, InvocationHandler h) {
		try {
			return (T) this.cache.get(new Pair2<ClassLoader, Class<?>>(loader, _interface)).newInstance(h);
		}
		catch (final Exception e) {
			throw new Error(e);
		}
	}

	protected Constructor<?> loadProxyClass(final Pair2<ClassLoader, Class<?>> key) {
		return AccessController.doPrivileged(new PrivilegedAction<Constructor<?>>() {

			public Constructor<?> run() {
				final Class<?> proxyClass = Proxy.getProxyClass(key.getFirst(), key.getSecond());
				try {
					return proxyClass.getConstructor(InvocationHandler.class);
				}
				catch (final Exception e) {
					LOG.fatal(e, "Cannot load proxy class {0}", key);

					throw new Error(e);
				}
			}
		});
	}
}
