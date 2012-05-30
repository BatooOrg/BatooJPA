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
package org.batoo.jpa.common.reflect;

/**
 * Abstract definition of accessors.
 * 
 * @author hceylan
 * @since $version
 */
public abstract class AbstractAccessor {

	/**
	 * Returns the value of the member.
	 * 
	 * @param instance
	 *            the instance of which the member value to return
	 * @return the value of the member
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public abstract Object get(Object instance);

	/**
	 * Sets the value of the member.
	 * 
	 * @param instance
	 *            the instance of which the member will be set
	 * @param value
	 *            the value to set
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public abstract void set(Object instance, Object value);
}
