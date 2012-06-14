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

import org.batoo.jpa.core.impl.model.attribute.SingularAttributeImpl;
import org.batoo.jpa.core.impl.model.type.EntityTypeImpl;

/**
 * The base class for {@link EmbeddedMapping} and {@link BasicMapping}.
 * 
 * @param <X>
 *            the type of the entity
 * @param <Y>
 *            the type of the value
 * 
 * @author hceylan
 * @since $version
 */
public abstract class SingularMapping<X, Y> extends AbstractMapping<X, Y> {

	/**
	 * 
	 * @param parent
	 *            the parent mapping, may be <code>null</code>
	 * @param entity
	 *            the entity
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public SingularMapping(EmbeddedMapping<?, ?> parent, EntityTypeImpl<X> entity) {
		super(parent, entity);
	}

	/**
	 * Fills the sequence / table generated value.
	 * <p>
	 * The operation returns false if at least one entity needs to obtain identity from the database.
	 * 
	 * @param instance
	 *            the instance to fill ids.
	 * @return false if all OK, true if if at least one entity needs to obtain identity from the database
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public abstract boolean fillValue(Object instance);

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public abstract SingularAttributeImpl<? super X, Y> getAttribute();
}
