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

import java.io.Serializable;

/**
 * Interface implemented by enhanced managed instances.
 * 
 * @author hceylan
 * @since $version
 */
public interface EnhancedInstance extends Serializable {

	/**
	 * Returns the id of the instance.
	 * 
	 * @return the id of the instance
	 * 
	 * @since $version
	 * @author hceylan
	 */
	ManagedInstance<?> __enhanced__$$__getId();

	/**
	 * Returns the managed instance of the instance.
	 * 
	 * @return the managed instance of the instance
	 * 
	 * @since $version
	 * @author hceylan
	 */
	ManagedInstance<?> __enhanced__$$__getManagedInstance();

	/**
	 * Returns if the instance has been initialized.
	 * 
	 * @return true if the instance has been initialized
	 * 
	 * @since $version
	 * @author hceylan
	 */
	boolean __enhanced__$$__isInitialized();

	/**
	 * Sets the managed instance of the instance.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	void __enhanced__$$__setManagedInstance(ManagedInstance instance);

}
