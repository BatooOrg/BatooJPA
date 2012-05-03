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

import java.util.Arrays;
import java.util.List;

import org.batoo.jpa.core.impl.SessionImpl;
import org.batoo.jpa.core.impl.mapping.BasicMapping;
import org.batoo.jpa.core.impl.types.EntityTypeImpl;
import org.batoo.jpa.core.impl.types.SingularAttributeImpl;
import org.batoo.jpa.core.impl.types.TypeImpl;

import com.google.common.base.Joiner;

/**
 * Id of a persistent class
 * 
 * @author hceylan
 * @since $version
 */
public class ManagedId<X> {

	private final EntityTypeImpl<X> type;
	private final SessionImpl session;
	private X instance;
	private final BasicResolver[] resolvers;

	private BasicResolver singleId;

	private int h;

	private boolean initialized;
	private SingularAttributeImpl<? super X, ?> embeddedAttribute;
	private Class<?> idJavaType;
	private Object id;

	/**
	 * @param type
	 *            the type of the entity
	 * @param instance
	 *            the entity insance
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public ManagedId(EntityTypeImpl<X> type, SessionImpl session, X instance) {
		super();

		this.type = type;
		this.session = session;
		this.instance = instance;

		final List<BasicMapping<?, ?>> idMappings = this.type.getIdentityRoot().getIdMappings();
		this.resolvers = new BasicResolver[idMappings.size()];
		for (int i = 0; i < idMappings.size(); i++) {
			this.resolvers[i] = idMappings.get(i).createResolver(instance);
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

		final ManagedId<?> other = (ManagedId<?>) obj;

		if (!this.session.equals(other.session)) {
			return false;
		}

		if (!this.type.getIdentityRoot().equals(other.type.getIdentityRoot())) {
			return false;
		}

		return Arrays.equals(this.resolvers, other.resolvers);
	}

	/**
	 * Fills the sequence / table generated values.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void fillIdValues() {
		for (final BasicResolver resolver : this.resolvers) {
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
	@SuppressWarnings("unchecked")
	public Object getId() {
		if (this.id != null) {
			return this.id;
		}

		this.initialize();

		if (this.singleId != null) {
			return this.singleId.getValue();
		}

		if (this.embeddedAttribute != null) {
			return this.embeddedAttribute.get(this.instance);
		}

		Object id = null;
		try {
			id = this.idJavaType.newInstance();
		}
		catch (final Exception e) {}

		for (final SingularAttributeImpl<?, ?> attribute : this.type.getIdAttributes()) {
			final SingularAttributeImpl<? super X, ?> idAttribute = (SingularAttributeImpl<? super X, ?>) attribute;
			idAttribute.set(id, idAttribute.get(this.instance));
		}

		return id;
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
		result = (prime * result) + Arrays.hashCode(this.resolvers);
		result = (prime * result) + this.session.hashCode();
		return this.h = (prime * result) + this.type.hashCode();
	}

	/**
	 * 
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@SuppressWarnings("unchecked")
	private void initialize() {
		if (!this.initialized) {

			if (this.resolvers.length == 1) {
				this.singleId = this.resolvers[0];
			}
			else {
				final TypeImpl<?> idType = this.type.getIdType();
				if ((idType != null) && idType.isEmbeddable()) {
					this.embeddedAttribute = (SingularAttributeImpl<? super X, ?>) this.type.getIdAttributes()[0];
				}
				else if (this.type.getIdJavaType() != null) {
					this.idJavaType = this.type.getIdJavaType();
				}
			}
			this.initialized = true;
		}
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
	@SuppressWarnings("unchecked")
	public void populate(Object id) {
		this.id = id;

		this.initialize();

		if (this.embeddedAttribute != null) {
			this.embeddedAttribute.set(this.instance, id);
		}
		else if (this.type.getIdJavaType() != null) {
			for (final SingularAttributeImpl<?, ?> attribute : this.type.getIdAttributes()) {
				final SingularAttributeImpl<? super X, ?> idAttribute = (SingularAttributeImpl<? super X, ?>) attribute;
				idAttribute.set(this.instance, idAttribute.get(id));
			}
		}
		else {
			for (final BasicResolver resolver : this.resolvers) {
				resolver.setValue(id);
			}
		}
	}

	/**
	 * Sets the instance.
	 * 
	 * @param instance
	 *            the instance to set
	 * @since $version
	 */
	public void setInstance(X instance) {
		this.instance = instance;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String toString() {
		final String[] ids = new String[this.resolvers.length];
		for (int i = 0; i < this.resolvers.length; i++) {
			ids[i] = this.resolvers[i].mapping.getPathAsString() + "= " + this.resolvers[i].getValue();
		}

		final String idsStr = "[" + Joiner.on(", ").join(ids) + "]";

		return "ManagedId [type=" + this.type.getIdentityRoot().getName() + ", ids=" + idsStr + "]";
	}

}
