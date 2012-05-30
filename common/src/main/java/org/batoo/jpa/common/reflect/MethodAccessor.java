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
package org.batoo.jpa.common.reflect;

import java.lang.reflect.Method;

import org.batoo.jpa.common.BatooException;

/**
 * Accessor implementation of {@link AbstractAccessor} for the members of {@link Method}s.
 * 
 * @author hceylan
 * @since $version
 */
public class MethodAccessor extends AbstractAccessor {

	private final Method getter;
	private final Method setter;

	/**
	 * @param getter
	 *            the method to get
	 * @param setter
	 *            the method to set
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public MethodAccessor(Method getter, Method setter) {
		super();

		this.getter = getter;
		this.setter = setter;

		this.getter.setAccessible(true);
		this.setter.setAccessible(true);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Object get(Object instance) {
		try {
			return this.getter.invoke(instance);
		}
		catch (final Exception e) {
			throw new BatooException("Getter invocation failed");
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void set(Object instance, Object value) {
		try {
			this.setter.invoke(instance, value);
		}
		catch (final Exception e) {
			throw new BatooException("Setter invocation failed");
		}
	}

}
