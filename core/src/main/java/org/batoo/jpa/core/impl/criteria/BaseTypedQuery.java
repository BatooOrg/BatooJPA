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

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.mutable.MutableInt;
import org.batoo.jpa.common.log.BLogger;
import org.batoo.jpa.common.log.BLoggerFactory;
import org.batoo.jpa.core.impl.criteria.expression.ParameterExpressionImpl;
import org.batoo.jpa.core.impl.manager.EntityManagerImpl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * The base implementation of {@link TypedQueryImpl}.
 * 
 * @param <X>
 *            query result type
 * 
 * @author hceylan
 * @since $version
 */
public abstract class BaseTypedQuery<X> implements TypedQuery<X>, ResultSetHandler<List<X>> {

	private static final int MAX_COL_LENGTH = 30;

	private static final BLogger LOG = BLoggerFactory.getLogger(BaseTypedQuery.class);

	protected final CriteriaQueryImpl<X> cq;
	private final EntityManagerImpl em;
	private final AbstractSelection<X> selection;
	protected final Map<ParameterExpressionImpl<?>, Object> parameters = Maps.newHashMap();
	private final List<X> results = Lists.newArrayList();

	private final LinkedList<Map<String, Object>> data = Lists.newLinkedList();
	private ResultSetMetaData md;
	private String[] labels;

	/**
	 * @param criteriaQuery
	 *            the criteria query
	 * @param entityManager
	 *            the entity manager
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public BaseTypedQuery(CriteriaQueryImpl<X> criteriaQuery, EntityManagerImpl entityManager) {
		super();

		this.cq = criteriaQuery;
		this.em = entityManager;
		this.selection = this.cq.getSelection();
	}

	private void dumpResultSet() throws SQLException {
		final int[] lengths = new int[this.labels.length];
		for (int i = 0; i < lengths.length; i++) {
			lengths[i] = this.max(lengths[i], StringUtils.length(this.labels[i]));
		}

		for (final Map<String, Object> data : this.data) {
			for (int i = 0; i < this.labels.length; i++) {
				final Object value = data.get(this.md.getColumnName(i + 1));
				if (value != null) {
					lengths[i] = this.max(lengths[i], StringUtils.length(value.toString()));
				}
			}
		}

		int length = 1;
		for (final int l : lengths) {
			length += l + 3;
		}

		final StringBuffer dump = new StringBuffer("\n");

		// the labels
		dump.append(StringUtils.repeat("-", length));
		dump.append("\n| ");

		for (int i = 0; i < this.labels.length; i++) {
			String strValue = StringUtils.abbreviate(this.labels[i], lengths[i]);
			strValue = StringUtils.rightPad(strValue, lengths[i]);

			dump.append(strValue);
			dump.append(" | ");
		}

		// the data
		dump.append("\n");
		dump.append(StringUtils.repeat("-", length));

		for (final Map<String, Object> data : this.data) {
			dump.append("\n| ");

			for (int i = 0; i < this.labels.length; i++) {
				final Object value = data.get(this.md.getColumnName(i + 1));

				String strValue = value != null ? value.toString() : "!NULL!";
				strValue = StringUtils.abbreviate(strValue, lengths[i]);
				if (value instanceof Number) {
					strValue = StringUtils.leftPad(strValue, lengths[i]);
				}
				else {
					strValue = StringUtils.rightPad(strValue, lengths[i]);
				}

				dump.append(strValue);
				dump.append(" | ");
			}

		}

		dump.append("\n");
		dump.append(StringUtils.repeat("-", length));

		BaseTypedQuery.LOG.debug(dump.toString());
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public List<X> getResultList() {
		this.em.getSession().setLoadTracker();
		try {

			int paramCount = 0;

			final String sql = this.cq.getSql();

			final List<ParameterExpressionImpl<?>> sqlParameters = this.cq.getSqlParameters();

			for (final ParameterExpressionImpl<?> parameter : sqlParameters) {
				paramCount += parameter.getExpandedCount();
			}

			final MutableInt sqlIndex = new MutableInt(0);
			final MutableInt paramIndex = new MutableInt(0);
			final Object[] parameters = new Object[paramCount];

			for (final ParameterExpressionImpl<?> parameter : sqlParameters) {
				parameter.setParameter(parameters, sqlIndex, paramIndex, this.parameters.get(parameter));
			}

			try {
				return new QueryRunner().query(this.em.getConnection(), sql, this, parameters);
			}
			catch (final SQLException e) {
				BaseTypedQuery.LOG.error(e, "Query failed" + BaseTypedQuery.LOG.lazyBoxed(sql, parameters));

				final EntityTransaction transaction = this.em.getTransaction();
				if (transaction != null) {
					transaction.setRollbackOnly();
				}

				throw new PersistenceException("Query failed", e);
			}
		}
		finally {
			this.em.getSession().releaseLoadTracker();
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public X getSingleResult() {
		final List<X> resultList = this.getResultList();

		if (resultList.size() > 1) {
			throw new NonUniqueResultException();
		}

		if (resultList.size() == 0) {
			throw new NoResultException();
		}

		return resultList.get(0);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public List<X> handle(ResultSet rs) throws SQLException {
		this.md = rs.getMetaData();

		// store the data
		while (rs.next()) {
			this.storeData(rs);
		}

		if (this.data.size() == 0) {
			BaseTypedQuery.LOG.debug("No result returned");

			return Collections.emptyList();
		}

		if (BaseTypedQuery.LOG.isDebugEnabled()) {
			this.prepareLabels(this.md);
		}

		if (BaseTypedQuery.LOG.isDebugEnabled()) {
			this.dumpResultSet();
		}

		// process the resultset
		final MutableInt rowNo = new MutableInt(0);
		while (rowNo.intValue() < this.data.size()) {
			this.results.addAll(this.selection.handle(this.em.getSession(), this.data, rowNo));
		}

		return this.results;
	}

	private int max(int length1, int length2) {
		return Math.min(BaseTypedQuery.MAX_COL_LENGTH, Math.max(length1, length2));
	}

	private void prepareLabels(final ResultSetMetaData md) throws SQLException {
		this.labels = new String[md.getColumnCount()];

		for (int i = 0; i < this.labels.length; i++) {
			String label = md.getColumnName(i + 1) + " (" + md.getColumnTypeName(i + 1) + ")";
			label = StringUtils.abbreviate(label, BaseTypedQuery.MAX_COL_LENGTH);

			this.labels[i] = label;
		}
	}

	/**
	 * Stores the row to report the result set.
	 * 
	 * @param rs
	 *            the resultset
	 * @throws SQLException
	 *             thrown in case of an underlying SQL Exception
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void storeData(ResultSet rs) throws SQLException {
		final Map<String, Object> data = Maps.newHashMap();

		for (int i = 0; i < this.md.getColumnCount(); i++) {
			final Object value = rs.getObject(i + 1);
			data.put(this.md.getColumnName(i + 1), value);
		}

		this.data.add(data);
	}
}
