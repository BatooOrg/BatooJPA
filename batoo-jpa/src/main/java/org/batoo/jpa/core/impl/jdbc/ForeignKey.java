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
import java.util.Comparator;
import java.util.List;

import javax.persistence.criteria.JoinType;

import org.apache.commons.lang.StringUtils;
import org.batoo.common.util.FinalWrapper;
import org.batoo.jpa.core.impl.instance.ManagedInstance;
import org.batoo.jpa.core.impl.jdbc.dbutils.QueryRunner;
import org.batoo.jpa.core.impl.model.attribute.AssociatedSingularAttribute;
import org.batoo.jpa.core.impl.model.attribute.AttributeImpl;
import org.batoo.jpa.core.impl.model.attribute.SingularAttributeImpl;
import org.batoo.jpa.core.impl.model.mapping.AssociationMapping;
import org.batoo.jpa.core.impl.model.mapping.BasicMapping;
import org.batoo.jpa.core.impl.model.mapping.EmbeddedMapping;
import org.batoo.jpa.core.impl.model.mapping.Mapping;
import org.batoo.jpa.core.impl.model.mapping.SingularAssociationMapping;
import org.batoo.jpa.core.impl.model.mapping.SingularMapping;
import org.batoo.jpa.core.impl.model.type.EntityTypeImpl;
import org.batoo.jpa.core.jdbc.adapter.JdbcAdaptor;
import org.batoo.jpa.core.util.Pair;
import org.batoo.jpa.parser.MappingException;
import org.batoo.jpa.parser.impl.AbstractLocator;
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
	private final boolean joinMetadataProvided;
	private final boolean inverseOwner;
	private AbstractTable table;

	private String tableName;
	private OrderColumn orderColumn;

	private FinalWrapper<String> singleChildSql;
	private AbstractColumn[] singleChildRestrictions;

	private FinalWrapper<String> allChildrenSql;
	private AbstractColumn[] singleChildUpdates;

	private JoinColumn[] allChildrenRestrictions;
	private final JdbcAdaptor jdbcAdaptor;
	private FinalWrapper<JoinColumn[]> columns; // optimize getJoinColumns return to array

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

		this.joinMetadataProvided = this.joinColumns.size() > 0;
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

		if (entity.hasSingleIdAttribute()) {
			this.createJoinColumns(table, entity.getIdMapping(), metadata);
		}
		else {
			for (final Pair<?, SingularAttributeImpl<?, ?>> pair : entity.getIdMappings()) {
				this.createJoinColumns(table, (SingularMapping<?, ?>) pair.getFirst(), metadata);
			}
		}

		this.table = table;
		this.table.addForeignKey(this);
		this.joinMetadataProvided = this.joinColumns.size() > 0;
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

		return this.createJoin(joinType, parentAlias, alias, this.joinColumns.get(0).getReferencedTable().getQName(), false);
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

	private void createJoinColumns(SecondaryTable table, SingularMapping<?, ?> mapping, List<PrimaryKeyJoinColumnMetadata> metadata) {
		// if the mapping BasicMapping then create a single join column
		if (mapping instanceof BasicMapping) {
			final BasicMapping<?, ?> basicMapping = (BasicMapping<?, ?>) mapping;

			final PrimaryKeyJoinColumnMetadata columnMetadata = this.getColumnMetadata(metadata, basicMapping.getColumn(), mapping);

			this.joinColumns.add(new JoinColumn(this.jdbcAdaptor, columnMetadata, table, ((BasicMapping<?, ?>) mapping).getColumn()));
		}

		// if the mapping SingularAssociationMapping then create a join column for each of the join columns of the mapping
		else if (mapping instanceof SingularAssociationMapping) {
			final ForeignKey foreignKey = ((SingularAssociationMapping<?, ?>) mapping).getForeignKey();

			for (final JoinColumn joinColumn : foreignKey.getJoinColumns()) {
				final PrimaryKeyJoinColumnMetadata columnMetadata = this.getColumnMetadata(metadata, joinColumn, mapping);

				this.joinColumns.add(new JoinColumn(this.jdbcAdaptor, columnMetadata, table, joinColumn));
			}
		}

		// if the mapping is embedded mapping enumerate child mappings and polymorphically create the join columns
		else {
			final EmbeddedMapping<?, ?> embeddedMapping = (EmbeddedMapping<?, ?>) mapping;

			for (final Mapping<?, ?, ?> child : embeddedMapping.getChildren()) {
				if (child instanceof SingularMapping) {
					this.createJoinColumns(table, (SingularMapping<?, ?>) child, metadata);
				}
				else {
					throw new MappingException("EmbeddedId types cannot have plural mappings", mapping.getAttribute().getLocator());
				}
			}
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
		return this.createJoin(joinType, parentAlias, alias, this.joinColumns.get(0).getTable().getQName(), true);
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
		FinalWrapper<String> wrapper = this.allChildrenSql;

		if (wrapper == null) {
			synchronized (this) {
				if (this.allChildrenSql == null) {

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

					this.allChildrenSql = new FinalWrapper<String>("UPDATE " + this.table.getQName() + "\nSET " + updates + order + "\nWHERE " + restrictions);
				}

				wrapper = this.allChildrenSql;
			}
		}
		return wrapper.value;
	}

	private PrimaryKeyJoinColumnMetadata getColumnMetadata(List<PrimaryKeyJoinColumnMetadata> metadata, AbstractColumn column, SingularMapping<?, ?> mapping) {
		if ((metadata == null) || metadata.isEmpty()) {
			return null;
		}

		for (final PrimaryKeyJoinColumnMetadata columnMetadata : metadata) {
			if (column.getName().equals(columnMetadata.getReferencedColumnName())) {
				return columnMetadata;
			}
		}

		throw new MappingException("Primary key field cannot be found for " + mapping.getAttribute().getJavaMember() + ".", mapping.getAttribute().getLocator());
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
	 * Returns the qualified referenced table of the foreign key.
	 * 
	 * @return the qualified referenced table of the foreign key
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public String getReferencedTableQName() {
		return this.joinColumns.get(0).getReferencedTable().getQName();
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
		FinalWrapper<String> wrapper = this.singleChildSql;

		if (wrapper == null) {
			synchronized (this) {
				if (this.singleChildSql == null) {

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

					this.singleChildSql = new FinalWrapper<String>("UPDATE " + this.table.getQName() + "\nSET " + updates + order + "\nWHERE " + restrictions);
				}

				wrapper = this.singleChildSql;
			}
		}

		return wrapper.value;
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
			this.linkJoinColumns(mapping, targetEntity.getIdMapping());
		}
		// multiple id
		else {
			for (final Pair<?, SingularAttributeImpl<?, ?>> pair : targetEntity.getIdMappings()) {
				this.linkJoinColumns(mapping, (SingularMapping<?, ?>) pair.getFirst());
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

	private void linkJoinColumns(AssociationMapping<?, ?, ?> mapping, final SingularMapping<?, ?> idMapping) {
		boolean id = false;

		if (mapping != null) {
			final AttributeImpl<?, ?> attribute = mapping.getAttribute();
			if (attribute instanceof AssociatedSingularAttribute) {
				id = ((AssociatedSingularAttribute<?, ?>) attribute).isId();
			}
		}

		// no definition for the join columns
		if (!this.joinMetadataProvided) {
			// create the join column
			this.linkJoinColumns(mapping, idMapping, id);
		}
		// existing definition for the join column
		else {
			this.linkJoinColumnsImpl(mapping, idMapping);

		}
	}

	private void linkJoinColumns(AssociationMapping<?, ?, ?> mapping, final SingularMapping<?, ?> idMapping, boolean id) {
		// if the mapping BasicMapping then create a single join column
		if (idMapping instanceof BasicMapping) {
			this.joinColumns.add(new JoinColumn(this.jdbcAdaptor, mapping, ((BasicMapping<?, ?>) idMapping).getColumn(), id));
		}

		// if the mapping SingularAssociationMapping then create a join column for each of the join columns of the mapping
		else if (idMapping instanceof SingularAssociationMapping) {
			final ForeignKey foreignKey = ((SingularAssociationMapping<?, ?>) idMapping).getForeignKey();

			for (final JoinColumn joinColumn : foreignKey.getJoinColumns()) {
				this.joinColumns.add(new JoinColumn(this.jdbcAdaptor, mapping, joinColumn, id));
			}
		}

		// if the mapping is embedded mapping enumerate child mappings and polymorphically create the join columns
		else {
			final EmbeddedMapping<?, ?> embeddedMapping = (EmbeddedMapping<?, ?>) idMapping;

			for (final Mapping<?, ?, ?> child : embeddedMapping.getChildren()) {
				if (child instanceof SingularMapping) {
					this.linkJoinColumns(mapping, (SingularMapping<?, ?>) child, id);
				}
				else {
					throw new MappingException("EmbeddedId types cannot have plural mappings", mapping.getAttribute().getLocator());
				}
			}
		}

	}

	private void linkJoinColumnsImpl(AssociationMapping<?, ?, ?> mapping, final SingularMapping<?, ?> idMapping) {
		// if the mapping BasicMapping then create a single join column
		if (idMapping instanceof BasicMapping) {
			final BasicMapping<?, ?> basicMapping = (BasicMapping<?, ?>) idMapping;

			final JoinColumn joinColumn = this.locateJoinColumn(basicMapping.getColumn());

			// link the join column
			joinColumn.setColumnProperties(mapping, ((BasicMapping<?, ?>) idMapping).getColumn());
		}

		// if the mapping SingularAssociationMapping then create a join column for each of the join columns of the mapping
		else if (idMapping instanceof SingularAssociationMapping) {
			final ForeignKey foreignKey = ((SingularAssociationMapping<?, ?>) mapping).getForeignKey();

			for (final JoinColumn referencedColumn : foreignKey.getJoinColumns()) {
				final JoinColumn joinColumn = this.locateJoinColumn(referencedColumn);

				// link the join column
				joinColumn.setColumnProperties(mapping, referencedColumn);
			}
		}

		// if the mapping is embedded mapping enumerate child mappings and polymorphically create the join columns
		else {
			final EmbeddedMapping<?, ?> embeddedMapping = (EmbeddedMapping<?, ?>) idMapping;

			for (final Mapping<?, ?, ?> child : embeddedMapping.getChildren()) {
				if (child instanceof SingularMapping) {
					this.linkJoinColumnsImpl(mapping, (SingularMapping<?, ?>) child);
				}
				else {
					throw new MappingException("EmbeddedId types cannot have plural mappings", mapping.getAttribute().getLocator());
				}
			}
		}
	}

	private JoinColumn locateJoinColumn(AbstractColumn referencedColumn) {
		JoinColumn joinColumn = null;

		// locate the corresponding join column
		if ((this.joinColumns.size() == 1) && StringUtils.isBlank(this.joinColumns.get(0).getReferencedColumnName())) {
			joinColumn = this.joinColumns.get(0);
		}
		else {
			for (final JoinColumn column : this.joinColumns) {
				if (referencedColumn.getName().equals(column.getReferencedColumnName())) {
					joinColumn = column;
					break;
				}
			}
		}
		// if cannot be found then throw
		if (joinColumn == null) {
			throw new MappingException("Join column cannot be located in a composite key target entity");
		}

		return joinColumn;
	}

	/**
	 * Attaches the child to the managed instance.
	 * 
	 * @param connection
	 *            the connection
	 * @param instance
	 *            the instance
	 * @param batch
	 *            the batch of joinables
	 * @param size
	 *            the size of the batch
	 * @throws SQLException
	 *             thrown in case of an SQL error
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void performAttachChild(Connection connection, ManagedInstance<?> instance, Joinable[] batch, int size) throws SQLException {
		final String sql = this.getSingleChildSql();

		final Object[] parameters = new Object[this.singleChildUpdates.length + this.singleChildRestrictions.length];

		for (int i = 0; i < size; i++) {
			final Joinable joinable = batch[i];
			int paramIndex = 0;
			for (final AbstractColumn column : this.singleChildUpdates) {
				if (column instanceof JoinColumn) {
					parameters[paramIndex++] = column.getValue(connection, instance.getInstance());
				}
				else {
					parameters[paramIndex++] = joinable.getIndex();
				}
			}

			for (final AbstractColumn column : this.singleChildRestrictions) {
				try {
					parameters[paramIndex++] = column.getValue(connection, joinable.getValue());
				}
				catch (final NullPointerException e) {
					System.out.println("");
				}
			}

			new QueryRunner(this.jdbcAdaptor.isPmdBroken(), false).update(connection, sql, parameters);
		}
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
	public void performDetachAll(Connection connection, ManagedInstance<?> instance) throws SQLException {
		final String sql = this.getAllChildrenSql();

		final Object[] parameters = new Object[this.allChildrenRestrictions.length];

		int i = 0;
		for (final AbstractColumn column : this.allChildrenRestrictions) {
			parameters[i++] = column.getValue(connection, instance.getInstance());
		}

		new QueryRunner(this.jdbcAdaptor.isPmdBroken(), false).update(connection, sql, parameters);
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
	public void performDetachChild(Connection connection, Object key, Object child) throws SQLException {
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
			parameters[i++] = column.getValue(connection, child);
		}

		new QueryRunner(this.jdbcAdaptor.isPmdBroken(), false).update(connection, sql, parameters);
	}

	/**
	 * Sets the order column.
	 * 
	 * @param orderColumn
	 *            the order column
	 * @param name
	 *            the name of the column
	 * @param locator
	 *            the locator
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void setOrderColumn(ColumnMetadata orderColumn, String name, AbstractLocator locator) {
		this.orderColumn = new OrderColumn(this.table, orderColumn, name, locator);
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
		return "ForeignKey [tableName=" + this.table.getQName() + ", joinColumns=" + this.joinColumns + "]";
	}
}
