/*
 * Copyright (c) 2012 - Batoo Software ve Consultancy Ltd.
 * 
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */
package org.batoo.jpa.core.impl.criteria;

import java.sql.Connection;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.persistence.CacheRetrieveMode;
import javax.persistence.CacheStoreMode;
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

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.mutable.MutableInt;
import org.batoo.common.log.BLogger;
import org.batoo.common.log.BLoggerFactory;
import org.batoo.jpa.JPASettings;
import org.batoo.jpa.core.impl.cache.CacheImpl;
import org.batoo.jpa.core.impl.cache.CacheReference;
import org.batoo.jpa.core.impl.criteria.expression.AbstractParameterExpressionImpl;
import org.batoo.jpa.core.impl.criteria.expression.EntityConstantExpression;
import org.batoo.jpa.core.impl.criteria.expression.ParameterExpressionImpl;
import org.batoo.jpa.core.impl.instance.ManagedInstance;
import org.batoo.jpa.core.impl.manager.EntityManagerFactoryImpl;
import org.batoo.jpa.core.impl.manager.EntityManagerImpl;
import org.batoo.jpa.core.impl.manager.SessionImpl;
import org.batoo.jpa.core.impl.model.EntityTypeImpl;
import org.batoo.jpa.core.impl.model.MetamodelImpl;
import org.batoo.jpa.jdbc.PreparedStatementProxy;
import org.batoo.jpa.jdbc.adapter.JdbcAdaptor.PaginationParamsOrder;
import org.batoo.jpa.jdbc.dbutils.QueryRunner;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * The type used to control the execution of typed queries.
 * 
 * @param <X>
 *            query result type
 * @author hceylan
 * @since 2.0.0
 */
public class QueryImpl<X> implements TypedQuery<X>, Query {

	private static final int MAX_COL_LENGTH = 30;

	private static final BLogger LOG = BLoggerFactory.getLogger(QueryImpl.class);

	private final EntityManagerFactoryImpl emf;
	private final EntityManagerImpl em;
	private final BaseQuery<X> q;
	private String sql;
	private final Map<String, Object> hints = Maps.newHashMap();
	private int startPosition = 0;
	private int maxResult = Integer.MAX_VALUE;

	private final Map<ParameterExpressionImpl<?>, Object> parameters = Maps.newHashMap();

	private List<X> results;

	private LockModeType lockMode;

	private final List<Object[]> data = Lists.newArrayList();
	private ResultSetMetaData md;
	private String[] labels;

	private FlushModeType flushMode = FlushModeType.AUTO;

	private boolean pmdBroken;

	/**
	 * @param q
	 *            the criteria query
	 * @param entityManager
	 *            the entity manager
	 * 
	 * @since 2.0.0
	 */
	public QueryImpl(BaseQuery<X> q, EntityManagerImpl entityManager) {
		super();

		this.emf = entityManager.getEntityManagerFactory();
		this.em = entityManager;
		this.q = q;
		this.sql = this.q.getSql();

		for (final ParameterExpression<?> p : this.q.getParameters()) {
			this.parameters.put((ParameterExpressionImpl<?>) p, null);
		}

		this.pmdBroken = entityManager.getJdbcAdaptor().isPmdBroken();
	}

	private Object[] applyParameters(Connection connection) {
		if ((this.startPosition != 0) || (this.maxResult != Integer.MAX_VALUE)) {
			QueryImpl.LOG.debug("Rows restricted to {0} / {1}", this.startPosition, this.maxResult);

			this.sql = this.q.getMetamodel().getJdbcAdaptor().applyPagination(this.sql, this.startPosition, this.maxResult);
		}

		final MetamodelImpl metamodel = this.em.getMetamodel();

		final List<AbstractParameterExpressionImpl<?>> sqlParameters = this.q.getSqlParameters();

		int paramCount = 0;
		for (int i = 0; i < sqlParameters.size(); i++) {
			paramCount += sqlParameters.get(i).getExpandedCount(metamodel);
		}

		// determine if we need to expand param count for pagination
		if (this.q.getMetamodel().getJdbcAdaptor().parameterizedPagination() && ((this.maxResult != Integer.MAX_VALUE) || (this.startPosition != 0))) {
			final PaginationParamsOrder paginationParamsOrder = this.q.getJdbcAdaptor().getPaginationParamsOrder();

			final boolean paginationHasStart = (this.startPosition != 0) || this.q.getJdbcAdaptor().paginationNeedsStartAlways();
			final boolean paginationHasMaxResults = (this.maxResult != Integer.MAX_VALUE) || this.q.getJdbcAdaptor().paginationNeedsMaxResultsAlways();

			paramCount = paramCount + (paginationHasMaxResults && paginationHasStart ? 2 : 1);

			final MutableInt sqlIndex = new MutableInt(0);
			final Object[] parameters = new Object[paramCount];

			if (!paginationParamsOrder.isAfterMainSql()) {
				if (paginationParamsOrder == PaginationParamsOrder.MAX_START_SQL) {
					if (paginationHasStart) {
						parameters[sqlIndex.intValue()] = this.startPosition;
						sqlIndex.increment();
					}

					if (paginationHasMaxResults) {
						parameters[sqlIndex.intValue()] = this.maxResult;
						sqlIndex.increment();
					}
				}
				else {
					if (paginationHasMaxResults) {
						parameters[sqlIndex.intValue()] = this.maxResult;
						sqlIndex.increment();
					}

					if (paginationHasStart) {
						parameters[sqlIndex.intValue()] = this.startPosition;
						sqlIndex.increment();
					}
				}
			}

			for (int i = 0; i < sqlParameters.size(); i++) {
				final AbstractParameterExpressionImpl<?> parameter = sqlParameters.get(i);
				if (parameter instanceof EntityConstantExpression) {
					((EntityConstantExpression<?>) parameter).setParameter(metamodel, connection, parameters, sqlIndex);
				}
				else {
					((ParameterExpressionImpl<?>) parameter).setParameter(metamodel, connection, parameters, sqlIndex, this.parameters.get(parameter));
				}
			}

			if (paginationParamsOrder.isAfterMainSql()) {
				if (paginationParamsOrder == PaginationParamsOrder.SQL_START_MAX) {
					if (paginationHasStart) {
						parameters[sqlIndex.intValue()] = this.startPosition;
						sqlIndex.increment();
					}

					if (paginationHasMaxResults) {
						parameters[sqlIndex.intValue()] = this.maxResult;
						sqlIndex.increment();
					}
				}
				else if (paginationParamsOrder == PaginationParamsOrder.SQL_START_END) {
					if (paginationHasStart) {
						parameters[sqlIndex.intValue()] = this.startPosition;
						sqlIndex.increment();
					}

					if (paginationHasMaxResults) {
						parameters[sqlIndex.intValue()] = (long) this.startPosition + (long) this.maxResult;
						sqlIndex.increment();
					}
				}
				else if (paginationParamsOrder == PaginationParamsOrder.SQL_END_START) {
					if (paginationHasMaxResults) {
						parameters[sqlIndex.intValue()] = (long) this.startPosition + (long) this.maxResult;
						sqlIndex.increment();
					}

					if (paginationHasStart) {
						parameters[sqlIndex.intValue()] = this.startPosition;
						sqlIndex.increment();
					}
				}
				else {
					if (paginationHasMaxResults) {
						parameters[sqlIndex.intValue()] = this.maxResult;
						sqlIndex.increment();
					}

					if (paginationHasStart) {
						parameters[sqlIndex.intValue()] = this.startPosition;
						sqlIndex.increment();
					}
				}
			}

			return parameters;
		}

		// no pagination parameter
		final MutableInt sqlIndex = new MutableInt(0);
		final Object[] parameters = new Object[paramCount];

		for (int i = 0; i < sqlParameters.size(); i++) {
			final AbstractParameterExpressionImpl<?> parameter = sqlParameters.get(i);
			if (parameter instanceof EntityConstantExpression) {
				((EntityConstantExpression<?>) parameter).setParameter(metamodel, connection, parameters, sqlIndex);
			}
			else {
				((ParameterExpressionImpl<?>) parameter).setParameter(metamodel, connection, parameters, sqlIndex, this.parameters.get(parameter));
			}
		}

		return parameters;
	}

	@SuppressWarnings("unchecked")
	private List<X> buildResultSet(CacheImpl cache, List<CacheReference[]> cachedResults) {
		final MetamodelImpl metamodel = this.em.getMetamodel();

		for (int i = 0; i < cachedResults.size(); i++) {
			final CacheReference[] references = cachedResults.get(i);
			if (references.length == 1) {
				final EntityTypeImpl<?> childType = metamodel.entity(references[0].getType());
				final X instance = (X) this.em.find(childType.getJavaType(), references[0].getId());

				this.results.add(instance);
			}
			else {
				final Object[] instanceArray = new Object[references.length];
				for (int j = 0; j < references.length; j++) {

					final EntityTypeImpl<?> childType = metamodel.entity(references[0].getType());
					instanceArray[j] = this.em.find(childType.getJavaType(), references[0].getId());

				}

				this.results.add((X) instanceArray);
			}
		}

		return this.results;
	}

	private List<X> buildResultSet(Connection connection, final Object[] parameters, CacheStoreMode cacheStoreMode) {
		try {
			this.buildResultSetImpl(connection, parameters);

			if (((cacheStoreMode == CacheStoreMode.REFRESH) || (cacheStoreMode == CacheStoreMode.USE)) && (this.q instanceof CriteriaQueryImpl)) {
				this.emf.getCache().put(this.sql, parameters, this.results);
			}

			return this.results;
		}
		catch (final SQLException e) {
			QueryImpl.LOG.error(e, "Query failed" + QueryImpl.LOG.lazyBoxed(this.sql, parameters));

			this.em.setRollbackOnly();

			throw new PersistenceException("Query failed", e);
		}
	}

	/**
	 * The implementation of the result set build. Manages the statement, parameters and result set.
	 * 
	 * @param connection
	 *            the connection
	 * @param parameters
	 *            the parameters
	 * @throws SQLException
	 *             thrown by the underlying database in case of an error
	 * 
	 * @since 2.0.0
	 */
	private void buildResultSetImpl(final Connection connection, final Object[] parameters) throws SQLException {
		PreparedStatement statement = null;
		ResultSet resultSet = null;

		try {
			final String _sql = this.sql;

			final Map<Integer, Integer> repeat = Maps.newHashMap();

			int sqlParamNo = 0;
			for (final Object parameter : parameters) {
				if (parameter != null) {
					if (parameter instanceof Collection) {
						repeat.put(sqlParamNo, ((Collection<?>) parameter).size());
					}
					else if (parameter.getClass().isArray()) {
						repeat.put(sqlParamNo, ((Object[]) parameter).length);
					}
				}

				sqlParamNo++;
			}

			if (repeat.size() > 0) {
				statement = connection.prepareStatement(this.expandParams(_sql, repeat));
			}
			else {
				statement = connection.prepareStatement(_sql);
			}

			this.fillStatement(statement, parameters, repeat);

			resultSet = statement.executeQuery();

			this.handle(resultSet);
		}
		finally {
			try {
				DbUtils.close(resultSet);
			}
			finally {
				DbUtils.close(statement);
			}
		}
	}

	private void dumpResultSet() throws SQLException {
		final int[] lengths = new int[this.labels.length];
		for (int i = 0; i < lengths.length; i++) {
			lengths[i] = this.max(lengths[i], StringUtils.length(this.labels[i]));
		}

		for (final Object[] data : this.data) {
			for (int i = 0; i < this.labels.length; i++) {
				final Object value = data[i];
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

		for (final Object[] data : this.data) {
			dump.append("\n| ");

			for (int i = 0; i < this.labels.length; i++) {
				final Object value = data[i];

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
		// flush if specified
		if (!this.q.isInternal() && this.em.hasActiveTransaction()
			&& ((this.flushMode == FlushModeType.AUTO) || (this.em.getFlushMode() == FlushModeType.AUTO))) {
			this.em.flush();
		}

		final Connection connection = this.em.getConnection();
		final Object[] parameters = this.applyParameters(connection);

		try {
			this.em.assertTransaction();

			return new QueryRunner(this.em.getJdbcAdaptor().isPmdBroken(), false).update(connection, this.sql, parameters);
		}
		catch (final SQLException e) {
			QueryImpl.LOG.error(e, "Query failed" + QueryImpl.LOG.lazyBoxed(this.sql, parameters));

			this.em.setRollbackOnly();

			throw new PersistenceException("Query failed", e);
		}
	}

	/**
	 * Expands the repeated parameters.
	 * 
	 * @param _sql
	 *            the original SQL
	 * @param repeat
	 *            the repeat map
	 * @return the expanded SQL
	 * 
	 * @since 2.0.0
	 */
	private String expandParams(String _sql, Map<Integer, Integer> repeat) {
		final StringBuffer outSql = new StringBuffer();

		final int sqlIndex = 0;
		int i = 0;
		boolean inQuot = false;

		while (i < _sql.length()) {
			final char current = _sql.charAt(i);

			if (current == '\'') {
				if (inQuot) {
					inQuot = !inQuot;
				}

				outSql.append('\'');
			}
			else if (!inQuot && (current == '?')) {
				final Integer repeatCount = repeat.get(sqlIndex);

				if (repeatCount != null) {
					int left = repeatCount;

					outSql.append('?');
					left--;

					while (left > 0) {
						outSql.append(", ?");
						left--;
					}
				}
				else {
					outSql.append('?');
				}
			}
			else {
				outSql.append(current);
			}

			i++;
		}

		return outSql.toString();
	}

	/**
	 * Fills the statement with the parameters supplied.
	 * 
	 * @param statement
	 *            the statement
	 * @param parameters
	 *            the parameters
	 * @param repeat
	 *            the parameter repeat map
	 * @throws SQLException
	 *             thrown in case of an underlying SQL Exception
	 * 
	 * @since 2.0.0
	 */
	private void fillStatement(PreparedStatement statement, Object[] parameters, Map<Integer, Integer> repeat) throws SQLException {
		// the following code has been adopted from Apache Commons DBUtils.

		// no paramaters nothing to do
		if ((parameters == null) || (parameters.length == 0)) {
			return;
		}

		final ParameterMetaData pmd = this.pmdBroken ? null : statement.getParameterMetaData();

		if (this.pmdBroken) {
			int total = parameters.length - repeat.size();

			if (repeat.size() > 0) {
				for (final Integer repeatSize : repeat.values()) {
					if (repeatSize != null) {
						total += repeatSize;
					}
				}
			}

			((PreparedStatementProxy) statement).setParamCount(total);
		}

		int index = 1;
		for (int i = 0; i < parameters.length; i++) {
			if (parameters[i] != null) {
				if (repeat.containsKey(i)) {
					final Object paramValue = parameters[i];

					if (paramValue instanceof Collection) {
						final Collection<?> collection = (Collection<?>) paramValue;
						for (final Object subParamValue : collection) {
							statement.setObject(index++, subParamValue);
						}
					}
					else {
						final Object[] array = (Object[]) paramValue;
						for (final Object subParamValue : array) {
							statement.setObject(index++, subParamValue);
						}
					}
				}
				else {
					statement.setObject(index++, parameters[i]);
				}
			}
			else {
				// VARCHAR works with many drivers regardless
				// of the actual column type. Oddly, NULL and
				// OTHER don't work with Oracle's drivers.
				int sqlType = Types.VARCHAR;
				if (!this.pmdBroken) {
					try {
						sqlType = pmd.getParameterType(index++ + 1);
					}
					catch (final SQLException e) {
						this.pmdBroken = true;
					}
				}

				statement.setNull(index++, sqlType);
			}
		}
	}

	/**
	 * Returns the criteria query of the typed query.
	 * 
	 * @return the criteria query of the typed query
	 * 
	 * @since 2.0.0
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
	 * Returns the JPQL that representing the query.
	 * 
	 * @return the JPQL that representing the query
	 * 
	 * @since $version
	 */
	public String getJpql() {
		return this.q.getJpql();
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

		throw new IllegalArgumentException("Query does not have parameter number " + position);
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
		// flush if specified
		if (!this.q.isInternal() && this.em.hasActiveTransaction()
			&& ((this.flushMode == FlushModeType.AUTO) || (this.em.getFlushMode() == FlushModeType.AUTO))) {
			this.em.flush();
		}

		ManagedInstance.LOCK_CONTEXT.set(this.getLockMode());
		try {
			return this.getResultListImpl();
		}
		finally {
			ManagedInstance.LOCK_CONTEXT.set(null);
		}
	}

	private List<X> getResultListImpl() {
		this.em.getSession().setLoadTracker();

		final CacheImpl cache = this.emf.getCache();

		final CacheRetrieveMode cacheRetrieveMode = (CacheRetrieveMode) this.hints.get(JPASettings.SHARED_CACHE_RETRIEVE_MODE);
		if (cacheRetrieveMode != null) {
			cache.setCacheRetrieveMode(cacheRetrieveMode);
		}

		final CacheStoreMode cacheStoreMode = (CacheStoreMode) this.hints.get(JPASettings.SHARED_CACHE_STORE_MODE);
		if (cacheStoreMode != null) {
			cache.setCacheStoreMode(cacheStoreMode);
		}

		final Connection connection = this.em.getConnection();
		try {
			final LockModeType lockMode = this.getLockMode();
			final boolean hasLock = (lockMode == LockModeType.PESSIMISTIC_READ) || (lockMode == LockModeType.PESSIMISTIC_WRITE)
				|| (lockMode == LockModeType.PESSIMISTIC_FORCE_INCREMENT);
			if (hasLock) {
				this.sql = this.em.getJdbcAdaptor().applyLock(this.sql, lockMode);
			}

			final Object[] parameters = this.applyParameters(connection);

			if ((cacheRetrieveMode == CacheRetrieveMode.USE) && !hasLock && (this.q instanceof CriteriaQueryImpl)) {
				final CriteriaQueryImpl<X> cq = (CriteriaQueryImpl<X>) this.q;
				if (cq.getSelection().isEntityList()) {
					final List<CacheReference[]> cachedResults = cache.get(this.sql, parameters);

					if (cachedResults != null) {
						return this.buildResultSet(cache, cachedResults);
					}
				}
			}

			return this.buildResultSet(connection, parameters, cacheStoreMode);

		}
		finally {
			this.em.closeConnectionIfNecessary();

			this.em.getSession().releaseLoadTracker();

			if (cacheRetrieveMode != null) {
				cache.setCacheRetrieveMode(null);
			}

			if (cacheStoreMode != null) {
				cache.setCacheStoreMode(null);
			}
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
	 * Handles and returns the value created from the result set.
	 * 
	 * @param rs
	 *            the result set
	 * @return the value created from the result set
	 * @throws SQLException
	 * 
	 * @since 2.0.0
	 */
	private List<X> handle(ResultSet rs) throws SQLException {
		this.md = rs.getMetaData();

		final CriteriaQueryImpl<X> cq = (CriteriaQueryImpl<X>) this.q;
		final AbstractSelection<X> selection = cq.getSelection();
		final boolean debug = QueryImpl.LOG.isDebugEnabled();
		if (debug) {
			this.prepareLabels(this.md);
		}

		this.results = Lists.newArrayList();

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
			for (int i = 0; i < this.results.size(); i++) {
				this.em.lock(session.get(this.results.get(i)), lockMode, null);
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
		this.flushMode = flushMode;

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
	 * @since 2.0.0
	 */
	public void storeData(ResultSet rs) throws SQLException {
		final Object[] data = new Object[this.md.getColumnCount()];

		final int columnCount = this.md.getColumnCount();
		for (int i = 0; i < columnCount; i++) {
			data[i] = rs.getObject(i + 1);
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
