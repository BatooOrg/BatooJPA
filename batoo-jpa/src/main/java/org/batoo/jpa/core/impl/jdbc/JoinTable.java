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

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import javax.persistence.EnumType;
import javax.persistence.TemporalType;
import javax.persistence.criteria.JoinType;

import org.apache.commons.dbutils.QueryRunner;
import org.batoo.jpa.core.impl.model.type.EntityTypeImpl;
import org.batoo.jpa.core.jdbc.adapter.JdbcAdaptor;
import org.batoo.jpa.parser.metadata.ColumnMetadata;
import org.batoo.jpa.parser.metadata.JoinColumnMetadata;
import org.batoo.jpa.parser.metadata.JoinTableMetadata;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

/**
 * 
 * 
 * @author hceylan
 * @since $version
 */
public class JoinTable extends AbstractTable implements JoinableTable {

	private final ForeignKey sourceKey;
	private final ForeignKey destinationKey;
	private final EntityTypeImpl<?> entity;

	private OrderColumn orderColumn;

	private String removeSql;

	private String removeAllSql;
	private JoinColumn[] sourceRemoveColumns;
	private JoinColumn[] destinationRemoveColumns;
	private JoinColumn[] removeAllColumns;
	private final JdbcAdaptor jdbcAdaptor;

	/**
	 * @param entity
	 *            the owner entity
	 * @param metadata
	 *            the metadata
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public JoinTable(EntityTypeImpl<?> entity, JoinTableMetadata metadata) {
		super(metadata);

		this.entity = entity;

		this.jdbcAdaptor = this.entity.getMetamodel().getJdbcAdaptor();

		this.sourceKey = new ForeignKey(this.jdbcAdaptor, metadata != null ? metadata.getJoinColumns() : Collections.<JoinColumnMetadata> emptyList());
		this.destinationKey = new ForeignKey(this.jdbcAdaptor, metadata != null ? metadata.getInverseJoinColumns()
			: Collections.<JoinColumnMetadata> emptyList());
	}

	/**
	 * Creates a join between the source and destination entities
	 * 
	 * @param joinType
	 *            the type of the join
	 * @param parentAlias
	 *            the alias of the parent table
	 * @param alias
	 *            the alias of the table
	 * @param forward
	 *            if the join if forward or backwards
	 * @return the join SQL fragment
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public String createJoin(JoinType joinType, String parentAlias, String alias, boolean forward) {
		String sourceJoin, destinationJoin;

		if (forward) {
			sourceJoin = this.sourceKey.createSourceJoin(joinType, parentAlias, alias + "_J");
			destinationJoin = this.destinationKey.createDestinationJoin(joinType, alias + "_J", alias);
		}
		else {
			sourceJoin = this.destinationKey.createSourceJoin(joinType, parentAlias, alias + "_J");
			destinationJoin = this.sourceKey.createDestinationJoin(joinType, alias + "_J", alias);
		}

		return sourceJoin + "\n" + destinationJoin;
	}

	/**
	 * Returns the destinationKey of the JoinTable.
	 * 
	 * @return the destinationKey of the JoinTable
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public ForeignKey getDestinationKey() {
		return this.destinationKey;
	}

	/**
	 * Returns the oner entity of the table.
	 * 
	 * @return the oner entity of the table
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public EntityTypeImpl<?> getEntity() {
		return this.entity;
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
		if (this.removeAllSql != null) {
			return this.removeAllSql;
		}

		synchronized (this) {
			if (this.removeAllSql != null) {
				return this.removeAllSql;
			}

			final List<String> restrictions = Lists.newArrayList();
			this.removeAllColumns = new JoinColumn[this.sourceKey.getJoinColumns().size()];

			int i = 0;
			for (final JoinColumn column : this.sourceKey.getJoinColumns()) {
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
			this.sourceRemoveColumns = new JoinColumn[this.sourceKey.getJoinColumns().size()];
			this.destinationRemoveColumns = new JoinColumn[this.sourceKey.getJoinColumns().size()];

			int i = 0;
			for (final JoinColumn column : this.sourceKey.getJoinColumns()) {
				restrictions.add(column.getName() + " = ?");
				this.sourceRemoveColumns[i++] = column;
			}

			i = 0;
			for (final JoinColumn column : this.destinationKey.getJoinColumns()) {
				restrictions.add(column.getName() + " = ?");
				this.destinationRemoveColumns[i++] = column;
			}

			return this.removeSql = "DELETE FROM " + this.getQName() + " WHERE " + Joiner.on(" AND ").join(restrictions);
		}
	}

	/**
	 * Returns the sourceKey of the JoinTable.
	 * 
	 * @return the sourceKey of the JoinTable
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public ForeignKey getSourceKey() {
		return this.sourceKey;
	}

	/**
	 * @param source
	 *            the source entity
	 * @param destination
	 *            the destination entity
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void link(EntityTypeImpl<?> source, EntityTypeImpl<?> destination) {
		if (this.getName() == null) {
			this.setName(source.getName() + "_" + destination.getName());
		}

		this.sourceKey.link(null, source);
		this.destinationKey.link(null, destination);

		this.sourceKey.setTable(this);
		this.destinationKey.setTable(this);
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

			final Object object = this.sourceKey.getJoinColumns().contains(column) ? source : destination;
			if (column == this.orderColumn) {
				params[i] = order;
			}
			else {
				params[i] = column.getValue(object);
			}
		}

		new QueryRunner(this.jdbcAdaptor.isPmdBroken()).update(connection, insertSql, params);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void performRemove(ConnectionImpl connection, Object source, Object key, Object destination) throws SQLException {
		final String removeSql = this.getRemoveSql();

		final Object[] params = new Object[this.sourceKey.getJoinColumns().size() + this.destinationKey.getJoinColumns().size()];

		int i = 0;
		for (final JoinColumn sourceRemoveColumn : this.sourceRemoveColumns) {
			params[i++] = sourceRemoveColumn.getValue(source);
		}

		for (final JoinColumn destinationRemoveColumn : this.destinationRemoveColumns) {
			params[i++] = destinationRemoveColumn.getValue(destination);
		}

		new QueryRunner(this.jdbcAdaptor.isPmdBroken()).update(connection, removeSql, params);
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
		new MapKeyColumn(this, mapKeyColumn, name, mapKeyTemporalType, mapKeyEnumType, mapKeyJavaType);
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
