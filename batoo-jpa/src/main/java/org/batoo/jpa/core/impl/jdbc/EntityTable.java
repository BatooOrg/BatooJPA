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
package org.batoo.jpa.core.impl.jdbc;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.batoo.common.util.FinalWrapper;
import org.batoo.jpa.core.impl.instance.ManagedInstance;
import org.batoo.jpa.core.impl.instance.Status;
import org.batoo.jpa.core.impl.jdbc.dbutils.QueryRunner;
import org.batoo.jpa.core.impl.jdbc.dbutils.SingleValueHandler;
import org.batoo.jpa.core.impl.model.type.EntityTypeImpl;
import org.batoo.jpa.core.jdbc.IdType;
import org.batoo.jpa.core.jdbc.adapter.JdbcAdaptor;
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

	private final EntityTypeImpl<?> entity;
	private final Map<String, AbstractColumn> pkColumns = Maps.newHashMap();

	private final JdbcAdaptor jdbcAdaptor;
	private BasicColumn identityColumn;
	private final Map<String, BasicColumn[]> indexes = Maps.newHashMap();

	private final HashMap<Integer, String> removeSqlMap = Maps.newHashMap();
	private AbstractColumn[] removeColumns;
	private FinalWrapper<HashMap<AbstractColumn, String>> idColumns;

	/**
	 * @param entity
	 *            the entity
	 * @param metadata
	 *            the table metadata
	 * 
	 * @since 2.0.0
	 */
	public EntityTable(EntityTypeImpl<?> entity, TableMetadata metadata) {
		super(entity.getName(), metadata);

		this.entity = entity;

		this.jdbcAdaptor = entity.getMetamodel().getJdbcAdaptor();
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
	public EntityTypeImpl<?> getEntity() {
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

			this.removeColumns = new AbstractColumn[this.pkColumns.size()];
			this.pkColumns.values().toArray(this.removeColumns);

			String restriction;
			if (this.pkColumns.size() > 1) {
				final Collection<String> restrictions = Collections2.transform(this.pkColumns.values(), new Function<AbstractColumn, String>() {

					@Override
					public String apply(AbstractColumn input) {
						return input.getName() + " = ?";
					}
				});
				final String singleRestriction = Joiner.on(" AND ").join(restrictions);
				restriction = StringUtils.repeat(singleRestriction, " OR ", size);
			}
			else if (size == 1) {
				restriction = this.removeColumns[0].getName() + " = ?";
			}
			else {
				restriction = this.removeColumns[0].getName() + " IN (" + StringUtils.repeat("?", ", ", size) + ")";
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
	 * @param managedInstances
	 *            the managed instances to perform insert for
	 * @param size
	 *            the size of the batch
	 * @throws SQLException
	 *             thrown in case of underlying SQLException
	 * 
	 * @since 2.0.0
	 */
	public void performInsert(Connection connection, final ManagedInstance<?>[] managedInstances, int size) throws SQLException {
		final EntityTypeImpl<?> entityType = managedInstances[0].getType();

		// Do not inline, generation of the insert SQL will initialize the insertColumns!
		final String insertSql = this.getInsertSql(entityType, size);
		final AbstractColumn[] insertColumns = this.getInsertColumns(entityType, size);

		// prepare the parameters
		final Object[] params = new Object[insertColumns.length * size];

		boolean hasLob = false;
		for (int i = 0; i < size; i++) {
			final ManagedInstance<?> managedInstance = managedInstances[i];
			final Object instance = managedInstance.getInstance();

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

			managedInstance.setStatus(Status.MANAGED);
		}

		new QueryRunner(this.jdbcAdaptor.isPmdBroken(), hasLob).update(connection, insertSql, params);

		// if there is an identity column, extract the identity and set it back to the instance
		if (this.identityColumn != null) {
			final String selectLastIdSql = this.jdbcAdaptor.getSelectLastIdentitySql(this.identityColumn);
			final Number id = new QueryRunner(this.jdbcAdaptor.isPmdBroken(), false).query(connection, selectLastIdSql, new SingleValueHandler<Number>());

			this.identityColumn.setValue(managedInstances[0].getInstance(), id);
		}
	}

	/**
	 * Performs removes from the table for the managed instance or joins.
	 * 
	 * @param connection
	 *            the connection to use
	 * @param managedInstances
	 *            the managed instance to perform remove for
	 * @param size
	 *            the size of the batch
	 * @throws SQLException
	 *             thrown in case of underlying SQLException
	 * 
	 * @since 2.0.0
	 */
	public void performRemove(Connection connection, final ManagedInstance<?>[] managedInstances, int size) throws SQLException {
		final String removeSql = this.getRemoveSql(size);

		// prepare the parameters
		final Object[] params = new Object[size * this.removeColumns.length];
		for (int i = 0; i < size; i++) {
			final Object instance = managedInstances[i].getInstance();

			for (int j = 0; j < this.removeColumns.length; j++) {
				final AbstractColumn column = this.removeColumns[j];
				params[(i * this.removeColumns.length) + j] = column.getValue(connection, instance);
			}
		}

		final QueryRunner runner = new QueryRunner(this.jdbcAdaptor.isPmdBroken(), false);
		runner.update(connection, removeSql, params);
	}

	/**
	 * Selects the version from the table.
	 * 
	 * @param connection
	 *            the connection to use
	 * @param managedInstance
	 *            the managed instance to perform select for
	 * @return the version
	 * @throws SQLException
	 *             thrown in case of underlying SQLException
	 * 
	 * @since 2.0.0
	 */
	public Object performSelectVersion(Connection connection, final ManagedInstance<?> managedInstance) throws SQLException {
		final Object instance = managedInstance.getInstance();

		// Do not inline, generation of the update SQL will initialize the insertColumns!
		final String updateSql = this.getSelectVersionSql(this.pkColumns);
		final AbstractColumn[] selectVersionColumns = this.getSelectVersionColumns();

		// prepare the parameters
		final Object[] params = new Object[selectVersionColumns.length];
		for (int i = 0; i < selectVersionColumns.length; i++) {
			final AbstractColumn column = selectVersionColumns[i];
			params[i] = column.getValue(connection, instance);
		}

		// execute the insert
		final QueryRunner runner = new QueryRunner(this.jdbcAdaptor.isPmdBroken(), false);

		return runner.query(connection, updateSql, new SingleValueHandler<Object>(), params);
	}

	/**
	 * Performs update to the table for the managed instance or joins.
	 * 
	 * @param connection
	 *            the connection to use
	 * @param managedInstance
	 *            the managed instance to perform update for
	 * @throws SQLException
	 *             thrown in case of underlying SQLException
	 * 
	 * @since 2.0.0
	 */
	public void performUpdate(Connection connection, final ManagedInstance<?> managedInstance) throws SQLException {
		final EntityTypeImpl<?> entityType = managedInstance.getType();
		final Object instance = managedInstance.getInstance();

		// Do not inline, generation of the update SQL will initialize the insertColumns!
		final String updateSql = this.getUpdateSql(entityType, this.pkColumns);
		final AbstractColumn[] updateColumns = this.getUpdateColumns(entityType);

		boolean hasLob = false;
		// prepare the parameters
		final Object[] params = new Object[updateColumns.length];
		for (int i = 0; i < updateColumns.length; i++) {
			final AbstractColumn column = updateColumns[i];
			params[i] = column.getValue(connection, instance);

			hasLob |= column.isLob();
		}

		// execute the insert
		final QueryRunner runner = new QueryRunner(this.jdbcAdaptor.isPmdBroken(), hasLob);
		runner.update(connection, updateSql, params);
	}

	/**
	 * Performs update to the table for the managed instance or joins. In addition checks if the table participate in update.
	 * 
	 * @param connection
	 *            the connection to use
	 * @param managedInstance
	 *            the managed instance to perform update for
	 * @return returns true if the table is updatable
	 * @throws SQLException
	 *             thrown in case of underlying SQLException
	 * 
	 * @since 2.0.0
	 */
	public boolean performUpdateWithUpdatability(Connection connection, final ManagedInstance<?> managedInstance) throws SQLException {
		final EntityTypeImpl<?> entityType = managedInstance.getType();
		final Object instance = managedInstance.getInstance();

		// Do not inline, generation of the update SQL will initialize the insertColumns!
		final String updateSql = this.getUpdateSql(entityType, this.pkColumns);
		final AbstractColumn[] updateColumns = this.getUpdateColumns(entityType);

		boolean hasLob = false;

		// prepare the parameters
		final Object[] params = new Object[updateColumns.length];
		for (int i = 0; i < updateColumns.length; i++) {
			final AbstractColumn column = updateColumns[i];

			// if the first column is primary key then the table is not updatable
			if ((i == 0) && column.isPrimaryKey()) {
				return false;
			}

			params[i] = column.getValue(connection, instance);

			hasLob |= column.isLob();
		}

		// execute the insert
		final QueryRunner runner = new QueryRunner(this.jdbcAdaptor.isPmdBroken(), hasLob);
		runner.update(connection, updateSql, params);

		return true;
	}

	/**
	 * Performs version update to the table.
	 * 
	 * @param connection
	 *            the connection to use
	 * @param managedInstance
	 *            the managed instance to perform version update for
	 * @throws SQLException
	 *             thrown in case of underlying SQLException
	 * 
	 * @since 2.0.0
	 */
	public void performVersionUpdate(Connection connection, final ManagedInstance<?> managedInstance) throws SQLException {
		final Object instance = managedInstance.getInstance();

		// Do not inline, generation of the update SQL will initialize the insertColumns!
		final String updateSql = this.getVersionUpdateSql(this.pkColumns);
		final AbstractColumn[] versionUpdateColumns = this.getVersionUpdateColumns();

		// prepare the parameters
		final Object[] params = new Object[versionUpdateColumns.length];
		for (int i = 0; i < versionUpdateColumns.length; i++) {
			final AbstractColumn column = versionUpdateColumns[i];
			params[i] = column.getValue(connection, instance);
		}

		// execute the update
		new QueryRunner(this.jdbcAdaptor.isPmdBroken(), false).update(connection, updateSql, params);
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
