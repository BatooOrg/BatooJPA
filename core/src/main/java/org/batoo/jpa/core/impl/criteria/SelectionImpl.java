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
package org.batoo.jpa.core.impl.criteria;

import java.util.List;
import java.util.Map;

import javax.persistence.criteria.Selection;

import org.apache.commons.lang.mutable.MutableInt;
import org.batoo.jpa.core.impl.manager.SessionImpl;

/**
 * The definition of an item that is to be
 * returned in a query result.
 * 
 * @param <X>
 *            the type of the selection item
 * @author hceylan
 * @since $version
 */
public abstract class SelectionImpl<X> extends TupleElementImpl<X> implements Selection<X> {

	private String alias;

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Selection<X> alias(String alias) {
		if (alias == null) {
			throw new IllegalStateException("Alias has already been set");
		}

		this.alias = alias;

		return this;
	}

	/**
	 * Returns the generated SQL fragment.
	 * 
	 * @param query
	 *            the query
	 * @return the generated SQL fragment
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public abstract String generate(CriteriaQueryImpl<?> query);

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getAlias() {
		return this.alias;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public List<Selection<?>> getCompoundSelectionItems() {
		return null;
	}

	/**
	 * Handles the row.
	 * <p>
	 * The default implementation does nothing.
	 * 
	 * @param session
	 *            the session
	 * @param query
	 *            the query
	 * @param data
	 *            the resultset data
	 * @return the managed instance
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public List<X> handle(SessionImpl session, BaseTypedQueryImpl<?> query, List<Map<String, Object>> data, MutableInt rowNo) {
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean isCompoundSelection() {
		return false;
	}
}
