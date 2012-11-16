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
import org.batoo.jpa.core.impl.model.mapping.Mapping;
import org.batoo.jpa.core.impl.model.type.EntityTypeImpl;
import org.batoo.jpa.core.jdbc.adapter.JdbcAdaptor;
import org.batoo.jpa.parser.metadata.JoinColumnMetadata;
import org.batoo.jpa.parser.metadata.PrimaryKeyJoinColumnMetadata;

/**
 * Mapping of two {@link BasicColumn}s.
 * 
 * @author hceylan
 * @since 2.0.0
 */
public class JoinColumn extends AbstractColumn {

	private final JdbcAdaptor jdbcAdaptor;
	private AbstractTable table;

	private String name;
	private String referencedColumnName;

	private final String tableName;
	private final boolean insertable;
	private final boolean nullable;
	private final boolean unique;
	private final boolean updatable;

	private String columnDefinition;

	private int length;
	private int precision;
	private int sqlType;
	private int scale;
	private AssociationMapping<?, ?, ?> mapping;

	private AbstractColumn referencedColumn;
	private AbstractColumn masterColumn;

	/**
	 * Constructor with no metadata for mappings with MapsId.
	 * 
	 * @param jdbcAdaptor
	 *            the JDBC Adaptor
	 * @param mapping
	 *            the mapping
	 * @param referencedColumn
	 *            the referenced column
	 * @param masterColumn
	 *            the master column
	 * 
	 * @since 2.0.0
	 */
	public JoinColumn(JdbcAdaptor jdbcAdaptor, AssociationMapping<?, ?, ?> mapping, AbstractColumn referencedColumn, BasicColumn masterColumn) {
		super(null, false);

		this.jdbcAdaptor = jdbcAdaptor;
		this.mapping = mapping;

		this.columnDefinition = "";
		this.tableName = "";
		this.insertable = true;
		this.nullable = true;
		this.unique = false;
		this.updatable = true;

		this.name = masterColumn.getName();
		this.masterColumn = masterColumn;

		this.setColumnProperties(mapping, referencedColumn, false);
	}

	/**
	 * Constructor with no metadata.
	 * 
	 * @param jdbcAdaptor
	 *            the JDBC Adaptor
	 * @param mapping
	 *            the mapping
	 * @param referencedColumn
	 *            the referenced column
	 * @param id
	 *            if the column is id
	 * 
	 * @since 2.0.0
	 */
	public JoinColumn(JdbcAdaptor jdbcAdaptor, AssociationMapping<?, ?, ?> mapping, AbstractColumn referencedColumn, boolean id) {
		super(null, id);

		this.jdbcAdaptor = jdbcAdaptor;
		this.mapping = mapping;

		this.columnDefinition = "";
		this.tableName = "";
		this.insertable = true;
		this.nullable = true;
		this.unique = false;
		this.updatable = true;

		this.setColumnProperties(mapping, referencedColumn, id);
	}

	/**
	 * Constructor with metadata.
	 * 
	 * @param jdbcAdaptor
	 *            the JDBC adaptor
	 * @param metadata
	 *            the metadata for the join
	 * @param readOnly
	 *            if the column is readonly
	 * 
	 * @since 2.0.0
	 */
	public JoinColumn(JdbcAdaptor jdbcAdaptor, JoinColumnMetadata metadata, boolean readOnly) {
		super(metadata.getLocator(), false);

		this.jdbcAdaptor = jdbcAdaptor;

		this.referencedColumnName = metadata.getReferencedColumnName();
		this.columnDefinition = metadata.getColumnDefinition();
		this.tableName = metadata.getTable();
		this.name = metadata.getName();
		this.insertable = !readOnly && metadata.isInsertable();
		this.nullable = metadata.isNullable();
		this.unique = metadata.isUnique();
		this.updatable = !readOnly && metadata.isUpdatable();
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
	 * @param referencedColumn
	 *            the referenced column
	 * 
	 * @since 2.0.0
	 */
	public JoinColumn(JdbcAdaptor jdbcAdaptor, PrimaryKeyJoinColumnMetadata metadata, SecondaryTable table, AbstractColumn referencedColumn) {
		super(metadata != null ? metadata.getLocator() : null, false);

		this.jdbcAdaptor = jdbcAdaptor;
		this.tableName = table.getName();

		this.referencedColumnName = referencedColumn.getName();
		this.columnDefinition = metadata != null ? metadata.getColumnDefinition() : referencedColumn.getColumnDefinition();
		this.name = metadata != null ? jdbcAdaptor.escape(metadata.getName()) : referencedColumn.getName();

		this.insertable = true;
		this.nullable = false;
		this.unique = false;
		this.updatable = false;

		this.setColumnProperties(referencedColumn);
		this.setTable(table);
	}

	/**
	 * Returns the columnDefinition of the JoinColumn.
	 * 
	 * @return the columnDefinition of the JoinColumn
	 * 
	 * @since 2.0.0
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
	 * Returns the master column.
	 * <p>
	 * Master column is the delegate column for virtual join columns.
	 * 
	 * @return the master column
	 * 
	 * @since 2.0.0
	 */
	public AbstractColumn getMasterColumn() {
		return this.masterColumn;
	}

	/**
	 * Returns the name of the JoinColumn.
	 * 
	 * @return the name of the JoinColumn
	 * 
	 * @since 2.0.0
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
	 * Returns the referenced column of the join column.
	 * 
	 * @return the referenced column of the join column
	 * 
	 * @since 2.0.0
	 */
	public AbstractColumn getReferencedColumn() {
		return this.referencedColumn;
	}

	/**
	 * Returns the name of the referenced column of the join column.
	 * 
	 * @return the name of the referenced column of the join column
	 * 
	 * @since 2.0.0
	 */
	public String getReferencedColumnName() {
		return this.referencedColumnName;
	}

	/**
	 * Returns the referenced table.
	 * 
	 * @return the referenced table
	 * 
	 * @since 2.0.0
	 */
	public AbstractTable getReferencedTable() {
		return this.referencedColumn.getTable();
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
		final Object value = this.mapping != null ? this.mapping.get(instance) : instance;

		return value != null ? this.referencedColumn.getValue(connection, value) : null;
	}

	/**
	 * Returns the insertable of the JoinColumn.
	 * 
	 * @return the insertable of the JoinColumn
	 * 
	 * @since 2.0.0
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
	 * @since 2.0.0
	 */
	@Override
	public boolean isNullable() {
		return this.nullable;
	}

	/**
	 * Returns the unique of the JoinColumn.
	 * 
	 * @return the unique of the JoinColumn
	 * 
	 * @since 2.0.0
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
	 * @since 2.0.0
	 */
	@Override
	public boolean isUpdatable() {
		return this.updatable;
	}

	/**
	 * Returns if this join column is a virtual join column.
	 * <p>
	 * Virtual join columns actually do not have their own storage, but rather references an existing id basic column.
	 * 
	 * @return true if this join column is a virtual join column, false otherwise
	 * 
	 * @since 2.0.0
	 */
	public boolean isVirtual() {
		return this.masterColumn != null;
	}

	private void setColumnProperties(AbstractColumn referencedColumn) {
		this.referencedColumn = referencedColumn;

		this.referencedColumnName = referencedColumn.getName();
		this.columnDefinition = referencedColumn.getColumnDefinition();
		this.sqlType = referencedColumn.getSqlType();
		this.length = referencedColumn.getLength();
		this.precision = referencedColumn.getPrecision();
		this.scale = referencedColumn.getScale();
	}

	/**
	 * Sets the column definition for virtual foreign column.
	 * 
	 * @param mapping
	 *            the owner mapping
	 * @param referencedColumn
	 *            the refernced column
	 * @param masterColumn
	 *            the master column
	 * 
	 * @since 2.0.0
	 */
	public void setColumnProperties(AssociationMapping<?, ?, ?> mapping, AbstractColumn referencedColumn, AbstractColumn masterColumn) {
		this.masterColumn = masterColumn;

		// if attribute present then the join column belongs to an entity table
		if (mapping != null) {
			this.mapping = mapping;

			if (StringUtils.isBlank(this.name)) {
				this.name = this.jdbcAdaptor.escape(mapping.getName() + "_" + referencedColumn.getName());
			}
		}
		else {
			final EntityTypeImpl<?> type = (EntityTypeImpl<?>) referencedColumn.getMapping().getRoot().getType();

			if (StringUtils.isBlank(this.name)) {
				this.name = this.jdbcAdaptor.escape(type.getName() + "_" + referencedColumn.getName());
			}
		}

		this.setColumnProperties(referencedColumn);
	}

	/**
	 * Sets the column definition.
	 * 
	 * @param mapping
	 *            the owner mapping
	 * @param referencedColumn
	 *            the referenced column
	 * @param id
	 *            if the column is id column
	 * 
	 * @since 2.0.0
	 */
	public void setColumnProperties(AssociationMapping<?, ?, ?> mapping, AbstractColumn referencedColumn, boolean id) {
		// if attribute present then the join column belongs to an entity table
		if (mapping != null) {
			this.mapping = mapping;

			if (StringUtils.isBlank(this.name)) {
				this.name = this.jdbcAdaptor.escape(mapping.getName() + "_" + referencedColumn.getName());
			}
		}
		else {
			final EntityTypeImpl<?> type = (EntityTypeImpl<?>) referencedColumn.getMapping().getRoot().getType();

			if (StringUtils.isBlank(this.name)) {
				this.name = this.jdbcAdaptor.escape(type.getName() + "_" + referencedColumn.getName());
			}
		}

		if (id) {
			super.setId();
		}

		this.setColumnProperties(referencedColumn);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void setTable(AbstractTable table) {
		this.table = table;

		if (!this.isVirtual()) {
			this.table.addColumn(this);
		}
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

	/**
	 * Sets the master column of the join column.
	 * 
	 * @param masterColumn
	 *            the master column
	 * 
	 * 
	 * @since 2.0.0
	 */
	public void setVirtual(AbstractColumn masterColumn) {
		this.masterColumn = masterColumn;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String toString() {
		final String tableName = this.getTable() != null ? this.getTable().getName() : "N/A";
		final String mapping = this.getMapping() != null ? " " + this.getMapping().toString() + " " : "";

		return this.getClass().getSimpleName() + mapping + " [name=" + this.getName() + ", type=" + this.getSqlType() + ", length=" + this.getLength()
			+ ", precision=" + this.getPrecision() + ", scale=" + this.getScale() + ", table=" + tableName + ", refrencedColumn=" + this.referencedColumn + "]";
	}
}
