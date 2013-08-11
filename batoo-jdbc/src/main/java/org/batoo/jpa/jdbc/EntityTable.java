/*
 * Copyright (c) 2012-2013, Batu Alp Ceylan
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
package org.batoo.jpa.jdbc;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.batoo.common.util.FinalWrapper;
import org.batoo.jpa.jdbc.adapter.JdbcAdaptor;
import org.batoo.jpa.jdbc.dbutils.QueryRunner;
import org.batoo.jpa.jdbc.dbutils.SingleValueHandler;
import org.batoo.jpa.jdbc.model.EntityTypeDescriptor;
import org.batoo.jpa.parser.metadata.TableMetadata;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Collections2;
import com.google.common.collect.Maps;

/**
 * Table representing an entity persistent storage.
 * 
 * @author hceylan
 * @since 2.0.0
 */
public class EntityTable extends AbstractTable {

	private final EntityTypeDescriptor entity;
	private final Map<String, AbstractColumn> pkColumns = Maps.newHashMap();

	private final JdbcAdaptor jdbcAdaptor;
	private BasicColumn identityColumn;
	private final Map<String, BasicColumn[]> indexes = Maps.newHashMap();

	private final HashMap<Integer, String> removeSqlMap = Maps.newHashMap();
	private FinalWrapper<HashMap<AbstractColumn, String>> idColumns;

	/**
	 * @param jdbcAdaptor
	 *            the jdbc adaptor
	 * @param entity
	 *            the entity
	 * @param metadata
	 *            the table metadata
	 * 
	 * @since 2.0.0
	 */
	public EntityTable(JdbcAdaptor jdbcAdaptor, EntityTypeDescriptor entity, TableMetadata metadata) {
		super(entity.getName(), metadata);

		this.jdbcAdaptor = jdbcAdaptor;
		this.entity = entity;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void addColumn(AbstractColumn column) {
		super.addColumn(column);

		if (column.isPrimaryKey()) {
			this.pkColumns.put(column.getName(), column);

			if (column.getIdType() == IdType.IDENTITY) {
				this.identityColumn = (BasicColumn) column;
			}
		}
		else if (column instanceof JoinColumn) {
			final JoinColumn joinColumn = (JoinColumn) column;
			if (joinColumn.isPrimaryKey()) {
				this.pkColumns.put(column.getName(), joinColumn);
			}
		}
	}

	/**
	 * Adds the index to table.
	 * 
	 * @param name
	 *            the name of the index
	 * @param columns
	 *            the columns
	 * @return true if an index with the <code>name</code> already existed, false otherwise
	 * 
	 * @since 2.0.0
	 */
	public boolean addIndex(String name, BasicColumn... columns) {
		if (this.indexes.containsKey(name)) {
			return true;
		}

		this.indexes.put(name, columns);

		return false;
	}

	/**
	 * Returns the entity of the EntityTable.
	 * 
	 * @return the entity of the EntityTable
	 * 
	 * @since 2.0.0
	 */
	public EntityTypeDescriptor getEntity() {
		return this.entity;
	}

	/**
	 * Returns the id fields of the table.
	 * 
	 * @return the id fields of the table
	 * 
	 * @since 2.0.0
	 */
	public HashMap<AbstractColumn, String> getIdFields() {
		if (this.idColumns != null) {
			return this.idColumns.value;
		}

		synchronized (this) {
			if (this.idColumns != null) {
				return this.idColumns.value;
			}

			final HashMap<AbstractColumn, String> _idFields = Maps.newHashMap();
			for (final AbstractColumn column : this.pkColumns.values()) {
				_idFields.put(column, column.getName());
			}

			this.idColumns = new FinalWrapper<HashMap<AbstractColumn, String>>(_idFields);
		}

		return this.idColumns.value;
	}

	/**
	 * Returns the indexes of the table.
	 * 
	 * @return the indexes of the table
	 * 
	 * @since 2.0.0
	 */
	public Map<String, BasicColumn[]> getIndexes() {
		return this.indexes;
	}

	/**
	 * Returns the jdbcAdaptor of the EntityTable.
	 * 
	 * @return the jdbcAdaptor of the EntityTable
	 * 
	 * @since 2.0.0
	 */
	protected JdbcAdaptor getJdbcAdaptor() {
		return this.jdbcAdaptor;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Set<String> getPkColumnNames() {
		return this.pkColumns.keySet();
	}

	/**
	 * Returns the pkColumns of the EntityTable.
	 * 
	 * @return the pkColumns of the EntityTable
	 * 
	 * @since 2.0.0
	 */
	public Collection<AbstractColumn> getPkColumns() {
		return this.pkColumns.values();
	}

	private String getRemoveSql(int size) {
		String sql = this.removeSqlMap.get(size);
		if (sql != null) {
			return sql;
		}

		synchronized (this) {
			sql = this.removeSqlMap.get(size);
			if (sql != null) {
				return sql;
			}

			String restriction;
			if (size == 1) {
				restriction = this.getRestrictionSql(this.pkColumns);
			}
			else {
				restriction = this.pkColumns.values().iterator().next().getName() + " IN (" + StringUtils.repeat("?", ", ", size) + ")";
			}

			sql = "DELETE FROM " + this.getQName() + " WHERE " + restriction;
			this.removeSqlMap.put(size, sql);

			return sql;
		}
	}

	/**
	 * Performs inserts to the table for the managed instance or joins.
	 * 
	 * @param connection
	 *            the connection to use
	 * @param entityType
	 *            the entity type of the instances
	 * @param instances
	 *            the instances to perform insert for
	 * @param size
	 *            the size of the batch
	 * @throws SQLException
	 *             thrown in case of underlying SQLException
	 * 
	 * @since 2.0.0
	 */
	public void performInsert(Connection connection, EntityTypeDescriptor entityType, Object[] instances, int size) throws SQLException {
		// Do not inline, generation of the insert SQL will initialize the insertColumns!
		final String insertSql = this.getInsertSql(entityType, size);
		final AbstractColumn[] insertColumns = this.getInsertColumns(entityType, size);

		// prepare the parameters
		final Object[] params = new Object[insertColumns.length * size];

		boolean hasLob = false;
		for (int i = 0; i < size; i++) {
			final Object instance = instances[i];

			for (int j = 0; j < insertColumns.length; j++) {
				final AbstractColumn column = insertColumns[j];

				if (column instanceof DiscriminatorColumn) {
					params[(i * insertColumns.length) + j] = entityType.getDiscriminatorValue();
				}
				else {
					params[(i * insertColumns.length) + j] = column.getValue(connection, instance);
				}

				hasLob |= column.isLob();
			}
		}

		new QueryRunner(this.jdbcAdaptor, hasLob).update(connection, insertSql, params);

		// if there is an identity column, extract the identity and set it back to the instance
		if (this.identityColumn != null) {
			final String selectLastIdSql = this.jdbcAdaptor.getSelectLastIdentitySql(this.identityColumn);
			final Number id = new QueryRunner(this.jdbcAdaptor, false).query(connection, selectLastIdSql, new SingleValueHandler<Number>());

			this.identityColumn.setValue(instances[0], id);
		}
	}

	/**
	 * Performs removes from the table for the managed instance or joins.
	 * 
	 * @param connection
	 *            the connection to use
	 * @param instances
	 *            the instances to perform remove for
	 * @param size
	 *            the size of the batch
	 * @throws SQLException
	 *             thrown in case of underlying SQLException
	 * 
	 * @since 2.0.0
	 */
	public void performRemove(Connection connection, Object[] instances, int size) throws SQLException {
		final String removeSql = this.getRemoveSql(size);

		// prepare the parameters
		final AbstractColumn[] restrictionColumns = this.getRestrictionColumns();
		final Object[] params = new Object[size * restrictionColumns.length];
		for (int i = 0; i < size; i++) {
			final Object instance = instances[i];

			for (int j = 0; j < restrictionColumns.length; j++) {
				final AbstractColumn column = restrictionColumns[j];
				params[(i * restrictionColumns.length) + j] = column.getValue(connection, instance);
			}
		}

		final QueryRunner runner = new QueryRunner(this.jdbcAdaptor, false);
		if (size != runner.update(connection, removeSql, params)) {
			throw new OptimisticLockFailedException();
		}
	}

	/**
	 * Performs update to the table for the managed instance or joins.
	 * 
	 * @param connection
	 *            the connection to use
	 * @param type
	 *            the entity type of the instance
	 * @param instance
	 *            the instance to perform update for
	 * @param oldVersion
	 *            the old version value
	 * @throws SQLException
	 *             thrown in case of underlying SQLException
	 * 
	 * @since 2.0.0
	 */
	public void performUpdate(Connection connection, EntityTypeDescriptor type, Object instance, Object oldVersion) throws SQLException {
		// Do not inline, generation of the update SQL will initialize the insertColumns!
		final String updateSql = this.getUpdateSql(type, this.pkColumns);
		final AbstractColumn[] updateColumns = this.getUpdateColumns(type);
		final AbstractColumn[] restrictionColumns = this.getRestrictionColumns();

		boolean hasLob = false;
		int nextParamNo = 0;

		// prepare the parameters
		final Object[] params = new Object[updateColumns.length + restrictionColumns.length];
		for (final AbstractColumn column : updateColumns) {
			params[nextParamNo++] = column.getValue(connection, instance);

			hasLob |= column.isLob();
		}

		for (final AbstractColumn column : restrictionColumns) {
			if (column.isVersion()) {
				params[nextParamNo++] = oldVersion;
			}
			else {
				params[nextParamNo++] = column.getValue(connection, instance);
			}
		}

		// execute the insert
		final QueryRunner runner = new QueryRunner(this.jdbcAdaptor, hasLob);
		if (1 != runner.update(connection, updateSql, params)) {
			throw new OptimisticLockFailedException();
		}
	}

	/**
	 * Performs update to the table for the managed instance or joins. In addition checks if the table participates in update.
	 * 
	 * @param connection
	 *            the connection to use
	 * @param type
	 *            the entity type of the instance
	 * @param instance
	 *            the instance to perform update for
	 * @param oldVersion
	 *            the old version value
	 * @return returns true if the table is updatable
	 * @throws SQLException
	 *             thrown in case of underlying SQLException
	 * 
	 * @since 2.0.0
	 */
	public boolean performUpdateWithUpdatability(Connection connection, EntityTypeDescriptor type, Object instance, Object oldVersion) throws SQLException {
		// Do not inline, generation of the update SQL will initialize the insertColumns!
		final String updateSql = this.getUpdateSql(type, this.pkColumns);
		final AbstractColumn[] updateColumns = this.getUpdateColumns(type);
		final AbstractColumn[] restrictionColumns = this.getRestrictionColumns();

		if (updateColumns.length == 0) {
			return false;
		}

		int nextParam = 0;
		boolean hasLob = false;

		// prepare the parameters
		final Object[] params = new Object[updateColumns.length + restrictionColumns.length];
		for (final AbstractColumn column : updateColumns) {
			params[nextParam++] = column.getValue(connection, instance);

			hasLob |= column.isLob();
		}

		for (final AbstractColumn column : restrictionColumns) {
			if (column.isVersion()) {
				params[nextParam++] = oldVersion;
			}
			else {
				params[nextParam++] = column.getValue(connection, instance);
			}
		}

		// execute the insert
		final QueryRunner runner = new QueryRunner(this.jdbcAdaptor, hasLob);
		if (1 != runner.update(connection, updateSql, params)) {
			throw new OptimisticLockFailedException();
		}

		return true;
	}

	/**
	 * Performs version update to the table.
	 * 
	 * @param connection
	 *            the connection to use
	 * @param instance
	 *            the instance to perform version update for
	 * @param oldVersion
	 *            the old version value
	 * @param newVersion
	 *            the new version value
	 * @throws SQLException
	 *             thrown in case of underlying SQLException
	 * 
	 * @since 2.0.0
	 */
	public void performVersionUpdate(Connection connection, Object instance, Object oldVersion, Object newVersion) throws SQLException {
		// Do not inline, generation of the update SQL will initialize the insertColumns!
		final String updateSql = this.getVersionUpdateSql(this.pkColumns);
		final AbstractColumn[] restrictionColumns = this.getRestrictionColumns();

		// prepare the parameters
		final Object[] params = new Object[restrictionColumns.length];

		params[0] = newVersion;
		for (int i = 1; i < (restrictionColumns.length - 1); i++) {
			final AbstractColumn column = restrictionColumns[i];
			params[i] = column.getValue(connection, instance);
		}
		params[params.length - 1] = oldVersion;

		// execute the update
		if (1 != new QueryRunner(this.jdbcAdaptor, false).update(connection, updateSql, params)) {
			throw new OptimisticLockFailedException();
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String toString() {
		final String columns = Joiner.on(", ").join(Collections2.transform(this.getColumnMap().values(), new Function<AbstractColumn, String>() {

			@Override
			public String apply(AbstractColumn input) {
				final StringBuffer out = new StringBuffer();
				out.append(input.isPrimaryKey() ? "ID [" : "COL [");

				out.append("name=");
				out.append(input.getName());
				out.append(", type=");
				out.append(input.getSqlType());

				out.append("]");
				return out.toString();
			}
		}));

		return "Table [owner=" + this.entity.getName() //
			+ ", name=" + this.getQName() //
			+ ", columns=[" + columns + "]]";
	}
}
