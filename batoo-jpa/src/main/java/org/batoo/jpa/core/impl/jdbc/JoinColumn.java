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

import org.apache.commons.lang.StringUtils;
import org.batoo.jpa.core.impl.model.mapping.AssociationMapping;
import org.batoo.jpa.core.impl.model.mapping.BasicMapping;
import org.batoo.jpa.core.impl.model.mapping.Mapping;
import org.batoo.jpa.core.impl.model.type.EntityTypeImpl;
import org.batoo.jpa.core.jdbc.adapter.JdbcAdaptor;
import org.batoo.jpa.parser.metadata.JoinColumnMetadata;
import org.batoo.jpa.parser.metadata.PrimaryKeyJoinColumnMetadata;

/**
 * Mapping of two {@link BasicColumn}s.
 * 
 * @author hceylan
 * @since $version
 */
public class JoinColumn extends AbstractColumn {

	private final JdbcAdaptor jdbcAdaptor;
	private AbstractTable table;

	private final boolean primaryKey;
	private String mappingName;
	private String name;
	private String referencedColumnName;

	private final String columnDefinition;
	private final String tableName;
	private final boolean insertable;
	private final boolean nullable;
	private final boolean unique;
	private final boolean updatable;

	private int length;
	private int precision;
	private int sqlType;
	private int scale;

	private AssociationMapping<?, ?, ?> mapping;
	private BasicMapping<?, ?> referencedMapping;

	/**
	 * Constructor with no metadata.
	 * 
	 * @param jdbcAdaptor
	 *            the JDBC Adaptor
	 * @param mapping
	 *            the mapping
	 * @param idMapping
	 *            the referenced mapping
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public JoinColumn(JdbcAdaptor jdbcAdaptor, AssociationMapping<?, ?, ?> mapping, BasicMapping<?, ?> idMapping) {
		super(null);

		this.jdbcAdaptor = jdbcAdaptor;
		this.mapping = mapping;
		this.primaryKey = false;

		this.columnDefinition = "";
		this.tableName = "";
		this.insertable = true;
		this.nullable = true;
		this.unique = false;
		this.updatable = true;

		this.setColumnProperties(mapping, idMapping);
	}

	/**
	 * Constructor for inheritance and secondary table joins.
	 * 
	 * @param jdbcAdaptor
	 *            the JDBC Adaptor
	 * @param table
	 *            the table
	 * @param idMapping
	 *            the referenced id mapping
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public JoinColumn(JdbcAdaptor jdbcAdaptor, EntityTable table, BasicMapping<?, ?> idMapping) {
		super(null);

		this.jdbcAdaptor = jdbcAdaptor;
		this.referencedMapping = idMapping;
		this.primaryKey = false;

		final PkColumn referencedColumn = (PkColumn) idMapping.getColumn();

		this.referencedColumnName = referencedColumn.getName();
		this.columnDefinition = referencedColumn.getColumnDefinition();
		this.tableName = table.getName();
		this.mappingName = referencedColumn.getMappingName();
		this.name = referencedColumn.getName();
		this.insertable = referencedColumn.isInsertable();
		this.nullable = referencedColumn.isNullable();
		this.unique = referencedColumn.isUnique();
		this.updatable = referencedColumn.isUnique();

		this.setColumnProperties(idMapping);
		this.setTable(table);
	}

	/**
	 * Constructor with metadata.
	 * 
	 * @param jdbcAdaptor
	 *            the JDBC adaptor
	 * @param metadata
	 *            the metadata for the join
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public JoinColumn(JdbcAdaptor jdbcAdaptor, JoinColumnMetadata metadata) {
		super(metadata.getLocator());

		this.jdbcAdaptor = jdbcAdaptor;
		this.primaryKey = false;

		this.referencedColumnName = metadata.getReferencedColumnName();
		this.columnDefinition = metadata.getColumnDefinition();
		this.tableName = metadata.getTable();
		this.name = metadata.getName();
		this.insertable = metadata.isInsertable();
		this.nullable = metadata.isNullable();
		this.unique = metadata.isUnique();
		this.updatable = metadata.isUpdatable();
	}

	/**
	 * Constructor for secondary table join column with metadata.
	 * 
	 * @param jdbcAdaptor
	 *            the JDBC Adaptor
	 * @param metadata
	 *            the metadata for the join
	 * @param table
	 *            the secondary table
	 * @param idMapping
	 *            the id mapping
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public JoinColumn(JdbcAdaptor jdbcAdaptor, PrimaryKeyJoinColumnMetadata metadata, SecondaryTable table, BasicMapping<?, ?> idMapping) {
		super(metadata.getLocator());

		this.jdbcAdaptor = jdbcAdaptor;
		this.tableName = table.getName();
		this.primaryKey = false;

		this.referencedColumnName = metadata.getReferencedColumnName();
		this.columnDefinition = metadata.getColumnDefinition();
		this.mappingName = metadata.getName();

		this.insertable = true;
		this.nullable = false;
		this.unique = false;
		this.updatable = false;

		this.setColumnProperties(idMapping);
		this.setTable(table);
	}

	/**
	 * Constructor for inheritance and secondary table joins.
	 * 
	 * @param jdbcAdaptor
	 *            the JDBC Adaptor
	 * @param table
	 *            the table
	 * @param idMapping
	 *            the referenced id mapping
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public JoinColumn(JdbcAdaptor jdbcAdaptor, SecondaryTable table, BasicMapping<?, ?> idMapping) {
		super(null);

		this.jdbcAdaptor = jdbcAdaptor;
		this.referencedMapping = idMapping;
		this.primaryKey = true;

		final PkColumn referencedColumn = (PkColumn) idMapping.getColumn();

		this.referencedColumnName = referencedColumn.getName();
		this.columnDefinition = referencedColumn.getColumnDefinition();
		this.tableName = table.getName();
		this.mappingName = referencedColumn.getMappingName();
		this.name = referencedColumn.getName();
		this.insertable = referencedColumn.isInsertable();
		this.nullable = referencedColumn.isNullable();
		this.unique = referencedColumn.isUnique();
		this.updatable = referencedColumn.isUnique();

		this.setColumnProperties(idMapping);
		this.setTable(table);

	}

	/**
	 * Returns the columnDefinition of the JoinColumn.
	 * 
	 * @return the columnDefinition of the JoinColumn
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Override
	public String getColumnDefinition() {
		return this.columnDefinition;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public int getLength() {
		return this.length;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Mapping<?, ?, ?> getMapping() {
		return this.mapping;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getMappingName() {
		return this.mappingName;
	}

	/**
	 * Returns the name of the JoinColumn.
	 * 
	 * @return the name of the JoinColumn
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Override
	public String getName() {
		return this.name;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public int getPrecision() {
		return this.precision;
	}

	/**
	 * Returns the name of the referenced column of the join column.
	 * 
	 * @return the name of the referenced column of the join column
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public String getReferencedColumnName() {
		return this.referencedColumnName;
	}

	/**
	 * Returns the referencedMapping of the JoinColumn.
	 * 
	 * @return the referencedMapping of the JoinColumn
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public BasicMapping<?, ?> getReferencedMapping() {
		return this.referencedMapping;
	}

	/**
	 * Returns the referenced table.
	 * 
	 * @return the referenced table
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public AbstractTable getReferencedTable() {
		return this.referencedMapping.getColumn().getTable();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public int getScale() {
		return this.scale;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public int getSqlType() {
		return this.sqlType;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public AbstractTable getTable() {
		return this.table;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getTableName() {
		return this.tableName;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Object getValue(Connection connection, Object instance) {
		if (this.mapping != null) {
			instance = this.mapping.get(instance);
		}

		return instance != null ? this.referencedMapping.get(instance) : null;
	}

	/**
	 * Returns the insertable of the JoinColumn.
	 * 
	 * @return the insertable of the JoinColumn
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Override
	public boolean isInsertable() {
		return this.insertable;
	}

	/**
	 * Returns the nullable of the JoinColumn.
	 * 
	 * @return the nullable of the JoinColumn
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Override
	public boolean isNullable() {
		return this.nullable;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean isPrimaryKey() {
		return this.primaryKey;
	}

	/**
	 * Returns the unique of the JoinColumn.
	 * 
	 * @return the unique of the JoinColumn
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Override
	public boolean isUnique() {
		return this.unique;
	}

	/**
	 * Returns the updatable of the JoinColumn.
	 * 
	 * @return the updatable of the JoinColumn
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Override
	public boolean isUpdatable() {
		return this.updatable;
	}

	/**
	 * Sets the column definition.
	 * 
	 * @param mapping
	 *            the owner mapping
	 * @param referencedMapping
	 *            the referenced primary key Column
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void setColumnProperties(AssociationMapping<?, ?, ?> mapping, BasicMapping<?, ?> referencedMapping) {
		// if attribute present then the join column belongs to an entity table
		if (mapping != null) {
			this.mapping = mapping;
			if (StringUtils.isBlank(this.name)) {
				this.mappingName = mapping.getName() + "_" + referencedMapping.getColumn().getName();
				this.name = this.jdbcAdaptor.escape(this.mappingName);
			}
			else {
				this.mappingName = this.name;
			}
		}
		else {
			final EntityTypeImpl<?> type = (EntityTypeImpl<?>) referencedMapping.getRoot().getType();
			if (StringUtils.isBlank(this.name)) {
				this.mappingName = type.getName() + "_" + referencedMapping.getColumn().getName();
				this.name = this.jdbcAdaptor.escape(this.mappingName);
			}
			else {
				this.mappingName = this.name;
			}
		}

		this.setColumnProperties(referencedMapping);
	}

	private void setColumnProperties(BasicMapping<?, ?> referencedMapping) {
		this.referencedMapping = referencedMapping;
		final PkColumn referencedColumn = (PkColumn) referencedMapping.getColumn();

		this.referencedColumnName = referencedColumn.getName();
		this.sqlType = referencedColumn.getSqlType();
		this.length = referencedColumn.getLength();
		this.precision = referencedColumn.getPrecision();
		this.scale = referencedColumn.getScale();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void setTable(AbstractTable table) {
		this.table = table;

		this.table.addColumn(this);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void setValue(Object instance, Object value) {
		if (this.mapping != null) {
			this.mapping.set(instance, value);
		}
	}
}
