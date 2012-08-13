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
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.batoo.jpa.core.impl.model.attribute.AttributeImpl;
import org.batoo.jpa.core.impl.model.attribute.BasicAttribute;
import org.batoo.jpa.core.impl.model.type.EntityTypeImpl;
import org.batoo.jpa.core.jdbc.IdType;
import org.batoo.jpa.parser.MappingException;
import org.batoo.jpa.parser.impl.AbstractLocator;
import org.batoo.jpa.parser.metadata.TableMetadata;
import org.batoo.jpa.parser.metadata.UniqueConstraintMetadata;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Abstract implementation for Entity, Secondary and Join tables.
 * 
 * @author hceylan
 * @since $version
 */
public class AbstractTable {

	private final AbstractLocator locator;

	private final String catalog;
	private final String schema;
	private String name;
	private final Map<String, AbstractColumn> columns = Maps.newHashMap();
	private final Map<String, String[]> uniqueConstraints = Maps.newHashMap();
	private final List<ForeignKey> foreignKeys = Lists.newArrayList();

	private final Map<EntityTypeImpl<?>, String> insertSqlMap = Maps.newHashMap();
	private final Map<EntityTypeImpl<?>, String> updateSqlMap = Maps.newHashMap();
	private String insertSql;
	private String updateSql;
	private String versionUpdateSql;
	private String versionSelectSql;

	private AbstractColumn[] insertColumns;
	private AbstractColumn[] updateColumns;
	private AbstractColumn[] versionUpdateColumns;
	private PkColumn[] selectVersionColumns;
	private final Map<EntityTypeImpl<?>, AbstractColumn[]> insertColumnsMap = Maps.newHashMap();

	private final Map<EntityTypeImpl<?>, AbstractColumn[]> updateColumnsMap = Maps.newHashMap();

	/**
	 * @param defaultName
	 *            the default name for the table
	 * @param metadata
	 *            the metadata
	 * 
	 * @since $version
	 * @author hceylan
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
	 * @since $version
	 * @author hceylan
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
	 * @since $version
	 * @author hceylan
	 */
	public void addColumn(AbstractColumn column) {
		final AbstractColumn existing = this.columns.put(column.getMappingName(), column);

		if (existing != null) {
			throw new MappingException("Duplicate column names " + column.getMappingName() + " on table " + this.name, column.getLocator(),
				existing.getLocator());
		}
	}

	/**
	 * Adds a foreign key to the table.
	 * 
	 * @param foreignKey
	 *            the foreign key to add
	 * 
	 * @since $version
	 * @author hceylan
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
	 * @since $version
	 * @author hceylan
	 */
	private synchronized void generateInsertSql(final EntityTypeImpl<?> type) {
		String sql = type != null ? this.insertSqlMap.get(type) : this.insertSql;
		if (sql != null) { // other thread finished the job for us
			return;
		}

		final List<AbstractColumn> insertColumns = Lists.newArrayList();
		// Filter out the identity physicalColumns
		final Collection<AbstractColumn> filteredColumns = type == null ? this.getColumns() : Collections2.filter(this.getColumns(),
			new Predicate<AbstractColumn>() {

				@Override
				public boolean apply(AbstractColumn input) {
					if ((input instanceof PkColumn) && (((PkColumn) input).getIdType() == IdType.IDENTITY)) {
						return false;
					}

					if (input instanceof DiscriminatorColumn) {
						return true;
					}

					final EntityTypeImpl<?> root;

					if ((input instanceof JoinColumn) && (input.getMapping() == null)) {
						root = (EntityTypeImpl<?>) ((JoinColumn) input).getReferencedMapping().getRoot().getType();
					}
					else {
						root = (EntityTypeImpl<?>) input.getMapping().getRoot().getType();
					}

					final Class<?> parent = root.getJavaType();
					final Class<?> javaType = type.getJavaType();

					return parent.isAssignableFrom(javaType);
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

		// prepare the parameters in the form of "? [, ?]*"
		final Collection<String> parameters = Collections2.transform(filteredColumns, new Function<AbstractColumn, String>() {

			@Override
			public String apply(AbstractColumn input) {
				return "?";
			}
		});

		final String columnNamesStr = Joiner.on(", ").join(columnNames);
		final String parametersStr = Joiner.on(", ").join(parameters);

		// INSERT INTO SCHEMA.TABLE
		// (COL [, COL]*)
		// VALUES (PARAM [, PARAM]*)
		sql = "INSERT INTO " + this.getQName() //
			+ "\n(" + columnNamesStr + ")"//
			+ "\nVALUES (" + parametersStr + ")";

		if (type != null) {
			this.insertSqlMap.put(type, sql);
			this.insertColumnsMap.put(type, insertColumns.toArray(new AbstractColumn[insertColumns.size()]));
		}
		else {
			this.insertSql = sql;
			this.insertColumns = insertColumns.toArray(new AbstractColumn[insertColumns.size()]);
		}
	}

	/**
	 * Generates the version select statement for the type.
	 * 
	 * @since $version
	 * @author hceylan
	 * @param pkColumns
	 */
	private synchronized void generateSelectVersionSql(Map<String, PkColumn> pkColumns) {
		if (this.versionSelectSql != null) {
			return;
		}

		AbstractColumn versionColumn = null;

		for (final AbstractColumn column : this.getColumns()) {
			final AttributeImpl<?, ?> attribute = column.getMapping().getAttribute();

			if ((attribute instanceof BasicAttribute) && ((BasicAttribute<?, ?>) attribute).isVersion()) {
				versionColumn = column;

				break;
			}
		}

		final List<PkColumn> selectVersionColumns = Lists.newArrayList();

		final Collection<String> restrictions = Collections2.transform(pkColumns.values(), new Function<PkColumn, String>() {

			@Override
			public String apply(PkColumn input) {
				selectVersionColumns.add(input);

				return input.getName() + " = ?";
			}
		});

		final String restrictionStr = Joiner.on(" AND ").join(restrictions);

		// SELECT VERSION_COLUMN FROM SCHEMA.TABLE SET
		// WHERE (PARAM [, PARAM]*)
		this.versionSelectSql = "SELECT " + versionColumn.getName() + " FROM " + this.getQName() //
			+ "\nWHERE " + restrictionStr;

		this.selectVersionColumns = selectVersionColumns.toArray(new PkColumn[selectVersionColumns.size()]);
	}

	/**
	 * Generates the update statement for the type.
	 * 
	 * @param type
	 *            the type to generate the update statement for
	 * 
	 * @since $version
	 * @author hceylan
	 * @param pkColumns
	 */
	private synchronized void generateUpdateSql(final EntityTypeImpl<?> type, Map<String, PkColumn> pkColumns) {
		String sql = type != null ? this.updateSqlMap.get(type) : this.insertSql;
		if (sql != null) { // other thread finished the job for us
			return;
		}

		final List<AbstractColumn> updateColumns = Lists.newArrayList();
		// Filter out the identity physicalColumns
		final Collection<AbstractColumn> filteredColumns = type == null ? this.getColumns() : Collections2.filter(this.getColumns(),
			new Predicate<AbstractColumn>() {

				@Override
				public boolean apply(AbstractColumn input) {
					if (input instanceof PkColumn) {
						return true;
					}

					final EntityTypeImpl<?> root;

					if ((input instanceof JoinColumn) && (input.getMapping() == null)) {
						root = (EntityTypeImpl<?>) ((JoinColumn) input).getReferencedMapping().getRoot().getType();
					}
					else {
						root = (EntityTypeImpl<?>) input.getMapping().getRoot().getType();
					}

					final Class<?> parent = root.getJavaType();
					final Class<?> javaType = type.getJavaType();

					return parent.isAssignableFrom(javaType);
				}
			});

		// prepare the names tuple in the form of "COLNAME = ? [, COLNAME = ?]*"
		final Collection<String> columnNames = Collections2.transform(filteredColumns, new Function<AbstractColumn, String>() {

			@Override
			public String apply(AbstractColumn input) {
				if (!(input instanceof PkColumn)) {
					updateColumns.add(input);

					return input.getName() + " = ?";
				}

				return null;
			}
		});

		final Collection<String> restrictions = Collections2.transform(pkColumns.values(), new Function<PkColumn, String>() {

			@Override
			public String apply(PkColumn input) {
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
	 * Generates the version update statement for the type.
	 * 
	 * @since $version
	 * @author hceylan
	 * @param pkColumns
	 */
	private synchronized void generateVersionUpdateSql(Map<String, PkColumn> pkColumns) {
		if (this.versionUpdateSql != null) {
			return;
		}

		final List<AbstractColumn> versionUpdateColumns = Lists.newArrayList();

		for (final AbstractColumn column : this.getColumns()) {
			final AttributeImpl<?, ?> attribute = column.getMapping().getAttribute();

			if ((attribute instanceof BasicAttribute) && ((BasicAttribute<?, ?>) attribute).isVersion()) {
				versionUpdateColumns.add(column);

				break;
			}
		}

		final Collection<String> restrictions = Collections2.transform(pkColumns.values(), new Function<PkColumn, String>() {

			@Override
			public String apply(PkColumn input) {
				versionUpdateColumns.add(input);

				return input.getName() + " = ?";
			}
		});

		final String columnNamesStr = versionUpdateColumns.get(0).getName() + " = ?";
		final String restrictionStr = Joiner.on(" AND ").join(restrictions);

		// UPDATE SCHEMA.TABLE SET
		// (COL [, COL]*)
		// VALUES (PARAM [, PARAM]*)
		this.versionUpdateSql = "UPDATE " + this.getQName() + " SET"//
			+ "\n" + columnNamesStr //
			+ "\nWHERE " + restrictionStr;

		this.versionUpdateColumns = versionUpdateColumns.toArray(new AbstractColumn[versionUpdateColumns.size()]);
	}

	/**
	 * Returns the catalog.
	 * 
	 * @return the catalog
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public String getCatalog() {
		return this.catalog;
	}

	/**
	 * Returns the collection of basicColumns of the table.
	 * 
	 * @return the collection of basicColumns of the table
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public Collection<AbstractColumn> getColumns() {
		return this.columns.values();
	}

	/**
	 * Returns the foreign keys of the table.
	 * 
	 * @return the foreign keys of the table
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public List<ForeignKey> getForeignKeys() {
		return this.foreignKeys;
	}

	/**
	 * Returns the columns for the insert.
	 * 
	 * @param entity
	 *            the entity to returns columns for or null for generic columns
	 * @return the insert columns
	 * 
	 * @since $version
	 * @author hceylan
	 */
	protected AbstractColumn[] getInsertColumns(final EntityTypeImpl<?> entity) {
		if (entity == null) {
			return this.insertColumns;
		}

		return this.insertColumnsMap.get(entity);
	}

	/**
	 * Returns the insert statement for the table specifically.
	 * 
	 * @param entity
	 *            the entity to return insert statement for or null for generic SQL
	 * @return the insert statement
	 * 
	 * @since $version
	 * @author hceylan
	 */
	protected String getInsertSql(EntityTypeImpl<?> entity) {
		if (entity == null) {
			if (this.insertSql == null) {
				this.generateInsertSql(null);
			}

			return this.insertSql;
		}

		String sql = this.insertSqlMap.get(entity);
		if (sql == null) {
			this.generateInsertSql(entity);

			sql = this.insertSqlMap.get(entity);
		}

		return sql;
	}

	/**
	 * Returns the locator.
	 * 
	 * @return the locator
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public AbstractLocator getLocator() {
		return this.locator;
	}

	/**
	 * Returns the name.
	 * 
	 * @return the name
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Returns the qualified name of the table.
	 * 
	 * @return the qualified name of the table
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public String getQName() {
		return Joiner.on(".").skipNulls().join(this.schema, this.name);
	}

	/**
	 * Returns the schema.
	 * 
	 * @return the schema
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public String getSchema() {
		return this.schema;
	}

	/**
	 * Returns the select version columns.
	 * 
	 * @return the select version columns
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public PkColumn[] getSelectVersionColumns() {
		return this.selectVersionColumns;
	}

	/**
	 * Returns the version select statement for the table specifically.
	 * 
	 * @param pkColumns
	 *            the primary key columns
	 * @return the select statement
	 * 
	 * @since $version
	 * @author hceylan
	 */
	protected String getSelectVersionSql(Map<String, PkColumn> pkColumns) {
		if (this.versionSelectSql == null) {
			this.generateSelectVersionSql(pkColumns);
		}

		return this.versionSelectSql;
	}

	/**
	 * Returns the unique constraints.
	 * 
	 * @return the unique constraints
	 * 
	 * @since $version
	 * @author hceylan
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
	 * @since $version
	 * @author hceylan
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
	 * @since $version
	 * @author hceylan
	 */
	protected String getUpdateSql(EntityTypeImpl<?> entity, Map<String, PkColumn> pkColumns) {
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
	 * @since $version
	 * @author hceylan
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
	 * @since $version
	 * @author hceylan
	 */
	protected String getVersionUpdateSql(Map<String, PkColumn> pkColumns) {
		if (this.updateSql == null) {
			this.generateVersionUpdateSql(pkColumns);
		}

		return this.updateSql;
	}

	/**
	 * Updates the name of the table.
	 * 
	 * @param name
	 *            the name of the table
	 * 
	 * @since $version
	 * @author hceylan
	 */
	protected void setName(String name) {
		this.name = name;
	}
}
