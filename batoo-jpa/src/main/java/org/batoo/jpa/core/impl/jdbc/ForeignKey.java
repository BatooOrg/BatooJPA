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
import java.util.Comparator;
import java.util.List;

import javax.persistence.criteria.JoinType;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.lang.StringUtils;
import org.batoo.jpa.core.impl.instance.ManagedInstance;
import org.batoo.jpa.core.impl.model.attribute.BasicAttribute;
import org.batoo.jpa.core.impl.model.mapping.AssociationMapping;
import org.batoo.jpa.core.impl.model.mapping.BasicMapping;
import org.batoo.jpa.core.impl.model.mapping.EmbeddedMapping;
import org.batoo.jpa.core.impl.model.mapping.Mapping;
import org.batoo.jpa.core.impl.model.mapping.SingularMapping;
import org.batoo.jpa.core.impl.model.type.EntityTypeImpl;
import org.batoo.jpa.core.jdbc.adapter.JdbcAdaptor;
import org.batoo.jpa.core.util.Pair;
import org.batoo.jpa.parser.MappingException;
import org.batoo.jpa.parser.metadata.ColumnMetadata;
import org.batoo.jpa.parser.metadata.JoinColumnMetadata;
import org.batoo.jpa.parser.metadata.PrimaryKeyJoinColumnMetadata;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

/**
 * Foreign key definition.
 * 
 * @author hceylan
 * @since $version
 */
public class ForeignKey {

	private final List<JoinColumn> joinColumns = Lists.newArrayList();
	private final boolean inverseOwner;
	private AbstractTable table;

	private String tableName;
	private OrderColumn orderColumn;

	private String singleChildSql;
	private AbstractColumn[] singleChildRestrictions;
	private String allChildrenSql;
	private AbstractColumn[] singleChildUpdates;

	private JoinColumn[] allChildrenRestrictions;
	private final JdbcAdaptor jdbcAdaptor;

	/**
	 * Constructor to create a join foreign key
	 * 
	 * @param jdbcAdaptor
	 *            the JDBC Adaptor
	 * @param metadata
	 *            the metadata for join column
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public ForeignKey(JdbcAdaptor jdbcAdaptor, List<JoinColumnMetadata> metadata) {
		this(jdbcAdaptor, metadata, false);
	}

	/**
	 * Constructor to create a join foreign key
	 * 
	 * @param jdbcAdaptor
	 *            the JDBC Adaptor
	 * @param metadata
	 *            the metadata for join column
	 * @param inverseOwner
	 *            true if the foreign key is inverse owner
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public ForeignKey(JdbcAdaptor jdbcAdaptor, List<JoinColumnMetadata> metadata, boolean inverseOwner) {
		super();

		this.jdbcAdaptor = jdbcAdaptor;
		this.inverseOwner = inverseOwner;

		for (final JoinColumnMetadata columnMetadata : metadata) {
			this.joinColumns.add(new JoinColumn(jdbcAdaptor, columnMetadata));
		}
	}

	/**
	 * @param jdbcAdaptor
	 *            the JDBC Adaptor
	 * @param table
	 *            the secondary table
	 * @param entity
	 *            the entity
	 * @param metadata
	 *            the metadata
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public ForeignKey(JdbcAdaptor jdbcAdaptor, SecondaryTable table, EntityTypeImpl<?> entity, List<PrimaryKeyJoinColumnMetadata> metadata) {
		super();

		this.jdbcAdaptor = jdbcAdaptor;
		this.inverseOwner = false;

		/**
		 * Here's how this works:
		 * 
		 * If there is single id mapping then it is either a single basic id or embedded id. <br />
		 * if it is basic id. If there are multiple id mappings they all are basic mappings.
		 * 
		 * For basic mappings, if there is metatada, then join columns are created with metadata, otherwise join columns are created only
		 * with the basic mapping.
		 * 
		 * In the case of embedded id's the child mappings of the embedded mappings are iterated recursively and the join columns are
		 * created the same way for simple basic mappings.
		 */

		// there is no metadata definition
		if ((metadata == null) || (metadata.size() == 0)) {

			// has single id
			if (entity.hasSingleIdAttribute()) {
				final SingularMapping<?, ?> idMapping = entity.getIdMapping();

				// single basic id attribute
				if (idMapping instanceof BasicMapping) {
					this.joinColumns.add(new JoinColumn(jdbcAdaptor, table, (BasicMapping<?, ?>) idMapping));
				}
				// embedded id
				else {
					this.createEmbeddedJoins(table, (EmbeddedMapping<?, ?>) idMapping, null);
				}
			}
			// has multiple id
			else {
				for (final Pair<?, BasicAttribute<?, ?>> pair : entity.getIdMappings()) {
					this.joinColumns.add(new JoinColumn(jdbcAdaptor, table, (BasicMapping<?, ?>) pair.getFirst()));
				}
			}
		}
		// there is metadata definition
		else {
			// has single id
			if (entity.hasSingleIdAttribute()) {
				final SingularMapping<?, ?> idMapping = entity.getIdMapping();

				// single basic id
				if (idMapping instanceof BasicMapping) {
					final BasicMapping<?, ?> basicMapping = (BasicMapping<?, ?>) idMapping;
					final PrimaryKeyJoinColumnMetadata columnMetadata = this.getColumnMetadata(metadata, basicMapping);

					this.joinColumns.add(new JoinColumn(jdbcAdaptor, columnMetadata, table, basicMapping));
				}
				// embedded id
				else {
					this.createEmbeddedJoins(table, (EmbeddedMapping<?, ?>) idMapping, metadata);
				}
			}
			// has multiple id
			else {
				for (final Pair<?, BasicAttribute<?, ?>> pair : entity.getIdMappings()) {
					final BasicMapping<?, ?> basicMapping = (BasicMapping<?, ?>) pair.getFirst();
					final PrimaryKeyJoinColumnMetadata columnMetadata = this.getColumnMetadata(metadata, basicMapping);

					this.joinColumns.add(new JoinColumn(jdbcAdaptor, columnMetadata, table, basicMapping));
				}
			}
		}

		this.table = table;
		this.table.addForeignKey(this);
	}

	/**
	 * Creates the join for destination foreign keys.
	 * 
	 * @param joinType
	 *            the type of the join
	 * @param parentAlias
	 *            the alias of the parent table
	 * @param alias
	 *            the alias of the table
	 * @return the join SQL fragment
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public String createDestinationJoin(JoinType joinType, String parentAlias, String alias) {
		if (this.inverseOwner) {
			return this.createSourceJoin(joinType, parentAlias, alias);
		}

		return this.createJoin(joinType, parentAlias, alias, this.joinColumns.get(0).getReferencedTable().getName(), false);
	}

	private void createEmbeddedJoins(SecondaryTable table, EmbeddedMapping<?, ?> mapping, List<PrimaryKeyJoinColumnMetadata> metadata) {
		for (final Mapping<?, ?, ?> child : mapping.getChildren()) {
			if (child instanceof BasicMapping) {
				final BasicMapping<?, ?> basicMapping = (BasicMapping<?, ?>) child;

				// if metadata present, we match it
				if ((metadata != null) && (metadata.size() > 0)) {
					final PrimaryKeyJoinColumnMetadata columnMetadata = this.getColumnMetadata(metadata, basicMapping);
					this.joinColumns.add(new JoinColumn(this.jdbcAdaptor, columnMetadata, table, basicMapping));
				}
				// otherwise we create with the defaults
				else {
					this.joinColumns.add(new JoinColumn(this.jdbcAdaptor, table, basicMapping));
				}
			}
			// recursively process embedded child
			else {
				this.createEmbeddedJoins(table, (EmbeddedMapping<?, ?>) child, metadata);
			}
		}
	}

	private String createJoin(JoinType joinType, String parentAlias, String alias, final String tableName, final boolean source) {
		final List<String> parts = Lists.newArrayList();

		for (final JoinColumn joinColumn : this.joinColumns) {
			final String leftColumnName = source ? joinColumn.getReferencedColumnName() : joinColumn.getName();
			final String rightColumnName = source ? joinColumn.getName() : joinColumn.getReferencedColumnName();

			final String part = parentAlias + "." + leftColumnName + " = " + alias + "." + rightColumnName;
			parts.add(part);
		}

		final String join = Joiner.on(" AND ").join(parts);

		// append the join part
		switch (joinType) {
			case INNER:
				return "INNER JOIN " + tableName + " " + alias + " ON " + join;
			case LEFT:
				return "LEFT JOIN " + tableName + " " + alias + " ON " + join;
			default:
				return "RIGHT JOIN " + tableName + " " + alias + " ON " + join;
		}
	}

	/**
	 * Creates the join for source foreign keys.
	 * 
	 * @param joinType
	 *            the type of the join
	 * @param parentAlias
	 *            the alias of the parent table
	 * @param alias
	 *            the alias of the table
	 * @return the join SQL fragment
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public String createSourceJoin(JoinType joinType, String parentAlias, String alias) {
		return this.createJoin(joinType, parentAlias, alias, this.joinColumns.get(0).getTable().getName(), true);
	}

	/**
	 * Returns the single child SQL.
	 * 
	 * @return the single child SQL
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private String getAllChildrenSql() {
		if (this.allChildrenSql != null) {
			return this.allChildrenSql;
		}

		synchronized (this) {
			if (this.allChildrenSql != null) {
				return this.allChildrenSql;
			}

			final List<JoinColumn> allChildrenRestrictions = Lists.newArrayList();

			final String updates = Joiner.on(", ").join(Lists.transform(this.joinColumns, new Function<JoinColumn, String>() {

				@Override
				public String apply(JoinColumn input) {
					return input.getName() + " = NULL";
				}
			}));

			final String restrictions = Joiner.on(", ").join(Lists.transform(this.joinColumns, new Function<JoinColumn, String>() {

				@Override
				public String apply(JoinColumn input) {
					allChildrenRestrictions.add(input);

					return input.getName() + " = ?";
				}
			}));

			final String order;
			if (this.orderColumn != null) {
				order = ", " + this.orderColumn.getName() + " = NULL";
			}
			else {
				order = "";
			}

			this.allChildrenRestrictions = allChildrenRestrictions.toArray(new JoinColumn[allChildrenRestrictions.size()]);

			return this.allChildrenSql = "UPDATE " + this.table.getQName() + "\nSET " + updates + order + "\nWHERE " + restrictions;
		}
	}

	private PrimaryKeyJoinColumnMetadata getColumnMetadata(List<PrimaryKeyJoinColumnMetadata> metadata, BasicMapping<?, ?> basicMapping) {
		final String columnName = basicMapping.getColumn().getMappingName();
		for (final PrimaryKeyJoinColumnMetadata columnMetadata : metadata) {
			if (columnName.equals(columnMetadata.getReferencedColumnName())) {
				return columnMetadata;
			}
		}

		throw new MappingException("Primary key field cannot be found for " + basicMapping.getAttribute().getJavaMember() + ".",
			basicMapping.getAttribute().getLocator());
	}

	/**
	 * Returns the list of join columns of the foreign key.
	 * 
	 * @return the list of join columns of the foreign key.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public List<JoinColumn> getJoinColumns() {
		return this.joinColumns;
	}

	/**
	 * Returns a generated name for the foreign key.
	 * 
	 * @return the name of the foreign key
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public String getName() {
		final List<JoinColumn> columns = Lists.newArrayList(this.joinColumns);

		// sort the column names for consistent order
		Collections.sort(columns, new Comparator<JoinColumn>() {
			@Override
			public int compare(JoinColumn o1, JoinColumn o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});

		final int prime = 31;
		int id = 31 * columns.get(0).getReferencedTable().getQName().hashCode();
		id = 31 * columns.get(0).getTable().getQName().hashCode();

		for (final JoinColumn joinColumn : columns) {
			id = (prime * id) + joinColumn.getName().hashCode();
		}

		return "FK_" + Integer.toHexString(id).toUpperCase();
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

	/**
	 * Returns the referenced table of the foreign key.
	 * 
	 * @return the referenced table of the foreign key
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public String getReferencedTableName() {
		return this.joinColumns.get(0).getReferencedTable().getName();
	}

	/**
	 * Returns the single child SQL.
	 * 
	 * @return the single child SQL
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private String getSingleChildSql() {
		if (this.singleChildSql != null) {
			return this.singleChildSql;
		}

		synchronized (this) {
			if (this.singleChildSql != null) {
				return this.singleChildSql;
			}

			final List<AbstractColumn> singleChildRestrictions = Lists.newArrayList();
			final List<AbstractColumn> singleChildUpdates = Lists.newArrayList();

			final String updates = Joiner.on(", ").join(Lists.transform(this.joinColumns, new Function<JoinColumn, String>() {

				@Override
				public String apply(JoinColumn input) {
					singleChildUpdates.add(input);

					return input.getName() + " = ?";
				}
			}));

			final String order;
			if (this.orderColumn != null) {
				singleChildUpdates.add(this.orderColumn);

				order = ", " + this.orderColumn.getName() + " = ?";
			}
			else {
				order = "";
			}

			final EntityTable table = (EntityTable) this.table;
			final String restrictions = Joiner.on(" AND ").join(Collections2.transform(table.getPkColumns(), new Function<AbstractColumn, String>() {

				@Override
				public String apply(AbstractColumn input) {
					singleChildRestrictions.add(input);

					return input.getName() + " = ?";
				}
			}));

			this.singleChildRestrictions = singleChildRestrictions.toArray(new AbstractColumn[singleChildRestrictions.size()]);
			this.singleChildUpdates = singleChildUpdates.toArray(new AbstractColumn[singleChildUpdates.size()]);

			return this.singleChildSql = "UPDATE " + this.table.getQName() + "\nSET " + updates + order + "\nWHERE " + restrictions;
		}
	}

	/**
	 * Returns the table of the ForeignKey.
	 * 
	 * @return the table of the ForeignKey
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public AbstractTable getTable() {
		return this.table;
	}

	/**
	 * Links the foreign key.
	 * 
	 * @param mapping
	 *            the owner attribute
	 * @param targetEntity
	 *            the target entity
	 * 
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void link(AssociationMapping<?, ?, ?> mapping, EntityTypeImpl<?> targetEntity) {
		// single primary key
		if (targetEntity.hasSingleIdAttribute()) {
			final SingularMapping<?, ?> idMapping = targetEntity.getIdMapping();

			// simple basic id
			if (idMapping instanceof BasicMapping) {
				this.linkJoinColumn(mapping, (BasicMapping<?, ?>) idMapping);
			}
			// embedded id
			else {
				this.linkEmbeddedJoins(mapping, (EmbeddedMapping<?, ?>) idMapping);
			}
		}
		// multiple id
		else {
			for (final Pair<?, BasicAttribute<?, ?>> pair : targetEntity.getIdMappings()) {
				this.linkJoinColumn(mapping, (BasicMapping<?, ?>) pair.getFirst());
			}
		}

		if (mapping != null) {
			final AbstractTable table = ((EntityTypeImpl<?>) mapping.getRoot().getType()).getTable(this.tableName);
			if (table == null) {
				throw new MappingException("Table " + this.tableName + " could not be found");
			}

			this.setTable(table);
		}
	}

	private void linkEmbeddedJoins(AssociationMapping<?, ?, ?> mapping, EmbeddedMapping<?, ?> embeddedMapping) {
		for (final Mapping<?, ?, ?> child : embeddedMapping.getChildren()) {
			if (child instanceof BasicMapping) {
				this.linkJoinColumn(mapping, (BasicMapping<?, ?>) child);
			}
			// recursively process embedded child
			else {
				this.linkEmbeddedJoins(mapping, (EmbeddedMapping<?, ?>) child);
			}
		}
	}

	private void linkJoinColumn(AssociationMapping<?, ?, ?> mapping, final BasicMapping<?, ?> idMapping) {
		// no definition for the join columns
		if (this.joinColumns.size() == 0) {
			// create the join column
			this.joinColumns.add(new JoinColumn(this.jdbcAdaptor, mapping, idMapping));
		}
		// existing definition for the join column
		else {
			// locate the corresponding join column
			JoinColumn joinColumn = null;
			if ((this.joinColumns.size() == 1) && StringUtils.isBlank(this.joinColumns.get(0).getReferencedColumnName())) {
				joinColumn = this.joinColumns.get(0);
			}
			else {
				for (final JoinColumn column : this.joinColumns) {
					if (idMapping.getColumn().getName().equals(column.getReferencedColumnName())) {
						joinColumn = column;
						break;
					}
				}
			}
			// if cannot be found then throw
			if (joinColumn == null) {
				throw new MappingException("Join column cannot be located in a composite key target entity");
			}

			// link the join column
			joinColumn.setColumnProperties(mapping, idMapping);
		}
	}

	/**
	 * Attaches the child to the managed instance.
	 * 
	 * @param connection
	 *            the connection
	 * @param instance
	 *            the instance
	 * @param key
	 *            the key object
	 * @param child
	 *            the child
	 * @param index
	 *            the index
	 * @throws SQLException
	 *             thrown in case of an SQL error
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void performAttachChild(ConnectionImpl connection, ManagedInstance<?> instance, Object key, Object child, int index) throws SQLException {
		final String sql = this.getSingleChildSql();

		final Object[] parameters = new Object[this.singleChildUpdates.length + this.singleChildRestrictions.length];

		int i = 0;
		for (final AbstractColumn column : this.singleChildUpdates) {
			if (column instanceof JoinColumn) {
				parameters[i++] = column.getValue(instance.getInstance());
			}
			else {
				parameters[i++] = index;
			}
		}

		for (final AbstractColumn column : this.singleChildRestrictions) {
			parameters[i++] = column.getValue(child);
		}

		new QueryRunner(this.jdbcAdaptor.isPmdBroken()).update(connection, sql, parameters);
	}

	/**
	 * Detaches the instance from all the children.
	 * 
	 * @param connection
	 *            the connection
	 * @param instance
	 *            the instance
	 * @throws SQLException
	 *             thrown in case of an SQL error
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void performDetachAll(ConnectionImpl connection, ManagedInstance<?> instance) throws SQLException {
		final String sql = this.getAllChildrenSql();

		final Object[] parameters = new Object[this.allChildrenRestrictions.length];

		int i = 0;
		for (final AbstractColumn column : this.allChildrenRestrictions) {
			parameters[i++] = column.getValue(instance.getInstance());
		}

		new QueryRunner(this.jdbcAdaptor.isPmdBroken()).update(connection, sql, parameters);
	}

	/**
	 * Detaches the child.
	 * 
	 * @param connection
	 *            the connection
	 * @param key
	 *            the key object
	 * @param child
	 *            the child
	 * @throws SQLException
	 *             thrown in case of an SQL error
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void performDetachChild(ConnectionImpl connection, Object key, Object child) throws SQLException {
		final String sql = this.getSingleChildSql();

		final Object[] parameters = new Object[this.singleChildUpdates.length + this.singleChildRestrictions.length];

		int i = 0;
		for (final AbstractColumn column : this.singleChildUpdates) {
			if (column instanceof JoinColumn) {
				parameters[i++] = null;
			}
			else {
				parameters[i++] = 0;
			}
		}

		for (final AbstractColumn column : this.singleChildRestrictions) {
			parameters[i++] = column.getValue(child);
		}

		new QueryRunner(this.jdbcAdaptor.isPmdBroken()).update(connection, sql, parameters);
	}

	/**
	 * Sets the order column.
	 * 
	 * @param orderColumn
	 *            the order column
	 * @param name
	 *            the name of the column
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void setOrderColumn(ColumnMetadata orderColumn, String name) {
		this.orderColumn = new OrderColumn(this.table, orderColumn, name);
	}

	/**
	 * Sets the table of the foreign key.
	 * 
	 * @param table
	 *            the table
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void setTable(AbstractTable table) {
		this.table = table;

		for (final JoinColumn joinColumn : this.joinColumns) {
			joinColumn.setTable(table);
		}

		this.table.addForeignKey(this);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String toString() {
		return "ForeignKey [tableName=" + this.table.getName() + ", joinColumns=" + this.joinColumns + "]";
	}
}
