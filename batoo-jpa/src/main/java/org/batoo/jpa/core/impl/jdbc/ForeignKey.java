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
import org.batoo.common.reflect.AbstractAccessor;
import org.batoo.common.util.FinalWrapper;
import org.batoo.jpa.core.impl.instance.ManagedInstance;
import org.batoo.jpa.core.impl.jdbc.dbutils.QueryRunner;
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
 * @since 2.0.0
 */
public class ForeignKey {

	private final JdbcAdaptor jdbcAdaptor;
	private Mapping<?, ?, ?> masterMapping;
	private final List<JoinColumn> joinColumns = Lists.newArrayList();
	private final boolean joinMetadataProvided;
	private final boolean inverseOwner;
	private final boolean readOnly;
	private AbstractTable table;

	private String tableName;
	private Mapping<?, ?, ?> mapping;

	private OrderColumn orderColumn;

	private FinalWrapper<String> singleChildSql;
	private AbstractColumn[] singleChildRestrictions;

	private FinalWrapper<String> allChildrenSql;
	private AbstractColumn[] singleChildUpdates;

	private JoinColumn[] allChildrenRestrictions;

	/**
	 * Constructor to create a join foreign key
	 * 
	 * @param jdbcAdaptor
	 *            the JDBC Adaptor
	 * @param mapping
	 *            the owner mapping
	 * @param metadata
	 *            the metadata for join column
	 * 
	 * @since 2.0.0
	 */
	public ForeignKey(JdbcAdaptor jdbcAdaptor, Mapping<?, ?, ?> mapping, List<JoinColumnMetadata> metadata) {
		this(jdbcAdaptor, mapping, metadata, false);
	}

	/**
	 * Constructor to create a join foreign key
	 * 
	 * @param jdbcAdaptor
	 *            the JDBC Adaptor
	 * @param mapping
	 *            the owner mapping
	 * @param metadata
	 *            the metadata for join column
	 * @param inverseOwner
	 *            true if the foreign key is inverse owner
	 * 
	 * @since 2.0.0
	 */
	public ForeignKey(JdbcAdaptor jdbcAdaptor, Mapping<?, ?, ?> mapping, List<JoinColumnMetadata> metadata, boolean inverseOwner) {
		super();

		this.jdbcAdaptor = jdbcAdaptor;
		this.mapping = mapping;
		this.inverseOwner = inverseOwner;
		this.readOnly = this.isReadOnly(metadata);

		for (final JoinColumnMetadata columnMetadata : metadata) {
			this.joinColumns.add(new JoinColumn(jdbcAdaptor, columnMetadata, this.readOnly));
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
	 * @since 2.0.0
	 */
	public ForeignKey(JdbcAdaptor jdbcAdaptor, SecondaryTable table, EntityTypeImpl<?> entity, List<PrimaryKeyJoinColumnMetadata> metadata) {
		super();

		this.jdbcAdaptor = jdbcAdaptor;
		this.inverseOwner = false;
		this.readOnly = false;

		if (entity.hasSingleIdAttribute()) {
			this.createJoinColumns(table, entity.getIdMapping(), metadata);
		}
		else {
			for (final Pair<?, AbstractAccessor> pair : entity.getIdMappings()) {
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
	 * @since 2.0.0
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
	 * @since 2.0.0
	 */
	public String createSourceJoin(JoinType joinType, String parentAlias, String alias) {
		return this.createJoin(joinType, parentAlias, alias, this.joinColumns.get(0).getTable().getQName(), true);
	}

	/**
	 * Returns the single child SQL.
	 * 
	 * @return the single child SQL
	 * 
	 * @since 2.0.0
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
	 * @since 2.0.0
	 */
	public List<JoinColumn> getJoinColumns() {
		return this.joinColumns;
	}

	/**
	 * Returns the mapping of the ForeignKey.
	 * 
	 * @return the mapping of the ForeignKey
	 * 
	 * @since $version
	 */
	public Mapping<?, ?, ?> getMapping() {
		return this.mapping;
	}

	/**
	 * Returns a generated name for the foreign key.
	 * 
	 * @return the name of the foreign key
	 * 
	 * @since 2.0.0
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
	 * @since 2.0.0
	 */
	public OrderColumn getOrderColumn() {
		return this.orderColumn;
	}

	/**
	 * Returns the referenced table of the foreign key.
	 * 
	 * @return the referenced table of the foreign key
	 * 
	 * @since 2.0.0
	 */
	public String getReferencedTableName() {
		return this.joinColumns.get(0).getReferencedTable().getName();
	}

	/**
	 * Returns the qualified referenced table of the foreign key.
	 * 
	 * @return the qualified referenced table of the foreign key
	 * 
	 * @since 2.0.0
	 */
	public String getReferencedTableQName() {
		return this.joinColumns.get(0).getReferencedTable().getQName();
	}

	/**
	 * Returns the single child SQL.
	 * 
	 * @return the single child SQL
	 * 
	 * @since 2.0.0
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
	 * @since 2.0.0
	 */
	public AbstractTable getTable() {
		return this.table;
	}

	/**
	 * Returns if the foreign key maps to a readonly join.
	 * 
	 * @return true if the foreign key maps to a readonly join, false otherwise
	 * 
	 * @since 2.0.0
	 */
	public boolean isReadOnly() {
		return this.readOnly;
	}

	/**
	 * Returns if the foreign key is read only.
	 * 
	 * @param metadata
	 *            the metadata
	 * @return if the foreign key is read only
	 * 
	 * @since 2.0.0
	 */
	private boolean isReadOnly(List<JoinColumnMetadata> metadata) {
		for (final JoinColumnMetadata columnMetadata : metadata) {
			if (!columnMetadata.isUpdatable() && !columnMetadata.isInsertable()) {
				return true;
			}
		}

		return false;
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
	 * @since 2.0.0
	 */
	public void link(AssociationMapping<?, ?, ?> mapping, EntityTypeImpl<?> targetEntity) {
		if (mapping instanceof SingularAssociationMapping) {
			final SingularAssociationMapping<?, ?> singularAssociationMapping = (SingularAssociationMapping<?, ?>) mapping;

			final String mapsId = singularAssociationMapping.getAttribute().getMapsId();

			if (mapsId != null) {
				final EntityTypeImpl<?> type = (EntityTypeImpl<?>) singularAssociationMapping.getRoot().getType();

				if (!type.hasSingleIdAttribute() || !(type.getIdMapping() instanceof EmbeddedMapping)) {
					throw new MappingException("MapsId can only be used in combination with EmbeddedId", mapping.getAttribute().getLocator());
				}

				final EmbeddedMapping<?, ?> embeddedMapping = (EmbeddedMapping<?, ?>) type.getIdMapping();
				if (!mapsId.isEmpty()) {
					this.masterMapping = embeddedMapping.getChild(mapsId);
				}
				else {
					this.masterMapping = embeddedMapping.getChild(mapping.getAttribute().getName());
				}

				if (this.masterMapping == null) {
					throw new MappingException("Cannot locate the mapping declared by MapsId " + mapsId, mapping.getAttribute().getLocator());
				}

				final Class<?> idClass = targetEntity.hasSingleIdAttribute() ? targetEntity.getIdMapping().getAttribute().getJavaType()
					: targetEntity.getIdClass();

				if (idClass != this.masterMapping.getAttribute().getJavaType()) {
					throw new MappingException("MapsId mapped attribute type " + this.masterMapping.getAttribute().getJavaType().getName()
						+ " is not compatible with target entity primary key type " + idClass.getName(), mapping.getAttribute().getLocator());
				}
			}
		}

		// single primary key
		if (targetEntity.hasSingleIdAttribute()) {
			this.linkImpl(mapping, targetEntity.getIdMapping());
		}

		// multiple id
		else {
			for (final Pair<?, AbstractAccessor> pair : targetEntity.getIdMappings()) {
				this.linkImpl(mapping, (SingularMapping<?, ?>) pair.getFirst());
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

	private void linkImpl(AssociationMapping<?, ?, ?> mapping, SingularMapping<?, ?> idMapping) {
		// no definition for the join columns
		if (!this.joinMetadataProvided) {
			// if maps id present link as virtual join columns
			if (this.masterMapping != null) {
				// if the mapping BasicMapping then create a single join column
				if (!(idMapping instanceof BasicMapping)) {
					throw new MappingException("MapsId without metadata points to composite primary key", mapping.getAttribute().getLocator());
				}

				final BasicMapping<?, ?> basicMapping = (BasicMapping<?, ?>) this.masterMapping;

				this.joinColumns.add(new JoinColumn(this.jdbcAdaptor, mapping, ((BasicMapping<?, ?>) idMapping).getColumn(), basicMapping.getColumn()));
			}
			// create the join column
			else {
				this.linkJoinColumns(mapping, idMapping);
			}
		}

		// existing definition for the join column
		else {
			this.linkJoinColumnsWithMetadata(mapping, idMapping);
		}
	}

	private void linkJoinColumns(AssociationMapping<?, ?, ?> mapping, final SingularMapping<?, ?> idMapping) {
		final boolean id = (mapping != null) && mapping.isId();

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
					this.linkJoinColumns(mapping, (SingularMapping<?, ?>) child);
				}
				else {
					throw new MappingException("EmbeddedId types cannot have plural mappings", mapping.getAttribute().getLocator());
				}
			}
		}
	}

	private void linkJoinColumnsWithMetadata(AssociationMapping<?, ?, ?> mapping, final SingularMapping<?, ?> idMapping) {
		final boolean virtual = this.masterMapping != null;
		final boolean id = !virtual && (mapping != null) && mapping.isId();

		// if the mapping BasicMapping then create a single join column
		if (idMapping instanceof BasicMapping) {
			final BasicMapping<?, ?> basicMapping = (BasicMapping<?, ?>) idMapping;

			final JoinColumn joinColumn = this.locateJoinColumn(basicMapping.getColumn());

			// link the join column
			if (virtual) {
				joinColumn.setColumnProperties(mapping, ((BasicMapping<?, ?>) idMapping).getColumn(), this.locateMasterColumn(mapping, joinColumn));
			}
			else {
				joinColumn.setColumnProperties(mapping, ((BasicMapping<?, ?>) idMapping).getColumn(), id);
			}
		}

		// if the mapping SingularAssociationMapping then create a join column for each of the join columns of the mapping
		else if (idMapping instanceof SingularAssociationMapping) {
			final ForeignKey foreignKey = ((SingularAssociationMapping<?, ?>) mapping).getForeignKey();

			for (final JoinColumn referencedColumn : foreignKey.getJoinColumns()) {
				final JoinColumn joinColumn = this.locateJoinColumn(referencedColumn);

				// link the join column
				if (virtual) {
					joinColumn.setColumnProperties(mapping, referencedColumn, this.locateMasterColumn(mapping, joinColumn));
				}
				else {
					joinColumn.setColumnProperties(mapping, referencedColumn, id);
				}

				// link the join column
				joinColumn.setColumnProperties(mapping, referencedColumn, id);
			}
		}

		// if the mapping is embedded mapping enumerate child mappings and polymorphically create the join columns
		else {
			final EmbeddedMapping<?, ?> embeddedMapping = (EmbeddedMapping<?, ?>) idMapping;

			for (final Mapping<?, ?, ?> child : embeddedMapping.getChildren()) {
				if (child instanceof SingularMapping) {
					this.linkJoinColumnsWithMetadata(mapping, (SingularMapping<?, ?>) child);
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

	private AbstractColumn locateMasterColumn(AssociationMapping<?, ?, ?> mapping, final JoinColumn joinColumn) {
		final EntityTypeImpl<?> type = (EntityTypeImpl<?>) mapping.getRoot().getType();

		for (final AbstractColumn column : type.getPrimaryTable().getPkColumns()) {
			if (column.getName().equals(joinColumn.getName())) {
				return column;
			}
		}

		throw new MappingException("Primary key column cannot be located: " + joinColumn.getName() + ". Possible columns are: "
			+ type.getPrimaryTable().getPkColumnNames(), mapping.getAttribute().getLocator());
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
	 * @since 2.0.0
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
	 * @since 2.0.0
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
	 * @since 2.0.0
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
	 * @since 2.0.0
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
	 * @since 2.0.0
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
