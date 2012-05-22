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
package org.batoo.jpa.common.log;

import org.batoo.jpa.common.impl.log.BLoggerImpl;
import org.slf4j.LoggerFactory;

/**
 * Standard logging facility for the Batoo
 * <p>
 * You should get an instance by calling
 * 
 * <pre>
 * public class MyClass {
 * 
 *     private static final BLogger LOG = BLoggerFactory.getLogger(MyClass.class);
 *     
 *     {...}
 * }
 * </pre>
 * 
 * <p>
 * You can alternatively give your log an arbitrary name
 * 
 * <pre>
 * public class MyClass {
 * 
 *     private static final BLogger LOG = BLoggerFactory.getLogger("MyCoolLogger");
 * 
 *     {...}
 * }
 * </pre>
 * 
 * <p>
 * If you would like to log to general application log use
 * 
 * <pre>
 * public class MyClass {
 * 
 *     private static final BLogger LOG = BLoggerFactory.getLogger();
 * 
 *     {...}
 * }
 * </pre>
 * 
 * @author hceylan
 * 
 */
public class BLoggerFactory {

	/**
	 * Returns an instance of BLogger for the class
	 * 
	 * @param clazz
	 *            the class of the log
	 * @return the logger - {@link BLoggerFactory}
	 */
	public static final BLogger getLogger(Class<?> clazz) {
		return new BLoggerImpl(LoggerFactory.getLogger(clazz));
	}

	/**
	 * Returns an instance of BLogger with the class
	 * 
	 * @param name
	 *            the name of the log
	 * @return the logger - {@link BLoggerFactory}
	 */
	public static final BLogger getLogger(String name) {
		return new BLoggerImpl(LoggerFactory.getLogger(name));
	}
}
