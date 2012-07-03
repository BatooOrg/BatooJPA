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
package org.batoo.jpa.core.impl.criteria2;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.persistence.criteria.Selection;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.mutable.MutableInt;
import org.batoo.jpa.core.impl.manager.SessionImpl;

/**
 * The abstract implementation of {@link Selection}.
 * 
 * @param <X>
 *            the type of the selection item
 * 
 * @author hceylan
 * @since $version
 */
public abstract class AbstractSelection<X> extends AbstractTupleElement<X> implements Selection<X> {

	private String alias;
	private final String generatedAlias;

	/**
	 * @param javaType
	 *            the java type
	 * @param generatedAlias
	 *            the generated alias
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public AbstractSelection(Class<X> javaType, String generatedAlias) {
		super(javaType);

		this.generatedAlias = generatedAlias;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Selection<X> alias(String name) {
		if (this.alias != null) {
			throw new IllegalStateException("alias has already been set to" + this.alias);
		}

		this.alias = name;

		return this;
	}

	/**
	 * Returns the qualified name for the <code>JPQL</code> generation.
	 * 
	 * @return the qualified fragment for the <code>JPQL</code> generation
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public abstract String generateJpql();

	/**
	 * Returns the <code>select</code> for the <code>JPQL</code> generation.
	 * 
	 * @return the <code>select</code> for the <code>JPQL</code> generation
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public String generateSelectJpql() {
		final StringBuilder sb = new StringBuilder(this.generateJpql());

		if (StringUtils.isNotBlank(this.getAlias())) {
			sb.append(" as ").append(this.getAlias());
		}

		return sb.toString();
	}

	/**
	 * Returns the <code>select</code> fragment for the <code>SQL</code> generation.
	 * 
	 * @param query
	 *            the query
	 * @return the <code>select</code> fragment for the <code>SQL</code> generation
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public abstract String generateSelectSql(CriteriaQueryImpl<?> query);

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getAlias() {
		return this.alias != null ? this.alias : this.generatedAlias;
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
	 * @param session
	 *            the session
	 * @param data
	 *            the data
	 * @param rowNo
	 *            the current row no
	 * @return the collection of selection items
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public abstract Collection<? extends X> handle(SessionImpl session, LinkedList<Map<String, Object>> data, MutableInt rowNo);

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean isCompoundSelection() {
		return false;
	}
}
