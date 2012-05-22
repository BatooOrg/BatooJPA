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
package org.batoo.jpa.parser.impl.orm;

import java.util.Map;

import org.batoo.jpa.common.log.ToStringBuilder;
import org.batoo.jpa.common.log.ToStringBuilder.DetailLevel;

/**
 * Element factories that have no children elements.
 * 
 * @author hceylan
 * @since $version
 */
public abstract class ChildElementFactory extends ElementFactory {

	private static final String[] NO_CHILDREN = new String[] {};

	/**
	 * @param parent
	 *            the parent element factory
	 * @param attributes
	 *            the attributes
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public ChildElementFactory(ParentElementFactory parent, Map<String, String> attributes) {
		super(parent, attributes, ChildElementFactory.NO_CHILDREN);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String toString() {
		return new ToStringBuilder(this, DetailLevel.SHORT)//
		.excludeFieldNames("parent", "attributes", "expectedChildElements") //
		.toString();
	}

}
