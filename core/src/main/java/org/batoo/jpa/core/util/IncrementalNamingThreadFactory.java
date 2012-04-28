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
package org.batoo.jpa.core.util;

import java.util.concurrent.ThreadFactory;

/**
 * Thread factory that names the threads in the form of "name [no]".
 * 
 * @author hceylan
 * @since $version
 */
public class IncrementalNamingThreadFactory implements ThreadFactory {

	private int nextThreadNo = 1;

	private final String name;

	/**
	 * @param name
	 *            the common name for the threads
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public IncrementalNamingThreadFactory(String name) {
		super();

		this.name = name;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Thread newThread(Runnable r) {
		return new Thread(r, this.name + " [" + this.nextThreadNo++ + "]");
	}

}
