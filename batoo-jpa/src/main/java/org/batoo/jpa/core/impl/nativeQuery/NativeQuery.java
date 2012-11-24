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
package org.batoo.jpa.core.impl.nativeQuery;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Parameter;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.persistence.TemporalType;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.Tree;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.lang.NotImplementedException;
import org.batoo.common.log.BLogger;
import org.batoo.common.log.BLoggerFactory;
import org.batoo.jpa.core.impl.instance.EnhancedInstance;
import org.batoo.jpa.core.impl.instance.ManagedId;
import org.batoo.jpa.core.impl.instance.ManagedInstance;
import org.batoo.jpa.core.impl.manager.EntityManagerImpl;
import org.batoo.jpa.core.impl.manager.SessionImpl;
import org.batoo.jpa.core.impl.model.EntityTypeImpl;
import org.batoo.jpa.core.impl.model.mapping.AbstractMapping;
import org.batoo.jpa.core.impl.model.mapping.BasicMappingImpl;
import org.batoo.jpa.core.impl.model.mapping.EmbeddedMappingImpl;
import org.batoo.jpa.jdbc.BasicColumn;
import org.batoo.jpa.jdbc.dbutils.QueryRunner;
import org.batoo.jpa.jdbc.mapping.Mapping;
import org.batoo.jpa.sql.SqlLexer;
import org.batoo.jpa.sql.SqlParser;
import org.batoo.jpa.sql.SqlParser.statements_return;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * The implementation of the native query interface.
 * 
 * @author hceylan
 * @since 2.0.0
 */
public class NativeQuery implements Query, ResultSetHandler<List<Object>> {

	private static final BLogger LOG = BLoggerFactory.getLogger(NativeQuery.class);

	private final EntityManagerImpl em;
	private final String query;
	private final Class<?> resultClass;

	private FlushModeType flushMode;
	private int maxResults;
	private int firstResult;
	private EntityTypeImpl<?> entity;

	private final HashMap<String, NativeParameter<?>> parameters = Maps.newHashMap();

	private final Map<String, Object> hints = Maps.newHashMap();
	private final HashMap<NativeParameter<?>, Object> parameterValues = Maps.newHashMap();

	private List<?> results;

	/**
	 * @param entityManager
	 *            the entity manager
	 * @param query
	 *            the native SQL query
	 * 
	 * @since 2.0.0
	 */
	public NativeQuery(EntityManagerImpl entityManager, String query) {
		super();

		this.em = entityManager;
		this.query = this.parseParameters(query);
		this.resultClass = null;
	}

	/**
	 * @param entityManager
	 *            the entity manager
	 * @param sqlString
	 *            the native SQL query
	 * @param resultClass
	 *            the result class
	 * 
	 * @since 2.0.0
	 */
	public NativeQuery(EntityManagerImpl entityManager, String sqlString, Class<?> resultClass) {
		super();

		this.em = entityManager;
		this.query = this.parseParameters(sqlString);
		this.resultClass = resultClass;
	}

	private Object convertTemporal(TemporalType temporalType, Calendar value) {
		switch (temporalType) {
			case DATE:
				return new java.sql.Date(value.getTimeInMillis());
			case TIME:
				return new java.sql.Time(value.getTimeInMillis());
			default:
				return new java.sql.Timestamp(value.getTimeInMillis());
		}
	}

	private Object convertTemporal(TemporalType temporalType, Date value) {
		switch (temporalType) {
			case DATE:
				return new java.sql.Date(value.getTime());
			case TIME:
				return new java.sql.Time(value.getTime());
			default:
				return new java.sql.Timestamp(value.getTime());
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public int executeUpdate() {
		this.em.assertTransaction();

		// flush if specified
		if ((this.flushMode == FlushModeType.AUTO) || (this.em.getFlushMode() == FlushModeType.AUTO)) {
			this.em.flush();
		}

		try {
			if (!this.parameters.isEmpty()) {
				final Object[] parameters = new Object[this.parameters.size()];

				for (int i = 0; i < this.parameters.size(); i++) {
					parameters[i] = this.getParameterValue(i);
				}

				return new QueryRunner(this.em.getJdbcAdaptor().isPmdBroken(), false).update(this.query, parameters);
			}

			return new QueryRunner(this.em.getJdbcAdaptor().isPmdBroken(), false).update(this.query);

		}
		catch (final SQLException e) {
			throw new PersistenceException("Native query execution has failed!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public int getFirstResult() {
		return this.firstResult;
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
		return Collections.unmodifiableMap(this.hints);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public LockModeType getLockMode() {
		throw new UnsupportedOperationException("Locking is not supported in native queries");
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public int getMaxResults() {
		return this.maxResults;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public NativeParameter<?> getParameter(int position) {
		return this.parameters.get(Integer.toString(position));
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <T> Parameter<T> getParameter(int position, Class<T> type) {
		throw new NotImplementedException("Native queries do not define parameter types. Use getParameter(int)");
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public NativeParameter<?> getParameter(String name) {
		return this.parameters.get(name);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <T> Parameter<T> getParameter(String name, Class<T> type) {
		throw new NotImplementedException("Native queries do not define parameter types. Use getParameter(String)");
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Set<Parameter<?>> getParameters() {
		final Set<Parameter<?>> parameters = Sets.newHashSet();

		for (final NativeParameter<?> parameter : this.parameters.values()) {
			parameters.add(parameter);
		}

		return parameters;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Object getParameterValue(int position) {
		return this.parameterValues.get(this.getParameter(position));
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <T> T getParameterValue(Parameter<T> param) {
		return (T) this.parameterValues.get(param);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Object getParameterValue(String name) {
		return this.parameterValues.get(this.parameters.get(name));
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public List<?> getResultList() {
		if (this.results != null) {
			return this.results;
		}

		return this.getResultListImpl();
	}

	private List<?> getResultListImpl() {
		this.em.getSession().setLoadTracker();

		try {
			if (this.resultClass != null) {
				this.entity = this.em.getMetamodel().entity(this.resultClass);
			}

			final Object[] paramValues = new Object[this.parameters.size()];
			for (int i = 0; i < paramValues.length; i++) {
				paramValues[i] = this.getParameterValue(i + 1);
			}

			try {
				return this.results = new QueryRunner(this.em.getJdbcAdaptor().isPmdBroken(), false).query(this.em.getConnection(), this.query, this,
					paramValues);
			}
			catch (final SQLException e) {
				throw new PersistenceException("Native query execution failed!", e);
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
	public Object getSingleResult() {
		final List<?> resultList = this.getResultList();

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
	public List<Object> handle(ResultSet resultSet) throws SQLException {
		// designated return type
		if (this.resultClass != null) {
			if (this.entity != null) {
				return this.handleAsEntityList(resultSet);
			}

			// DTO types
			return this.handleAsResultClass(resultSet);
		}

		// handle as scalar or scalar array

		final ArrayList<Object> results = Lists.newArrayList();

		final int columnCount = resultSet.getMetaData().getColumnCount();
		while (resultSet.next()) {
			// single scalar
			if (columnCount == 1) {
				results.add(resultSet.getObject(1));
				continue;
			}

			// array of scalars
			final Object[] result = new Object[columnCount];
			for (int i = 0; i < columnCount; i++) {
				result[i] = resultSet.getObject(i + 1);
			}

			results.add(result);
		}

		return results;
	}

	private List<Object> handleAsEntityList(ResultSet resultSet) throws SQLException {
		final ArrayList<Object> results = Lists.newArrayList();

		while (resultSet.next()) {
			final SessionImpl session = this.em.getSession();

			final ManagedId<?> id = this.entity.getId(session, resultSet);
			if (id == null) {
				continue;
			}

			// look for it in the session
			final ManagedInstance<?> instance = session.get(id);

			// if found then return it
			if (instance != null) {
				// if it is a new instance simply return it
				if (!(instance.getInstance() instanceof EnhancedInstance)) {
					results.add(instance.getInstance());
					continue;
				}

				final EnhancedInstance enhancedInstance = (EnhancedInstance) instance.getInstance();

				// if it is a lazy instance mark as loading and initialize
				if (!enhancedInstance.__enhanced__$$__isInitialized()) {
					this.initializeInstance(session, resultSet, instance);

					session.lazyInstanceLoading(instance);
					enhancedInstance.__enhanced__$$__setInitialized();
				}

				results.add(instance.getInstance());
			}
		}

		return results;
	}

	private List<Object> handleAsResultClass(ResultSet resultSet) {
		// TODO Auto-generated method stub
		return null;
	}

	private void initializeInstance(SessionImpl session, ResultSet row, ManagedInstance<?> managedInstance) throws SQLException {
		managedInstance.setLoading(true);

		final Object instance = managedInstance.getInstance();

		for (final AbstractMapping<?, ?, ?> mapping : this.entity.getMappingsSingular()) {
			this.initializeMapping(session, instance, row, mapping);
		}
	}

	private void initializeMapping(SessionImpl session, Object instance, ResultSet row, Mapping<?, ?, ?> mapping) throws SQLException {
		if (mapping instanceof BasicMappingImpl) {
			final BasicMappingImpl<?, ?> basicMapping = (BasicMappingImpl<?, ?>) mapping;
			final BasicColumn column = basicMapping.getColumn();
			column.setValue(instance, row.getObject(column.getName()));
		}
		else if (mapping instanceof EmbeddedMappingImpl) {
			for (final Mapping<?, ?, ?> childMapping : ((EmbeddedMappingImpl<?, ?>) mapping).getChildren()) {
				this.initializeMapping(session, instance, row, childMapping);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean isBound(Parameter<?> param) {
		return this.getParameterValue(param) != null;
	}

	private CommonTree parse(String query) {
		try {
			final SqlLexer lexer = new SqlLexer(new ANTLRStringStream(query));
			final CommonTokenStream tokenStream = new CommonTokenStream(lexer);
			final SqlParser parser = new SqlParser(tokenStream);

			final statements_return statements = parser.statements();
			final CommonTree tree = (CommonTree) statements.getTree();

			final List<String> errors = parser.getErrors();
			if (errors.size() > 0) {
				final String errorMsg = Joiner.on("\n\t").join(errors);

				NativeQuery.LOG.error("Cannot parse query: {0}", //
					NativeQuery.LOG.boxed(query, //
						new Object[] { "\n\t" + errorMsg, "\n\n" + tree.toStringTree() + "\n" }));

				throw new PersistenceException("Cannot parse the query:\n " + errorMsg + ".\n" + query);
			}

			return tree;
		}
		catch (final PersistenceException e) {
			throw e;
		}
		catch (final Exception e) {
			throw new PersistenceException("Cannot parse the query:\n " + e.getMessage() + ".\n" + query, e);
		}
	}

	/**
	 * Parses the parameters.
	 * 
	 * @param query
	 *            the original query
	 * 
	 * @since 2.0.0
	 */
	private String parseParameters(String query) {
		final CommonTree tree = this.parse(query);

		boolean hasNamed = false;
		boolean hasOrdinal = false;
		boolean hasAnonymous = false;
		int maxOrdinal = 0;

		final boolean supportsOrdinalParams = this.em.getJdbcAdaptor().supportsOrdinalParams();
		final boolean supportsNamedParams = this.em.getJdbcAdaptor().supportsNamedParams();

		final StringBuilder sqlBuilder = new StringBuilder();

		if (tree.getChildCount() > 1) {
			throw new PersistenceException("Multi-statement native queries not supported");
		}

		if (tree.getChildCount() == 0) {
			throw new PersistenceException("Null query passed");
		}

		final Tree statementTree = tree.getChild(0);

		for (int i = 0; i < statementTree.getChildCount(); i++) {
			final Tree chunk = statementTree.getChild(i);

			if (chunk.getType() == SqlParser.COLUMN) {
				hasNamed = true;

				final String name = chunk.getChild(0).getText();

				NativeParameter<?> parameter = this.parameters.get(name);

				if (parameter == null) {
					parameter = new NativeParameter<Object>(name, maxOrdinal++);
					this.parameters.put(name, parameter);
				}

				if (!supportsOrdinalParams) {
					sqlBuilder.append(":p" + parameter.getPosition());
				}
				else if (!supportsNamedParams) {
					sqlBuilder.append("?" + parameter.getPosition());
				}
				else {
					sqlBuilder.append(":" + parameter.getPosition());
				}
			}
			else if (chunk.getType() == SqlParser.QUESTION_MARK) {
				// anonymous?
				if (chunk.getChildCount() == 0) {
					hasAnonymous = true;

					final NativeParameter<?> parameter = new NativeParameter<Object>(maxOrdinal++);
					final String name = Integer.toString(maxOrdinal);
					this.parameters.put(name, parameter);
				}
				else {
					hasOrdinal = true;

					final int ordinalNo = Integer.parseInt(chunk.getChild(0).getText());
					if (ordinalNo <= 0) {
						throw new PersistenceException("Invalid ordinal number for parameter: " + ordinalNo + " in query: " + this.query);
					}

					final String name = Integer.toString(ordinalNo);

					NativeParameter<?> parameter = this.parameters.get(name);

					if (parameter == null) {
						parameter = new NativeParameter<Object>(ordinalNo);
						this.parameters.put(name, parameter);
					}

					maxOrdinal = Math.max(maxOrdinal, ordinalNo);

					if (!supportsOrdinalParams) {
						sqlBuilder.append(":p" + parameter.getPosition());
					}
					else if (!supportsNamedParams) {
						sqlBuilder.append("?" + parameter.getPosition());
					}
					else {
						sqlBuilder.append(":" + parameter.getPosition());
					}
				}
			}
			else {
				sqlBuilder.append(chunk.getText());
			}
		}

		int types = 0;
		if (hasAnonymous) {
			types++;
		}
		if (hasNamed) {
			types++;
		}
		if (hasOrdinal) {
			types++;
		}

		if (types > 1) {
			throw new PersistenceException("Mix of ordinal, named and anonymous parameters are not allowed in SQL queries: " + this.query);
		}

		if (this.parameters.size() != maxOrdinal) {
			throw new PersistenceException("Query defines ordinal parameter " + maxOrdinal + " but does not define " + maxOrdinal + " parameters");
		}

		return sqlBuilder.toString();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public NativeQuery setFirstResult(int startPosition) {
		this.firstResult = startPosition;

		return this;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public NativeQuery setFlushMode(FlushModeType flushMode) {
		this.flushMode = flushMode;

		return this;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public NativeQuery setHint(String hintName, Object value) {
		this.hints.put(hintName, value);

		return this;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public NativeQuery setLockMode(LockModeType lockMode) {
		throw new UnsupportedOperationException("Locking is not supported in native queries");
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public NativeQuery setMaxResults(int maxResults) {
		this.maxResults = maxResults;

		return this;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public NativeQuery setParameter(int position, Calendar value, TemporalType temporalType) {
		this.parameterValues.put(this.getParameter(position), this.convertTemporal(temporalType, value));

		return this;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public NativeQuery setParameter(int position, Date value, TemporalType temporalType) {
		this.parameterValues.put(this.getParameter(position), this.convertTemporal(temporalType, value));

		return this;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public NativeQuery setParameter(int position, Object value) {
		this.parameterValues.put(this.getParameter(position), value);

		return this;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public NativeQuery setParameter(Parameter<Calendar> param, Calendar value, TemporalType temporalType) {
		this.parameterValues.put((NativeParameter<?>) param, this.convertTemporal(temporalType, value));

		return this;

	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public NativeQuery setParameter(Parameter<Date> param, Date value, TemporalType temporalType) {
		this.parameterValues.put((NativeParameter<?>) param, this.convertTemporal(temporalType, value));

		return this;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <T> NativeQuery setParameter(Parameter<T> param, T value) {
		this.parameterValues.put((NativeParameter<?>) param, value);

		return this;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public NativeQuery setParameter(String name, Calendar value, TemporalType temporalType) {
		this.parameterValues.put(this.getParameter(name), this.convertTemporal(temporalType, value));

		return this;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public NativeQuery setParameter(String name, Date value, TemporalType temporalType) {
		this.parameterValues.put(this.getParameter(name), this.convertTemporal(temporalType, value));

		return this;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public NativeQuery setParameter(String name, Object value) {
		this.parameterValues.put(this.getParameter(name), value);

		return this;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <T> T unwrap(Class<T> cls) {
		return null;
	}
}
