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
import javax.persistence.criteria.JoinType;

import org.batoo.common.util.FinalWrapper;
import org.batoo.jpa.core.impl.jdbc.dbutils.QueryRunner;
import org.batoo.jpa.core.impl.model.type.EntityTypeImpl;
import org.batoo.jpa.core.jdbc.adapter.JdbcAdaptor;
import org.batoo.jpa.parser.impl.AbstractLocator;
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

	private final JdbcAdaptor jdbcAdaptor;

	private final ForeignKey sourceKey;
	private final ForeignKey destinationKey;
	private final EntityTypeImpl<?> entity;

	private OrderColumn orderColumn;

	private FinalWrapper<String> removeSql;
	private FinalWrapper<String> removeAllSql;

	private JoinColumn[] sourceRemoveColumns;
	private JoinColumn[] destinationRemoveColumns;
	private JoinColumn[] removeAllColumns;

	/**
	 * @param entity
	 *            the owner entity
	 * @param metadata
	 *            the metadata
	 * 
	 * @since $version
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
					this.removeAllColumns = new JoinColumn[this.sourceKey.getJoinColumns().size()];

					int i = 0;
					for (final JoinColumn column : this.sourceKey.getJoinColumns()) {
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

					this.removeSql = new FinalWrapper<String>("DELETE FROM " + this.getQName() + " WHERE " + Joiner.on(" AND ").join(restrictions));
				}

				wrapper = this.removeSql;
			}
		}

		return wrapper.value;
	}

	/**
	 * Returns the sourceKey of the JoinTable.
	 * 
	 * @return the sourceKey of the JoinTable
	 * 
	 * @since $version
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
	public void performInsert(Connection connection, Object source, Joinable[] batch, int size) throws SQLException {
		final String insertSql = this.getInsertSql(null, size);
		final AbstractColumn[] insertColumns = this.getInsertColumns(null, size);

		// prepare the parameters
		final Object[] params = new Object[insertColumns.length * size];

		int paramIndex = 0;
		for (int i = 0; i < size; i++) {
			for (final AbstractColumn column : insertColumns) {
				final Joinable joinable = batch[i];
				final Object destination = joinable.getValue();
				final int order = joinable.getIndex();

				final Object object = this.sourceKey.getJoinColumns().contains(column) ? source : destination;
				if (column != this.orderColumn) {
					params[paramIndex++] = column.getValue(connection, object);
				}
				else {
					params[paramIndex++] = order;
				}
			}
		}

		new QueryRunner(this.jdbcAdaptor.isPmdBroken(), false).update(connection, insertSql, params);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void performRemove(Connection connection, Object source, Object key, Object destination) throws SQLException {
		final String removeSql = this.getRemoveSql();

		final Object[] params = new Object[this.sourceKey.getJoinColumns().size() + this.destinationKey.getJoinColumns().size()];

		int i = 0;
		for (final JoinColumn sourceRemoveColumn : this.sourceRemoveColumns) {
			params[i++] = sourceRemoveColumn.getValue(connection, source);
		}

		for (final JoinColumn destinationRemoveColumn : this.destinationRemoveColumns) {
			params[i++] = destinationRemoveColumn.getValue(connection, destination);
		}

		new QueryRunner(this.jdbcAdaptor.isPmdBroken(), false).update(connection, removeSql, params);
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

		new QueryRunner(this.jdbcAdaptor.isPmdBroken(), false).update(connection, removeAllSql, params);
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
	 * @param locator
	 *            the locator
	 * 
	 * @since $version
	 */
	public void setOrderColumn(ColumnMetadata orderColumn, String name, AbstractLocator locator) {
		this.orderColumn = new OrderColumn(this, orderColumn, name, locator);
	}
}
