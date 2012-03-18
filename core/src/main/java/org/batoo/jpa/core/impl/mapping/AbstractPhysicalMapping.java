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

import java.util.Deque;
import java.util.Map;
import java.util.Set;

import org.batoo.jpa.core.MappingException;
import org.batoo.jpa.core.impl.jdbc.PhysicalColumn;
import org.batoo.jpa.core.impl.types.AttributeImpl;

import com.google.common.collect.Maps;

/**
 * A Mapping with a physical column.
 * 
 * @author hceylan
 * @since $version
 */
public abstract class AbstractPhysicalMapping<X, T> extends AbstractMapping<X, T> implements PhysicalMapping<X, T> {

	private final Set<ColumnTemplate<X, T>> columnTemplates;

	private final Map<String, PhysicalColumn> physicalColumns = Maps.newHashMap();

	/**
	 * @param associationType
	 *            the type of the association
	 * @param declaringAttribute
	 *            the attribute which declares the mapping
	 * @param path
	 *            the path to the declaringAttribute
	 * @param columnTemplates
	 *            the set of column templates of the mapping
	 * @throws MappingException
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public AbstractPhysicalMapping(AssociationType associationType, AttributeImpl<X, T> declaringAttribute,
		Deque<AttributeImpl<?, ?>> path, Set<ColumnTemplate<X, T>> columnTemplates) throws MappingException {
		super(associationType, declaringAttribute, path);

		this.columnTemplates = columnTemplates;

		this.getOwner().addMapping(this);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void addColumn(PhysicalColumn physicalColumn) {
		this.physicalColumns.put(physicalColumn.getName(), physicalColumn);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public final Set<ColumnTemplate<X, T>> getColumnTemplates() {
		return this.columnTemplates;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Map<String, PhysicalColumn> getPhysicalColumns() {
		return this.physicalColumns;
	}
}
