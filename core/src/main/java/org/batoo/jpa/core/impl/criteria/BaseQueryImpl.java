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

import java.util.ArrayList;
import java.util.HashSet;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Root;

import org.batoo.jpa.core.impl.metamodel.MetamodelImpl;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

/**
 * Base of the {@link CriteriaQueryImpl} that performs the SQL generations.
 * 
 * @param <T>
 *            the type of the result
 * 
 * @author hceylan
 * @since $version
 */
public abstract class BaseQueryImpl<T> extends AbstractQueryImpl<T> {

	protected boolean distinct;
	protected SelectionImpl<? extends T> selection;
	protected Expression<Boolean> restriction;

	private String sql;

	/**
	 * @param original
	 *            the original query item
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public BaseQueryImpl(BaseQueryImpl<T> original) {
		super(original);

		this.distinct = original.distinct;
		this.selection = original.selection;
		this.restriction = original.restriction;
	}

	/**
	 * @param metamodel
	 *            the metamodel
	 * @param resultType
	 *            the result type
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public BaseQueryImpl(MetamodelImpl metamodel, Class<T> resultType) {
		super(metamodel, resultType);
	}

	private String generateWhere() {
		// TODO Auto-generated method stub
		return "";
	}

	/**
	 * Returns the generated SQL.
	 * 
	 * @return the generated SQL
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public String getSql() {
		if (this.sql != null) {
			return this.sql;
		}

		return this.prepareSql();
	}

	/**
	 * Returns the generated SQL.
	 * 
	 * @return the generated SQL
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private synchronized String prepareSql() {
		// other thread prepared already?
		if (this.sql != null) {
			return this.sql;
		}

		final String select = this.selection.generateSelect();

		final ArrayList<String> froms = Lists.newArrayList();
		final HashSet<Root<?>> roots = this.getRoots();
		for (final Root<?> root : roots) {
			froms.add(((RootImpl<?>) root).generateFrom(this));
		}

		final String from = "FROM " + Joiner.on(",").join(froms);
		final String where = this.generateWhere();

		return Joiner.on("\n").join(select, from, where);
	}

}
