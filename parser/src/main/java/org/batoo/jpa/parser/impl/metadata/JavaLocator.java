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
package org.batoo.jpa.parser.impl.metadata;

import java.lang.reflect.Member;

import org.batoo.jpa.common.reflect.ReflectHelper;
import org.batoo.jpa.parser.impl.AbstractLocator;

/**
 * Locator for the java locations.
 * 
 * @author hceylan
 * @since $version
 */
public class JavaLocator extends AbstractLocator {

	private final Member member;
	private final Class<?> clazz;

	/**
	 * Constructs a new {@link JavaLocator} with class location.
	 * 
	 * @param clazz
	 *            the class
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public JavaLocator(Class<?> clazz) {
		super();

		this.clazz = clazz;

		this.member = null;
	}

	/**
	 * Constructs a new {@link JavaLocator} with member location.
	 * 
	 * @param member
	 *            the member
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public JavaLocator(Member member) {
		super();

		this.member = member;

		this.clazz = null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String toString() {
		if (this.member != null) {
			return ReflectHelper.createMemberName(this.member);
		}

		return this.clazz.getName();
	}

}
