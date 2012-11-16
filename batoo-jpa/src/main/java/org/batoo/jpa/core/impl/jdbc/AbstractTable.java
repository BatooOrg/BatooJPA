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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.batoo.common.util.FinalWrapper;
import org.batoo.jpa.core.impl.model.attribute.AttributeImpl;
import org.batoo.jpa.core.impl.model.attribute.BasicAttribute;
import org.batoo.jpa.core.impl.model.type.EntityTypeImpl;
import org.batoo.jpa.core.jdbc.IdType;
import org.batoo.jpa.parser.MappingException;
import org.batoo.jpa.parser.impl.AbstractLocator;
import org.batoo.jpa.parser.metadata.ColumnTransformerMetadata;
import org.batoo.jpa.parser.metadata.TableMetadata;
import org.batoo.jpa.parser.metadata.UniqueConstraintMetadata;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Abstract implementation for Entity, Secondary and Join tables.
 * 
 * @author hceylan
 * @since 2.0.0
 */
public abstract class AbstractTable {

	private final AbstractLocator locator;

	private final String catalog;
	private final String schema;
	private String name;
	private final Map<String, AbstractColumn> columnMap = Maps.newHashMap();
	private final Map<String, String[]> uniqueConstraints = Maps.newHashMap();
	private final List<ForeignKey> foreignKeys = Lists.newArrayList();

	private final HashMap<String, String> insertSqlMap = Maps.newHashMap();
	private final HashMap<EntityTypeImpl<?>, String> updateSqlMap = Maps.newHashMap();
	private String updateSql;
	private FinalWrapper<String> versionUpdateSql;
	private FinalWrapper<String> versionSelectSql;
	private FinalWrapper<AbstractColumn[]> columns;

	private AbstractColumn[] updateColumns;
	private AbstractColumn[] versionUpdateColumns;
	private AbstractColumn[] selectVersionColumns;
	private final Map<String, AbstractColumn[]> insertColumnsMap = Maps.newHashMap();
	private final Map<EntityTypeImpl<?>, AbstractColumn[]> updateColumnsMap = Maps.newHashMap();

	/**
	 * @param defaultName
	 *            the default name for the table
	 * @param metadata
	 *            the metadata
	 * 
	 * @since 2.0.0
	 */
	public AbstractTable(String defaultName, TableMetadata metadata) {
		this(metadata);

		if (this.name == null) {
			this.name = defaultName;
		}
	}

	/**
	 * @param metadata
	 *            the metadata
	 * 
	 * @since 2.0.0
	 */
	public AbstractTable(TableMetadata metadata) {
		super();

		this.locator = metadata != null ? metadata.getLocator() : null;
		this.catalog = (metadata != null) && StringUtils.isNotBlank(metadata.getCatalog()) ? metadata.getCatalog() : null;
		this.schema = (metadata != null) && StringUtils.isNotBlank(metadata.getSchema()) ? metadata.getSchema() : null;

		if (metadata != null) {
			if (StringUtils.isNotBlank(metadata.getName())) {
				this.name = metadata.getName();
			}

			for (final UniqueConstraintMetadata constraint : metadata.getUniqueConstraints()) {
				this.uniqueConstraints.put(constraint.getName(), constraint.getColumnNames());
			}
		}
	}

	/**
	 * Adds the column to the table
	 * 
	 * @param column
	 *            the column to add
	 * 
	 * @since 2.0.0
	 */
	public void addColumn(AbstractColumn column) {
		final AbstractColumn existing = this.columnMap.get(column.getName());

		if (existing != null) {
			if (column instanceof JoinColumn) {
				final JoinColumn joinColumn = (JoinColumn) column;
				if (!joinColumn.isInsertable() && !joinColumn.isUpdatable()) {
					joinColumn.setVirtual(existing);
				}

				return;
			}

			throw new MappingException("Duplicate column names " + column.getName() + " on table " + this.name, column.getLocator(), existing.getLocator());
		}

		this.columnMap.put(column.getName(), column);
	}

	/**
	 * Adds a foreign key to the table.
	 * 
	 * @param foreignKey
	 *            the foreign key to add
	 * 
	 * @since 2.0.0
	 */
	public void addForeignKey(ForeignKey foreignKey) {
		this.foreignKeys.add(foreignKey);
	}

	/**
	 * Generates the insert statement for the type.
	 * 
	 * @param type
	 *            the type to generate the insert statement for
	 * @param pkColumns
	 *            the primary key columns
	 * 
	 * @since 2.0.0
	 */
	private synchronized void generateInsertSql(final EntityTypeImpl<?> type, int size) {
		final String sqlKey = type != null ? type.getName() + size : "" + size;

		String sql = this.insertSqlMap.get(sqlKey);
		if (sql != null) { // other thread finished the job for us
			return;
		}

		final List<AbstractColumn> insertColumns = Lists.newArrayList();

		// Filter out the identity physicalColumns
		final Collection<AbstractColumn> filteredColumns = type == null ? this.columnMap.values() : Collections2.filter(this.columnMap.values(),
			new Predicate<AbstractColumn>() {

				@Override
				public boolean apply(AbstractColumn input) {
					return AbstractTable.this.isInsertableColumn(type, input);
				}

			});

		// prepare the names tuple in the form of "COLNAME [, COLNAME]*"

		final Collection<String> columnNames = Collections2.transform(filteredColumns, new Function<AbstractColumn, String>() {

			@Override
			public String apply(AbstractColumn input) {
				insertColumns.add(input);

				return input.getName();
			}
		});

		if (columnNames.size() == 0) {
			// TODO investigate in others with identity
			sql = "INSERT INTO " + this.getQName() + " DEFAULT VALUES";
		}
		else {

			final Collection<String> singleParams = Collections2.transform(filteredColumns, new Function<AbstractColumn, String>() {

				@Override
				public String apply(AbstractColumn input) {
					String writeParam = null;
					if (input instanceof BasicColumn) {
						final ColumnTransformerMetadata columnTransformer = ((BasicColumn) input).getMapping().getAttribute().getColumnTransformer();
						writeParam = columnTransformer != null ? columnTransformer.getWrite() : null;
					}
					writeParam = Strings.isNullOrEmpty(writeParam) ? "?" : writeParam;

					return writeParam;
				}
			});

			// prepare the parameters in the form of "? [, ?]*"
			final String singleParamStr = "\t(" + Joiner.on(", ").join(singleParams) + ")";
			final String parametersStr = StringUtils.repeat(singleParamStr, ",\n", size);

			final String columnNamesStr = Joiner.on(", ").join(columnNames);

			// INSERT INTO SCHEMA.TABLE
			// (COL [, COL]*)
			// VALUES (PARAM [, PARAM]*)
			sql = "INSERT INTO " + this.getQName() //
				+ "\n(" + columnNamesStr + ")"//
				+ "\nVALUES\n" + parametersStr;
		}

		this.insertSqlMap.put(sqlKey, sql);
		this.insertColumnsMap.put(sqlKey, insertColumns.toArray(new AbstractColumn[insertColumns.size()]));
	}

	/**
	 * Generates the update statement for the type.
	 * 
	 * @param type
	 *            the type to generate the update statement for
	 * 
	 * @since 2.0.0
	 * @param pkColumns
	 */
	private synchronized void generateUpdateSql(final EntityTypeImpl<?> type, Map<String, AbstractColumn> pkColumns) {
		String sql = this.updateSqlMap.get(type);
		if (sql != null) { // other thread finished the job for us
			return;
		}

		final List<AbstractColumn> updateColumns = Lists.newArrayList();
		// Filter out the identity physicalColumns
		final Collection<AbstractColumn> filteredColumns = type == null ? this.columnMap.values() : Collections2.filter(this.columnMap.values(),
			new Predicate<AbstractColumn>() {

				@Override
				public boolean apply(AbstractColumn input) {
					return AbstractTable.this.isUpdatableColumn(type, input);
				}
			});

		// prepare the names tuple in the form of "COLNAME = ? [, COLNAME = ?]*"
		final Collection<String> columnNames = Collections2.transform(filteredColumns, new Function<AbstractColumn, String>() {

			@Override
			public String apply(AbstractColumn input) {
				if (!input.isPrimaryKey()) {
					updateColumns.add(input);

					return input.getName() + " = ?";
				}

				return null;
			}
		});

		final Collection<String> restrictions = Collections2.transform(pkColumns.values(), new Function<AbstractColumn, String>() {

			@Override
			public String apply(AbstractColumn input) {
				updateColumns.add(input);

				return input.getName() + " = ?";
			}
		});

		final String columnNamesStr = Joiner.on(", ").skipNulls().join(columnNames);
		final String restrictionStr = Joiner.on(" AND ").join(restrictions);

		// UPDATE SCHEMA.TABLE SET
		// (COL [, COL]*)
		// WHERE ID = ? [, ID = ?]*)
		sql = "UPDATE " + this.getQName() + " SET"//
			+ "\n" + columnNamesStr //
			+ "\nWHERE " + restrictionStr;

		if (type != null) {
			this.updateSqlMap.put(type, sql);
			this.updateColumnsMap.put(type, updateColumns.toArray(new AbstractColumn[updateColumns.size()]));
		}
		else {
			this.updateSql = sql;
			this.updateColumns = updateColumns.toArray(new AbstractColumn[updateColumns.size()]);
		}
	}

	/**
	 * Returns the catalog.
	 * 
	 * @return the catalog
	 * 
	 * @since 2.0.0
	 */
	public String getCatalog() {
		return this.catalog;
	}

	/**
	 * Returns the columnMap of the AbstractTable.
	 * 
	 * @return the columnMap of the AbstractTable
	 * 
	 * @since 2.0.0
	 */
	protected Map<String, AbstractColumn> getColumnMap() {
		return this.columnMap;
	}

	/**
	 * Returns the set of column names.
	 * 
	 * @return the set of column names
	 * 
	 * @since 2.0.0
	 */
	public Collection<String> getColumnNames() {
		return Collections2.transform(this.columnMap.values(), new Function<AbstractColumn, String>() {

			@Override
			public String apply(AbstractColumn input) {
				return input.getName();
			}
		});
	}

	/**
	 * Returns the array of columns the table has
	 * 
	 * @return the array of columns the table has
	 * 
	 * @since 2.0.0
	 */
	public AbstractColumn[] getColumns() {
		FinalWrapper<AbstractColumn[]> wrapper = this.columns;

		if (wrapper == null) {
			synchronized (this) {
				if (this.columns == null) {
					this.columns = new FinalWrapper<AbstractColumn[]>(this.columnMap.values().toArray(new AbstractColumn[this.columnMap.values().size()]));
				}

				wrapper = this.columns;
			}
		}

		return wrapper.value;
	}

	/**
	 * Returns the foreign keys of the table.
	 * 
	 * @return the foreign keys of the table
	 * 
	 * @since 2.0.0
	 */
	public List<ForeignKey> getForeignKeys() {
		return this.foreignKeys;
	}

	/**
	 * Returns the columns for the insert.
	 * 
	 * @param entity
	 *            the entity to returns columns for or null for generic columns
	 * @param size
	 *            the batch size
	 * @return the insert columns
	 * 
	 * @since 2.0.0
	 */
	protected AbstractColumn[] getInsertColumns(final EntityTypeImpl<?> entity, int size) {
		return this.insertColumnsMap.get(entity != null ? entity.getName() + size : "" + size);
	}

	/**
	 * Returns the insert statement for the table specifically.
	 * 
	 * @param entity
	 *            the entity to return insert statement for or null for generic SQL
	 * @param size
	 *            the batch size
	 * @return the insert statement
	 * 
	 * @since 2.0.0
	 */
	protected String getInsertSql(EntityTypeImpl<?> entity, int size) {
		final String sqlKey = entity != null ? entity.getName() + size : "" + size;

		final String sql = this.insertSqlMap.get(sqlKey);
		if (sql != null) { // other thread finished the job for us
			return sql;
		}

		this.generateInsertSql(entity, size);

		return this.insertSqlMap.get(sqlKey);
	}

	/**
	 * Returns the locator.
	 * 
	 * @return the locator
	 * 
	 * @since 2.0.0
	 */
	public AbstractLocator getLocator() {
		return this.locator;
	}

	/**
	 * Returns the name.
	 * 
	 * @return the name
	 * 
	 * @since 2.0.0
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Returns the set of primary key column names.
	 * 
	 * @return the set of primary column names
	 * 
	 * @since 2.0.0
	 */
	public Set<String> getPkColumnNames() {
		return Collections.emptySet();
	}

	/**
	 * Returns the qualified name of the table.
	 * 
	 * @return the qualified name of the table
	 * 
	 * @since 2.0.0
	 */
	public String getQName() {
		return Joiner.on(".").skipNulls().join(this.schema, this.name);
	}

	/**
	 * Returns the schema.
	 * 
	 * @return the schema
	 * 
	 * @since 2.0.0
	 */
	public String getSchema() {
		return this.schema;
	}

	/**
	 * Returns the select version columns.
	 * 
	 * @return the select version columns
	 * 
	 * @since 2.0.0
	 */
	public AbstractColumn[] getSelectVersionColumns() {
		return this.selectVersionColumns;
	}

	/**
	 * Returns the version select statement for the table specifically.
	 * 
	 * @param pkColumns
	 *            the primary key columns
	 * @return the select statement
	 * 
	 * @since 2.0.0
	 */
	protected String getSelectVersionSql(Map<String, AbstractColumn> pkColumns) {
		FinalWrapper<String> wrapper = this.versionSelectSql;

		if (wrapper == null) {
			synchronized (this) {
				if (this.versionSelectSql == null) {

					AbstractColumn versionColumn = null;

					for (final AbstractColumn column : this.getColumns()) {
						if (column instanceof DiscriminatorColumn) {
							continue;
						}

						final AttributeImpl<?, ?> attribute = column.getMapping().getAttribute();

						if ((attribute instanceof BasicAttribute) && ((BasicAttribute<?, ?>) attribute).isVersion()) {
							versionColumn = column;

							break;
						}
					}

					final List<AbstractColumn> selectVersionColumns = Lists.newArrayList();

					final Collection<String> restrictions = Collections2.transform(pkColumns.values(), new Function<AbstractColumn, String>() {

						@Override
						public String apply(AbstractColumn input) {
							selectVersionColumns.add(input);

							return input.getName() + " = ?";
						}
					});

					final String restrictionStr = Joiner.on(" AND ").join(restrictions);

					if (versionColumn != null) {
						// SELECT VERSION_COLUMN FROM SCHEMA.TABLE SET
						// WHERE (PARAM [, PARAM]*)
						this.versionSelectSql = new FinalWrapper<String>("SELECT " + versionColumn.getName() //
							+ " FROM " + this.getQName() //
							+ "\nWHERE " + restrictionStr);

						this.selectVersionColumns = selectVersionColumns.toArray(new AbstractColumn[selectVersionColumns.size()]);
					}
				}

				wrapper = this.versionSelectSql;
			}
		}

		return wrapper.value;
	}

	/**
	 * Returns the unique constraints.
	 * 
	 * @return the unique constraints
	 * 
	 * @since 2.0.0
	 */
	public Map<String, String[]> getUniqueConstraints() {
		return this.uniqueConstraints;
	}

	/**
	 * Returns the columns for the update.
	 * 
	 * @param entity
	 *            the entity to returns columns for or null for generic columns
	 * @return the insert columns
	 * 
	 * @since 2.0.0
	 */
	protected AbstractColumn[] getUpdateColumns(final EntityTypeImpl<?> entity) {
		if (entity == null) {
			return this.updateColumns;
		}

		return this.updateColumnsMap.get(entity);
	}

	/**
	 * Returns the update statement for the table specifically.
	 * 
	 * @param entity
	 *            the entity to return update statement for or null for generic SQL
	 * @param pkColumns
	 *            the primary key columns
	 * @return the insert statement
	 * 
	 * @since 2.0.0
	 */
	protected String getUpdateSql(EntityTypeImpl<?> entity, Map<String, AbstractColumn> pkColumns) {
		if (entity == null) {
			if (this.updateSql == null) {
				this.generateUpdateSql(null, pkColumns);
			}

			return this.updateSql;
		}

		String sql = this.updateSqlMap.get(entity);
		if (sql == null) {
			this.generateUpdateSql(entity, pkColumns);

			sql = this.updateSqlMap.get(entity);
		}

		return sql;
	}

	/**
	 * Returns the version update columns.
	 * 
	 * @return the version update columns
	 * 
	 * @since 2.0.0
	 */
	public AbstractColumn[] getVersionUpdateColumns() {
		return this.versionUpdateColumns;
	}

	/**
	 * Returns the version update statement for the table specifically.
	 * 
	 * @param pkColumns
	 *            the primary key columns
	 * @return the update statement
	 * 
	 * @since 2.0.0
	 */
	protected String getVersionUpdateSql(Map<String, AbstractColumn> pkColumns) {
		FinalWrapper<String> wrapper = this.versionUpdateSql;

		if (wrapper == null) {
			synchronized (this) {
				if (this.versionUpdateSql == null) {

					final List<AbstractColumn> versionUpdateColumns = Lists.newArrayList();

					for (final AbstractColumn column : this.getColumns()) {
						final AttributeImpl<?, ?> attribute = column.getMapping().getAttribute();

						if ((attribute instanceof BasicAttribute) && ((BasicAttribute<?, ?>) attribute).isVersion()) {
							versionUpdateColumns.add(column);

							break;
						}
					}

					final Collection<String> restrictions = Collections2.transform(pkColumns.values(), new Function<AbstractColumn, String>() {

						@Override
						public String apply(AbstractColumn input) {
							versionUpdateColumns.add(input);

							return input.getName() + " = ?";
						}
					});

					final String columnNamesStr = versionUpdateColumns.get(0).getName() + " = ?";
					final String restrictionStr = Joiner.on(" AND ").join(restrictions);

					// UPDATE SCHEMA.TABLE SET
					// (COL [, COL]*)
					// VALUES (PARAM [, PARAM]*)
					this.versionUpdateSql = new FinalWrapper<String>("UPDATE " + this.getQName() + " SET"//
						+ "\n" + columnNamesStr //
						+ "\nWHERE " + restrictionStr);

					this.versionUpdateColumns = versionUpdateColumns.toArray(new AbstractColumn[versionUpdateColumns.size()]);
				}

				wrapper = this.versionUpdateSql;
			}
		}

		return wrapper.value;
	}

	private boolean isInsertableColumn(final EntityTypeImpl<?> type, AbstractColumn input) {
		if (input.getIdType() == IdType.IDENTITY) {
			return false;
		}

		if (!input.isInsertable()) {
			return false;
		}

		if (input instanceof DiscriminatorColumn) {
			return true;
		}

		final EntityTypeImpl<?> root;

		if ((input instanceof JoinColumn) && (input.getMapping() == null)) {
			root = (EntityTypeImpl<?>) ((JoinColumn) input).getReferencedColumn().getMapping().getRoot().getType();
		}
		else if ((input instanceof OrderColumn)) {
			return input.isInsertable();
		}
		else {
			root = (EntityTypeImpl<?>) input.getMapping().getRoot().getType();
		}

		final Class<?> parent = root.getJavaType();
		final Class<?> javaType = type.getJavaType();

		return parent.isAssignableFrom(javaType);
	}

	private boolean isUpdatableColumn(final EntityTypeImpl<?> type, AbstractColumn input) {
		if ((input.isPrimaryKey()) || (input instanceof DiscriminatorColumn)) {
			return false;
		}

		if (!input.isUpdatable()) {
			return false;
		}

		final EntityTypeImpl<?> root;

		if ((input instanceof JoinColumn) && (input.getMapping() == null)) {
			root = (EntityTypeImpl<?>) ((JoinColumn) input).getReferencedColumn().getMapping().getRoot().getType();
		}
		else {
			root = (EntityTypeImpl<?>) input.getMapping().getRoot().getType();
		}

		final Class<?> parent = root.getJavaType();
		final Class<?> javaType = type.getJavaType();

		return parent.isAssignableFrom(javaType);
	}

	/**
	 * Updates the name of the table.
	 * 
	 * @param name
	 *            the name of the table
	 * 
	 * @since 2.0.0
	 */
	protected void setName(String name) {
		this.name = name;
	}
}
