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

import org.batoo.jpa.core.impl.mapping.OwnerAssociationMapping;

/**
 * Resolver for association mappings.
 * 
 * @author hceylan
 * @since $version
 */
public class AssociateResolver<X> extends AbstractResolver<X> {

	/**
	 * @param mapping
	 *            the mapping
	 * @param instance
	 *            the instance
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public AssociateResolver(OwnerAssociationMapping<?, ?> mapping, X instance) {
		super(mapping, instance);
	}

	/**
	 * Returns if the mapping contains the instance.
	 * 
	 * @param instance
	 * @return true if the mapping contains the instance
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public boolean contains(Object instance) {
		return this.getMapping().contains(instance);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public OwnerAssociationMapping<X, ?> getMapping() {
		return (OwnerAssociationMapping<X, ?>) super.getMapping();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean isAssociateResolver() {
		return true;
	}
}
