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
package org.batoo.jpa.benchmark.insert;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Locale;

/**
 * A test class loader to map test persistence.xml's
 * 
 * @author hceylan
 * 
 * @since $version
 */
public class BenchmarkClassLoader extends ClassLoader {

	/**
	 * Benchmark type.
	 * 
	 * @author hceylan
	 * @since $version
	 */
	public enum Type {
		/**
		 * Batoo
		 */
		BATOO,

		/**
		 * Hibernate
		 */
		HIBERNATE
	}

	private final Type type;
	private final String xml;

	/**
	 * @param parent
	 *            the parent class loader
	 * @param type
	 *            the type
	 * @param xml
	 *            the xml
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public BenchmarkClassLoader(ClassLoader parent, Type type, String xml) {
		super(parent);

		this.type = type;
		this.xml = xml;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public URL getResource(String name) {
		if ("META-INF/persistence.xml".equals(name)) {
			return super.getResource(this.type.name().toLowerCase(Locale.ENGLISH) + "-" + this.xml + ".xml");
		}

		return super.getResource(name);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Enumeration<URL> getResources(String name) throws IOException {
		if ("META-INF/persistence.xml".equals(name)) {
			return super.getResources(this.type.name().toLowerCase(Locale.ENGLISH) + "-" + this.xml + ".xml");
		}

		return super.getResources(name);
	}
}
