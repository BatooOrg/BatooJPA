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

import org.apache.commons.proxy.Invoker;
import org.apache.commons.proxy.factory.javassist.JavassistProxyFactory;
import org.batoo.jpa.core.impl.SessionImpl;
import org.batoo.jpa.core.impl.types.EntityTypeImpl;

/**
 * 
 * @author hceylan
 * @since $version
 */
public final class InstanceInvoker<X> implements Invoker {

	public interface EnhancedInstance {
		Object __getInstance();
	}

	/**
	 * @param classLoader
	 *            the class loader
	 * @param type
	 *            the type of the instance
	 * @param session
	 *            the session
	 * @param managedId
	 *            the managed id of the instance
	 * @return the instance invoker for the instance
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@SuppressWarnings("unchecked")
	public static <X> X createInvoker(EntityTypeImpl<X> type, SessionImpl session, ManagedId<X> managedId, boolean lazy) {
		final InstanceInvoker<X> invoker = new InstanceInvoker<X>(type, session, managedId, managedId.getInstance(), lazy);

		return (X) new JavassistProxyFactory().createInvokerProxy(type.getClassLoader(), invoker, new Class[] { type.getJavaType(),
			EnhancedInstance.class });
	}

	private final EntityTypeImpl<X> type;
	private final ManagedId<X> id;
	private final X instance;
	private final SessionImpl session;

	private boolean initialized;

	private InstanceInvoker(EntityTypeImpl<X> type, SessionImpl session, ManagedId<X> id, X instance, boolean lazy) {
		super();

		this.type = type;
		this.id = id;
		this.instance = instance;
		this.session = session;
		this.initialized = !lazy;
	}

	@Override
	public Object invoke(Object proxy1, final Method method, Object[] arguments) throws Throwable {
		if (method.getName().equals("__getInstance")) {
			return this.instance;
		}

		if (!method.isAccessible()) {
			AccessController.doPrivileged(new PrivilegedAction<Void>() {

				@Override
				public Void run() {
					method.setAccessible(true);
					return null;
				}
			});
		}

		// if initialized, let go
		if (this.initialized || ((arguments.length == 0) && this.type.isIdMethod(method.getName()))) {
			return method.invoke(this.instance, arguments);
		}

		this.session.getEntityManager().find(this.type.getJavaType(), this.id.getId());
		this.initialized = true;

		return method.invoke(this.instance, arguments);
	}
}
