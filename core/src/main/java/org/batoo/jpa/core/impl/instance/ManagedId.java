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

import java.util.Collection;
import java.util.Map;

import org.batoo.jpa.core.impl.SessionImpl;
import org.batoo.jpa.core.impl.types.EntityTypeImpl;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Collections2;

/**
 * Id of a persistent class
 * 
 * @author hceylan
 * @since $version
 */
public class ManagedId<X> {

	private final EntityTypeImpl<X> type;
	private final SessionImpl session;
	private final X instance;

	private final Map<String, BasicResolver<X>> resolvers;

	private int h;
	private X proxy;
	private BasicResolver<X> singleId;

	/**
	 * @param type
	 *            the type of the entity
	 * @param instance
	 *            the entity insance
	 * @param resolvers
	 *            id resolvers of the instance
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public ManagedId(EntityTypeImpl<X> type, SessionImpl session, X instance, Map<String, BasicResolver<X>> resolvers) {
		super();

		this.type = type;
		this.session = session;
		this.instance = instance;
		this.resolvers = resolvers;

		if (resolvers.size() == 1) {
			this.singleId = this.resolvers.values().iterator().next();
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		final ManagedId<?> other = (ManagedId<?>) obj;
		if (!this.type.equals(other.type)) {
			return false;
		}
		if (!this.session.equals(other.session)) {
			return false;
		}
		if (!this.resolvers.equals(other.resolvers)) {
			return false;
		}
		return true;
	}

	/**
	 * Fills the sequence / table generated values.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void fillIdValues() {
		for (final BasicResolver<X> resolver : this.resolvers.values()) {
			resolver.fillValue();
		}
	}

	/**
	 * Returns the id of the instance.
	 * 
	 * @return the id of the instance
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public Object getId() {
		return this.singleId.getValue();
	}

	/**
	 * Returns the instance.
	 * 
	 * @return the instance
	 * @since $version
	 */
	public X getInstance() {
		return this.proxy != null ? this.proxy : this.instance;
	}

	/**
	 * Returns the unproxied instance.
	 * 
	 * @return the unproxied instance
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public Object getInstance0() {
		return this.instance;
	}

	/**
	 * Returns the session.
	 * 
	 * @return the session
	 * @since $version
	 */
	public SessionImpl getSession() {
		return this.session;
	}

	/**
	 * Returns the type.
	 * 
	 * @return the type
	 * @since $version
	 */
	public EntityTypeImpl<X> getType() {
		return this.type;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public int hashCode() {
		if (this.h != 0) {
			return this.h;
		}

		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((this.resolvers == null) ? 0 : this.resolvers.hashCode());
		result = (prime * result) + ((this.session == null) ? 0 : this.session.hashCode());
		return this.h = (prime * result) + ((this.type == null) ? 0 : this.type.hashCode());
	}

	/**
	 * Populates the instance's id with the id.
	 * 
	 * @param id
	 *            the id for the instance
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void populate(Object id) {
		for (final BasicResolver<X> resolver : this.resolvers.values()) {
			synchronized (resolver) {
				resolver.unlock();
				resolver.setValue(id);
				resolver.relock();
			}
		}
	}

	/**
	 * Proxifies the instance with the proxy.
	 * 
	 * @param proxy
	 *            the proxy to proxify with
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void proxify(X proxy) {
		this.proxy = proxy;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String toString() {
		final Collection<String> ids = Collections2.transform(this.resolvers.values(), new Function<BasicResolver<X>, String>() {

			@Override
			public String apply(BasicResolver<X> input) {
				return input.mapping.getPathAsString() + "= " + input.getValue();
			}
		});

		final String idsStr = "[" + Joiner.on(", ").join(ids) + "]";

		return "ManagedId [type=" + this.type.getTopType().getName() + ", ids=" + idsStr + "]";
	}

}
