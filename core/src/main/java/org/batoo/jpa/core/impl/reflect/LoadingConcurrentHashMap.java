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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.google.common.base.Function;

/**
 * A Map implementation that loads the items lazily.
 * 
 * @author hceylan
 * @since $version
 */
public class LoadingConcurrentHashMap<K, V> extends ConcurrentHashMap<K, V> {

	private static final long serialVersionUID = -8443237773033919952L;

	/**
	 * Creates a new {@link LoadingConcurrentHashMap}.
	 * 
	 * @param function
	 *            the function to load
	 * @return
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public static <K, V> Map<K, V> newInstance(Function<K, V> function) {
		return new LoadingConcurrentHashMap<K, V>(function);
	}

	private final Function<K, V> f;

	/**
	 * @param f
	 *            the function to load
	 * @since $version
	 * @author hceylan
	 */
	public LoadingConcurrentHashMap(Function<K, V> f) {
		super();

		this.f = f;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@SuppressWarnings("unchecked")
	@Override
	public V get(Object key) {
		V v = super.get(key);

		if (v == null) {
			v = this.f.apply((K) key);
		}

		return v;
	}
}
