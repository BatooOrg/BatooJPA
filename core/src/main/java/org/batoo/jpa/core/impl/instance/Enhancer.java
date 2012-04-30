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

import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;

import javassist.util.proxy.MethodHandler;

import org.batoo.jpa.core.impl.SessionImpl;
import org.batoo.jpa.core.impl.types.EntityTypeImpl;

/**
 * 
 * @author hceylan
 * @since $version
 */
public final class Enhancer<X> implements MethodHandler {

	private final EntityTypeImpl<X> type;
	private final Object id;
	private final SessionImpl session;
	private boolean initialized;

	public Enhancer(EntityTypeImpl<X> type, SessionImpl session, Object id, boolean lazy) {
		super();

		this.type = type;
		this.id = id;
		this.session = session;
		this.initialized = !lazy;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Object invoke(Object self, Method thisMethod, final Method proceed, Object[] args) throws Throwable {
		if (thisMethod.getName().equals("__Javassist__$$__isInitialized")) {
			return this.initialized;
		}

		if (!proceed.isAccessible()) {
			AccessController.doPrivileged(new PrivilegedAction<Void>() {

				@Override
				public Void run() {
					proceed.setAccessible(true);
					return null;
				}
			});
		}

		if (thisMethod.getDeclaringClass() == Object.class) {
			return proceed.invoke(self, args);
		}

		// if initialized, let go
		if (this.initialized) {
			return proceed.invoke(self, args);
		}

		if ((args.length == 0) && this.type.isIdMethod(thisMethod)) {
			return proceed.invoke(self, args);
		}

		this.session.getEntityManager().find(this.type.getJavaType(), this.id);
		this.initialized = true;

		return proceed.invoke(self, args);
	}

}
