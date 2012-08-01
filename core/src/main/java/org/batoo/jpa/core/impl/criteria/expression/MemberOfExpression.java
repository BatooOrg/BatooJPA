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
package org.batoo.jpa.core.impl.criteria.expression;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Root;

import org.batoo.jpa.core.impl.criteria.AbstractCriteriaQueryImpl;
import org.batoo.jpa.core.impl.criteria.CriteriaBuilderImpl;
import org.batoo.jpa.core.impl.criteria.QueryImpl;
import org.batoo.jpa.core.impl.criteria.SubqueryImpl;
import org.batoo.jpa.core.impl.criteria.join.AbstractPluralJoin;
import org.batoo.jpa.core.impl.criteria.join.Joinable;
import org.batoo.jpa.core.impl.manager.SessionImpl;
import org.batoo.jpa.core.impl.model.attribute.PluralAttributeImpl;
import org.batoo.jpa.core.impl.model.type.EntityTypeImpl;

/**
 * Expression for empty
 * 
 * @param <C>
 *            the type of the collection
 * @param <E>
 *            the type of the elemenet
 * 
 * @author hceylan
 * @since $version
 */
public class MemberOfExpression<C extends Collection<E>, E> extends AbstractExpression<Boolean> {

	private final boolean not;
	private final CollectionExpression<?, ?> values;
	private final AbstractExpression<?> value;

	/**
	 * @param not
	 *            if not empty
	 * @param value
	 *            the value expression
	 * @param values
	 *            the values expression
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public MemberOfExpression(boolean not, Expression<E> value, Expression<C> values) {
		super(Boolean.class);

		this.not = not;
		this.values = (CollectionExpression<?, ?>) values;
		this.value = (AbstractExpression<?>) value;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateJpqlRestriction(AbstractCriteriaQueryImpl<?> query) {
		return this.value.generateJpqlRestriction(query) + (this.not ? " not member of " : " member of ") + this.values.generateJpqlRestriction(query);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateJpqlSelect(AbstractCriteriaQueryImpl<?> query, boolean selected) {
		throw new IllegalArgumentException("Collection expressions cannot be selected");
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateSqlSelect(AbstractCriteriaQueryImpl<?> query, boolean selected) {
		throw new IllegalArgumentException("Collection expressions cannot be selected");
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public String[] getSqlRestrictionFragments(AbstractCriteriaQueryImpl<?> query) {
		final CriteriaBuilderImpl cb = query.getMetamodel().getEntityManagerFactory().getCriteriaBuilder();
		final Joinable rp = this.values.getParentPath().getRootPath();

		final SubqueryImpl<Object> s = query.subquery(Object.class);

		final Root<?> r = s.from(rp.getEntity());
		final PluralAttributeImpl<?, C, E> attribute = (PluralAttributeImpl<?, C, E>) this.values.getMapping().getAttribute();
		final AbstractPluralJoin<?, C, E> j = (AbstractPluralJoin<?, C, E>) r.join(attribute.getName());

		final EntityTypeImpl<E> entity = (EntityTypeImpl<E>) attribute.getElementType();
		final String idName = entity.getIdMapping().getAttribute().getName();
		s.select(j.get(idName));

		s.where(cb.equal(r, (AbstractExpression<?>) rp));

		return new String[] { this.value.getSqlRestrictionFragments(query)[0] + " IN " + s.getSqlRestrictionFragments(query)[0] };
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Boolean handle(QueryImpl<?> query, SessionImpl session, ResultSet row) throws SQLException {
		return null; // N/A
	}
}
