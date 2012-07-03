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
package org.batoo.jpa.core.impl.criteria;

import javax.persistence.Tuple;
import javax.persistence.TupleElement;

/**
 * The definition of an element that is returned in a query result tuple.
 * 
 * @param <X>
 *            the type of the element
 * @see Tuple
 * 
 * @author hceylan
 * @since $version
 */
public abstract class TupleElementImpl<X> implements TupleElement<X> {

	private final Class<X> javaType;

	/**
	 * @param javaType
	 *            the java type
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public TupleElementImpl(Class<X> javaType) {
		super();

		this.javaType = javaType;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Class<? extends X> getJavaType() {
		return this.javaType;
	}
}
