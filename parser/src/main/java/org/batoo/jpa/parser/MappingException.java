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

import java.util.Arrays;
import java.util.Collection;

import org.batoo.jpa.parser.impl.AbstractLocator;

import com.google.common.base.Joiner;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;

/**
 * 
 * Thrown when ORM XML or annotations' parsing encountered an error.
 * 
 * @author hceylan
 * @since $version
 */
public class MappingException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private static String getLocation(AbstractLocator[] locators) {
		if (locators == null) {
			return "";
		}

		final Collection<AbstractLocator> filteredLocators = Collections2.filter(Arrays.asList(locators),
			Predicates.not(Predicates.isNull()));
		if (filteredLocators.size() == 0) {
			return "";
		}

		return " Defined at:" + (filteredLocators.size() > 1 ? "\n\t" : " ") + Joiner.on("\n\t").skipNulls().join(filteredLocators);
	}

	/**
	 * @param message
	 *            the message to prepend to the constructed message
	 * @param locators
	 *            the locators
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public MappingException(String message, AbstractLocator... locators) {
		super(message + MappingException.getLocation(locators));
	}

	/**
	 * @param message
	 *            the message to prepend to the constructed message
	 * @param cause
	 *            the cause of the exception
	 * @param locators
	 *            the locators
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public MappingException(String message, Throwable cause, AbstractLocator... locators) {
		super(message + MappingException.getLocation(locators), cause);
	}

}
