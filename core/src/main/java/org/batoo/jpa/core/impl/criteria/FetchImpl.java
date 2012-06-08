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

import javax.persistence.criteria.Fetch;
import javax.persistence.criteria.JoinType;
import javax.persistence.metamodel.SingularAttribute;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.mutable.MutableInt;
import org.batoo.jpa.core.impl.instance.ManagedInstance;
import org.batoo.jpa.core.impl.jdbc.ForeignKey;
import org.batoo.jpa.core.impl.jdbc.JoinTable;
import org.batoo.jpa.core.impl.manager.SessionImpl;
import org.batoo.jpa.core.impl.model.EntityTypeImpl;
import org.batoo.jpa.core.impl.model.attribute.AssociatedAttribute;
import org.batoo.jpa.core.impl.model.attribute.PluralAttributeImpl;
import org.batoo.jpa.core.impl.model.attribute.SingularAttributeImpl;

/**
 * Implementation of {@link Fetch}.
 * 
 * @param <Z>
 *            the source type of the fetch
 * @param <X>
 *            the target type of the fetch
 * 
 * @author hceylan
 * @since $version
 */
public class FetchImpl<Z, X> extends FetchParentImpl<Z, X> implements Fetch<Z, X> {

	@SuppressWarnings("unchecked")
	private static <X, Z> EntityTypeImpl<X> getEntity(AssociatedAttribute<? super Z, X, ?> attribute) {
		if (attribute instanceof SingularAttribute) {
			return (EntityTypeImpl<X>) ((SingularAttributeImpl<? super Z, X>) attribute).getType();
		}

		return (EntityTypeImpl<X>) ((PluralAttributeImpl<? super Z, ?, X>) attribute).getElementType();
	}

	private final FetchParentImpl<?, Z> parent;
	private final AssociatedAttribute<? super Z, X, ?> attribute;
	private final JoinType joinType;
	private ManagedInstance<?> parentInstance;

	/**
	 * @param parent
	 *            the parent of the join
	 * @param attribute
	 *            the attribute of the join
	 * @param joinType
	 *            the join type
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public FetchImpl(FetchParentImpl<?, Z> parent, AssociatedAttribute<? super Z, X, ?> attribute, JoinType joinType) {
		super(FetchImpl.getEntity(attribute));

		this.parent = parent;
		this.attribute = attribute;
		this.joinType = joinType;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateJoins(CriteriaQueryImpl<?> query) {
		final String parentAlias = this.getParent().getPrimaryTableAlias(query);
		final String alias = this.getPrimaryTableAlias(query);

		String join;

		final ForeignKey foreignKey = this.attribute.getInverse() != null ? this.attribute.getInverse().getForeignKey()
			: this.attribute.getForeignKey();
		if (foreignKey != null) {
			join = foreignKey.createSourceJoin(this.joinType, parentAlias, alias);
		}
		else {
			final JoinTable joinTable = this.attribute.getJoinTable() != null ? this.attribute.getJoinTable()
				: this.attribute.getInverse().getJoinTable();
			join = joinTable.createJoin(this.joinType, parentAlias, alias);
		}

		final String joins = super.generateJoins(query);

		return join + (StringUtils.isBlank(joins) ? "" : "\n\t") + joins;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public AssociatedAttribute<? super Z, ?, ?> getAttribute() {
		return this.attribute;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public JoinType getJoinType() {
		return this.joinType;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public FetchParentImpl<?, Z> getParent() {
		return this.parent;
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
	 * @param rowNo
	 *            the current row no
	 * @param parent
	 *            the parent managed instance
	 * @param leap
	 *            the jump size
	 * @return the managed instance
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public List<X> handle(SessionImpl session, BaseTypedQueryImpl<?> query, List<Map<String, Object>> data, MutableInt rowNo, int leap,
		ManagedInstance<?> parent) {
		this.parentInstance = parent;

		return super.handle(session, query, data, rowNo, leap);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	protected boolean shouldContinue(SessionImpl session, Map<String, Object> row) {
		final ManagedInstance<Z> instance = this.getParent().getInstance(session, row);

		return this.parentInstance.equals(instance);
	}
}
