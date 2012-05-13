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
package org.batoo.jpa.core.impl.mapping;

import javax.persistence.JoinColumn;

import org.batoo.jpa.core.impl.metamodel.AttributeImpl;
import org.batoo.jpa.core.impl.metamodel.PluralAttributeImpl;
import org.batoo.jpa.core.impl.metamodel.SingularAttributeImpl;

/**
 * Column template for Join Columns
 * 
 * @author hceylan
 * @since $version
 */
public class JoinColumnTemplate<X, T> extends ColumnTemplate<X, T> {

	private final BasicMapping<?, ?> referencedMapping;

	/**
	 * @param attribute
	 *            the attribute that owns that column
	 * @param column
	 *            the {@link JoinColumn} declaration
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public JoinColumnTemplate(AttributeImpl<X, T> attribute, JoinColumn column) {
		super(attribute, column.table(), column.name(), column.nullable(), column.unique(), column.insertable(), column.updatable());

		this.referencedMapping = null;
	}

	/**
	 * Cloning constructor
	 * 
	 * @param attribute
	 *            the new attribute
	 * @param original
	 *            the original column template
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public JoinColumnTemplate(AttributeImpl<X, T> attribute, JoinColumnTemplate<?, T> original) {
		super(attribute, original);

		this.referencedMapping = original.referencedMapping;
	}

	/**
	 * @param attribute
	 *            the attribute that owns the column
	 * @param referencedMapping
	 *            the referenced mapping
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public JoinColumnTemplate(PluralAttributeImpl<X, T, ?> attribute, BasicMapping<?, ?> referencedMapping) {
		super(attribute, null, attribute.getName() + "_" + referencedMapping.getDeclaringAttribute().getName(), true, false, true, true);

		this.referencedMapping = referencedMapping;
	}

	/**
	 * @param attribute
	 *            the attribute that owns that column
	 * @param referencedMapping
	 *            the referenced mapping
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public JoinColumnTemplate(SingularAttributeImpl<X, T> attribute, BasicMapping<?, ?> referencedMapping) {
		super(attribute, null, attribute.getName() + "_" + referencedMapping.getDeclaringAttribute().getName(), true, false, true, true);

		this.referencedMapping = referencedMapping;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <Y> JoinColumnTemplate<Y, T> clone(AttributeImpl<Y, T> attribute) {
		return new JoinColumnTemplate<Y, T>(attribute, this);
	}

	/**
	 * Returns the referenced mapping.
	 * 
	 * @return the referenced mapping
	 * @since $version
	 */
	public BasicMapping<?, ?> getReferencedMapping() {
		return this.referencedMapping;
	}
}
