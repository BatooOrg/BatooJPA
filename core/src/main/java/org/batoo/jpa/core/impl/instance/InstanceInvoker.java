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
import java.util.List;

import org.apache.commons.proxy.Invoker;
import org.apache.commons.proxy.factory.javassist.JavassistProxyFactory;
import org.batoo.jpa.core.impl.SessionImpl;
import org.batoo.jpa.core.impl.types.EntityTypeImpl;
import org.batoo.jpa.core.impl.types.SingularAttributeImpl;

import com.google.common.collect.Lists;

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
	public static <X> X createInvoker(ClassLoader classLoader, EntityTypeImpl<X> type, SessionImpl session, ManagedId<? super X> managedId) {
		return (X) new JavassistProxyFactory().createInvokerProxy(classLoader, new InstanceInvoker<X>(type, session, managedId.getId(),
			(X) managedId.getInstance()), new Class[] { type.getJavaType(), EnhancedInstance.class });
	}

	private final EntityTypeImpl<X> type;
	private final Object id;
	private final X instance;
	private final SessionImpl session;

	private final List<String> idMethods = Lists.newArrayList();

	private boolean initialized;

	private InstanceInvoker(EntityTypeImpl<X> type, SessionImpl session, Object id, X instance) {
		super();

		this.type = type;
		this.id = id;
		this.instance = instance;
		this.session = session;
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
		if (this.initialized) {
			return method.invoke(this.instance, arguments);
		}

		if (arguments.length == 0) {
			final String methodName = method.getName();
			if (this.idMethods.contains(methodName)) { // if known id method, let go
				return method.invoke(this.instance, arguments);
			}

			if (methodName.startsWith("get") && (methodName.length() > 3)) { // check if id method
				for (final SingularAttributeImpl<? super X, ?> attribute : this.type.getIdAttributes()) {
					if (attribute.getGetterName().equals(method.getName())) {
						this.idMethods.add(methodName);
						return method.invoke(this.instance, arguments);
					}
				}
			}
		}

		this.session.getEntityManager().find(this.type.getJavaType(), this.id);
		this.initialized = true;

		return method.invoke(this.instance, arguments);
	}
}
