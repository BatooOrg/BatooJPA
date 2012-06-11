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
package org.batoo.jpa.core.impl.model.mapping;

import org.batoo.jpa.core.impl.jdbc.BasicColumn;
import org.batoo.jpa.core.impl.model.attribute.PhysicalAttributeImpl;
import org.batoo.jpa.core.impl.model.type.EntityTypeImpl;
import org.batoo.jpa.core.impl.model.type.TypeImpl;

/**
 * Mapping for basic types, id, version, basic attributes.
 * 
 * @param <X>
 *            the type of the entity
 * @param <Y>
 *            the type of the value
 * 
 * @author hceylan
 * @since $version
 */
public abstract class PhysicalMapping<X, Y> extends AbstractMapping<X, Y> {

	/**
	 * @param entity
	 *            the entity
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public PhysicalMapping(EntityTypeImpl<X> entity) {
		super(entity);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public abstract PhysicalAttributeImpl<? super X, Y> getAttribute();

	/**
	 * Returns the physical column of the attribute.
	 * 
	 * @return the physical column of the attribute
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public abstract BasicColumn getColumn();

	/**
	 * Returns the bindable entity type.
	 * 
	 * @return the bindable entity type
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public abstract TypeImpl<Y> getType();
}
