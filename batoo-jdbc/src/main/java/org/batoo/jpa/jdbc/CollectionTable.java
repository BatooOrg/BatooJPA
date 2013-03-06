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
package org.batoo.jpa.jdbc;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import javax.persistence.EnumType;
import javax.persistence.TemporalType;

import org.apache.commons.lang.StringUtils;
import org.batoo.common.util.FinalWrapper;
import org.batoo.jpa.jdbc.adapter.JdbcAdaptor;
import org.batoo.jpa.jdbc.dbutils.QueryRunner;
import org.batoo.jpa.jdbc.mapping.ElementCollectionMapping;
import org.batoo.jpa.jdbc.mapping.RootMapping;
import org.batoo.jpa.jdbc.model.EmbeddableTypeDescriptor;
import org.batoo.jpa.jdbc.model.EntityTypeDescriptor;
import org.batoo.jpa.jdbc.model.TypeDescriptor;
import org.batoo.jpa.parser.AbstractLocator;
import org.batoo.jpa.parser.metadata.CollectionTableMetadata;
import org.batoo.jpa.parser.metadata.ColumnMetadata;
import org.batoo.jpa.parser.metadata.JoinColumnMetadata;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

/**
 * The table for element collection attributes.
 * 
 * @author hceylan
 * @since 2.0.0
 */
public class CollectionTable extends AbstractTable implements JoinableTable {

	private final JdbcAdaptor jdbcAdaptor;
	private final ForeignKey key;

	private OrderColumn orderColumn;
	private ElementColumn elementColumn;

	private FinalWrapper<String> removeSql;
	private FinalWrapper<String> removeAllSql;

	private AbstractColumn[] removeColumns;
	private JoinColumn[] removeAllColumns;
	private MapKeyColumn keyColumn;
	private final ElementCollectionMapping<?, ?, ?> mapping;

	/**
	 * @param jdbcAdaptor
	 *            the JDBC adaptor
	 * @param mapping
	 *            the owner mapping
	 * @param metadata
	 *            the metadata
	 * 
	 * @since 2.0.0
	 */
	public CollectionTable(JdbcAdaptor jdbcAdaptor, ElementCollectionMapping<?, ?, ?> mapping, CollectionTableMetadata metadata) {
		super(metadata);

		this.jdbcAdaptor = jdbcAdaptor;
		this.mapping = mapping;
		this.key = new ForeignKey(this.jdbcAdaptor, this.mapping, //
			metadata != null ? metadata.getJoinColumns() : Collections.<JoinColumnMetadata> emptyList());
	}

	/**
	 * Returns the element column of the collection table.
	 * 
	 * @return the element column of the collection table
	 * 
	 * @since $version
	 */
	public ElementColumn getElementColumn() {
		return this.elementColumn;
	}

	/**
	 * Returns the key.
	 * 
	 * @return the key
	 * 
	 * @since 2.0.0
	 */
	public ForeignKey getKey() {
		return this.key;
	}

	/**
	 * Returns the key column of the collection table.
	 * 
	 * @return the key column of the collection table
	 * 
	 * @since 2.0.0
	 */
	public MapKeyColumn getKeyColumn() {
		return this.keyColumn;
	}

	/**
	 * Returns the mapping of the collection table.
	 * 
	 * @return the mapping of the collection table
	 * 
	 * @since $version
	 */
	public ElementCollectionMapping<?, ?, ?> getMapping() {
		return this.mapping;
	}

	/**
	 * Returns the order column.
	 * 
	 * @return the order column
	 * 
	 * @since 2.0.0
	 */
	public OrderColumn getOrderColumn() {
		return this.orderColumn;
	}

	private String getRemoveAllSql() {
		FinalWrapper<String> wrapper = this.removeAllSql;

		if (wrapper == null) {
			synchronized (this) {
				if (this.removeAllSql == null) {

					final List<String> restrictions = Lists.newArrayList();
					this.removeAllColumns = new JoinColumn[this.key.getJoinColumns().size()];

					int i = 0;
					for (final JoinColumn column : this.key.getJoinColumns()) {
						restrictions.add(column.getName() + " = ?");
						this.removeAllColumns[i++] = column;
					}

					this.removeAllSql = new FinalWrapper<String>("DELETE FROM " + this.getQName() + " WHERE " + Joiner.on(" AND ").join(restrictions));
				}

				wrapper = this.removeAllSql;
			}
		}

		return wrapper.value;
	}

	private String getRemoveSql() {
		FinalWrapper<String> wrapper = this.removeSql;

		if (wrapper == null) {
			synchronized (this) {
				if (this.removeSql == null) {

					final List<String> restrictions = Lists.newArrayList();
					this.removeColumns = new AbstractColumn[this.getColumns().length];

					int i = 0;
					for (final AbstractColumn column : this.getColumns()) {
						if (column != this.orderColumn) {
							restrictions.add(column.getName() + " = ?");
							this.removeColumns[i++] = column;
						}
					}

					this.removeSql = new FinalWrapper<String>("DELETE FROM " + this.getQName() + " WHERE " + Joiner.on(" AND ").join(restrictions));
				}
				wrapper = this.removeSql;
			}
		}

		return wrapper.value;
	}

	/**
	 * Links the collection table.
	 * 
	 * @param entity
	 *            the entity
	 * @param type
	 *            the type of the collection
	 * @param defaultName
	 *            the default name
	 * @param elementMapping
	 *            the element mapping
	 * 
	 * @since 2.0.0
	 */
	public void link(EntityTypeDescriptor entity, EmbeddableTypeDescriptor type, String defaultName, RootMapping<?> elementMapping) {
		if (StringUtils.isBlank(this.getName())) {
			this.setName(defaultName);
		}

		this.key.link(null, entity);
		this.key.setTable(this);
	}

	/**
	 * Links the collection table.
	 * 
	 * @param entity
	 *            the root entity
	 * @param type
	 *            the type of the collection
	 * @param defaultName
	 *            the default name
	 * @param metadata
	 *            the column metadata
	 * @param lob
	 *            if the column is a lob type
	 * @param temporalType
	 *            the temporal type
	 * @param enumType
	 *            the enum type
	 * 
	 * @since 2.0.0
	 */
	public void link(EntityTypeDescriptor entity, TypeDescriptor type, String defaultName, ColumnMetadata metadata, EnumType enumType,
		TemporalType temporalType, boolean lob) {
		if (StringUtils.isBlank(this.getName())) {
			this.setName(entity.getName() + "_" + defaultName);
		}

		this.key.link(null, entity);
		this.key.setTable(this);

		this.elementColumn = new ElementColumn(this.jdbcAdaptor, //
			this.mapping, //
			this, //
			(metadata == null) || StringUtils.isBlank(metadata.getName()) ? defaultName : metadata.getName(), //
			type.getJavaType(), //
			enumType, //
			temporalType, //
			lob, //
			metadata);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void performInsert(Connection connection, Object source, Joinable[] batch, int size) throws SQLException {
		final String insertSql = this.getInsertSql(null, size);
		final AbstractColumn[] insertColumns = this.getInsertColumns(null, size);

		// prepare the parameters
		final Object[] params = new Object[insertColumns.length * size];

		boolean hasLob = false;
		int paramIndex = 0;
		for (int i = 0; i < size; i++) {
			for (final AbstractColumn column : insertColumns) {
				if (column == this.orderColumn) {
					params[paramIndex++] = batch[i].getIndex();
				}
				else if (column == this.keyColumn) {
					params[paramIndex++] = this.keyColumn.getValue(connection, batch[i].getKey());
				}
				else if (this.elementColumn == column) {
					params[paramIndex++] = this.elementColumn.getValue(connection, batch[i].getValue());
				}
				else if (column instanceof JoinColumn) {
					params[paramIndex++] = column.getValue(connection, source);
				}
				else {
					params[paramIndex++] = column.getValue(connection, batch[i].getValue());
				}

				hasLob |= column.isLob();
			}
		}

		new QueryRunner(this.jdbcAdaptor, hasLob).update(connection, insertSql, params);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void performRemove(Connection connection, Object source, Object key, Object destination) throws SQLException {
		final String removeSql = this.getRemoveSql();

		final Object[] params = new Object[this.removeColumns.length];

		boolean hasLob = false;
		int i = 0;
		for (final AbstractColumn column : this.removeColumns) {
			if (column instanceof ElementColumn) {
				params[i++] = column.getValue(connection, destination);
			}
			else if (column == this.keyColumn) {
				params[i++] = this.keyColumn.getValue(connection, key);;
			}
			else if (column instanceof JoinColumn) {
				params[i++] = column.getValue(connection, source);
			}
			else {
				params[i++] = column.getValue(connection, destination);
			}

			hasLob |= column.isLob();
		}

		new QueryRunner(this.jdbcAdaptor, hasLob).update(connection, removeSql, params);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void performRemoveAll(Connection connection, Object source) throws SQLException {
		final String removeAllSql = this.getRemoveAllSql();

		final Object[] params = new Object[this.removeAllColumns.length];

		int i = 0;
		for (final JoinColumn sourceRemoveColumn : this.removeAllColumns) {
			params[i++] = sourceRemoveColumn.getValue(connection, source);
		}

		new QueryRunner(this.jdbcAdaptor, false).update(connection, removeAllSql, params);
	}

	/**
	 * Sets the map key column.
	 * 
	 * @param mapKeyColumn
	 *            the map key column definition
	 * @param name
	 *            the name of the column
	 * @param mapKeyTemporalType
	 *            the temporal type of the map key
	 * @param mapKeyEnumType
	 *            the enum type of the map key
	 * @param mapKeyJavaType
	 *            the java type of the map's key
	 * 
	 * @since 2.0.0
	 */
	public void setKeyColumn(ColumnMetadata mapKeyColumn, String name, TemporalType mapKeyTemporalType, EnumType mapKeyEnumType, Class<?> mapKeyJavaType) {
		this.keyColumn = new MapKeyColumn(this, mapKeyColumn, name, mapKeyTemporalType, mapKeyEnumType, mapKeyJavaType);
	}

	/**
	 * Sets the order column for the owned list type joins
	 * 
	 * @param orderColumn
	 *            the order column definition
	 * @param name
	 *            the name of the column
	 * @param locator
	 *            the locator
	 * 
	 * @since 2.0.0
	 */
	public void setOrderColumn(ColumnMetadata orderColumn, String name, AbstractLocator locator) {
		this.orderColumn = new OrderColumn(this, orderColumn, name, locator);
	}
}
