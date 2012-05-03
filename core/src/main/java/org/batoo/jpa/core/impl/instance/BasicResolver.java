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

import org.batoo.jpa.core.impl.mapping.BasicMapping;

/**
 * Resolver for basic mappings.
 * 
 * @author hceylan
 * @since $version
 */
public class BasicResolver extends AbstractResolver {

	private final boolean id;
	private int h;

	/**
	 * @param mapping
	 *            the mapping
	 * @param instance
	 *            the instance
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public BasicResolver(BasicMapping<?, ?> mapping, Object instance) {
		super(mapping, instance);
		this.id = this.getMapping().getDeclaringAttribute().isId();
	}

	/**
	 * Fills the sequence / table generated value.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void fillValue() {
		if (!this.id) {
			return;
		}

		this.getMapping().fillValue(this.instance);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public BasicMapping<?, ?> getMapping() {
		return (BasicMapping<?, ?>) this.mapping;
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

		if (this.id) {
			this.h = super.hashCode();

			return this.h;
		}

		return super.hashCode();
	}

	/**
	 * Sets the value of the instance for the mapping.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void setValue(Object value) {
		this.mapping.setValue(this.instance, value);
	}

}
