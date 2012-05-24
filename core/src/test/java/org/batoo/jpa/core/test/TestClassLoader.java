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
package org.batoo.jpa.core.test;

import java.io.InputStream;

/**
 * A test class loader to map test persistence.xml's
 * 
 * @author hceylan
 * 
 * @since $version
 */
public class TestClassLoader extends ClassLoader {

	private String root;

	/**
	 * @since $version
	 * @author hceylan
	 */
	public TestClassLoader() {
		super();
	}

	/**
	 * Sets the parent class loader.
	 * 
	 * @param parent
	 *            the parent class loader
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public TestClassLoader(ClassLoader parent) {
		super(parent);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public InputStream getResourceAsStream(String name) {
		if (name.startsWith("META-INF")) {
			name = this.root + name.substring(8);
			return super.getResourceAsStream(name);
		}

		return super.getResourceAsStream(name);
	}

	/**
	 * Sets the root of the test.
	 * 
	 * @param root
	 *            the root of the test
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void setRoot(String root) {
		this.root = root.replace('.', '/');
	}
}
