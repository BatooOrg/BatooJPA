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
import java.util.Collections;
import java.util.List;

import javax.persistence.EnumType;
import javax.persistence.TemporalType;

import org.apache.commons.lang.StringUtils;
import org.batoo.jpa.core.impl.jdbc.dbutils.QueryRunner;
import org.batoo.jpa.core.impl.model.mapping.ElementCollectionMapping;
import org.batoo.jpa.core.impl.model.mapping.ElementMapping;
import org.batoo.jpa.core.impl.model.type.EmbeddableTypeImpl;
import org.batoo.jpa.core.impl.model.type.EntityTypeImpl;
import org.batoo.jpa.core.impl.model.type.TypeImpl;
import org.batoo.jpa.core.jdbc.adapter.JdbcAdaptor;
import org.batoo.jpa.parser.metadata.CollectionTableMetadata;
import org.batoo.jpa.parser.metadata.ColumnMetadata;
import org.batoo.jpa.parser.metadata.JoinColumnMetadata;
import org.batoo.jpa.util.FinalWrapper;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

/**
 * The table for element collection attributes.
 * 
 * @author hceylan
 * @since $version
 */
public class CollectionTable extends AbstractTable implements JoinableTable {

	private final JdbcAdaptor jdbcAdaptor;
	private final EntityTypeImpl<?> entity;
	private final ForeignKey key;

	private OrderColumn orderColumn;
	private ElementColumn elementColumn;

	private FinalWrapper<String> removeSql;
	private FinalWrapper<String> removeAllSql;

	private AbstractColumn[] removeColumns;
	private JoinColumn[] removeAllColumns;
	private MapKeyColumn keyColumn;

	/**
	 * @param entity
	 *            the owner type
	 * @param metadata
	 *            the metadata
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public CollectionTable(EntityTypeImpl<?> entity, CollectionTableMetadata metadata) {
		super(metadata);

		this.entity = entity;
		this.jdbcAdaptor = entity.getMetamodel().getJdbcAdaptor();
		this.key = new ForeignKey(entity.getMetamodel().getJdbcAdaptor(), //
			metadata != null ? metadata.getJoinColumns() : Collections.<JoinColumnMetadata> emptyList());
	}

	/**
	 * Returns the key.
	 * 
	 * @return the key
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public ForeignKey getKey() {
		return this.key;
	}

	/**
	 * Returns the key column of the collection table.
	 * 
	 * @return the key column of the collection table
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public MapKeyColumn getKeyColumn() {
		return this.keyColumn;
	}

	/**
	 * Returns the order column.
	 * 
	 * @return the order column
	 * 
	 * @since $version
	 * @author hceylan
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
					this.removeColumns = new AbstractColumn[this.getColumns().size()];

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
	 * @since $version
	 * @author hceylan
	 */
	public void link(ElementCollectionMapping mapping, TypeImpl<?> type, String defaultName, ColumnMetadata metadata, EnumType enumType,
		TemporalType temporalType, boolean lob) {
		if (StringUtils.isBlank(this.getName())) {
			this.setName(this.entity.getName() + "_" + defaultName);
		}

		this.key.link(null, this.entity);
		this.key.setTable(this);

		this.elementColumn = new ElementColumn(this.entity.getMetamodel().getJdbcAdaptor(), //
			mapping, //
			this, //
			(metadata == null) || StringUtils.isBlank(metadata.getName()) ? defaultName : metadata.getName(), //
			type.getJavaType(), //
			enumType, //
			temporalType, //
			lob, //
			metadata);
	}

	/**
	 * Links
	 * 
	 * @param type
	 *            the type of the collection
	 * @param defaultName
	 *            the default name
	 * @param elementMapping
	 *            the element mapping
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void link(EmbeddableTypeImpl<?> type, String defaultName, ElementMapping<?> elementMapping) {
		if (StringUtils.isBlank(this.getName())) {
			this.setName(defaultName);
		}

		this.key.link(null, this.entity);
		this.key.setTable(this);
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

		int paramIndex = 0;
		for (int i = 0; i < size; i++) {
			for (final AbstractColumn column : insertColumns) {
				if (column == this.orderColumn) {
					params[paramIndex++] = batch[i].getIndex();
				}
				else if (column == this.keyColumn) {
					params[paramIndex++] = batch[i].getKey();
				}
				else if (this.elementColumn == column) {
					params[paramIndex++] = this.elementColumn.getValue(batch[i].getValue());
				}
				else if (column instanceof JoinColumn) {
					params[paramIndex++] = column.getValue(source);
				}
				else {
					params[paramIndex++] = column.getValue(batch[i].getValue());
				}
			}
		}

		new QueryRunner(this.jdbcAdaptor.isPmdBroken()).update(connection, insertSql, params);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void performRemove(Connection connection, Object source, Object key, Object destination) throws SQLException {
		final String removeSql = this.getRemoveSql();

		final Object[] params = new Object[this.removeColumns.length];

		int i = 0;
		for (final AbstractColumn column : this.removeColumns) {
			if (column instanceof ElementColumn) {
				params[i++] = column.getValue(destination);
			}
			else if (column == this.keyColumn) {
				params[i++] = key;
			}
			else if (column instanceof JoinColumn) {
				params[i++] = column.getValue(source);
			}
			else {
				params[i++] = column.getValue(destination);
			}
		}

		new QueryRunner(this.jdbcAdaptor.isPmdBroken()).update(connection, removeSql, params);
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
			params[i++] = sourceRemoveColumn.getValue(source);
		}

		new QueryRunner(this.jdbcAdaptor.isPmdBroken()).update(connection, removeAllSql, params);
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
	 * @since $version
	 * @author hceylan
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
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void setOrderColumn(ColumnMetadata orderColumn, String name) {
		this.orderColumn = new OrderColumn(this, orderColumn, name);
	}
}
