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
package org.batoo.jpa.core.impl.criteria.path;

import javax.persistence.criteria.Path;

import org.batoo.jpa.core.impl.criteria.join.FetchParentImpl;

/**
 * The interface for the paths that can be parent.
 * 
 * @param <Z>
 *            the source type
 * @param <X>
 *            the target type
 * 
 * @author hceylan
 * @since $version
 */
public interface ParentPath<Z, X> extends Path<X> {

	/**
	 * Returns the fetch root of the path.
	 * 
	 * @return the fetch root of the path
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public abstract FetchParentImpl<?, X> getFetchRoot();
}
