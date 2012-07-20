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
package org.batoo.jpa.core.impl.jdbc;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import javax.persistence.EnumType;
import javax.persistence.TemporalType;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.lang.StringUtils;
import org.batoo.jpa.core.impl.model.mapping.ElementMapping;
import org.batoo.jpa.core.impl.model.type.EmbeddableTypeImpl;
import org.batoo.jpa.core.impl.model.type.EntityTypeImpl;
import org.batoo.jpa.core.impl.model.type.TypeImpl;
import org.batoo.jpa.parser.metadata.CollectionTableMetadata;
import org.batoo.jpa.parser.metadata.ColumnMetadata;
import org.batoo.jpa.parser.metadata.JoinColumnMetadata;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

/**
 * The table for element collection attributes.
 * 
 * @author hceylan
 * @since $version
 */
public class CollectionTable extends AbstractTable implements JoinableTable {

	private final EntityTypeImpl<?> entity;
	private final ForeignKey key;

	private OrderColumn orderColumn;
	private ElementColumn elementColumn;

	private String removeSql;
	private String removeAllSql;
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

	private String getRemoveAllSql() {
		if (this.removeAllSql != null) {
			return this.removeAllSql;
		}

		synchronized (this) {
			if (this.removeAllSql != null) {
				return this.removeAllSql;
			}

			final List<String> restrictions = Lists.newArrayList();
			this.removeAllColumns = new JoinColumn[this.key.getJoinColumns().size()];

			int i = 0;
			for (final JoinColumn column : this.key.getJoinColumns()) {
				restrictions.add(column.getName() + " = ?");
				this.removeAllColumns[i++] = column;
			}

			return this.removeAllSql = "DELETE FROM " + this.getQName() + " WHERE " + Joiner.on(" AND ").join(restrictions);
		}
	}

	private String getRemoveSql() {
		if (this.removeSql != null) {
			return this.removeSql;
		}

		synchronized (this) {
			if (this.removeSql != null) {
				return this.removeSql;
			}

			final List<String> restrictions = Lists.newArrayList();
			this.removeColumns = new AbstractColumn[this.getColumns().size()];

			int i = 0;
			for (final AbstractColumn column : this.getColumns()) {
				if (column != this.orderColumn) {
					restrictions.add(column.getName() + " = ?");
					this.removeColumns[i++] = column;
				}
			}

			return this.removeSql = "DELETE FROM " + this.getQName() + " WHERE " + Joiner.on(" AND ").join(restrictions);
		}
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
	public void link(TypeImpl<?> type, String defaultName, ColumnMetadata metadata, EnumType enumType, TemporalType temporalType, boolean lob) {
		if (StringUtils.isBlank(this.getName())) {
			this.setName(this.entity.getName() + "_" + defaultName);
		}

		this.key.link(null, this.entity);
		this.key.setTable(this);

		this.elementColumn = new ElementColumn(this.entity.getMetamodel().getJdbcAdaptor(), //
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
	public void performInsert(ConnectionImpl connection, Object source, Object key, Object destination, int order) throws SQLException {
		final String insertSql = this.getInsertSql(null);
		final AbstractColumn[] insertColumns = this.getInsertColumns(null);

		// prepare the parameters
		final Object[] params = new Object[insertColumns.length];
		for (int i = 0; i < insertColumns.length; i++) {
			final AbstractColumn column = insertColumns[i];

			if (column == this.orderColumn) {
				params[i] = order;
			}
			else if (column == this.keyColumn) {
				params[i] = key;
			}
			else if (this.elementColumn == column) {
				params[i] = this.elementColumn.getValue(destination);
			}
			else if (column instanceof JoinColumn) {
				params[i] = column.getValue(source);
			}
			else {
				params[i] = column.getValue(destination);
			}
		}

		new QueryRunner().update(connection, insertSql, params);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void performRemove(ConnectionImpl connection, Object source, Object key, Object destination) throws SQLException {
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

		new QueryRunner().update(connection, removeSql, params);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void performRemoveAll(ConnectionImpl connection, Object source) throws SQLException {
		final String removeAllSql = this.getRemoveAllSql();

		final Object[] params = new Object[this.removeAllColumns.length];

		int i = 0;
		for (final JoinColumn sourceRemoveColumn : this.removeAllColumns) {
			params[i++] = sourceRemoveColumn.getValue(source);
		}

		new QueryRunner().update(connection, removeAllSql, params);
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
