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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.persistence.EntityTransaction;
import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Parameter;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.ParameterExpression;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.mutable.MutableInt;
import org.batoo.jpa.common.log.BLogger;
import org.batoo.jpa.common.log.BLoggerFactory;
import org.batoo.jpa.core.impl.criteria.expression.ParameterExpressionImpl;
import org.batoo.jpa.core.impl.instance.ManagedInstance;
import org.batoo.jpa.core.impl.manager.EntityManagerImpl;
import org.batoo.jpa.core.impl.manager.SessionImpl;
import org.batoo.jpa.core.impl.model.MetamodelImpl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * The type used to control the execution of typed queries.
 * 
 * @param <X>
 *            query result type
 * @author hceylan
 * @since $version
 */
public class QueryImpl<X> implements TypedQuery<X>, ResultSetHandler<List<X>>, Query {

	private static final int MAX_COL_LENGTH = 30;

	private static final BLogger LOG = BLoggerFactory.getLogger(QueryImpl.class);

	private final EntityManagerImpl em;
	private final BaseQuery<X> q;
	private String sql;
	private final Map<String, Object> hints = Maps.newHashMap();
	private int startPosition = 0;
	private int maxResult = Integer.MAX_VALUE;

	private final Map<ParameterExpressionImpl<?>, Object> parameters = Maps.newHashMap();
	private final List<X> results = Lists.newArrayList();

	private LockModeType lockMode;

	private final List<Map<String, Object>> data = Lists.newArrayList();
	private ResultSetMetaData md;
	private String[] labels;

	private FlushModeType flushMode = FlushModeType.AUTO;

	/**
	 * @param q
	 *            the criteria query
	 * @param entityManager
	 *            the entity manager
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public QueryImpl(BaseQuery<X> q, EntityManagerImpl entityManager) {
		super();

		this.q = q;
		this.em = entityManager;
		this.sql = this.q.getSql();

		for (final ParameterExpression<?> p : this.q.getParameters()) {
			this.parameters.put((ParameterExpressionImpl<?>) p, null);
		}
	}

	private Object[] applyParameters() {
		int paramCount = 0;
		if ((this.startPosition != 0) || (this.maxResult != Integer.MAX_VALUE)) {
			QueryImpl.LOG.debug("Rows restricted to {0} / {1}", this.startPosition, this.maxResult);

			this.sql = this.q.getMetamodel().getJdbcAdaptor().applyPagination(this.sql, this.startPosition, this.maxResult);
		}

		final MetamodelImpl metamodel = this.em.getMetamodel();

		final List<ParameterExpressionImpl<?>> sqlParameters = this.q.getSqlParameters();

		for (final ParameterExpressionImpl<?> parameter : sqlParameters) {
			paramCount += parameter.getExpandedCount(metamodel);
		}

		final MutableInt sqlIndex = new MutableInt(0);
		final Object[] parameters = new Object[paramCount];

		for (final ParameterExpressionImpl<?> parameter : sqlParameters) {
			parameter.setParameter(metamodel, parameters, sqlIndex, this.parameters.get(parameter));
		}
		return parameters;
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

		final StringBuffer dump = new StringBuffer("Query returned {0} row(s):\n");

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

		QueryImpl.LOG.debug(dump.toString(), this.data.size());
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public int executeUpdate() {
		final Object[] parameters = this.applyParameters();

		try {
			return new QueryRunner().update(this.em.getConnection(), this.sql, parameters);
		}
		catch (final SQLException e) {
			QueryImpl.LOG.error(e, "Query failed" + QueryImpl.LOG.lazyBoxed(this.sql, parameters));

			final EntityTransaction transaction = this.em.getTransaction();
			if (transaction != null) {
				transaction.setRollbackOnly();
			}

			throw new PersistenceException("Query failed", e);
		}
	}

	/**
	 * Returns the criteria query of the typed query.
	 * 
	 * @return the criteria query of the typed query
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public BaseQuery<X> getCriteriaQuery() {
		return this.q;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public int getFirstResult() {
		return this.startPosition;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public FlushModeType getFlushMode() {
		return this.flushMode;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Map<String, Object> getHints() {
		return Maps.newHashMap(this.hints);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public LockModeType getLockMode() {
		return this.lockMode;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public int getMaxResults() {
		return this.maxResult;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public ParameterExpressionImpl<?> getParameter(int position) {
		for (final Entry<ParameterExpressionImpl<?>, Object> entry : this.parameters.entrySet()) {
			if (Integer.valueOf(position).equals(entry.getKey().getPosition())) {
				return entry.getKey();
			}
		}

		return this.getParameter(Integer.toString(position));
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <T> ParameterExpressionImpl<T> getParameter(int position, Class<T> type) {
		return (ParameterExpressionImpl<T>) this.getParameter(position);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public ParameterExpressionImpl<?> getParameter(String name) {
		for (final Entry<ParameterExpressionImpl<?>, Object> entry : this.parameters.entrySet()) {
			if (name.equals(entry.getKey().getAlias())) {
				return entry.getKey();
			}
		}

		throw new IllegalArgumentException("Parameter with the name " + name + " does not exist");
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <T> Parameter<T> getParameter(String name, Class<T> type) {
		return (Parameter<T>) this.getParameter(name);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Set<Parameter<?>> getParameters() {
		final Set<Parameter<?>> parameters = Sets.newHashSet();

		parameters.addAll(this.q.getParameters());

		return parameters;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Object getParameterValue(int position) {
		return this.parameters.get(this.getParameter(position));
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <T> T getParameterValue(Parameter<T> param) {
		return (T) this.parameters.get(param);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Object getParameterValue(String name) {
		return this.parameters.get(this.getParameter(name));
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public List<X> getResultList() {
		this.em.getSession().setLoadTracker();

		ManagedInstance.LOCK_CONTEXT.set(this.getLockMode());
		try {
			return this.getResultListImpl();
		}
		finally {
			ManagedInstance.LOCK_CONTEXT.set(null);
		}
	}

	private List<X> getResultListImpl() {
		try {

			final LockModeType lockMode = this.getLockMode();
			if ((lockMode == LockModeType.PESSIMISTIC_READ) || (lockMode == LockModeType.PESSIMISTIC_WRITE)
				|| (lockMode == LockModeType.PESSIMISTIC_FORCE_INCREMENT)) {
				this.sql = this.em.getJdbcAdaptor().applyLock(this.sql, lockMode);
			}

			final Object[] parameters = this.applyParameters();

			try {
				return new QueryRunner().query(this.em.getConnection(), this.sql, this, parameters);
			}
			catch (final SQLException e) {
				QueryImpl.LOG.error(e, "Query failed" + QueryImpl.LOG.lazyBoxed(this.sql, parameters));

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

		final CriteriaQueryImpl<X> cq = (CriteriaQueryImpl<X>) this.q;
		final AbstractSelection<X> selection = cq.getSelection();
		final boolean debug = QueryImpl.LOG.isDebugEnabled();
		if (debug) {
			this.prepareLabels(this.md);
		}

		final SessionImpl session = this.em.getSession();

		// process the resultset
		while (rs.next()) {

			final X instance = selection.handle(this, session, rs);
			if (!cq.isDistinct() || !this.results.contains(instance)) {
				this.results.add(instance);
			}

			if (debug) {
				this.storeData(rs);
			}
		}

		final LockModeType lockMode = this.getLockMode();
		if (lockMode != null) {
			for (final X instance : this.results) {
				this.em.lock(session.get(instance), lockMode, null);
			}
		}

		if (debug) {
			this.dumpResultSet();
		}

		return this.results;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean isBound(Parameter<?> param) {
		return this.parameters.containsKey(param);
	}

	private int max(int length1, int length2) {
		return Math.min(QueryImpl.MAX_COL_LENGTH, Math.max(length1, length2));
	}

	private void prepareLabels(final ResultSetMetaData md) throws SQLException {
		this.labels = new String[md.getColumnCount()];

		for (int i = 0; i < this.labels.length; i++) {
			String label = md.getColumnName(i + 1) + " (" + md.getColumnTypeName(i + 1) + ")";
			label = StringUtils.abbreviate(label, QueryImpl.MAX_COL_LENGTH);

			this.labels[i] = label;
		}
	}

	private QueryImpl<X> putParam(Parameter<?> param, Object value) {
		this.parameters.put((ParameterExpressionImpl<?>) param, value);

		return this;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public TypedQuery<X> setFirstResult(int startPosition) {
		this.startPosition = startPosition;

		return this;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public QueryImpl<X> setFlushMode(FlushModeType flushMode) {
		this.flushMode = flushMode;;

		return this;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public TypedQuery<X> setHint(String hintName, Object value) {
		this.hints.put(hintName, value);

		return this;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public TypedQuery<X> setLockMode(LockModeType lockMode) {
		this.lockMode = lockMode;

		return this;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public TypedQuery<X> setMaxResults(int maxResult) {
		this.maxResult = maxResult;
		return this;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public TypedQuery<X> setParameter(int position, Calendar value, TemporalType temporalType) {
		return this.setParameter(this.getParameter(position, Calendar.class), value, temporalType);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public TypedQuery<X> setParameter(int position, Date value, TemporalType temporalType) {
		return this.setParameter(this.getParameter(position, Date.class), value, temporalType);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public QueryImpl<X> setParameter(int position, Object value) {
		return this.putParam(this.getParameter(position), value);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public TypedQuery<X> setParameter(Parameter<Calendar> param, Calendar value, TemporalType temporalType) {
		switch (temporalType) {
			case DATE:
				return this.putParam(param, new java.sql.Date(value.getTimeInMillis()));
			case TIME:
				return this.putParam(param, new java.sql.Time(value.getTimeInMillis()));
			default:
				return this.putParam(param, new java.sql.Timestamp(value.getTimeInMillis()));
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public TypedQuery<X> setParameter(Parameter<Date> param, Date value, TemporalType temporalType) {
		switch (temporalType) {
			case DATE:
				return this.putParam(param, new java.sql.Date(value.getTime()));
			case TIME:
				return this.putParam(param, new java.sql.Time(value.getTime()));
			default:
				return this.putParam(param, new java.sql.Timestamp(value.getTime()));
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <T> TypedQuery<X> setParameter(Parameter<T> param, T value) {
		this.parameters.put((ParameterExpressionImpl<?>) param, value);

		return this;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public TypedQuery<X> setParameter(String name, Calendar value, TemporalType temporalType) {
		return this.setParameter(this.getParameter(name, Calendar.class), value, temporalType);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public TypedQuery<X> setParameter(String name, Date value, TemporalType temporalType) {
		return this.setParameter(this.getParameter(name, Date.class), value, temporalType);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public TypedQuery<X> setParameter(String name, Object value) {
		return this.putParam(this.getParameter(name), value);
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
		final HashMap<String, Object> data = Maps.newHashMap();

		final int columnCount = this.md.getColumnCount();
		for (int i = 0; i < columnCount; i++) {
			final Object value = rs.getObject(i + 1);
			data.put(this.md.getColumnName(i + 1), value);
		}

		this.data.add(data);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <T> T unwrap(Class<T> cls) {
		return (T) this;
	}
}
