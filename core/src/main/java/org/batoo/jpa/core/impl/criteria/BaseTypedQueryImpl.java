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
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.lang.StringUtils;
import org.batoo.jpa.common.log.BLogger;
import org.batoo.jpa.common.log.BLoggerFactory;
import org.batoo.jpa.core.impl.instance.ManagedInstance;
import org.batoo.jpa.core.impl.manager.EntityManagerImpl;
import org.batoo.jpa.core.impl.metamodel.MetamodelImpl;

import com.google.common.base.Function;
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
public abstract class BaseTypedQueryImpl<X> implements TypedQuery<X>, ResultSetHandler<List<ManagedInstance<X>>> {

	private static final int MAX_COL_LENGTH = 30;

	private static final BLogger LOG = BLoggerFactory.getLogger(BaseTypedQueryImpl.class);

	protected final CriteriaQueryImpl<X> cq;
	private final EntityManagerImpl em;
	private final MetamodelImpl metamodel;
	private final Class<X> resultType;
	private final SelectionImpl<X> selection;

	protected final Map<ParameterExpressionImpl<?>, Object> parameters = Maps.newHashMap();
	private final List<ManagedInstance<X>> results = Lists.newArrayList();

	private String[] labels;
	private List<Object[]> data;

	/**
	 * @param criteriaQuery
	 *            the criteria query
	 * @param entityManager
	 *            the entity manager
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public BaseTypedQueryImpl(CriteriaQueryImpl<X> criteriaQuery, EntityManagerImpl entityManager) {
		super();

		this.cq = criteriaQuery;
		this.em = entityManager;
		this.metamodel = criteriaQuery.getMetamodel();
		this.resultType = criteriaQuery.getResultType();
		this.selection = this.cq.getSelection();
	}

	private void dumpResultSet() {
		final int[] lengths = new int[this.labels.length];
		for (int i = 0; i < lengths.length; i++) {
			lengths[i] = this.max(lengths[i], StringUtils.length(this.labels[i]));
		}

		for (final Object[] data : this.data) {
			for (int i = 0; i < data.length; i++) {
				if (data[i] != null) {
					lengths[i] = this.max(lengths[i], StringUtils.length(data[i].toString()));
				}
			}
		}

		int length = 3;
		for (final int length2 : lengths) {
			length += length2 + 2;
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

		for (final Object[] data : this.data) {
			dump.append("\n| ");

			for (int i = 0; i < data.length; i++) {
				String strValue = data[i] != null ? data[i].toString() : "!NULL!";
				strValue = StringUtils.abbreviate(strValue, lengths[i]);
				if (data[i] instanceof Number) {
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

		BaseTypedQueryImpl.LOG.debug(dump.toString());
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public List<X> getResultList() {
		final String sql = this.cq.getSql();

		final Object[] parameters = new Object[this.parameters.size()];
		for (final Entry<ParameterExpressionImpl<?>, Object> e : this.parameters.entrySet()) {
			parameters[e.getKey().getPosition() - 1] = e.getValue();
		}

		try {
			final List<ManagedInstance<X>> result = new QueryRunner().query(this.em.getConnection(), sql, this, parameters);

			return Lists.transform(result, new Function<ManagedInstance<X>, X>() {

				@Override
				public X apply(ManagedInstance<X> input) {
					return input.getInstance();
				}
			});
		}
		catch (final SQLException e) {
			throw new PersistenceException("Query failed", e);
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
			throw new PersistenceException("Query result returned multiple results");
		}

		return resultList.get(0);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public List<ManagedInstance<X>> handle(ResultSet rs) throws SQLException {
		final ResultSetMetaData md = rs.getMetaData();

		// prepare the labels
		this.prepareLabels(md);

		this.data = Lists.newArrayList();

		while (rs.next()) {
			// store the data
			this.storeData(rs, md);

			// process the row
			this.results.add(this.handleRow(rs));
		}

		// report the result resultset
		this.dumpResultSet();

		return this.results;
	}

	/**
	 * Handles a single row from the result set.
	 * 
	 * @param rs
	 *            the resultset to handle
	 * @return the generated result instance
	 * @throws SQLException
	 *             thrown in case of an SQL error
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private ManagedInstance<X> handleRow(ResultSet rs) throws SQLException {
		return this.selection.handleRow(this.em, rs);
	}

	private int max(int length1, int length2) {
		return Math.min(BaseTypedQueryImpl.MAX_COL_LENGTH, Math.max(length1, length2));
	}

	private void prepareLabels(final ResultSetMetaData md) throws SQLException {
		this.labels = new String[md.getColumnCount()];
		for (int i = 0; i < this.labels.length; i++) {
			String label = md.getColumnName(i + 1) + " (" + md.getColumnTypeName(i + 1) + ")";
			label = StringUtils.abbreviate(label, BaseTypedQueryImpl.MAX_COL_LENGTH);

			this.labels[i] = label;
		}
	}

	private void storeData(ResultSet rs, final ResultSetMetaData md) throws SQLException {
		final Object[] data = new Object[md.getColumnCount()];
		for (int i = 0; i < this.labels.length; i++) {
			final Object value = rs.getObject(i + 1);
			if (value == null) {
				data[i] = "(null)";
			}
			else {
				data[i] = StringUtils.abbreviate(value.toString(), BaseTypedQueryImpl.MAX_COL_LENGTH);
			}
		}
		this.data.add(data);
	}
}
