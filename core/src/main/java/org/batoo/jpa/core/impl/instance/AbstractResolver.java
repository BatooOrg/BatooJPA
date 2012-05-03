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

import org.apache.commons.lang.ObjectUtils;
import org.batoo.jpa.core.impl.mapping.AbstractMapping;

/**
 * Base class for all the resolvers.
 * 
 * @author hceylan
 * @since $version
 */
public abstract class AbstractResolver {

	protected final AbstractMapping<?, ?> mapping;
	protected final Object instance;

	/**
	 * @param mapping
	 *            the mapping
	 * @param instance
	 *            the instance
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public AbstractResolver(AbstractMapping<?, ?> mapping, Object instance) {
		super();

		this.mapping = mapping;
		this.instance = instance;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public final boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		final AbstractResolver other = (AbstractResolver) obj;
		if (!this.mapping.equals(other.mapping)) {
			return false;
		}

		final Object thisValue = this.getValue();
		final Object otherValue = other.getValue();

		if ((thisValue == null) || (otherValue == null)) {
			return false;
		}

		return ObjectUtils.equals(thisValue, otherValue);
	}

	/**
	 * Returns the mapping.
	 * 
	 * @return the mapping
	 * @since $version
	 */
	public AbstractMapping<?, ?> getMapping() {
		return this.mapping;
	}

	/**
	 * Returns the value of the instance for the mapping.
	 * 
	 * @return the value of the instance for the mapping
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public final Object getValue() {
		return this.mapping.getValue(this.instance);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		final Object value = this.mapping.getValue(this.instance);
		result = (prime * result) + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String toString() {
		return "Resolver [mapping=" + this.mapping + ", instance=" + this.instance + "]";
	}
}
