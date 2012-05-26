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
package org.batoo.jpa.parser;

import org.batoo.jpa.parser.impl.AbstractLocator;

import com.google.common.base.Joiner;

/**
 * 
 * Thrown when ORM XML or annotations' parsing encountered an error.
 * 
 * @author hceylan
 * @since $version
 */
public class MappingException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	/**
	 * @param message
	 *            the message to prepend to the constructed message
	 * @param locations
	 *            the locators
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public MappingException(String message, AbstractLocator... locations) {
		super(message + " Defined at:" + (locations.length > 1 ? "\n\t" : " ") + Joiner.on("\n\t").join(locations));
	}

	/**
	 * @param message
	 *            the message to prepend to the constructed message
	 * @param cause
	 *            the cause of the exception
	 * @param locations
	 *            the locators
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public MappingException(String message, Throwable cause, AbstractLocator... locations) {
		super(message + " Defined at:" + (locations.length > 1 ? "\n\t" : " ") + Joiner.on("\n\t").join(locations), cause);
	}

}
