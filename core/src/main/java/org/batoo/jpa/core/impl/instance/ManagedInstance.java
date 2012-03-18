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

import java.util.Map;
import java.util.Map.Entry;

import org.batoo.jpa.core.impl.SessionImpl;
import org.batoo.jpa.core.impl.mapping.MetamodelImpl;
import org.batoo.jpa.core.impl.types.EntityTypeImpl;

import com.google.common.collect.Maps;

/**
 * The managed instance of {@link #instance}.
 * 
 * @author hceylan
 * @since $version
 */
public final class ManagedInstance<X> implements Comparable<ManagedInstance<?>> {

	/**
	 * The states for a managed instance
	 * 
	 * @author hceylan
	 * @since $version
	 */
	public enum Status {
		/**
		 * Instance is new
		 */
		NEW,

		/**
		 * Instance is managed
		 */
		MANAGED,

		/**
		 * Instance is removed
		 */
		REMOVED,

		/**
		 * Instance is detached
		 */
		DETACHED
	}

	@SuppressWarnings("unchecked")
	public static final <X> ManagedInstance<X> create(SessionImpl session, X instance) {
		final MetamodelImpl metamodel = session.getEntityManager().getMetamodel();

		final EntityTypeImpl<X> type = metamodel.entity(((Class<X>) instance.getClass()));

		return type.getManagedInstance(session, instance);
	}

	private final EntityTypeImpl<X> type;
	private final SessionImpl session;
	private final X instance;

	private final ManagedId<X> id;

	private final Map<String, AbstractResolver<X>> resolvers;
	private Map<String, AssociateResolver<X>> associateResolvers;
	private Status status;
	private boolean executed;

	/**
	 * @param type
	 *            the type of the instance
	 * @param instance
	 *            the instance
	 * 
	 * @since $version
	 * @author hceylan
	 * @param session
	 */
	public ManagedInstance(EntityTypeImpl<X> type, SessionImpl session, X instance, ManagedId<X> id,
		Map<String, AbstractResolver<X>> resolvers) {
		super();

		this.type = type;
		this.session = session;
		this.instance = instance;
		this.resolvers = resolvers;
		this.id = id;

		this.status = Status.NEW;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public int compareTo(ManagedInstance<?> o) {
		if (this.dependsOn(o)) {
			return -1;
		}

		if (o.dependsOn(this)) {
			return 1;
		}

		return 0;
	}

	private boolean dependsOn(ManagedInstance<?> o) {
		for (final AssociateResolver<X> resolver : this.getAssociateResolvers().values()) {
			if (resolver.contains(o.getInstance())) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Returns if any DML executed for the instance.
	 * 
	 * @return if any DML executed for the instance
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public boolean executed() {
		return this.executed;
	}

	/**
	 * Fills the sequence / table generated values.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void fillIdValues() {
		this.id.fillIdValues();
	}

	/**
	 * Returns the associate resolvers.
	 * 
	 * @return the associate resolvers
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private Map<String, AssociateResolver<X>> getAssociateResolvers() {
		if (this.associateResolvers == null) {
			this.associateResolvers = Maps.newHashMap();

			for (final Entry<String, AbstractResolver<X>> entry : this.resolvers.entrySet()) {
				if (entry.getValue() instanceof AssociateResolver) {
					this.associateResolvers.put(entry.getKey(), (AssociateResolver<X>) entry.getValue());
				}
			}
		}

		return this.associateResolvers;
	}

	/**
	 * Returns the id.
	 * 
	 * @return the id
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public ManagedId<X> getId() {
		return this.id;
	}

	/**
	 * Returns the instance.
	 * 
	 * @return the instance
	 * @since $version
	 */
	public X getInstance() {
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
	 * Returns the status of the managed instance.
	 * 
	 * @return the status
	 * @since $version
	 */
	public Status getStatus() {
		return this.status;
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
	 * Sets the instance as executed DML.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void setExecuted() {
		this.executed = true;
	}

	/**
	 * Sets the instance's managed status.
	 * 
	 * @param managed
	 *            the new managed status
	 * 
	 * @since $version
	 */
	public void setStatus(Status status) {
		this.status = status;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String toString() {
		return "ManagedInstance [type=" + this.type.getName() + ", id=" + this.id + "]";
	}

}
