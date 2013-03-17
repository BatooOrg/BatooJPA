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
package org.batoo.jpa.core.impl.nativequery;

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
import org.batoo.jpa.core.impl.model.mapping.SingularAssociationMappingImpl;
import org.batoo.jpa.jdbc.AbstractColumn;
import org.batoo.jpa.jdbc.BasicColumn;
import org.batoo.jpa.jdbc.JoinColumn;
import org.batoo.jpa.jdbc.dbutils.QueryRunner;
import org.batoo.jpa.parser.metadata.ColumnResultMetadata;
import org.batoo.jpa.parser.metadata.EntityResultMetadata;
import org.batoo.jpa.parser.metadata.FieldResultMetadata;
import org.batoo.jpa.parser.metadata.SqlResultSetMappingMetadata;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * The implementation of the native query interface.
 * 
 * @author hceylan
 * @author asimarslan
 * @since 2.0.0
 */
public class NativeQuery implements Query, ResultSetHandler<List<Object>> {

	/**
	 * Model class for fieldResult mapping for high performance iteration on SQL result sets
	 * 
	 * @author asimarslan
	 * @since 2.0.1
	 */
	private static class IdModel {

		public static final String DEFAULT_EMBEDDED_ID = "__pk__";
		public static final String DEFAULT_ID = "__id__";

		public static IdModel merge(IdModel idModel, String embeddedId, String id, String column) {
			idModel = idModel != null ? idModel : new IdModel();
			idModel.merge(embeddedId, id, column);
			return idModel;
		}

		HashMap<String, Object> idMap = Maps.newHashMap();

		String embeddedId;

		private IdModel() {
			super();
		}

		@SuppressWarnings("unused")
		public String getEmbeddedId() {
			return this.embeddedId;
		}

		public HashMap<String, Object> getIdMap() {
			return this.idMap;
		}

		private void merge(String embeddedId, String id, String column) {
			this.embeddedId = embeddedId;
			this.idMap.put(id, column);
		}

		@Override
		public String toString() {
			if (this.idMap.size() == 1) {
				return this.idMap.values().iterator().next().toString();
			}
			return null;
		}

	}

	private static final BLogger LOG = BLoggerFactory.getLogger(NativeQuery.class);

	private final EntityManagerImpl em;
	private final String query;
	private final Class<?> resultClass;

	private FlushModeType flushMode;
	private int maxResults;
	private int firstResult;

	private final HashMap<Integer, NativeParameter<?>> parameters = Maps.newHashMap();
	private final HashMap<NativeParameter<?>, Object> parameterValues = Maps.newHashMap();

	private final Map<String, Object> hints = Maps.newHashMap();

	private List<?> results;

	final SqlResultSetMappingMetadata sqlResultSetMapping;

	final ArrayList<String> entityList = Lists.newArrayList();

	// Map of entity-name, map of fied-name, column-name
	private final HashMap<Integer, HashMap<String, Object>> fieldMap = Maps.newHashMap();

	/**
	 * @param entityManager
	 *            the entity manager
	 * @param sqlString
	 *            the native SQL query
	 * 
	 * @since 2.0.0
	 */
	public NativeQuery(EntityManagerImpl entityManager, String sqlString) {
		super();

		this.em = entityManager;
		this.query = sqlString;
		this.resultClass = null;
		this.sqlResultSetMapping = null;
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
		this.query = sqlString;
		this.resultClass = resultClass;
		this.sqlResultSetMapping = null;
	}

	/**
	 * @param entityManager
	 *            the entity manager
	 * @param sqlString
	 *            the native SQL query
	 * @param resultSetMapping
	 *            the resultSetMapping
	 * 
	 * @since 2.0.0
	 */
	public NativeQuery(EntityManagerImpl entityManager, String sqlString, String resultSetMapping) {
		super();

		this.em = entityManager;
		this.query = sqlString;
		this.resultClass = null;

		this.sqlResultSetMapping = this.em.getMetamodel().getSqlResultSetMapping(resultSetMapping);
		if (this.sqlResultSetMapping == null) {
			throw new PersistenceException("SqlResultSetMapping does not exist! : " + resultSetMapping);
		}
		else {
			for (final EntityResultMetadata entityResultMetadata : this.sqlResultSetMapping.getEntities()) {
				final HashMap<String, Object> _fieldModelMap = Maps.newHashMap();

				for (final FieldResultMetadata field : entityResultMetadata.getFields()) {
					final String[] split;
					if (field.getName().contains(".")) {
						split = field.getName().split("\\.");
					}
					else {
						split = new String[] { field.getName(), IdModel.DEFAULT_EMBEDDED_ID, IdModel.DEFAULT_ID };
					}

					final String attr = split[0];
					final String embId = split.length > 2 ? split[split.length - 2] : IdModel.DEFAULT_EMBEDDED_ID;
					final String id = split.length > 1 ? split[split.length - 1] : IdModel.DEFAULT_ID;

					_fieldModelMap.put(attr, IdModel.merge((IdModel) _fieldModelMap.get(attr), embId, id, field.getColumn()));

				}
				this.entityList.add(entityResultMetadata.getEntityClass());
				this.fieldMap.put(this.entityList.size() - 1, _fieldModelMap);
			}

		}

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

				return new QueryRunner(this.em.getJdbcAdaptor(), false).update(this.query, parameters);
			}

			return new QueryRunner(this.em.getJdbcAdaptor(), false).update(this.query);

		}
		catch (final SQLException e) {
			throw new PersistenceException("Native query execution has failed!", e);
		}
	}

	/**
	 * 
	 * @param session
	 * @param row
	 * @param mapping
	 * @param session
	 * @param row
	 * @return
	 * @throws SQLException
	 * @since 2.0.1
	 */
	private HashMap<AbstractColumn, String> getAssociatedId(SingularAssociationMappingImpl<?, ?> mapping, HashMap<String, Object> _parentFieldMap)
		throws SQLException {
		final HashMap<AbstractColumn, String> translatedIdFields = Maps.newHashMap();

		for (final JoinColumn joinColumn : mapping.getForeignKey().getJoinColumns()) {
			final String name = joinColumn.getReferencedColumn().getMapping().getName();
			final Object colnameTemp = (_parentFieldMap != null) ? _parentFieldMap.get(name) : null;
			final String colname = colnameTemp == null ? joinColumn.getName() : colnameTemp.toString();

			translatedIdFields.put(joinColumn.getReferencedColumn(), colname);
		}
		return translatedIdFields;
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
	 * Transforms the column names for idField Map using a field,column-name map
	 * 
	 * @return
	 * @param idFields
	 * @param _fieldMap
	 * @since 2.0.1
	 */
	private HashMap<AbstractColumn, String> getIdFieldTransformed(HashMap<AbstractColumn, String> idFields, HashMap<String, Object> fieldIdMap) {
		if (fieldIdMap == null) {
			return idFields;
		}
		final HashMap<AbstractColumn, String> idFieldsMod = Maps.newHashMap();
		final BiMap<String, AbstractColumn> inverse = HashBiMap.create(idFields).inverse();
		for (final String field : idFields.values()) {
			final AbstractColumn column = inverse.get(field);
			final String _field = column.getMapping().getName();

			final Object colmVal = fieldIdMap.get(_field);

			if (colmVal != null && colmVal.toString() != null) {
				idFieldsMod.put(column, colmVal.toString());
			}
			else {
				idFieldsMod.put(column, _field);
			}

		}
		return idFieldsMod;
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
		return this.parameters.get(position);
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
		// JSR-317 3.8.15 >> The use of named parameters is not defined for native queries.
		throw new NotImplementedException("Native queries do not support named parameters.");
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <T> Parameter<T> getParameter(String name, Class<T> type) {
		// JSR-317 3.8.15 >> The use of named parameters is not defined for native queries.
		throw new NotImplementedException("Native queries do not support named parameters.");
	}

	@SuppressWarnings("rawtypes")
	private NativeParameter<?> getParameter0(int position) {
		if (this.getParameter(position) == null) {
			this.parameters.put(position, new NativeParameter(position));
		}
		return this.parameters.get(position);
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
		// JSR-317 3.8.15 >> The use of named parameters is not defined for native queries.
		throw new NotImplementedException("Native queries do not support named parameters.");
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

			// max of parameter index
			int max = 1;
			for (final int i : this.parameters.keySet()) {
				max = i > max ? i : max;
			}
			// // init with void
			final Object[] paramValues = new Object[max];
			for (int i = 0; i < paramValues.length; i++) {
				paramValues[i] = Void.TYPE;
			}

			// fill with real values
			for (int i = 0; i < paramValues.length; i++) {
				if (this.getParameter(i + 1) != null) {
					paramValues[i] = this.getParameterValue(i + 1);
				}
			}

			try {
				return this.results = new QueryRunner(this.em.getJdbcAdaptor(), false).query(this.em.getConnection(), this.query, this, paramValues);
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
		if (this.sqlResultSetMapping != null) {
			return this.handleWithSqlResultSetMapping(resultSet);
		}
		if (this.resultClass != null) {// designated return type
			return this.handleWithResultClass(resultSet);
		}
		// last option return query as scalar
		return handleAsScalar(resultSet);
	}

	private List<Object> handleAsScalar(ResultSet resultSet) throws SQLException {
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

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private ManagedInstance<?> handleInstance(ResultSet row, EntityTypeImpl<?> entityType, String discriminatorColumn, HashMap<String, Object> fieldMap)
		throws SQLException {
		final SessionImpl session = this.em.getSession();

		// get the id of for the instance
		final ManagedId<?> managedId = entityType.getId(session, row, this.getIdFieldTransformed(entityType.getPrimaryTable().getIdFields(), fieldMap));
		if (managedId == null) {
			return null;
		}

		// look for it in the session
		ManagedInstance<?> instance = session.get(managedId);
		// if found then return it
		if (instance != null) {
			// if it is a new instance simply return it
			if (!(instance.getInstance() instanceof EnhancedInstance)) {
				return instance;
			}

			final EnhancedInstance enhancedInstance = (EnhancedInstance) instance.getInstance();

			// if it is a lazy instance mark as loading and initialize
			if (!enhancedInstance.__enhanced__$$__isInitialized()) {
				this.initializeInstance(session, row, instance, entityType, fieldMap);

				session.lazyInstanceLoading(instance);
				enhancedInstance.__enhanced__$$__setInitialized();
			}
			return instance;
		}

		// if no inheritance then initialize and return
		if (entityType.getInheritanceType() == null) {
			instance = entityType.getManagedInstanceById(session, (ManagedId) managedId, false);
		}
		// inheritance is in place then locate the correct child type
		else {
			discriminatorColumn = (discriminatorColumn == null) ? entityType.getDiscriminatorColumn().getName() : discriminatorColumn;

			final String discriminatorValue = row.getObject(discriminatorColumn).toString();

			// check if we have a legal discriminator value
			final EntityTypeImpl<?> effectiveType = entityType.getChildType(discriminatorValue);
			if (effectiveType == null) {
				throw new IllegalArgumentException("Discriminator " + discriminatorValue + " not found in the type " + entityType.getName());
			}

			// initialize and return
			instance = effectiveType.getManagedInstanceById(session, (ManagedId) managedId, false);
		}

		this.initializeInstance(session, row, instance, entityType, fieldMap);
		session.put(instance);

		return instance;
	}

	/**
	 * result set handler for a given resultClass
	 * 
	 * @return result
	 * @param resultSet
	 * @throws SQLException
	 * @since 2.0.1
	 */
	private List<Object> handleWithResultClass(ResultSet resultSet) throws SQLException {
		final ArrayList<Object> result = Lists.newArrayList();

		final EntityTypeImpl<?> entityType = this.em.getMetamodel().entity(this.resultClass);
		if (entityType == null) {
			throw new PersistenceException("Entity Class is not managed :" + this.resultClass);
		}

		while (resultSet.next()) {// for each row
			final ManagedInstance<?> managedInstance = this.handleInstance(resultSet, entityType, null, null);

			if (managedInstance != null) {
				result.add(managedInstance.getInstance());
			}
			else {
				result.add(null);
			}
		}
		return result;
	}

	/**
	 * result set handler for SqlResultSetMapping annotation data
	 * 
	 * @return result
	 * @param resultSet
	 * @throws SQLException
	 * @since 2.0.1
	 */
	private List<Object> handleWithSqlResultSetMapping(ResultSet resultSet) throws SQLException {
		final ArrayList<Object> result = Lists.newArrayList();
		final List<EntityResultMetadata> entities = this.sqlResultSetMapping.getEntities();

		while (resultSet.next()) {// for each row
			final ArrayList<Object> resultRow = Lists.newArrayList();
			for (int i = 0; i < entities.size(); i++) {
				final EntityResultMetadata entityResultMetadata = entities.get(i);
				final EntityTypeImpl<?> entityType = this.em.getMetamodel().entity(entityResultMetadata.getEntityClass());
				if (entityType == null) {
					throw new PersistenceException("Entity Class is not managed :" + entityResultMetadata.getEntityClass());
				}

				final HashMap<String, Object> _fieldMap = this.fieldMap.get(i);
				final ManagedInstance<?> managedInstance = this.handleInstance(resultSet, entityType, entityResultMetadata.getDiscriminatorColumn(), _fieldMap);
				if (managedInstance != null) {
					resultRow.add(managedInstance.getInstance());
				}
				else {
					resultRow.add(null);
				}
			}
			for (final ColumnResultMetadata columnResultMetadata : this.sqlResultSetMapping.getColumns()) {
				resultRow.add(resultSet.getObject(columnResultMetadata.getName()));
			}
			if (resultRow.size() > 1) {
				result.add(resultRow.toArray());
			}
			else {
				result.add(resultRow.get(0));
			}
		}
		return result;
	}

	/**
	 * initialize the managedInstance with sql row data and fieldResult Mapping
	 * 
	 * @param session
	 * @param row
	 *            Sql data row
	 * @param managedInstance
	 * @param entityType
	 * @param fieldMap
	 *            fieldResult Mapping data
	 * @throws SQLException
	 * @since 2.0.1
	 */
	private void initializeInstance(SessionImpl session, ResultSet row, ManagedInstance<?> managedInstance, EntityTypeImpl<?> entityType,
		HashMap<String, Object> fieldMap) throws SQLException {
		managedInstance.setLoading(true);

		final Object instance = managedInstance.getInstance();
		// initialize all singular mappings
		for (final AbstractMapping<?, ?, ?> mapping : entityType.getMappingsSingular()) {

			if (mapping instanceof BasicMappingImpl) {
				final BasicMappingImpl<?, ?> basicMapping = (BasicMappingImpl<?, ?>) mapping;
				final BasicColumn column = basicMapping.getColumn();

				final String colName = (fieldMap != null && fieldMap.get(basicMapping.getName()) != null) ? //
					fieldMap.get(basicMapping.getName()).toString() : column.getName();

				column.setValue(instance, row.getObject(colName));
			}
			if (mapping instanceof SingularAssociationMappingImpl) {
				final SingularAssociationMappingImpl<?, ?> saMapping = (SingularAssociationMappingImpl<?, ?>) mapping;

				HashMap<String, Object> _parentFieldMap = fieldMap;
				if (fieldMap != null && fieldMap.get(saMapping.getName()) instanceof IdModel) {
					final IdModel idModel = (IdModel) fieldMap.get(saMapping.getName());
					if (idModel.getIdMap().size() > 1) {
						_parentFieldMap = idModel.getIdMap();
					}
				}

				// loop on join column and set their value using the result set, but we are mapping correct column name from fieldResult
				for (final JoinColumn joinColumn : saMapping.getForeignKey().getJoinColumns()) {
					final String name = joinColumn.getReferencedColumn().getMapping().getName();
					final Object colnameTemp = (_parentFieldMap != null) ? _parentFieldMap.get(name) : null;
					final String colname = colnameTemp == null ? joinColumn.getName() : colnameTemp.toString();

					try {
						final Object _id = row.getObject(colname);
						if (_id != null) {
							joinColumn.setValue(instance, _id);
						}
					}
					catch (final SQLException e) {
						LOG.debug("column not found with name: {0}", colname);
					}
				}

				final EntityTypeImpl<?> _childType = saMapping.getType();
				final HashMap<AbstractColumn, String> translatedIdFields = getAssociatedId(saMapping, _parentFieldMap);

				final ManagedId<?> managedId = saMapping.getType().getId(session, row, translatedIdFields);
				if (managedId != null && managedId.getId() != null) {
					final Object reference = session.getEntityManager().getReference(_childType.getJavaType(), managedId.getId());
					if (reference != null) {
						saMapping.set(instance, reference);
						managedInstance.setJoinLoaded(saMapping);
					}
				}
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
		this.parameterValues.put(this.getParameter0(position), this.convertTemporal(temporalType, value));

		return this;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public NativeQuery setParameter(int position, Date value, TemporalType temporalType) {
		this.parameterValues.put(this.getParameter0(position), this.convertTemporal(temporalType, value));

		return this;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public NativeQuery setParameter(int position, Object value) {
		final NativeParameter<?> parameter0 = this.getParameter0(position);
		this.parameterValues.put(parameter0, value);

		return this;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public NativeQuery setParameter(Parameter<Calendar> param, Calendar value, TemporalType temporalType) {
		this.parameterValues.put(this.getParameter0(param.getPosition()), this.convertTemporal(temporalType, value));

		return this;

	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public NativeQuery setParameter(Parameter<Date> param, Date value, TemporalType temporalType) {
		this.parameterValues.put(this.getParameter0(param.getPosition()), this.convertTemporal(temporalType, value));

		return this;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <T> NativeQuery setParameter(Parameter<T> param, T value) {
		this.parameterValues.put(this.getParameter0(param.getPosition()), value);

		return this;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public NativeQuery setParameter(String name, Calendar value, TemporalType temporalType) {
		// JSR-317 3.8.15 >> The use of named parameters is not defined for native queries.
		throw new NotImplementedException("Native queries do not support named parameters.");
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public NativeQuery setParameter(String name, Date value, TemporalType temporalType) {
		// JSR-317 3.8.15 >> The use of named parameters is not defined for native queries.
		throw new NotImplementedException("Native queries do not support named parameters.");
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public NativeQuery setParameter(String name, Object value) {
		// JSR-317 3.8.15 >> The use of named parameters is not defined for native queries.
		throw new NotImplementedException("Native queries do not support named parameters.");
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
